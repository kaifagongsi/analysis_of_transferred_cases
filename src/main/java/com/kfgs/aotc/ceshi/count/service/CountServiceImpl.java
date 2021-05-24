package com.kfgs.aotc.ceshi.count.service;

import com.kfgs.aotc.annotation.In;
import com.kfgs.aotc.ceshi.classifier.service.ClassifierService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.DetailOfCaseFinished;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailOfCaseFinishedRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import com.kfgs.aotc.util.CountUtil;
import com.kfgs.aotc.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;


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
    public Result getEffectiveTransferRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1: //按人计算
                return countAccuracyByWorkerID(parameterVo.getStartDate(),parameterVo.getEndDate(),parameterVo.getSecondClassify());
            case 2: //按部计算
                return countAccuracyByDepartment(parameterVo.getStartDate(),parameterVo.getEndDate(),parameterVo.getSecondClassify());
            case 3: //按室计算
                System.out.println("3");
                break;
            default:
        }
        return null;
    }

    /*@Override
    public Result<List<Map<String,String>>> countAccuracy(List<String> list, ParameterVo parameterVo) {
        List<Map<String,String>> resultList = new ArrayList<>();
        String startDate = parameterVo.getStartDate();
        String endDate = parameterVo.getEndDate();

        for (int i=0;i<list.size();i++){
            Map<String,String> map = new HashMap<>();
            String classifierCode = list.get(i);
            map = countAccuracyByWorkerID(classifierCode,startDate,endDate);
            resultList.add(map);
        }
        return Result.of(resultList);
    }*/
    private Result countAccuracyByDepartment(String startDate,String endDate,String department){
        return null;
    }

    private Result countAccuracyByWorkerID(String startDate,String endDate,String classifierID) {
        double accuracy_num = 0;//有效转案率
        List<Map<String,String>> resultList = new ArrayList<>();
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //分子1:获取接收转案案件数量
        int receiveTotals = transferProcessRepository.getSumOfReceiveBySendID(classifierID);
        //分子2:拒绝转案案件且已出案中案件数量
        //int rejectTotals = transferProcessRepository.getSumOFRejectBySendID(startDate,endDate,classifierID);
        //拒绝转案且在已出案案件列表中
        List<Object[]> transferProcessList = detailOfCaseFinishedRepository.getValidTransferCases(startDate,endDate,classifierID);
        List<DetailOfCaseFinished> detailOfCaseFinisheds = EntityUtils.castEntity(transferProcessList,DetailOfCaseFinished.class);
        int rejectTotals = transferProcessList.size();
        int totalTrans = receiveTotals + rejectTotals;
        int validTrans = 0; //有效转案
        if (totalTrans == 0){ //分母不能为0
            result.put("分类员代码",classifierID);
            result.put("转案总次数","0");
            result.put("转案接收总次数","0");
            result.put("转案退回且出案数","0");
            result.put("转案退回有效次数","0");
            result.put("有效转案率","0%");
        }else{
            /*
            有效转案案件
             */
            /*for (int i=0;i<detailOfCaseFinisheds.size();i++){
                DetailOfCaseFinished detailOfCaseTransProcess = detailOfCaseFinisheds.get(i);
                //案件ID
                String case_id = detailOfCaseTransProcess.getId();
                //接收分类员ID
                String classifiersCode = detailOfCaseTransProcess.getClassifiersCode();
                //获取接收分类员分类号CLASSIFIERS_AND_CODE
                List<String> detailCodes = CountUtil.CLASSIFIERS_AND_CODE.get(classifiersCode);
                if (detailCodes == null){
                    continue;
                }
                String ipc = "";
                if (detailOfCaseTransProcess.getIpcmi() != null){
                    ipc += detailOfCaseTransProcess.getIpcmi();
                }
                if (detailOfCaseTransProcess.getIpcoi() != null){
                    ipc += ",";
                    ipc += detailOfCaseTransProcess.getIpcoi();
                }
                if (detailOfCaseTransProcess.getIpca() != null){
                    ipc += ",";
                    ipc += detailOfCaseTransProcess.getIpca();
                }
                String[] finishCodes = ipc.split(",");
                for(int j=0;j<finishCodes.length;j++) {
                    if (finishCodes[j] != null && finishCodes[j].length()>=4){
                        finishCodes[j] = finishCodes[j].substring(0, 4);
                    }
                }
                //System.out.println(case_id+classifiersCode);
                //比对
                if (!Collections.disjoint(Arrays.asList(finishCodes),detailCodes)){
                    validTrans ++;
                }else{
                    System.out.println(case_id + classifiersCode);
                }
            }*/
            //2.拒绝转案并且在已出案中的所有ipc
            validTrans = 0; //有效转案
            List<String> ipc =  transferProcessRepository.getRefuseReferralBySendTimeBetweenAndTipeTitleAndSendId(startDate, endDate, "拒绝转案", classifierID);

            //获取接收分类员分类号CLASSIFIERS_AND_CODE
            List<String> detailCodes = CountUtil.CLASSIFIERS_AND_CODE.get(classifierID);
            // 如何同一个案子前四位均是一样的，那么就会添加两次，那么在，主分和副分均是H01R,那么finishCodes会有两个H01R
            for(String ipc_str : ipc){
                // 当前案子的所有前四位分类号
                String[] finishCodes = ipc_str.split(",");
                for(int j=0;j<finishCodes.length;j++) {
                    if (finishCodes[j] != null && finishCodes[j].length()>=4){
                        finishCodes[j] = finishCodes[j].substring(0, 4);
                    }
                }
                //比对
                if (!Collections.disjoint(Arrays.asList(finishCodes),detailCodes)){
                    validTrans ++;
                }

            }
            accuracy_num = (receiveTotals + validTrans)*100/totalTrans;
            result.put("分类员代码",classifierID);
            result.put("转案总次数",Integer.toString(totalTrans));
            result.put("转案接收总次数", Integer.toString(receiveTotals));
            result.put("转案退回且出案数",Integer.toString(rejectTotals));
            result.put("转案退回有效次数",Integer.toString(validTrans));
            result.put("有效转案率",df.format(accuracy_num)+"%");
            System.out.println(result);
        }
        resultList.add(result);
        return Result.of(resultList);
    }
}
