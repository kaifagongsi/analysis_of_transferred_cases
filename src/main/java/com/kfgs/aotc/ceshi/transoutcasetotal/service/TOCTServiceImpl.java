package com.kfgs.aotc.ceshi.transoutcasetotal.service;

import com.kfgs.aotc.common.pojo.PageCondition;
import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailOfCaseFinishedRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import com.kfgs.aotc.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oshi.hardware.platform.linux.LinuxDisks;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

@Service
@Transactional
public class TOCTServiceImpl implements TOCTService {

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;
    @Autowired
    private TransferProcessRepository transferProcessRepository;
    @Autowired
    private DetailOfCaseFinishedRepository detailOfCaseFinishedRepository;

    /**
     * 日转出案件
     * @param parameterVo
     * @return
     */
    @Override
    public Result getTotalByDay(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1: //按人计算
                return countDayForEachOne(parameterVo);
            case 2: //按部门计算
                return countDayForDep1(parameterVo);
            case 3: //按科室计算
                return countDayForDep2(parameterVo);
            case 4: //按领域计算
                return countDayForFiled(parameterVo);
        }
        return null;
    }
    /**
     * 周转出案件
     * @param parameterVo
     * @return
     */
    @Override
    public Result getTotalByWeek(ParameterVo parameterVo){
        switch (parameterVo.getFirstClassify()){
            case 1: //按人计算
                return countWeekForEachOne(parameterVo);
            case 2: //按部门计算
                return countWeekForDep1(parameterVo);
            case 3: //按科室计算
                return countWeekForDep2(parameterVo);
            case 4: //按领域计算
                return countWeekForFiled(parameterVo);
        }
        return null;
    }

    /**
     * 月转出案件
     * @param parameterVo
     * @return
     */
    public Result getTotalByMonth(ParameterVo parameterVo){
        switch (parameterVo.getFirstClassify()){
            case 1: //按人计算
                return countMonthForEachOne(parameterVo);
            case 2: //按部门计算
                return countMonthForDep1(parameterVo);
            case 3: //按科室计算
                return countMonthForDep2(parameterVo);
            case 4: //按领域计算
                return countMonthForFiled(parameterVo);
        }
        return null;
    }

    /**
     * 月，个人
     * @param parameterVo
     * @return
     */
    private Result countMonthForEachOne(ParameterVo parameterVo){
        List resultList = new ArrayList();
        PageCondition page = (PageCondition)parameterVo;
        LinkedList monthlist = DateUtil.getMonthBetween(parameterVo.getStartDate(),parameterVo.getEndDate());
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
        ClassifierInfo classifierInfo = classifierInfoRepository.findClassifierInfoByClassifiersCode(parameterVo.getSecondClassify());
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getOutCaseOfMonthByClassifiers(monthlist.get(i).toString(),classifierInfo.getClassifiersCode(),classifierInfo.getEname()));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 月，部门
     * @param parameterVo
     * @return
     */
    private Result countMonthForDep1(ParameterVo parameterVo){
        List resultList = new ArrayList();
        PageCondition page = (PageCondition)parameterVo;
        String dep1 = parameterVo.getSecondClassify();
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
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getOutCaseOfMonthByDep1(monthlist.get(i).toString(),dep1));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 月，科室
     * @param parameterVo
     * @return
     */
    private Result countMonthForDep2(ParameterVo parameterVo){
        List resultList = new ArrayList();
        PageCondition page = (PageCondition)parameterVo;
        String dep2 = parameterVo.getSecondClassify();
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
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getOutCaseOfMonthByDep2(monthlist.get(i).toString(),dep2));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 月，领域
     * @param parameterVo
     * @return
     */
    private Result countMonthForFiled(ParameterVo parameterVo){
        List resultList = new ArrayList();
        PageCondition page = (PageCondition)parameterVo;
        String filed = parameterVo.getSecondClassify();
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
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getOutCaseOfMonthByFiled(monthlist.get(i).toString(),filed));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    //周,个人
    private Result countWeekForEachOne(ParameterVo parameterVo){
        List resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        LinkedList weeklist = DateUtil.getWeekByLinkedList(DateUtil.getWeekLinkedList(parameterVo.getStartDate(),parameterVo.getEndDate()));
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = weeklist.size(); //总记录数
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
        ClassifierInfo classifierInfo = classifierInfoRepository.findClassifierInfoByClassifiersCode(parameterVo.getSecondClassify());
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getOutCaseOfWeekByClassifiers(weeklist.get(i).toString(),classifierInfo.getClassifiersCode(),classifierInfo.getEname()));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 周，部门
     * @param parameterVo
     * @return
     */
    private Result countWeekForDep1(ParameterVo parameterVo){
        List resultList = new ArrayList();
        PageCondition page = (PageCondition)parameterVo;
        String dep1 = parameterVo.getSecondClassify();
        LinkedList weeklist = DateUtil.getWeekByLinkedList(DateUtil.getWeekLinkedList(parameterVo.getStartDate(),parameterVo.getEndDate())); //时间段
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = weeklist.size(); //总记录数
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
            resultList.add(getOutCaseOfWeekByDep1(weeklist.get(i).toString(),dep1));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 周，科室
     * @param parameterVo
     * @return
     */
    private Result countWeekForDep2(ParameterVo parameterVo){
        List resultList = new ArrayList();
        PageCondition page = (PageCondition)parameterVo;
        String dep2 = parameterVo.getSecondClassify();
        LinkedList weeklist = DateUtil.getWeekByLinkedList(DateUtil.getWeekLinkedList(parameterVo.getStartDate(),parameterVo.getEndDate())); //时间段
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = weeklist.size(); //总记录数
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
            resultList.add(getOutCaseOfWeekByDep2(weeklist.get(i).toString(),dep2));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    private Result countWeekForFiled(ParameterVo parameterVo){
        List resultList = new ArrayList();
        PageCondition page = (PageCondition)parameterVo;
        String filed = parameterVo.getSecondClassify();
        LinkedList weeklist = DateUtil.getWeekByLinkedList(DateUtil.getWeekLinkedList(parameterVo.getStartDate(),parameterVo.getEndDate())); //时间段
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = weeklist.size(); //总记录数
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
            resultList.add(getOutCaseOfWeekByFiled(weeklist.get(i).toString(),filed));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 日出案，按个人计算
     * @param parameterVo
     * @return
     */
    private Result countDayForEachOne(ParameterVo parameterVo){
        PageCondition page = (PageCondition)parameterVo;
        String id = parameterVo.getSecondClassify();
        ClassifierInfo classifierInfo = classifierInfoRepository.findClassifierInfoByClassifiersCode(id);
        String ename = classifierInfo.getEname();
        String start = parameterVo.getStartDate(); //开始时间
        String end = parameterVo.getEndDate(); //结束时间
        List<String> days = DateUtil.getBetweenDays(start,end); //时间段
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = days.size(); //总记录数
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
        List resultList = new ArrayList<>();
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getOutCaseOfDayByClassifiers(days.get(i),id,ename));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 日出案，按部门计算
     * @param parameterVo
     * @return
     */
    private Result countDayForDep1(ParameterVo parameterVo){
        PageCondition page = (PageCondition)parameterVo;
        String dep1 = parameterVo.getSecondClassify();
        String start = parameterVo.getStartDate(); //开始时间
        String end = parameterVo.getEndDate(); //结束时间
        List<String> days = DateUtil.getBetweenDays(start,end); //时间段
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = days.size(); //总记录数
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
        List resultList = new ArrayList<>();
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getOutCaseOfDayByDep1(days.get(i),dep1));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 日出案，按科室计算
     * @param parameterVo
     * @return
     */
    private Result countDayForDep2(ParameterVo parameterVo){
        PageCondition page = (PageCondition)parameterVo;
        String dep2 = parameterVo.getSecondClassify();
        String start = parameterVo.getStartDate(); //开始时间
        String end = parameterVo.getEndDate(); //结束时间
        List<String> days = DateUtil.getBetweenDays(start,end); //时间段
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = days.size(); //总记录数
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
        List resultList = new ArrayList<>();
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getOutCaseOfDayByDep2(days.get(i),dep2));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 日出案，按领域计算
     * @param parameterVo
     * @return
     */
    private Result countDayForFiled(ParameterVo parameterVo){
        PageCondition page = (PageCondition)parameterVo;
        String filed = parameterVo.getSecondClassify();
        String start = parameterVo.getStartDate(); //开始时间
        String end = parameterVo.getEndDate(); //结束时间
        List<String> days = DateUtil.getBetweenDays(start,end); //时间段
        int pageNum = parameterVo.getPage(); //页码
        int rows = parameterVo.getRows(); //每页行数
        int records = days.size(); //总记录数
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
        List resultList = new ArrayList<>();
        for(int i=startIndex;i<endIndex;i++){
            resultList.add(getOutCaseOfDayByFiled(days.get(i),filed));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }
    /**
     * 按人员id计算某天的出案数
     * @param date
     * @param classifierID
     * @param ename
     * @return
     */
    private LinkedHashMap<String, String> getOutCaseOfDayByClassifiers(String date, String classifierID, String ename){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //转出总次数
        int transferOutNum = transferProcessRepository.getDayCountNumberBySendTimeAndSendId(date,classifierID);
        result.put("日期",date);
        result.put("分类员代码",classifierID);
        result.put("分类员姓名",ename);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    /**
     * 按人员id计算某周的出案数
     * @param week
     * @param classifierID
     * @param ename
     * @return
     */
    private LinkedHashMap<String,String> getOutCaseOfWeekByClassifiers(String week,String classifierID,String ename){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        String Monday = week.substring(0,8);
        String Sunday = week.substring(9);
        //个人转出总次数
        int transferOutNum = transferProcessRepository.getWeekCountNumberBySendTimeAndSendId(Monday,Sunday,classifierID);
        result.put("日期",week);
        result.put("分类员代码",classifierID);
        result.put("分类员姓名",ename);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    /**
     * 按个人计算每月转出案件数
     * @param month
     * @param classifierID
     * @param ename
     * @return
     */
    private LinkedHashMap<String,String> getOutCaseOfMonthByClassifiers(String month,String classifierID,String ename){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        String startDay = month.substring(0,8);
        String lastDay = month.substring(9);
        //个人转出总次数
        int transferOutNum = transferProcessRepository.getMonthCountNumberBySendTimeAndSendId(startDay,lastDay,classifierID);
        result.put("日期",month);
        result.put("分类员代码",classifierID);
        result.put("分类员姓名",ename);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    /**
     * 按部门计算每月转出案件数
     * @param month
     * @param dep1
     * @return
     */
    private LinkedHashMap<String ,String > getOutCaseOfMonthByDep1(String month,String dep1){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        String startDay = month.substring(0,8);
        String lastDay = month.substring(9);
        //部门转出总次数
        int transferOutNum = transferProcessRepository.getMonthCountNumberBySendTimeAndDep1(startDay,lastDay,dep1);
        result.put("日期",month);
        result.put("部门",dep1);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    /**
     * 按科室计算每月转出案件数
     * @param month
     * @param dep2
     * @return
     */
    private LinkedHashMap<String ,String> getOutCaseOfMonthByDep2(String month,String dep2){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        String startDay = month.substring(0,8);
        String lastDay = month.substring(9);
        //科室转出总次数
        int transferOutNum = transferProcessRepository.getMonthCountNumberBySendTimeAndDep2(startDay,lastDay,dep2);
        result.put("日期",month);
        result.put("科室",dep2);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    private LinkedHashMap<String ,String> getOutCaseOfMonthByFiled(String month,String filed){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        String startDay = month.substring(0,8);
        String lastDay = month.substring(9);
        //领域转出总次数
        int transferOutNum = transferProcessRepository.getMonthCountNumberBySendTimeAndFiled(startDay,lastDay,filed);
        result.put("日期",month);
        result.put("领域",filed);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }
    /**
     * 按部门计算某周的出案数
     * @param week
     * @param dep1
     * @return
     */
    private LinkedHashMap<String,String> getOutCaseOfWeekByDep1(String week,String dep1){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        String Monday = week.substring(0,8);
        String Sunday = week.substring(9);
        //部门转出总次数
        int transferOutNum = transferProcessRepository.getWeekCountNumberBySendTimeAndDep1(Monday,Sunday,dep1);
        result.put("日期",week);
        result.put("部门",dep1);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    /**
     * 按部门计算每天出案
     * @param date
     * @param dep1
     * @return
     */
    private LinkedHashMap<String,String> getOutCaseOfDayByDep1(String date,String dep1){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //部门转出总次数
        int transferOutNum = transferProcessRepository.getCountNumberBySendTimeAndDep1(date,dep1);
        result.put("日期",date);
        result.put("部门",dep1);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    /**
     * 按科室计算每天出案
     * @param date
     * @param dep2
     * @return
     */
    private LinkedHashMap<String,String> getOutCaseOfDayByDep2(String date,String dep2){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //科室转出总次数
        int transferOutNum = transferProcessRepository.getCountNumberBySendTimeAndDep2(date,dep2);
        result.put("日期",date);
        result.put("科室",dep2);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    private LinkedHashMap<String,String> getOutCaseOfWeekByDep2(String week,String dep2){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        String Monday = week.substring(0,8);
        String Sunday = week.substring(9);
        //科室转出总次数
        int transferOutNum = transferProcessRepository.getWeekCountNumberBySendTimeAndDep2(Monday,Sunday,dep2);
        result.put("日期",week);
        result.put("科室",dep2);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    /**
     * 按领域计算每天出案
     * @param date
     * @param filed
     * @return
     */
    private LinkedHashMap<String,String> getOutCaseOfDayByFiled(String date,String filed){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //科室转出总次数
        int transferOutNum = transferProcessRepository.getCountNumberBySendTimeAndFiled(date,filed);
        result.put("日期",date);
        result.put("领域",filed);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    private LinkedHashMap<String,String> getOutCaseOfWeekByFiled(String week,String filed){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        String Monday = week.substring(0,8);
        String Sunday = week.substring(9);
        //领域转出总次数
        int transferOutNum = transferProcessRepository.getWeekCountNumberBySendTimeAndFiled(Monday,Sunday,filed);
        result.put("日期",week);
        result.put("领域",filed);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }
}
