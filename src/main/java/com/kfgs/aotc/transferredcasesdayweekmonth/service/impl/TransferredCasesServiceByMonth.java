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

import java.util.*;

@Component
public class TransferredCasesServiceByMonth implements ITransferredCasesService {


    @Autowired
    ClassifierInfoRepository classifierInfoRepository;
    @Autowired
    TransferProcessRepository transferProcessRepository;

    @Autowired
    DetailsOfTheCaseExtRepository detailsOfTheCaseExtRepository;

    @Override
    public boolean choseCurrent(String type) {
        if("month".equalsIgnoreCase(type)){
            return true;
        }
        return false;
    }

    @Override
    public Result calculation(ParameterVo parameterVo) {
        System.out.println("按照月数开始计算");
        LinkedList monthlist = DateUtil.getMonthBetween(parameterVo.getStartDate(),parameterVo.getEndDate()); //时间段
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = monthlist.size(); //总记录数
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
        switch (parameterVo.getFirstClassify()){
            case 1:
                return transferredCasesServiceMonthByPerson(pageNum,rows,records,total,startIndex,endIndex,parameterVo,monthlist);
            case 2:
                return  transferredCasesServiceMonthByDep(pageNum,rows,records,total,startIndex,endIndex,parameterVo,monthlist);
            case 3:
                return  transferredCasesServiceMonthBySection(pageNum,rows,records,total,startIndex,endIndex,parameterVo,monthlist);
            case 4:
                return  transferredCasesServiceMonthByField(pageNum,rows,records,total,startIndex,endIndex,parameterVo,monthlist);
        }
        return null;
    }

    private Result transferredCasesServiceMonthByField(int pageNum, int rows, int records, int total, int startIndex, int endIndex, ParameterVo parameterVo, LinkedList<String> monthlist) {
        List resultList = new ArrayList<>();
        //1.获取部门下的所有人员
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(), null);
        List classifiers = new ArrayList();
        for(ClassifierInfo classifierInfo : classifiersCodeByFieldWithPageable){
            classifiers.add(classifierInfo.getClassifiersCode());
        }
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getTransferredCasesWeekByClassifiers(monthlist.get(i),classifiers,parameterVo.getSecondClassify()));
        }
        PageInfo pageInfo = PageInfo.createPageInfo(records,rows,total,resultList);
        return Result.of(pageInfo);
    }

    private Result transferredCasesServiceMonthBySection(int pageNum, int rows, int records, int total, int startIndex, int endIndex, ParameterVo parameterVo, LinkedList<String> monthlist) {
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
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getTransferredCasesWeekByClassifiers(monthlist.get(i),classifiers,parameterVo.getSecondClassify()));
        }
        PageInfo pageInfo = PageInfo.createPageInfo(records,rows,total,resultList);
        return Result.of(pageInfo);
    }

    private Result transferredCasesServiceMonthByDep(int pageNum, int rows, int records, int total, int startIndex, int endIndex, ParameterVo parameterVo, LinkedList<String> monthlist) {
        List resultList = new ArrayList<>();
        //1.查询人员
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(), null);
        List classifiers = new ArrayList();
        for(ClassifierInfo classifierInfo : classifiersCodeByFieldWithPageable){
            classifiers.add(classifierInfo.getClassifiersCode());
        }
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getTransferredCasesWeekByClassifiers(monthlist.get(i),classifiers,parameterVo.getSecondClassify()));
        }
        PageInfo pageInfo = PageInfo.createPageInfo(records,rows,total,resultList);
        return Result.of(pageInfo);
    }

    private LinkedHashMap getTransferredCasesWeekByClassifiers(String month, List classifiers, String secondClassify) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        //1. 获取转入总次数
        String startDay = month.split("~")[0];
        String endtDay = month.split("~")[1];
        int sumOfTheDateAndReceiveIdList = transferProcessRepository.getSumOfTheDateAndReceiveIdList(startDay,endtDay,classifiers);
        //2. 获取出案总数
        int sumAllNumberOfCaseByClassifiersCodeListAndOutTime = detailsOfTheCaseExtRepository.getSumNumberOfCaseByClassifiersCodeAndOutTime(startDay,endtDay,classifiers);
        int fenmu = sumOfTheDateAndReceiveIdList + sumAllNumberOfCaseByClassifiersCodeListAndOutTime;
        if(fenmu != 0  ){
            linkedHashMap.put("处理转案日期",month);
            linkedHashMap.put("接收类型",secondClassify);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率", Double.valueOf((sumOfTheDateAndReceiveIdList *100 ) / fenmu) + "%" );
        }else{
            linkedHashMap.put("处理转案日期",month);
            linkedHashMap.put("接收类型",secondClassify);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率",   "0%" );
        }
        return linkedHashMap;
    }

    private Result transferredCasesServiceMonthByPerson(int pageNum, int rows, int records, int total, int startIndex, int endIndex, ParameterVo parameterVo, LinkedList<String> monthlist) {
        List resultList = new ArrayList<>();
        //1.查询人员
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify(), null);
        for(int i = startIndex; i < endIndex; i++){
            resultList.add(transferredCasesServiceMonthByOnePerson(monthlist.get(i),classifiersCodeByFieldWithPageable.getContent().get(0).getClassifiersCode(),classifiersCodeByFieldWithPageable.getContent().get(0).getEname()));
        }
        PageInfo pageInfo = PageInfo.createPageInfo(records,rows,total,resultList);
        return Result.of(pageInfo);
    }

    private LinkedHashMap transferredCasesServiceMonthByOnePerson(String monthList, String classifiersCode, String ename) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        //1.查询当前人在某个时间段内 转入总次数
        String startDay = monthList.split("~")[0];
        String endtDay = monthList.split("~")[1];
        List<String> list = Arrays.asList(classifiersCode);
        int sumOfTheDateAndReceiveIdList = transferProcessRepository.getSumOfTheDateAndReceiveIdList(startDay, endtDay, list);
        //2.获取此人在此时间段内出案总数
        Integer sumNumberOfCaseByClassifiersCodeAndOutTime = detailsOfTheCaseExtRepository.getSumNumberOfCaseByClassifiersCodeAndOutTime(startDay, endtDay, classifiersCode);
        int  denominator = sumOfTheDateAndReceiveIdList + sumNumberOfCaseByClassifiersCodeAndOutTime;
        if(denominator !=0){
            linkedHashMap.put("处理转案日期",monthList);
            linkedHashMap.put("接收分类员代码",classifiersCode);
            linkedHashMap.put("接收分类员",ename);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率", Double.valueOf((sumOfTheDateAndReceiveIdList *100 ) / denominator) + "%" );
        }else{
            linkedHashMap.put("处理转案日期",monthList);
            linkedHashMap.put("接收分类员代码",classifiersCode);
            linkedHashMap.put("接收分类员",ename);
            linkedHashMap.put("转入总次数",sumOfTheDateAndReceiveIdList);
            linkedHashMap.put("处理转案率",  "0%" );
        }
        return linkedHashMap;
    }


}
