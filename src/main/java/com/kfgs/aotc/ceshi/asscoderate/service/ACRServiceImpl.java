package com.kfgs.aotc.ceshi.asscoderate.service;

import com.kfgs.aotc.common.pojo.PageCondition;
import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import com.kfgs.aotc.util.CountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@Service
@Transactional
public class ACRServiceImpl implements ACRService{

    @Autowired
    private TransferProcessRepository transferProcessRepository;
    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;
    static final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();

    @Override
    public Result getAssCodeRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1://按人计算
                return countAssCodeRateByPeople(parameterVo);
            case 2://按部门计算
                return countAssCodeRateByDep1(parameterVo);
            case 3://按处室计算
                return countAssCodeRateBySection(parameterVo);
            case 4://按领域计算
                return countAssCodeRateByFiled(parameterVo);
        }
        return null;
    }

    @Override
    public Result getAssCodeRateAll(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 2:
                return countAssCodeRateByDepAll(parameterVo);
            case 3:
                return countAssCodeRateBySectionAll(parameterVo);
            case 4:
                return countAssCodeRateByFieldAll(parameterVo);
        }
        return null;
    }

    /**
     * 计算个人
     * @param parameterVo
     * @return
     */
    private Result countAssCodeRateByPeople(ParameterVo parameterVo){
        List resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify() ,page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(getAssCodeRateByClassifiers(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 计算部门
     * @param parameterVo
     * @return
     */
    private Result countAssCodeRateByDep1(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(getAssCodeRateByClassifiers(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    private Result countAssCodeRateByDepAll(ParameterVo parameterVo){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByDep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),(Pageable)parameterVo.getPageable());
        List<ClassifierInfo> content = classifiersCodeByDep1WithPageable.getContent();
        List classifierInfoCode = new ArrayList();
        List list= new ArrayList<>();
        content.forEach((info)->{
            classifierInfoCode.add(info.getClassifiersCode());
        });
        Double accuracy_num = 0d;
        //1.分子：带分类号的拒绝转案次数(有效退回转案次数)
        int validTrans = 0;
        //2.分母: 转入总次数
        int transInNum = transferProcessRepository.getSumOfTheDateAndReceiveIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
        if (transInNum == 0){
            result.put("当前所选",parameterVo.getSecondClassify());
            result.put("转案退回有效次数","0");
            result.put("转入总次数", "0");
            result.put("加副分率","0%");
            list.add(result);
            PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByDep1WithPageable,list);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }else {
            accuracy_num = Double.valueOf(validTrans * 100 / transInNum);
            result.put("当前所选", parameterVo.getSecondClassify());
            result.put("转案退回有效次数", Integer.toString(validTrans));
            result.put("转入总次数", Integer.toString(transInNum));
            result.put("加副分率", df.format(accuracy_num) + "%");
            System.out.println(result);
            list.add(result);
            PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByDep1WithPageable, list);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }
    }

    /**
     * 计算处室
     * @param parameterVo
     * @return
     */
    private Result countAssCodeRateBySection(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        if(parameterVo.getSecondClassify().length() == 4) {
            String dep1 = parameterVo.getSecondClassify().substring(0, 2);
            String dep2 = parameterVo.getSecondClassify().substring(2, 4);
            Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1, dep2, page.getPageable());
            for (ClassifierInfo info : classifierCode) {
                resultList.add(getAssCodeRateByClassifiers(parameterVo.getStartDate(), parameterVo.getEndDate(), info.getClassifiersCode(), info.getEname()));
            }
            PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }else {
            return null;
        }
    }

    private Result countAssCodeRateBySectionAll(ParameterVo parameterVo){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //0.查询该处室所有人员
        PageCondition page = (PageCondition)parameterVo;
        parameterVo.setRows(1000);
        if(parameterVo.getSecondClassify().length() == 4){
            String dep1 = parameterVo.getSecondClassify().substring(0,2);
            String dep2 = parameterVo.getSecondClassify().substring(2,4);
            Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1,dep2,page.getPageable());
            List classifierInfoCode = new ArrayList();
            List list= new ArrayList<>();
            classifierCode.forEach((info)->{
                classifierInfoCode.add(info.getClassifiersCode());
            });
            Double accuracy_num = 0d;
            //1.分子：带分类号的拒绝转案次数(有效退回转案次数)
            int validTrans = 0;
            //2.分母: 转入总次数
            int transInNum = transferProcessRepository.getSumOfTheDateAndReceiveIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
            if (transInNum == 0){
                result.put("当前所选",parameterVo.getSecondClassify());
                result.put("转案退回有效次数","0");
                result.put("转入总次数", "0");
                result.put("加副分率","0%");
                list.add(result);
                PageInfo pageInfo = PageInfo.ofMap(classifierCode,list);
                Result<PageInfo> of = Result.of(pageInfo);
                return of;
            }else {
                accuracy_num = Double.valueOf(validTrans*100/transInNum);
                result.put("当前所选",parameterVo.getSecondClassify());
                result.put("转案退回有效次数",Integer.toString(validTrans));
                result.put("转入总次数", Integer.toString(transInNum));
                result.put("加副分率",df.format(accuracy_num)+"%");
                System.out.println(result);
                list.add(result);
                PageInfo pageInfo = PageInfo.ofMap(classifierCode,list);
                Result<PageInfo> of = Result.of(pageInfo);
                return of;
            }
        }else {
            return null;
        }
    }

    /**
     * 计算领域
     * @param parameterVo
     * @return
     */
    private Result countAssCodeRateByFiled(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo info : classifierCode){
            resultList.add(getAssCodeRateByClassifiers(parameterVo.getStartDate(),parameterVo.getEndDate(),info.getClassifiersCode(),info.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        return Result.of(pageInfo);
    }

    private Result countAssCodeRateByFieldAll(ParameterVo parameterVo){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        PageCondition page = (PageCondition)parameterVo;
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(), page.getPageable());
        List classifierInfoCode = new ArrayList();
        List list= new ArrayList<>();
        for(ClassifierInfo info : classifiersCodeByFieldWithPageable){
            classifierInfoCode.add(info.getClassifiersCode());
        }
        Double accuracy_num = 0d;
        //1.分子：带分类号的拒绝转案次数(有效退回转案次数)
        int validTrans = 0;
        //2.分母: 转入总次数
        int transInNum = transferProcessRepository.getSumOfTheDateAndReceiveIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
        if (transInNum == 0){
            result.put("当前所选",parameterVo.getSecondClassify());
            result.put("转案退回有效次数","0");
            result.put("转入总次数", "0");
            result.put("加副分率","0%");
            list.add(result);
            PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByFieldWithPageable,list);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }else {
            accuracy_num = Double.valueOf(validTrans*100/transInNum);
            result.put("当前所选",parameterVo.getSecondClassify());
            result.put("转案退回有效次数",Integer.toString(validTrans));
            result.put("转入总次数", Integer.toString(transInNum));
            result.put("加副分率",df.format(accuracy_num)+"%");
            System.out.println(result);
            list.add(result);
            PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByFieldWithPageable,list);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }
    }
    /**
     * 根据个人id计算加副分率
     */
    private LinkedHashMap<String, String> getAssCodeRateByClassifiers(String startDate,String endDate,String classifierID,String ename){
        double accuracy_num = 0;//有效转案率
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //1.分子 本人带分类号的拒绝转案次数(有效退回转案次数)
        int validTrans = 0; //有效转入
        List<String> ipc =  transferProcessRepository.getRefuseReferralByReceiveTimeBetweenAndTipeTitleAndReceiveId(startDate, endDate, "拒绝转案", classifierID);

        //获取接收分类员分类号CLASSIFIERS_AND_CODE
        List<String> detailCodes = CountUtil.CLASSIFIERS_AND_CODE.get(classifierID);
        // 如何同一个案子前四位均是一样的，那么就会添加两次，那么在，主分和副分均是H01R,那么finishCodes会有两个H01R
        for(String ipc_str : ipc){
            // 当前案子的所有前四位分类号
            String[] finishCodes = ipc_str.split(",");
            for(int j=0;j<finishCodes.length;j++) {
                if (finishCodes[j] != null && finishCodes[j].length()>=4){
                    finishCodes[j] = finishCodes[j].substring(0, 4);
                }
            }
            //比对
            if (!Collections.disjoint(Arrays.asList(finishCodes),detailCodes)){
                validTrans ++;
            }
        }
        //2.分母 转入总次数
        int transInNum = transferProcessRepository.getCountNumberByReceiveTimeBetweenAndReceiveId(startDate,endDate,classifierID);
        if (transInNum == 0){
            result.put("分类员代码",classifierID);
            result.put("分类员姓名",ename);
            result.put("转案退回有效次数","0");
            result.put("转入总次数","0");
            result.put("加副分率","0%");
            return result;
        }
        accuracy_num = validTrans * 100 / transInNum;
        result.put("分类员代码",classifierID);
        result.put("分类员姓名",ename);
        result.put("转案退回有效次数",Integer.toString(validTrans));
        result.put("转入总次数",Integer.toString(transInNum));
        result.put("加副分率",df.format(accuracy_num)+"%");
        System.out.println(result);
        return result;
    }
}
