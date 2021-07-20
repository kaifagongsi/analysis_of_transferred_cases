package com.kfgs.aotc.transferredcasesdayweekmonth.service.impl;

import com.kfgs.aotc.common.pojo.PageCondition;
import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailsOfTheCaseExtRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import com.kfgs.aotc.transferredcasesdayweekmonth.service.ITransferredCasesService;
import com.kfgs.aotc.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class TransferredCasesServiceByDay implements ITransferredCasesService {

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;

    @Autowired
    private TransferProcessRepository transferProcessRepository;

    @Autowired
    private DetailsOfTheCaseExtRepository detailsOfTheCaseExtRepository;

    @Override
    public boolean choseCurrent(String type) {
        if("day".equalsIgnoreCase(type)){
            return true;
        }
        return false;
    }

    @Override
    public Result calculation(ParameterVo parameterVo) {
        System.out.println("按照天数开始计算");
        switch (parameterVo.getFirstClassify()){
            case 1:
                return getTransferredCasesDayByPerson(parameterVo);
            case 2:
                return getTransferredCasesDayByDep(parameterVo);
            case 3:
                return getTransferredCasesDayByDepSection(parameterVo);
            case 4:
                return getTransferredCasesDayByField(parameterVo);
        }
        return null;
    }

    private Result getTransferredCasesDayByField(ParameterVo parameterVo) {
        //0.初始化参数
        List<LinkedHashMap> resultList = new ArrayList<>();
        //1.获取部门下的所有人员
        PageCondition page = (PageCondition)parameterVo;
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(), page.getPageable());
        List classifiers = new ArrayList();
        for(ClassifierInfo info : classifiersCodeByFieldWithPageable){
            classifiers.add(info.getClassifiersCode());
        }
        // 2.开始计算
        List<String> dayList = DateUtil.getBetweenDays(parameterVo.getStartDate(), parameterVo.getEndDate());
        for(String day : dayList){
            resultList.add(getTransferredCasesDayOneDep(day,classifiers,parameterVo.getSecondClassify()));
        }
        PageInfo pageInfo = PageInfo.createPageInfo(dayList.size(),dayList.size(),dayList.size(),resultList);
        return Result.of(pageInfo);
    }

    private Result getTransferredCasesDayByDepSection(ParameterVo parameterVo) {

        if(parameterVo.getSecondClassify().length() == 4){
            //0.初始化参数
            List<LinkedHashMap> resultList = new ArrayList<>();
            //1.获取部门下的所有人员
            PageCondition page = (PageCondition)parameterVo;
            parameterVo.setRows(1000);
            String dep1 = parameterVo.getSecondClassify().substring(0,2);
            String dep2 = parameterVo.getSecondClassify().substring(2,4);
            Page<ClassifierInfo> dep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1,dep2,page.getPageable());
            List classifiers = new ArrayList();
            for(ClassifierInfo classifierInfo : dep1WithPageable){
                classifiers.add(classifierInfo.getClassifiersCode());
            }
            // 2.开始计算
            List<String> dayList = DateUtil.getBetweenDays(parameterVo.getStartDate(), parameterVo.getEndDate());
            for(String day : dayList){
                resultList.add(getTransferredCasesDayOneDep(day,classifiers,parameterVo.getSecondClassify()));
            }
            PageInfo pageInfo = PageInfo.createPageInfo(dayList.size(),dayList.size(),dayList.size(),resultList);
            return Result.of(pageInfo);
        }else{
            return null;
        }

    }

    private Result getTransferredCasesDayByDep(ParameterVo parameterVo) {
        //0.初始化参数
        List<LinkedHashMap> resultList = new ArrayList<>();
        //1.获取部门下的所有人员
        PageCondition page = (PageCondition)parameterVo;
        parameterVo.setRows(1000);
        Page<ClassifierInfo> dep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),page.getPageable());
        List classifiers = new ArrayList();
        for(ClassifierInfo classifierInfo : dep1WithPageable){
            classifiers.add(classifierInfo.getClassifiersCode());
        }
        // 2.开始计算
        List<String> dayList = DateUtil.getBetweenDays(parameterVo.getStartDate(), parameterVo.getEndDate());
        for(String day : dayList){
            resultList.add(getTransferredCasesDayOneDep(day,classifiers,parameterVo.getSecondClassify()));
        }
        PageInfo pageInfo = PageInfo.createPageInfo(dayList.size(),dayList.size(),dayList.size(),resultList);
        return Result.of(pageInfo);
    }
    // 获取 部门某一天的结果
    private LinkedHashMap getTransferredCasesDayOneDep(String day, List classifiers,String secondClassify) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        //1. 获取转入总次数
        int sumOfTheDateAndReceiveIdList = transferProcessRepository.getSumOfTheDateAndReceiveIdListAndReceiveTimeEquals(day,classifiers);
        //2. 获取出案总数
        int sumAllNumberOfCaseByClassifiersCodeListAndOutTime = detailsOfTheCaseExtRepository.getSumAllNumberOfCaseByClassifiersCodeListAndOutTimeEquals(day,classifiers);
        int fenmu = sumOfTheDateAndReceiveIdList + sumAllNumberOfCaseByClassifiersCodeListAndOutTime;
        if(fenmu != 0  ){
            linkedHashMap.put("处理转案日期",day);
            linkedHashMap.put("接收类型",secondClassify);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率", Double.valueOf((sumOfTheDateAndReceiveIdList *100 ) / fenmu) + "%" );
        }else{
            linkedHashMap.put("处理转案日期",day);
            linkedHashMap.put("接收类型",secondClassify);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率",   "0%" );
        }
        return linkedHashMap;
    }

    private Result getTransferredCasesDayByPerson(ParameterVo parameterVo) {
        List<LinkedHashMap> resultList = new ArrayList<>();
        //1.查询人员
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify(), parameterVo.getPageable());
        List<String> dayList = DateUtil.getBetweenDays(parameterVo.getStartDate(), parameterVo.getEndDate());
        for(ClassifierInfo info : classifierCode.getContent()){//正常来说只有一个人
            for(String day : dayList){
                resultList.add(getTransferredCasesDayOnePerson(day,info.getClassifiersCode(),info.getEname()));
            }
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        return Result.of(pageInfo);
    }

    private LinkedHashMap getTransferredCasesDayOnePerson(String day, String classifiersCode, String ename) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        // 1. 获取转入总次数
        int sumOfTheDateAndReceiveIdList = transferProcessRepository.getSumOfTheDateAndReceiveIdEqualsAndReceiveTimeEquals( day,classifiersCode);
        //2. 获取出案总数
        int sumAllNumberOfCaseByClassifiersCodeAndOutTime = detailsOfTheCaseExtRepository.getSumAllNumberOfCaseByClassifiersCodeAndOutTimeEquals(day, classifiersCode);
        int denominator = sumOfTheDateAndReceiveIdList + sumAllNumberOfCaseByClassifiersCodeAndOutTime;
        if(denominator != 0  ){
            linkedHashMap.put("处理转案日期",day);
            linkedHashMap.put("接收分类员代码",classifiersCode);
            linkedHashMap.put("接收分类员姓名",ename);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率", Double.valueOf((sumOfTheDateAndReceiveIdList *100 ) / denominator) + "%" );
        }else{
            linkedHashMap.put("处理转案日期",day);
            linkedHashMap.put("接收分类员代码",classifiersCode);
            linkedHashMap.put("接收分类员姓名",ename);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率",   "0%" );
        }
        return linkedHashMap;
    }


}
