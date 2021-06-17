package com.kfgs.aotc.ceshi.receiverateoftransout.service;

import com.kfgs.aotc.common.pojo.PageCondition;
import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
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
public class RTOServiceImpl implements RTOService{

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;
    @Autowired
    private TransferProcessRepository transferProcessRepository;

    static final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();

    @Override
    public Result receiveRateOfTransOut(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1:
                return countReceiveRateByPeople(parameterVo); //按个人计算
            case 2:
                return countReceiveRateByDepartment(parameterVo);//按部门计算
            case 3:
                return countReceiveRateBySection(parameterVo);
            case 4:
                return countReceiveRateByField(parameterVo);
        }
        return null;
    }

    //计算所有
    @Override
    public Result getReceiveRateOfTransOutAll(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 2:
                return getReceiveRateOfTransOutByDepAll(parameterVo);
            case 3:
                return getReceiveRateOfTransOutBySectionAll(parameterVo);
            case 4:
                return getReceiveRateOfTransOutByFieldAll(parameterVo);
        }
        return null;
    }

    /**
     * 个人转出接收率
     */
    private Result countReceiveRateByPeople(ParameterVo parameterVo){
        List resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify() ,page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(countReceiveRateByWorkerID(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 按部门计算转出接收率
     */
    private Result countReceiveRateByDepartment(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo  info : classifierCode.getContent()){
            resultList.add(countReceiveRateByWorkerID(parameterVo.getStartDate(),parameterVo.getEndDate(),info.getClassifiersCode(),info.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 计算部门整体转出接收率
     */
    private Result getReceiveRateOfTransOutByDepAll(ParameterVo parameterVo){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByDep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),(Pageable)parameterVo.getPageable());
        List<ClassifierInfo> content = classifiersCodeByDep1WithPageable.getContent();
        List classifierInfoCode = new ArrayList();
        content.forEach((info)->{
            classifierInfoCode.add(info.getClassifiersCode());
        });
        Double accuracy_num = 0d;
        //1.分子  其他分类员接收转案次数
        int receiveTotals = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"接收转案",classifierInfoCode);
        //2.分母  转出总次数
        int transoutTotals = transferProcessRepository.getSumOfTheDateAndSendIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
        accuracy_num = Double.valueOf(receiveTotals*100/transoutTotals);
        result.put("当前所选",parameterVo.getSecondClassify());
        result.put("转出总次数",Integer.toString(transoutTotals));
        result.put("转出接收总次数", Integer.toString(receiveTotals));
        result.put("转出总次数",Integer.toString(transoutTotals));
        result.put("转出接收率",df.format(accuracy_num)+"%");
        System.out.println(result);
        List list= new ArrayList<>();
        list.add(result);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByDep1WithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 按处室计算转出接收率
     */
    private Result countReceiveRateBySection(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        if(parameterVo.getSecondClassify().length() == 4){
            String dep1 = parameterVo.getSecondClassify().substring(0,2);
            String dep2 = parameterVo.getSecondClassify().substring(2,4);
            Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1,dep2,page.getPageable());
            for(ClassifierInfo info : classifierCode){
                resultList.add(countReceiveRateByWorkerID(parameterVo.getStartDate(),parameterVo.getEndDate(),info.getClassifiersCode(),info.getEname()));
            }
            PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }else {
            return null;
        }
    }

    /**
     * 计算处室整体转出接收率
     */
    private Result getReceiveRateOfTransOutBySectionAll(ParameterVo parameterVo){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //0.查询该处室所有人员
        PageCondition page = (PageCondition)parameterVo;
        parameterVo.setRows(1000);
        if(parameterVo.getSecondClassify().length() == 4){
            String dep1 = parameterVo.getSecondClassify().substring(0,2);
            String dep2 = parameterVo.getSecondClassify().substring(2,4);
            Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1,dep2,page.getPageable());
            List classifierInfoCode = new ArrayList();
            classifierCode.forEach((info)->{
                classifierInfoCode.add(info.getClassifiersCode());
            });
            Double accuracy_num = 0d;
            //1.分子  其他分类员接收转案次数
            int receiveTotals = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"接收转案",classifierInfoCode);
            //2.分母  转出总次数
            int transoutTotals = transferProcessRepository.getSumOfTheDateAndSendIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
            accuracy_num = Double.valueOf(receiveTotals*100/transoutTotals);
            result.put("当前所选",parameterVo.getSecondClassify());
            result.put("转出总次数",Integer.toString(transoutTotals));
            result.put("转出接收总次数", Integer.toString(receiveTotals));
            result.put("转出总次数",Integer.toString(transoutTotals));
            result.put("转出接收率",df.format(accuracy_num)+"%");
            System.out.println(result);
            List list= new ArrayList<>();
            list.add(result);
            PageInfo pageInfo = PageInfo.ofMap(classifierCode,list);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }else {
            return null;
        }
    }
    /**
     * 按领域计算转出接受率
     */
    private Result countReceiveRateByField(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo info : classifierCode){
            resultList.add(countReceiveRateByWorkerID(parameterVo.getStartDate(),parameterVo.getEndDate(),info.getClassifiersCode(),info.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        return Result.of(pageInfo);
    }

    /**
     * 计算领域整体转出接收率
     */
    private Result getReceiveRateOfTransOutByFieldAll(ParameterVo parameterVo){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        PageCondition page = (PageCondition)parameterVo;
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(), page.getPageable());
        List classifierInfoCode = new ArrayList();
        for(ClassifierInfo info : classifiersCodeByFieldWithPageable){
            classifierInfoCode.add(info.getClassifiersCode());
        }
        Double accuracy_num = 0d;
        //1.分子  其他分类员接收转案次数
        int receiveTotals = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"接收转案",classifierInfoCode);
        //2.分母  转出总次数
        int transoutTotals = transferProcessRepository.getSumOfTheDateAndSendIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
        accuracy_num = Double.valueOf(receiveTotals*100/transoutTotals);
        result.put("当前所选",parameterVo.getSecondClassify());
        result.put("转出总次数",Integer.toString(transoutTotals));
        result.put("转出接收总次数", Integer.toString(receiveTotals));
        result.put("转出总次数",Integer.toString(transoutTotals));
        result.put("转出接收率",df.format(accuracy_num)+"%");
        System.out.println(result);
        List list= new ArrayList<>();
        list.add(result);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByFieldWithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }
    /**
     * 计算某个人的转出接受率(其他分类员接收转案次数/转出总次数)
     * @param startDate
     * @param endDate
     * @param classifierID
     * @param ename
     * @return
     */
    private LinkedHashMap<String,String> countReceiveRateByWorkerID(String startDate,String endDate,String classifierID,String ename){
        double accuracy_num = 0; //转出接受率
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //1.分子  其他分类员接收转案次数
        int receiveTotals = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndSendId(startDate,endDate,classifierID);
        //2.分母  转出总次数
        int transoutall = transferProcessRepository.getCountNumberBySendTimeBetweenAndSendId(startDate,endDate,classifierID);
        accuracy_num = receiveTotals*100/transoutall;
        result.put("分类员代码",classifierID);
        result.put("分类员姓名",ename);
        result.put("转案接收总次数", Integer.toString(receiveTotals));
        result.put("转出总次数",Integer.toString(transoutall));
        result.put("转出接收率",df.format(accuracy_num)+"%");
        System.out.println(result);
        return result;
    }
}
