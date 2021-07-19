package com.kfgs.aotc.ceshi.effectivetransferoutrate.service;

import com.kfgs.aotc.common.pojo.PageCondition;
import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.DetailOfCaseFinished;
import com.kfgs.aotc.pojo.business.ext.DetailsOfTheCaseExt;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailOfCaseFinishedRepository;
import com.kfgs.aotc.repository.DetailsOfTheCaseExtRepository;
import com.kfgs.aotc.repository.TransferProcessRepository;
import com.kfgs.aotc.util.CountUtil;
import com.kfgs.aotc.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;


@Service
@Transactional
public class ETOServiceImpl implements ETOService {

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;
    @Autowired
    private TransferProcessRepository transferProcessRepository;

    @Autowired
    DetailsOfTheCaseExtRepository detailsOfTheCaseExtRepository;

    static final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();


    @Override
    public Result getEffectiveTransferRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1: //按人计算个人
                return countAccuracyByPeople(parameterVo);
            case 2: //按部计算个人
                return countAccuracyByDepartment(parameterVo);
            case 3: //按室计算个人
                return countAccuracyByBranch(parameterVo);
            case 4: //按领域计算个人
                return countAccuracyByField(parameterVo);
            //default:
        }
        return null;
    }

    @Override
    public Result getEffectiveTransferOutRateAll(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 2:
                return getEffectiveTransferOutRateDepAll(parameterVo);
            case 3:
                return getEffectiveTransferOutRateBySectionAll(parameterVo);
            case 4:
                return getEffectiveTransferOutRateByFieldAll(parameterVo);
        }
        return null;
    }

    @Override
    public Result getAllFieldGroup() {
        List<String> filedslist = new ArrayList<>();
        filedslist = classifierInfoRepository.findDistinctByFieldGroup();
        return Result.of(filedslist);
    }

    /**
     * 获取个人有效转出率
     */
    private Result countAccuracyByPeople(ParameterVo parameterVo){
        List resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify() ,page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(countAccuracyByWorkerID(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }
    /**
     * 获取部门有效转出率
     * @return
     */
    private Result getEffectiveTransferOutRateDepAll(ParameterVo parameterVo){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        List list= new ArrayList<>();
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByDep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),(Pageable)parameterVo.getPageable());
        List<ClassifierInfo> content = classifiersCodeByDep1WithPageable.getContent();
        List classifierInfoCode = new ArrayList();
        content.forEach((info)->{
            classifierInfoCode.add(info.getClassifiersCode());
        });
        //1.分子1,查询该部门全部转出接收转案案件数量
        int receiveTotals = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"接收转案",classifierInfoCode);
        //2.分子2,拒绝转案且在已出案案件列表中
        List<Object[]> refuseReferral = detailsOfTheCaseExtRepository.getRefuseReferralBySendTimeBetweenAndTipeStateAndSendIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"拒绝转案",classifierInfoCode);
        //3.分母
        int validTrans = getRefuseReferralNumber(refuseReferral);
        Double accuracy_num = 0d;
        int fenmu = receiveTotals + refuseReferral.size();
        int fenzi = receiveTotals + validTrans;
        if (fenmu == 0){
            result.put("当前所选",parameterVo.getSecondClassify());
            result.put("转出总次数","0");
            result.put("转出接收总次数", "0");
            result.put("转出退回且出案数","0");
            result.put("转入退回有效次数","0");
            result.put("有效转出率","0%");
            list.add(result);
            PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByDep1WithPageable,list);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }
        accuracy_num = Double.valueOf(fenzi*100/fenmu);
        result.put("当前所选",parameterVo.getSecondClassify());
        result.put("转出总次数",Integer.toString(fenmu));
        result.put("转出接收总次数", Integer.toString(receiveTotals));
        result.put("转出退回且出案数",Integer.toString(refuseReferral.size()));
        result.put("转入退回有效次数",Integer.toString(validTrans));
        result.put("有效转出率",df.format(accuracy_num)+"%");
        System.out.println(result);
        list.add(result);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByDep1WithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 获取处室有效转出率
     */
    private Result getEffectiveTransferOutRateBySectionAll(ParameterVo parameterVo){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //0.查询该处室所有人员
        PageCondition page = (PageCondition)parameterVo;
        parameterVo.setRows(1000);
        if(parameterVo.getSecondClassify().length() == 4){
            List list= new ArrayList<>();
            String dep1 = parameterVo.getSecondClassify().substring(0,2);
            String dep2 = parameterVo.getSecondClassify().substring(2,4);
            Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1,dep2,page.getPageable());
            List classifierInfoCode = new ArrayList();
            classifierCode.forEach((info)->{
                classifierInfoCode.add(info.getClassifiersCode());
            });
            //1.分子1,查询该处室全部转出接收转案案件数量
            int receiveTotals = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"接收转案",classifierInfoCode);
            //2.分子2,拒绝转案且在已出案案件列表中
            List<Object[]> refuseReferral = detailsOfTheCaseExtRepository.getRefuseReferralBySendTimeBetweenAndTipeStateAndSendIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"拒绝转案",classifierInfoCode);
            //3.分母
            int validTrans = getRefuseReferralNumber(refuseReferral);
            Double accuracy_num = 0d;
            int fenmu = receiveTotals + refuseReferral.size();
            int fenzi = receiveTotals + validTrans;
            if (fenmu == 0){
                result.put("当前所选",parameterVo.getSecondClassify());
                result.put("转出总次数","0");
                result.put("转出接收总次数", "0");
                result.put("转出退回且出案数","0");
                result.put("转入退回有效次数","0");
                result.put("有效转出率","0%");
                list.add(result);
                PageInfo pageInfo = PageInfo.ofMap(classifierCode,list);
                Result<PageInfo> of = Result.of(pageInfo);
                return of;
            }
            accuracy_num = Double.valueOf(fenzi*100/fenmu);
            result.put("当前所选",parameterVo.getSecondClassify());
            result.put("转出总次数",Integer.toString(fenmu));
            result.put("转出接收总次数", Integer.toString(receiveTotals));
            result.put("转出退回且出案数",Integer.toString(refuseReferral.size()));
            result.put("转入退回有效次数",Integer.toString(validTrans));
            result.put("有效转出率",df.format(accuracy_num)+"%");
            System.out.println(result);
            list.add(result);
            PageInfo pageInfo = PageInfo.ofMap(classifierCode,list);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }else {
            return null;
        }
    }

    /**
     * 获取领域整体转出有效率
     */
    private Result getEffectiveTransferOutRateByFieldAll(ParameterVo parameterVo){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        List list= new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(), page.getPageable());
        List classifierInfoCode = new ArrayList();
        for(ClassifierInfo info : classifiersCodeByFieldWithPageable){
            classifierInfoCode.add(info.getClassifiersCode());
        }
        //1.分子1,查询该处室全部转出接收转案案件数量
        int receiveTotals = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndTipeTitleAndSendIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"接收转案",classifierInfoCode);
        //2.分子2,拒绝转案且在已出案案件列表中
        List<Object[]> refuseReferral = detailsOfTheCaseExtRepository.getRefuseReferralBySendTimeBetweenAndTipeStateAndSendIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"拒绝转案",classifierInfoCode);
        //3.分母
        int validTrans = getRefuseReferralNumber(refuseReferral);
        Double accuracy_num = 0d;
        int fenmu = receiveTotals + refuseReferral.size();
        int fenzi = receiveTotals + validTrans;
        if (fenmu == 0){
            result.put("当前所选",parameterVo.getSecondClassify());
            result.put("转出总次数","0");
            result.put("转出接收总次数", "0");
            result.put("转出退回且出案数","0");
            result.put("转入退回有效次数","0");
            result.put("有效转出率","0%");
            list.add(result);
            PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByFieldWithPageable,list);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }
        accuracy_num = Double.valueOf(fenzi*100/fenmu);
        result.put("当前所选",parameterVo.getSecondClassify());
        result.put("转出总次数",Integer.toString(fenmu));
        result.put("转出接收总次数", Integer.toString(receiveTotals));
        result.put("转出退回且出案数",Integer.toString(refuseReferral.size()));
        result.put("转入退回有效次数",Integer.toString(validTrans));
        result.put("有效转出率",df.format(accuracy_num)+"%");
        System.out.println(result);
        list.add(result);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByFieldWithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /*@Override
    public Result<List<Map<String,String>>> countAccuracy(List<String> list, ParameterVo parameterVo) {
        List<Map<String,String>> resultList = new ArrayList<>();
        String startDate = parameterVo.getStartDate();
        String endDate = parameterVo.getEndDate();

        for (int i=0;i<list.size();i++){
            Map<String,String> map = new HashMap<>();
            String classifierCode = list.get(i);
            map = countAccuracyByWorkerID(classifierCode,startDate,endDate);
            resultList.add(map);
        }
        return Result.of(resultList);
    }*/

    /**
     * 按照分类员领域计算
     * @param parameterVo
     * @return
     */
    private Result countAccuracyByField(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(),page.getPageable());
        LinkedHashMap<String,String> workerResult = new LinkedHashMap<>();
        for (ClassifierInfo classifierInfo:classifierCode.getContent()){
            workerResult = countAccuracyByWorkerID(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfo.getClassifiersCode(),classifierInfo.getEname());
            resultList.add(workerResult);
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        return Result.of(pageInfo);
    }

    /**
     * 按科室计算
     * @param parameterVo
     * @return
     */
    private Result countAccuracyByBranch(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        if(parameterVo.getSecondClassify().length() == 4){
            String dep1 = parameterVo.getSecondClassify().substring(0,2);
            String dep2 = parameterVo.getSecondClassify().substring(2,4);
            Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1,dep2,page.getPageable());
            for(ClassifierInfo info : classifierCode){
                resultList.add(countAccuracyByWorkerID(parameterVo.getStartDate(),parameterVo.getEndDate(),info.getClassifiersCode(),info.getEname()));
            }
            PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }else {
            return null;
        }
    }

    /**
     * 按部计算
     * @param parameterVo
     * @return
     */
    private Result countAccuracyByDepartment(ParameterVo parameterVo){
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(countAccuracyByWorkerID(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    private LinkedHashMap<String, String> countAccuracyByWorkerID(String startDate, String endDate, String classifierID,String ename) { //发送分类员
        double accuracy_num = 0;//有效转案率
        //List<Map<String,String>> resultList = new ArrayList<>();
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        //分子1:获取接收转案案件数量
        int receiveTotals = transferProcessRepository.getAcceptReferralCountNumberBySendTimeBetweenAndSendId(startDate,endDate,classifierID);
        List<Object[]> transferProcessList = detailsOfTheCaseExtRepository.getRefuseReferralBySendTimeBetweenAndTipeStateAndSendIds(startDate,endDate,"拒绝转案",Arrays.asList(classifierID));
        int rejectTotals = transferProcessList.size();
        int totalTrans = receiveTotals + rejectTotals; //分母
        int validTrans = 0; //有效转案
        if (totalTrans == 0){ //分母不能为0
            result.put("分类员代码",classifierID);
            result.put("分类员姓名",ename);
            result.put("转案总次数","0");
            result.put("转案接收总次数", "0");
            result.put("转案退回且出案数","0");
            result.put("转案退回有效次数","0");
            result.put("有效转出率","0%");
            return result;
        }else{
            //2.有效转案案件
            validTrans = getRefuseReferralNumber(transferProcessList);
            accuracy_num = (receiveTotals + validTrans)*100/totalTrans;
            result.put("分类员代码",classifierID);
            result.put("分类员姓名",ename);
            result.put("转案总次数",Integer.toString(totalTrans));
            result.put("转案接收总次数", Integer.toString(receiveTotals));
            result.put("转案退回且出案数",Integer.toString(rejectTotals));
            result.put("转案退回有效次数",Integer.toString(validTrans));
            result.put("有效转出率",df.format(accuracy_num)+"%");
            System.out.println(result);
        }
        return result;
    }

    /**
     * 查询某些用户在特定时间下的拒绝转案数量
     * @param refuseReferral 用户 转案列表
     * @return
     */
    public int getRefuseReferralNumber(List<Object[]> refuseReferral){
        List<DetailsOfTheCaseExt> detailOfCaseFinisheds = EntityUtils.castEntity(refuseReferral,DetailsOfTheCaseExt.class);
        //4.查找所有拒绝转案并且有效出案案件
        int validTrans = 0; //有效转案
        for(DetailsOfTheCaseExt detailsOfTheCaseExt : detailOfCaseFinisheds){
            List<String> detailCodes = CountUtil.CLASSIFIERS_AND_CODE.get(detailsOfTheCaseExt.getReceiveId());
            String[] finishCodes = detailsOfTheCaseExt.getIpc().split(",");
            if (finishCodes != null && detailCodes!=null){
                for(int j=0;j<finishCodes.length;j++) {
                    if (finishCodes[j] != null && finishCodes[j].length()>=4){
                        finishCodes[j] = finishCodes[j].substring(0, 4);
                    }
                }
                if(!Collections.disjoint(Arrays.asList(finishCodes),detailCodes)){
                    validTrans++;
                }
            }
        }
        return validTrans;
    }
}
