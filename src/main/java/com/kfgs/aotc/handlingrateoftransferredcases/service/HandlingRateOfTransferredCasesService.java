package com.kfgs.aotc.handlingrateoftransferredcases.service;

import com.kfgs.aotc.common.pojo.PageCondition;
import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailsOfTheCaseExtRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class HandlingRateOfTransferredCasesService {

    @Autowired
    TransferProcessRepository transferProcessRepository;

    @Autowired
    DetailsOfTheCaseExtRepository detailsOfTheCaseExtRepository;

    @Autowired
    ClassifierInfoRepository classifierInfoRepository;


    public Result getHandlingRateOfTransferredCases(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1:
                return getHandlingRateOfTransferredCasesByPerson(parameterVo);
            case 2:
                return getHandlingRateOfTransferredCasesByPersondep(parameterVo);
        }
        return null;
    }

    private Result getHandlingRateOfTransferredCasesByPersondep(ParameterVo parameterVo) {

        return null;
    }

    private Result getHandlingRateOfTransferredCasesByPerson(ParameterVo parameterVo) {
        List resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify() ,page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(getHandlingRateOfTransferredCasesByOnePerson(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        return Result.of(pageInfo);
    }

    /**
     * 个人返回 处理转案率
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param classifierCode classCode
     * @param ename 全名
     * @return
     */
    private LinkedHashMap getHandlingRateOfTransferredCasesByOnePerson(String startDate,String endDate,String classifierCode,String ename) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        //1.获取转入总次数
        Integer acceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId = transferProcessRepository.getCountNumberByReceiveTimeBetweenAndReceiveId(startDate,endDate,classifierCode);
        //2.获取出案案件数
        Integer sumNumberOfCaseByClassifiersCodeAndOutTime = detailsOfTheCaseExtRepository.getSumNumberOfCaseByClassifiersCodeAndOutTime(startDate, endDate, classifierCode);
        if( 0 != (sumNumberOfCaseByClassifiersCodeAndOutTime+ acceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId)){
            //3.转入总次数
            Double accuracy_num = Double.valueOf((acceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId * 100 / (acceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId + sumNumberOfCaseByClassifiersCodeAndOutTime)));
            System.out.println("当前人员：" + classifierCode + "时间：" + startDate + "-"+endDate +
                    "，转入总数：" + acceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId +
                    "，出案案件数：" + sumNumberOfCaseByClassifiersCodeAndOutTime +
                    "，百分比：" + accuracy_num);
            linkedHashMap.put("接受分类员代码",classifierCode);
            linkedHashMap.put("接受分类员姓名",ename);
            linkedHashMap.put("出案案件数",sumNumberOfCaseByClassifiersCodeAndOutTime);
            linkedHashMap.put("转入总次数",acceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId);
            linkedHashMap.put("处理转案率",accuracy_num + "%");

            return linkedHashMap;
        }else{
            linkedHashMap.put("接受分类员代码",classifierCode);
            linkedHashMap.put("接受分类员姓名",ename);
            linkedHashMap.put("出案案件数",0);
            linkedHashMap.put("转入总次数",0);
            linkedHashMap.put("处理转案率",0);
            return linkedHashMap;
        }
    }

}
