package com.kfgs.aotc.ceshi.transferoutrate.service;

import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailOfCaseFinishedRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TOServiceImpl implements TOService {

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;
    @Autowired
    private TransferProcessRepository transferProcessRepository;
    @Autowired
    private DetailOfCaseFinishedRepository detailOfCaseFinishedRepository;

    @Override
    public Result getTransferOutRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1: //按人计算
                LinkedHashMap<String,String> result = getTransferOutRateByClassifiers(parameterVo.getStartDate(),parameterVo.getEndDate(),parameterVo.getSecondClassify());
                List<Map<String,String>> resultList = new ArrayList<>();
                PageInfo pageInfo = new PageInfo();
                resultList.add(result);
                pageInfo.setPage(parameterVo.getPage()); //当前页
                pageInfo.setPageSize(parameterVo.getRows()); //每页条数
                pageInfo.setRecords(resultList.size()); //总记录数
                pageInfo.setTotal(1); //总页数
                pageInfo.setRows(resultList);
                return Result.of(pageInfo);
            case 2: //按部级转案

        }
        return null;
    }

    /**
     * 个人转出案件率
     * 转出总次数/(出案案件数+转出总次数)
     * @param startDate
     * @param endDate
     * @param classifierID
     * @return
     */
    private LinkedHashMap<String, String> getTransferOutRateByClassifiers(String startDate,String endDate,String classifierID){
        double accuracy_num = 0;//有效转案率
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //分子：转出总次数
        int transferOutNum = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndReceiveId(startDate,endDate,classifierID);
        //出案案件数
        int caseOutNum = detailOfCaseFinishedRepository.getSumTransferOut(startDate,endDate,classifierID);
        //分母
        int fenmu = transferOutNum+caseOutNum;
        accuracy_num = transferOutNum * 100/fenmu;
        result.put("分类员代码",classifierID);
        result.put("出案案件数",Integer.toString(caseOutNum));
        result.put("转出总次数",Integer.toString(transferOutNum));
        result.put("转出案件率",df.format(accuracy_num)+"%");
        System.out.println(result);
        return result;
    }
}
