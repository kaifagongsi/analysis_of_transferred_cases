package com.kfgs.aotc.transferinacceptancerate.service;

import com.kfgs.aotc.common.pojo.PageCondition;
import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class TransferInAcceptanceRateService {

    @Autowired
    ClassifierInfoRepository classifierInfoRepository;

    @Autowired
    TransferProcessRepository transferProcessRepository;

    /**
     * 转入接受率
     * 转入接受率 = 本人接受转案次数 / 转入总次数
     * @param parameterVo
     * @return
     */
    public Result getTransferInAcceptanceRateAll(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 2:
                return getTransferInAcceptanceRateByDepAll(parameterVo);
            case 3:
                return getTransferInAcceptanceRateBySectionAll(parameterVo);
            case 4:
                return getTransferInAcceptanceRateByFieldAll(parameterVo);
        }
        return null;
    }

    private Result getTransferInAcceptanceRateByFieldAll(ParameterVo parameterVo) {
        List resultList = new ArrayList();
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(), parameterVo.getPageable());
        List classifierInfoCode = new ArrayList();
        for(ClassifierInfo info : classifiersCodeByFieldWithPageable){
            classifierInfoCode.add(info.getClassifiersCode());
        }
        //1.本人接受转案次数
        int acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndReciveIds(parameterVo.getStartDate(), parameterVo.getEndDate(), "接收转案", classifierInfoCode);
        //2.该人员转入总次数
        int acceptReferralCountNumberBySendTimeBetweenAndReciveIds = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndReciveIds(parameterVo.getStartDate(), parameterVo.getEndDate(), classifierInfoCode);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if(0 == acceptReferralCountNumberBySendTimeBetweenAndReciveIds){
            linkedHashMap.put("转入领域",parameterVo.getSecondClassify());
            linkedHashMap.put("接收转案次数",0);
            linkedHashMap.put("转入总次数",0);
            linkedHashMap.put("转入接收率", "0%");
        }else{
            linkedHashMap.put("转入领域",parameterVo.getSecondClassify());
            linkedHashMap.put("接收转案次数",acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds);
            linkedHashMap.put("转入总次数",acceptReferralCountNumberBySendTimeBetweenAndReciveIds);
            linkedHashMap.put("转入接收率", Double.valueOf((acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds *100 ) / acceptReferralCountNumberBySendTimeBetweenAndReciveIds) + "%");
        }
        List list= new ArrayList<>();
        list.add(linkedHashMap);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByFieldWithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    private Result getTransferInAcceptanceRateBySectionAll(ParameterVo parameterVo) {
        String dep1 = parameterVo.getSecondClassify().substring(0,2);
        String dep2 = parameterVo.getSecondClassify().substring(2,4);
        parameterVo.setRows(1000);
        Page<ClassifierInfo> dep2WithPageable = classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1, dep2, parameterVo.getPageable());
        List classifierInfoCode = new ArrayList();
        dep2WithPageable.getContent().forEach(info -> {
            classifierInfoCode.add(info.getClassifiersCode());
        });

        //1.本人接受转案次数
        int acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndReciveIds(parameterVo.getStartDate(), parameterVo.getEndDate(), "接收转案", classifierInfoCode);
        //2.该人员转入总次数
        int acceptReferralCountNumberBySendTimeBetweenAndReciveIds = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndReciveIds(parameterVo.getStartDate(), parameterVo.getEndDate(), classifierInfoCode);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if(0 == acceptReferralCountNumberBySendTimeBetweenAndReciveIds){
            linkedHashMap.put("转入部门",parameterVo.getSecondClassify());
            linkedHashMap.put("接收转案次数",0);
            linkedHashMap.put("转入总次数",0);
            linkedHashMap.put("转入接收率", "0%");
        }else{
            linkedHashMap.put("转入部门",parameterVo.getSecondClassify());
            linkedHashMap.put("接收转案次数",acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds);
            linkedHashMap.put("转入总次数",acceptReferralCountNumberBySendTimeBetweenAndReciveIds);
            linkedHashMap.put("转入接收率", Double.valueOf((acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds *100 ) / acceptReferralCountNumberBySendTimeBetweenAndReciveIds) + "%");
        }
        List list= new ArrayList<>();
        list.add(linkedHashMap);
        PageInfo pageInfo = PageInfo.ofMap(dep2WithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    private Result getTransferInAcceptanceRateByDepAll(ParameterVo parameterVo) {
        //1.查询该部门下所有 人员
        PageCondition page = (PageCondition)parameterVo;
        parameterVo.setRows(1000);
        Page<ClassifierInfo> dep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),page.getPageable());
        List classifiers = new ArrayList();
        for(ClassifierInfo classifierInfo : dep1WithPageable){
            classifiers.add(classifierInfo.getClassifiersCode());
        }
        //1.本人接受转案次数
        int acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndReciveIds(parameterVo.getStartDate(), parameterVo.getEndDate(), "接收转案", classifiers);
        //2.该人员转入总次数
        int acceptReferralCountNumberBySendTimeBetweenAndReciveIds = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndReciveIds(parameterVo.getStartDate(), parameterVo.getEndDate(), classifiers);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if(0 == acceptReferralCountNumberBySendTimeBetweenAndReciveIds){
            linkedHashMap.put("转入部门",parameterVo.getSecondClassify());
            linkedHashMap.put("接收转案次数",0);
            linkedHashMap.put("转入总次数",0);
            linkedHashMap.put("转入接收率", "0%");
        }else{
            linkedHashMap.put("转入部门",parameterVo.getSecondClassify());
            linkedHashMap.put("接收转案次数",acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds);
            linkedHashMap.put("转入总次数",acceptReferralCountNumberBySendTimeBetweenAndReciveIds);
            linkedHashMap.put("转入接收率", Double.valueOf((acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds *100 ) / acceptReferralCountNumberBySendTimeBetweenAndReciveIds) + "%");
        }
        List list= new ArrayList<>();
        list.add(linkedHashMap);
        PageInfo pageInfo = PageInfo.ofMap(dep1WithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 转入接受率
     * 转入接受率 = 本人接受转案次数 / 转入总次数
     * @param parameterVo
     * @return
     */
    public Result getTransferInAcceptanceRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1:
                return getTransferInAcceptanceRateByPerson(parameterVo);
            case 2:
                return getTransferInAcceptanceRateByDep(parameterVo);
            case 3:
                return getHandlingRateOfTransferredCasesBySection(parameterVo);
            case 4:
                return getHandlingRateOfTransferredCasesByField(parameterVo);
        }
        return null;
    }

    private Result getHandlingRateOfTransferredCasesByField(ParameterVo parameterVo) {
        List resultList = new ArrayList<>();
        //1.查询领域下所有人员
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> fieldPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo classifierInfo : fieldPageable.getContent()){
            resultList.add(getTransferInAcceptanceRateByOnePerson(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfo.getClassifiersCode(),classifierInfo.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(fieldPageable,resultList);
        return Result.of(pageInfo);
    }

    private Result getHandlingRateOfTransferredCasesBySection(ParameterVo parameterVo) {
        List resultList = new ArrayList<>();
        //1.查询该部门下所有人员
        PageCondition page = (PageCondition)parameterVo;
        String dep1 = parameterVo.getSecondClassify().substring(0,2);
        String dep2 = parameterVo.getSecondClassify().substring(2,4);
        Page<ClassifierInfo> dep2WithPageable = classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1,dep2, page.getPageable());
        for(ClassifierInfo classifierInfo : dep2WithPageable.getContent()){
            resultList.add(getTransferInAcceptanceRateByOnePerson(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfo.getClassifiersCode(),classifierInfo.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(dep2WithPageable,resultList);
        return Result.of(pageInfo);
    }

    private Result getTransferInAcceptanceRateByDep(ParameterVo parameterVo) {
        List resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify() ,page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(getTransferInAcceptanceRateByOnePerson(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        return Result.of(pageInfo);
    }

    private Result getTransferInAcceptanceRateByPerson(ParameterVo parameterVo) {
        List resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify() ,page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(getTransferInAcceptanceRateByOnePerson(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        return Result.of(pageInfo);
    }
    // 转入接受率 = 本人接受转案次数 / 转入总次数
    private LinkedHashMap getTransferInAcceptanceRateByOnePerson(String startDate, String endDate, String classifiersCode, String ename) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        //1.本人接受转案次数
        int acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndReciveIds(startDate, endDate, "接收转案", Arrays.asList(classifiersCode));
        //2.该人员转入总次数
        int acceptReferralCountNumberBySendTimeBetweenAndReciveIds = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndReciveIds(startDate, endDate, Arrays.asList(classifiersCode));
        if(0 == acceptReferralCountNumberBySendTimeBetweenAndReciveIds){
            linkedHashMap.put("接收分类员代码",classifiersCode);
            linkedHashMap.put("接收分类员姓名",ename);
            linkedHashMap.put("接收转案次数",0);
            linkedHashMap.put("转入总次数",0);
            linkedHashMap.put("转入接收率", "0%");
            return linkedHashMap;
        }else{
            linkedHashMap.put("接收分类员代码",classifiersCode);
            linkedHashMap.put("接收分类员姓名",ename);
            linkedHashMap.put("接收转案次数",acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds);
            linkedHashMap.put("转入总次数",acceptReferralCountNumberBySendTimeBetweenAndReciveIds);
            linkedHashMap.put("转入接收率", Double.valueOf((acceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds *100 ) / acceptReferralCountNumberBySendTimeBetweenAndReciveIds) + "%");
            return linkedHashMap;
        }
    }


}
