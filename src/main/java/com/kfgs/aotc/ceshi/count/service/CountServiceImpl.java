package com.kfgs.aotc.ceshi.count.service;

import com.kfgs.aotc.annotation.In;
import com.kfgs.aotc.ceshi.classifier.service.ClassifierService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.DetailOfCaseFinished;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailOfCaseFinishedRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import com.kfgs.aotc.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class CountServiceImpl implements CountService {

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;
    @Autowired
    private TransferProcessRepository transferProcessRepository;
    @Autowired
    private DetailOfCaseFinishedRepository detailOfCaseFinishedRepository;


    @Override
    public Result<List<Map<String,String>>> countAccuracy(List<String> list) {
        List<Map<String,String>> resultList = new ArrayList<>();
        for (int i=0;i<list.size();i++){
            Map<String,String> map = new HashMap<>();
            String classifierCode = list.get(i);
            map = countAccuracyByID(classifierCode);
            resultList.add(map);
        }
        return Result.of(resultList);
    }

    //@Override
    public Map<String,String> countAccuracyByID(String classifierID) {
        double accuracy_num = 0;//有效转案率
        Map<String, ClassifierInfo> classifierInfoMap = new ConcurrentHashMap<>();
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //分子1:获取接收转案案件数量
        int receiveTotals = transferProcessRepository.getSumOfReceiveBySendID(classifierID);
        //分子2:拒绝转案案件且已出案中
        //int rejectTotals = transferProcessRepository.getSumOFRejectBySendID(classifierID);
        //拒绝转案且在已出案案件列表中
        List<Object[]> transferProcessList = detailOfCaseFinishedRepository.getValidTransferCases(classifierID);
        List<DetailOfCaseFinished> detailOfCaseFinisheds = EntityUtils.castEntity(transferProcessList,DetailOfCaseFinished.class);
        int rejectTotals = transferProcessList.size();
        int totalTrans = receiveTotals + rejectTotals;
        int validTrans = 0; //有效转案
        if (totalTrans == 0){ //分母不能为0
            return null;
        }else{
            for (int i=0;i<detailOfCaseFinisheds.size();i++){
                DetailOfCaseFinished detailOfCaseTransProcess = detailOfCaseFinisheds.get(i);
                //分类员ID
                String classifiersCode = detailOfCaseTransProcess.getClassifiersCode().toString();
                //分类员分类信息
                ClassifierInfo classifierInfo = classifierInfoRepository.getOne(classifiersCode);
                //获取案件出案分类号信息
                String finishIpcmi = "";
                String finishIpcoi = "";
                String finishIpca = "";
                if (detailOfCaseTransProcess.getIpcmi() != null){
                    finishIpcmi = detailOfCaseTransProcess.getIpcmi().substring(0,4);
                }
                if (detailOfCaseTransProcess.getIpcoi() != null){
                    finishIpcoi = detailOfCaseTransProcess.getIpcoi().substring(0,4);
                }
                if (detailOfCaseTransProcess.getIpca() != null){
                    finishIpca = detailOfCaseTransProcess.getIpca().substring(0,4);
                }
                List<String> finishCodes = new ArrayList<>();
                finishCodes.add(finishIpcmi);
                finishCodes.add(finishIpcoi);
                finishCodes.add(finishIpca);
                //获取分类员分类号
                String classification_field = classifierInfo.getClassificationField();
                String[] codes = classification_field.split(",");
                List<String> detailCodes = new ArrayList<>();
                for (int j=0;j<codes.length;j++){
                        detailCodes.add(codes[j]);
                }
                //比对
                if (!Collections.disjoint(finishCodes,detailCodes)){
                    validTrans ++;
                }
            }
            accuracy_num = (receiveTotals + validTrans)*100/totalTrans;
        }
        Map<String,String> result = new HashMap<>();
        result.put("分类员代码",classifierID);
        result.put("转案总次数",Integer.toString(totalTrans));
        result.put("转案接收总次数", Integer.toString(receiveTotals));
        result.put("转案退回且出案数",Integer.toString(rejectTotals));
        result.put("转案退回有效次数",Integer.toString(validTrans));
        result.put("有效转案率",df.format(accuracy_num)+"%");
        System.out.println(result);
        return result;
    }
}
