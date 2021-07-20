package com.kfgs.aotc.transferredcasesdayweekmonth.service.impl;

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
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

@Component
public class TransferredCasesServiceByWeek  implements ITransferredCasesService {
    @Override
    public boolean choseCurrent(String type) {
        if("week".equalsIgnoreCase(type)){
            return true;
        }
        return false;
    }

    @Autowired
    ClassifierInfoRepository classifierInfoRepository;
    @Autowired
    TransferProcessRepository transferProcessRepository;

    @Autowired
    DetailsOfTheCaseExtRepository detailsOfTheCaseExtRepository;

    @Override
    public Result calculation(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1:
                return transferredCasesServiceWeekByPerson(parameterVo);
            case 2:
                return transferredCasesServiceWeekByDep(parameterVo);
            case 3:
                return transferredCasesServiceWeekBySection(parameterVo);
            case 4:
                return transferredCasesServiceWeekByField(parameterVo);

        }
        return Result.of(new ArrayList<>(),false,"");
    }

    private Result transferredCasesServiceWeekByField(ParameterVo parameterVo) {
        //0.初始化结果
        List resultList = new ArrayList<>();
        //1.获取部门下的所有人员
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(), null);
        List classifiers = new ArrayList();
        for(ClassifierInfo classifierInfo : classifiersCodeByFieldWithPageable){
            classifiers.add(classifierInfo.getClassifiersCode());
        }
        // 2.开始计算
        List<String> weekList = DateUtil.getWeekByLinkedList(parameterVo.getStartDate(),parameterVo.getEndDate());
        // 自定义分页
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = weekList.size(); //总记录数
        int m = records % rows;
        int total = 0; // 总页数
        if (m > 0){
            total = records / rows + 1;
        }else {
            total = records / rows;
        }
        //计算当前需要显示的数据下标起始值
        int startIndex = (pageNum -1) * rows;
        int endIndex = Math.min(startIndex + rows,records);
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getTransferredCasesWeekByClassifiers(weekList.get(i),classifiers,parameterVo.getSecondClassify()));
        }
        PageInfo pageInfo = PageInfo.createPageInfo(weekList.size(),weekList.size(),weekList.size(),resultList);
        return Result.of(pageInfo);
    }

    private Result transferredCasesServiceWeekBySection(ParameterVo parameterVo) {
        //0.初始化结果
        List resultList = new ArrayList<>();
        //1.获取部门下的所有人员
        String dep1 = parameterVo.getSecondClassify().substring(0,2);
        String dep2 = parameterVo.getSecondClassify().substring(2,4);
        Page<ClassifierInfo> dep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1,dep2,null);
        List classifiers = new ArrayList();
        for(ClassifierInfo classifierInfo : dep1WithPageable){
            classifiers.add(classifierInfo.getClassifiersCode());
        }
        // 2.开始计算
        List<String> weekList = DateUtil.getWeekByLinkedList(parameterVo.getStartDate(),parameterVo.getEndDate());
        // 自定义分页
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = weekList.size(); //总记录数
        int m = records % rows;
        int total = 0; // 总页数
        if (m > 0){
            total = records / rows + 1;
        }else {
            total = records / rows;
        }
        //计算当前需要显示的数据下标起始值
        int startIndex = (pageNum -1) * rows;
        int endIndex = Math.min(startIndex + rows,records);
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getTransferredCasesWeekByClassifiers(weekList.get(i),classifiers,parameterVo.getSecondClassify()));
        }
        PageInfo pageInfo = PageInfo.createPageInfo(weekList.size(),weekList.size(),weekList.size(),resultList);
        return Result.of(pageInfo);
    }

    private Result transferredCasesServiceWeekByDep(ParameterVo parameterVo) {
        //0.初始化结果
        List resultList = new ArrayList<>();
        //1.获取部门下的所有人员
        Page<ClassifierInfo> dep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),null);
        List classifiers = new ArrayList();
        for(ClassifierInfo classifierInfo : dep1WithPageable){
            classifiers.add(classifierInfo.getClassifiersCode());
        }
        // 2.开始计算
        List<String> weekList = DateUtil.getWeekByLinkedList(parameterVo.getStartDate(),parameterVo.getEndDate());
        if(!weekList.isEmpty()){
            // 自定义分页
            int pageNum = parameterVo.getPage(); //页码
            int rows = parameterVo.getRows(); //每页行数
            int records = weekList.size(); //总记录数
            int m = records % rows;
            int total = 0; // 总页数
            if (m > 0){
                total = records / rows + 1;
            }else {
                total = records / rows;
            }
            //计算当前需要显示的数据下标起始值
            int startIndex = (pageNum -1) * rows;
            int endIndex = Math.min(startIndex + rows,records);
            for(int i=startIndex;i<endIndex;i++){
                resultList.add(getTransferredCasesWeekByClassifiers(weekList.get(i),classifiers,parameterVo.getSecondClassify()));
            }
            PageInfo pageInfo = PageInfo.createPageInfo(records,rows,total,resultList);
            return Result.of(pageInfo);
        }else{
            return Result.of(null,false,"日期选择错误，请核查开始日期是否为周一，结束日期是否为周日");
        }

    }

    private Object getTransferredCasesWeekByClassifiers(String week, List classifiers, String secondClassify) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        //1. 获取转入总次数
        String startDay = week.split("~")[0];
        String endtDay = week.split("~")[1];
        int sumOfTheDateAndReceiveIdList = transferProcessRepository.getSumOfTheDateAndReceiveIdList(startDay,endtDay,classifiers);
        //2. 获取出案总数
        int sumAllNumberOfCaseByClassifiersCodeListAndOutTime = detailsOfTheCaseExtRepository.getSumNumberOfCaseByClassifiersCodeAndOutTime(startDay,endtDay,classifiers);
        int fenmu = sumOfTheDateAndReceiveIdList + sumAllNumberOfCaseByClassifiersCodeListAndOutTime;
        if(fenmu != 0  ){
            linkedHashMap.put("处理转案日期",week);
            linkedHashMap.put("接收类型",secondClassify);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率", Double.valueOf((sumOfTheDateAndReceiveIdList *100 ) / fenmu) + "%" );
        }else{
            linkedHashMap.put("处理转案日期",week);
            linkedHashMap.put("接收类型",secondClassify);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率",   "0%" );
        }
        return linkedHashMap;
    }

    private Result transferredCasesServiceWeekByPerson(ParameterVo parameterVo) {
        //0. 初始化结果
        List resultList = new ArrayList<>();
        //1。获取人员
        Page<ClassifierInfo> classifiersCodeByClassifierCode = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify(), null);
        List<String> weekList = DateUtil.getWeekByLinkedList(parameterVo.getStartDate(),parameterVo.getEndDate());
        // 自定义分页
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = weekList.size(); //总记录数
        int m = records % rows;
        int total = 0; // 总页数
        if (m > 0){
            total = records / rows + 1;
        }else {
            total = records / rows;
        }
        //计算当前需要显示的数据下标起始值
        int startIndex = (pageNum -1) * rows;
        int endIndex = Math.min(startIndex + rows,records);
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(transferredCasesServiceWeekByOnePerson(weekList.get(i),classifiersCodeByClassifierCode.getContent().get(0).getClassifiersCode(),classifiersCodeByClassifierCode.getContent().get(0).getEname()));
        }
        PageInfo pageInfo = PageInfo.createPageInfo(records,rows,total,resultList);
        return Result.of(pageInfo);
    }

    private Object transferredCasesServiceWeekByOnePerson(String week, String classifiersCode, String ename) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        //1.获取此人在此时间段内的转入总次数
        String startDay = week.split("~")[0];
        String endtDay = week.split("~")[1];
        List<String> list = Arrays.asList(classifiersCode);
        int sumOfTheDateAndReceiveIdList = transferProcessRepository.getSumOfTheDateAndReceiveIdList(startDay, endtDay, list);
        //2.获取此人在此时间段内出案总数
        Integer sumNumberOfCaseByClassifiersCodeAndOutTime = detailsOfTheCaseExtRepository.getSumNumberOfCaseByClassifiersCodeAndOutTime(startDay, endtDay, classifiersCode);
        int  denominator = sumOfTheDateAndReceiveIdList + sumNumberOfCaseByClassifiersCodeAndOutTime;
        if(denominator !=0){
            linkedHashMap.put("处理转案日期",week);
            linkedHashMap.put("接收分类员代码",classifiersCode);
            linkedHashMap.put("接收分类员",ename);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率", Double.valueOf((sumOfTheDateAndReceiveIdList *100 ) / denominator) + "%" );
        }else{
            linkedHashMap.put("处理转案日期",week);
            linkedHashMap.put("接收分类员代码",classifiersCode);
            linkedHashMap.put("接收分类员",ename);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率",  "0%" );
        }
        return linkedHashMap;
    }
}
