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

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
                return countForEachOne(parameterVo);
            case 2: //按部门计算
                return countForDep1(parameterVo);
            case 3: //按科室计算
                return countForDep2(parameterVo);
            case 4: //按领域计算
                return countForFiled(parameterVo);
        }
        return null;
    }
    /**
     * 周转出案件
     * @param parameterVo
     * @return
     */
    @Override
    public Result getTotalByWeek(ParameterVo parameterVo) {
        return null;
    }

    /**
     * 月转出案件
     * @param parameterVo
     * @return
     */
    @Override
    public Result getTotalByMonth(ParameterVo parameterVo) {
        return null;
    }

    /**
     * 按个人计算
     * @param parameterVo
     * @return
     */
    private Result countForEachOne(ParameterVo parameterVo){
        PageCondition page = (PageCondition)parameterVo;
        List<String> days = DateUtil.getBetweenDays(parameterVo.getStartDate(),parameterVo.getEndDate());
        //List<String> days = DateUtil.getBetweenDays(parameterVo.getStartDate(),parameterVo.getStartDate()+parameterVo.getRows());
        List resultList = new ArrayList<>();
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify() ,page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            for (int i=0;i<days.size();i++){
                resultList.add(getOutCaseByClassifiers(days.get(i),id.getClassifiersCode(),id.getEname()));
            }
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 按部门计算
     * @param parameterVo
     * @return
     */
    private Result countForDep1(ParameterVo parameterVo){
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
            resultList.add(getOutCaseByDep1(days.get(i),dep1));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 按科室计算
     * @param parameterVo
     * @return
     */
    private Result countForDep2(ParameterVo parameterVo){
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
            resultList.add(getOutCaseByDep2(days.get(i),dep2));
        }
        PageInfo pageInfo = PageInfo.list2Page(resultList,pageNum,rows,records,total);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    private Result countForFiled(ParameterVo parameterVo){
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
            resultList.add(getOutCaseByFiled(days.get(i),filed));
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
    private LinkedHashMap<String, String> getOutCaseByClassifiers(String date, String classifierID, String ename){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //转出总次数
        int transferOutNum = transferProcessRepository.getCountNumberBySendTimeAndSendId(date,classifierID);
        result.put("日期",date);
        result.put("分类员代码",classifierID);
        result.put("分类员姓名",ename);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    private LinkedHashMap<String,String> getOutCaseByDep1(String date,String dep1){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //部门转出总次数
        int transferOutNum = transferProcessRepository.getCountNumberBySendTimeAndDep1(date,dep1);
        result.put("日期",date);
        result.put("部门",dep1);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    private LinkedHashMap<String,String> getOutCaseByDep2(String date,String dep2){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //科室转出总次数
        int transferOutNum = transferProcessRepository.getCountNumberBySendTimeAndDep2(date,dep2);
        result.put("日期",date);
        result.put("科室",dep2);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }

    private LinkedHashMap<String,String> getOutCaseByFiled(String date,String filed){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //科室转出总次数
        int transferOutNum = transferProcessRepository.getCountNumberBySendTimeAndFiled(date,filed);
        result.put("日期",date);
        result.put("领域",filed);
        result.put("转案总次数",Integer.toString(transferOutNum));
        System.out.println(result);
        return result;
    }
}
