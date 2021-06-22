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
import org.springframework.data.domain.Pageable;
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

    public Result getHandlingRateOfTransferredCasesByDepOrField(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 2:
                return getHandlingRateOfTransferredCasesByDepAll(parameterVo);
            case 3:
                return getHandlingRateOfTransferredCasesBySectionAll(parameterVo);
            case 4:
                return getHandlingRateOfTransferredCasesByFieldAll(parameterVo);
        }
        return null;
    }

    private Result getHandlingRateOfTransferredCasesByFieldAll(ParameterVo parameterVo) {
        List resultList = new ArrayList();
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(), parameterVo.getPageable());
        List classifierInfoCode = new ArrayList();
        for(ClassifierInfo info : classifiersCodeByFieldWithPageable){
            classifierInfoCode.add(info.getClassifiersCode());
        }
        // 1. 获取转入总次数
        int sumOfTheDateAndReceiveIdList = transferProcessRepository.getSumOfTheDateAndReceiveIdList(parameterVo.getStartDate(), parameterVo.getEndDate(), classifierInfoCode);

        //2. 获取出案总数
        List<Object[]> sumAllNumberOfCaseByClassifiersCodeAndOutTime = detailsOfTheCaseExtRepository.getSumAllNumberOfCaseByClassifiersCodeAndOutTime(parameterVo.getStartDate(), parameterVo.getEndDate(), classifierInfoCode);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if((sumAllNumberOfCaseByClassifiersCodeAndOutTime.size() + sumOfTheDateAndReceiveIdList) ==0){
            linkedHashMap.put("接收分类员部门",parameterVo.getSecondClassify());
            linkedHashMap.put("出案案件数",0);
            linkedHashMap.put("转入总次数",0);
            linkedHashMap.put("处理转案率",0);

        }else{
            linkedHashMap.put("接收分类员部门",parameterVo.getSecondClassify());
            linkedHashMap.put("出案案件数",sumAllNumberOfCaseByClassifiersCodeAndOutTime.size());
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率",Double.valueOf((sumOfTheDateAndReceiveIdList * 100 / (sumOfTheDateAndReceiveIdList + sumAllNumberOfCaseByClassifiersCodeAndOutTime.size()))) + "%");
        }
        List list= new ArrayList<>();
        list.add(linkedHashMap);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByFieldWithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    private Result getHandlingRateOfTransferredCasesBySectionAll(ParameterVo parameterVo) {
        String dep1 = parameterVo.getSecondClassify().substring(0,2);
        String dep2 = parameterVo.getSecondClassify().substring(2,4);
        parameterVo.setRows(1000);
        Page<ClassifierInfo> dep2WithPageable = classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1, dep2, parameterVo.getPageable());
        List classifierInfoCode = new ArrayList();
        dep2WithPageable.getContent().forEach(info -> {
            classifierInfoCode.add(info.getClassifiersCode());
        });
        // 1. 获取转入总次数
        int sumOfTheDateAndReceiveIdList = transferProcessRepository.getSumOfTheDateAndReceiveIdList(parameterVo.getStartDate(), parameterVo.getEndDate(), classifierInfoCode);
        //2. 获取出案总数
        List<Object[]> sumAllNumberOfCaseByClassifiersCodeAndOutTime = detailsOfTheCaseExtRepository.getSumAllNumberOfCaseByClassifiersCodeAndOutTime(parameterVo.getStartDate(), parameterVo.getEndDate(), classifierInfoCode);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if((sumAllNumberOfCaseByClassifiersCodeAndOutTime.size() + sumOfTheDateAndReceiveIdList) ==0){
            linkedHashMap.put("接收分类员部门",parameterVo.getSecondClassify());
            linkedHashMap.put("出案案件数",0);
            linkedHashMap.put("转入总次数",0);
            linkedHashMap.put("处理转案率",0);

        }else{
            linkedHashMap.put("接收分类员部门",parameterVo.getSecondClassify());
            linkedHashMap.put("出案案件数",sumAllNumberOfCaseByClassifiersCodeAndOutTime.size());
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率",Double.valueOf((sumOfTheDateAndReceiveIdList * 100 / (sumOfTheDateAndReceiveIdList + sumAllNumberOfCaseByClassifiersCodeAndOutTime.size()))) + "%");
        }
        List list= new ArrayList<>();
        list.add(linkedHashMap);
        PageInfo pageInfo = PageInfo.ofMap(dep2WithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    private Result getHandlingRateOfTransferredCasesByDepAll(ParameterVo parameterVo) {
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByDep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),(Pageable)parameterVo.getPageable());
        List<ClassifierInfo> content = classifiersCodeByDep1WithPageable.getContent();
        List classifierInfoCode = new ArrayList();
        content.forEach((info)->{
            classifierInfoCode.add(info.getClassifiersCode());
        });
        // 1. 获取转入总次数
        int sumOfTheDateAndReceiveIdList = transferProcessRepository.getSumOfTheDateAndReceiveIdList(parameterVo.getStartDate(), parameterVo.getEndDate(), classifierInfoCode);
        //2. 获取出案总数
        List<Object[]> sumAllNumberOfCaseByClassifiersCodeAndOutTime = detailsOfTheCaseExtRepository.getSumAllNumberOfCaseByClassifiersCodeAndOutTime(parameterVo.getStartDate(), parameterVo.getEndDate(), classifierInfoCode);
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        if((sumAllNumberOfCaseByClassifiersCodeAndOutTime.size() + sumOfTheDateAndReceiveIdList) ==0){
            linkedHashMap.put("接收分类员部门",parameterVo.getSecondClassify());
            linkedHashMap.put("出案案件数",0);
            linkedHashMap.put("转入总次数",0);
            linkedHashMap.put("处理转案率",0);

        }else{
            linkedHashMap.put("接收分类员部门",parameterVo.getSecondClassify());
            linkedHashMap.put("出案案件数",sumAllNumberOfCaseByClassifiersCodeAndOutTime.size());
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率",Double.valueOf((sumOfTheDateAndReceiveIdList * 100 / (sumOfTheDateAndReceiveIdList + sumAllNumberOfCaseByClassifiersCodeAndOutTime.size()))) + "%");
        }
        List list= new ArrayList<>();
        list.add(linkedHashMap);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByDep1WithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    public Result getHandlingRateOfTransferredCases(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1:
                return getHandlingRateOfTransferredCasesByPerson(parameterVo);
            case 2:
                return getHandlingRateOfTransferredCasesByDep(parameterVo);
            case 3:
                return getHandlingRateOfTransferredCasesBySection(parameterVo);
            case 4:
                return getHandlingRateOfTransferredCasesByField(parameterVo);
        }
        return null;
    }

    private Result getHandlingRateOfTransferredCasesByField(ParameterVo parameterVo) {
        List resultList = new ArrayList<>();
        //1.查询该部门下所有人员
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> fieldPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo classifierInfo : fieldPageable.getContent()){
            resultList.add(getHandlingRateOfTransferredCasesByOnePerson(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfo.getClassifiersCode(),classifierInfo.getEname()));
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
            resultList.add(getHandlingRateOfTransferredCasesByOnePerson(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfo.getClassifiersCode(),classifierInfo.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(dep2WithPageable,resultList);
        return Result.of(pageInfo);
    }

    private Result getHandlingRateOfTransferredCasesByDep(ParameterVo parameterVo) {
        List resultList = new ArrayList<>();
        //1.查询该部门下所有人员
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> dep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(), page.getPageable());
        for(ClassifierInfo classifierInfo : dep1WithPageable.getContent()){
            resultList.add(getHandlingRateOfTransferredCasesByOnePerson(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfo.getClassifiersCode(),classifierInfo.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(dep1WithPageable,resultList);
        return Result.of(pageInfo);
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
            linkedHashMap.put("接收分类员代码",classifierCode);
            linkedHashMap.put("接收分类员姓名",ename);
            linkedHashMap.put("出案案件数",sumNumberOfCaseByClassifiersCodeAndOutTime);
            linkedHashMap.put("转入总次数",acceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId);
            linkedHashMap.put("处理转案率",accuracy_num + "%");

            return linkedHashMap;
        }else{
            linkedHashMap.put("接收分类员代码",classifierCode);
            linkedHashMap.put("接收分类员姓名",ename);
            linkedHashMap.put("出案案件数",0);
            linkedHashMap.put("转入总次数",0);
            linkedHashMap.put("处理转案率",0);
            return linkedHashMap;
        }
    }


}
