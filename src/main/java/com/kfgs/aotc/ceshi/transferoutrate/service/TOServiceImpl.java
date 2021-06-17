package com.kfgs.aotc.ceshi.transferoutrate.service;

import com.kfgs.aotc.common.pojo.PageCondition;
import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailOfCaseFinishedRepository;
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
public class TOServiceImpl implements TOService {

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;
    @Autowired
    private TransferProcessRepository transferProcessRepository;
    @Autowired
    private DetailOfCaseFinishedRepository detailOfCaseFinishedRepository;

    static final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();

    @Override
    public Result getTransferOutRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1: //按人计算
                return countTransOutRateByPeople(parameterVo);
            case 2: //按部级计算
                return countTransOutRateByDep1(parameterVo);
            case 3: //按处室计算
                return countTransOutRateBySection(parameterVo);
            case 4: //按领域计算
                return countTransOutRateByFiled(parameterVo);
        }
        return null;
    }

    /**
     * 计算整体转出案件率
     * @param parameterVo
     * @return
     */
    @Override
    public Result getTransferOutRateAll(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 2:
                return getTransferOutRateByDepAll(parameterVo);
            case 3:
                return getTransferOutRateBySectionAll(parameterVo);
            case 4:
                return getTransferOutRateByFiledAll(parameterVo);
        }
        return null;
    }


    /**
     * 个人转出案件率
     * 转出总次数/(出案案件数+转出总次数)
     */
    private Result countTransOutRateByPeople(ParameterVo parameterVo){
        List resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify() ,page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(getTransferOutRateByClassifiers(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 部门转出案件率
     */
    private Result countTransOutRateByDep1(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(getTransferOutRateByClassifiers(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 部门整体转出案件率
     */
    private Result getTransferOutRateByDepAll(ParameterVo parameterVo){
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
        //1.分子：转出总次数
        int transferTotal = transferProcessRepository.getSumOfTheDateAndSendIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
        //2.分母: 出案案件数
        int transoutNum = detailOfCaseFinishedRepository.getCountTransOutByClassifiersCodes(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
        accuracy_num = Double.valueOf(transferTotal*100/transoutNum);
        result.put("当前所选",parameterVo.getSecondClassify());
        result.put("转出总次数",Integer.toString(transferTotal));
        result.put("出案案件数", Integer.toString(transoutNum));
        result.put("转出案件率",df.format(accuracy_num)+"%");
        System.out.println(result);
        List list= new ArrayList<>();
        list.add(result);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByDep1WithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 处室转出案件率
     */
    private Result countTransOutRateBySection(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        if(parameterVo.getSecondClassify().length() == 4) {
            String dep1 = parameterVo.getSecondClassify().substring(0, 2);
            String dep2 = parameterVo.getSecondClassify().substring(2, 4);
            Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1, dep2, page.getPageable());
            for (ClassifierInfo info : classifierCode) {
                resultList.add(getTransferOutRateByClassifiers(parameterVo.getStartDate(), parameterVo.getEndDate(), info.getClassifiersCode(), info.getEname()));
            }
            PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }else {
            return null;
        }
    }
    /**
     * 处室整体转出案件率
     */
    private Result getTransferOutRateBySectionAll(ParameterVo parameterVo){
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
            //1.分子：转出总次数
            int transferTotal = transferProcessRepository.getSumOfTheDateAndSendIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
            //2.分母: 出案案件数
            int transoutNum = detailOfCaseFinishedRepository.getCountTransOutByClassifiersCodes(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
            accuracy_num = Double.valueOf(transferTotal*100/transoutNum);
            result.put("当前所选",parameterVo.getSecondClassify());
            result.put("转出总次数",Integer.toString(transferTotal));
            result.put("出案案件数", Integer.toString(transoutNum));
            result.put("转出案件率",df.format(accuracy_num)+"%");
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
     * 领域转出案件率
     */
    private Result countTransOutRateByFiled(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo info : classifierCode){
            resultList.add(getTransferOutRateByClassifiers(parameterVo.getStartDate(),parameterVo.getEndDate(),info.getClassifiersCode(),info.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        return Result.of(pageInfo);
    }
    /**
     * 领域整体转出案件率
     */
    private Result getTransferOutRateByFiledAll(ParameterVo parameterVo){
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
        //1.分子：转出总次数
        int transferTotal = transferProcessRepository.getSumOfTheDateAndSendIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
        //2.分母: 出案案件数
        int transoutNum = detailOfCaseFinishedRepository.getCountTransOutByClassifiersCodes(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
        accuracy_num = Double.valueOf(transferTotal*100/transoutNum);
        result.put("当前所选",parameterVo.getSecondClassify());
        result.put("转出总次数",Integer.toString(transferTotal));
        result.put("出案案件数", Integer.toString(transoutNum));
        result.put("转出案件率",df.format(accuracy_num)+"%");
        System.out.println(result);
        List list= new ArrayList<>();
        list.add(result);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByFieldWithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 按照发送分类员的ID计算转出率
     * @param startDate
     * @param endDate
     * @param classifierID
     * @param ename
     * @return
     */
    private LinkedHashMap<String, String> getTransferOutRateByClassifiers(String startDate,String endDate,String classifierID,String ename){
        double accuracy_num = 0;//有效转案率
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //分子：转出总次数
        int transferOutNum = transferProcessRepository.getCountNumberBySendTimeBetweenAndSendId(startDate,endDate,classifierID);
        //出案案件数
        int caseOutNum = detailOfCaseFinishedRepository.getSumTransferOut(startDate,endDate,classifierID);
        //分母
        int fenmu = transferOutNum+caseOutNum;
        if(fenmu == 0){
            return null;
        }
        accuracy_num = transferOutNum * 100/fenmu;
        result.put("分类员代码",classifierID);
        result.put("分类员姓名",ename);
        result.put("出案案件数",Integer.toString(caseOutNum));
        result.put("转出总次数",Integer.toString(transferOutNum));
        result.put("转出案件率",df.format(accuracy_num)+"%");
        System.out.println(result);
        return result;
    }
}
