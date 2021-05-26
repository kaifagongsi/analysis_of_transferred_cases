package com.kfgs.aotc.ceshi.effectivetransferinrate.service;

import com.kfgs.aotc.common.pojo.PageCondition;
import com.kfgs.aotc.common.pojo.PageInfo;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

@Service
@Transactional
public class ETIServiceImpl implements ETIService{

    @Autowired
    TransferProcessRepository transferProcessRepository;

    @Autowired
    ClassifierInfoRepository classifierInfoRepository;

    @Autowired
    DetailOfCaseFinishedRepository detailOfCaseFinishedRepository;

    @Autowired
    DetailsOfTheCaseExtRepository  detailsOfTheCaseExtRepository;

    static final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();


    @Override
    public Result getEffectiveTransferInRateAll(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 2:
                return getEffectiveTransferInRateDepAll(parameterVo);
            case 3:
                return getEffectiveTransferInRateBySectionAll(parameterVo);
            case 4:
                return getEffectiveTransferInRateByFieldAll(parameterVo);
        }
        return null;
    }




    @Override
    public Result getEffectiveTransferInRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1:
                return getEffectiveTransferInRateByPeople(parameterVo);
            case 2:
                return getEffectiveTransferInRateByDep1(parameterVo);
            case 3:
                return getEffectiveTransferInRateBySection(parameterVo);
            case 4:
                return getEffectiveTransferInRateByField(parameterVo);
        }
        return null;
    }

    /**
     * 根据领域查询领域总的的转入率
     * @param parameterVo
     * @return
     */
    private Result getEffectiveTransferInRateByFieldAll(ParameterVo parameterVo) {
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        PageCondition page = (PageCondition)parameterVo;
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByFieldWithPageable = classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(), page.getPageable());
        List classifierInfoCode = new ArrayList();
        for(ClassifierInfo info : classifiersCodeByFieldWithPageable){
            classifierInfoCode.add(info.getClassifiersCode());
        }
        //1.查询所有人员的转入总和
        int sum = transferProcessRepository.getSumOfTheDateAndReceiveIdList(parameterVo.getStartDate(), parameterVo.getEndDate(), classifierInfoCode);
        //2.查询所有接收转案的总和
        int acceptReferral = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"接收转案",classifierInfoCode);
        //3.查找该部门下所有拒绝转案并且出案案件
        List<Object[]> refuseReferral = detailsOfTheCaseExtRepository.getRefuseReferralByReceiveTimeBetweenAndTipeStateAndReceiveIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"拒绝转案",classifierInfoCode);
        int validTrans = getRefuseReferralNumber(refuseReferral);
        Double accuracy_num = 0d;
        int fenzi =  (acceptReferral + validTrans) *100;
        accuracy_num = Double.valueOf(fenzi/sum);
        result.put("当前所选",parameterVo.getSecondClassify());
        result.put("转入总次数",Integer.toString(sum));
        result.put("转入接收总次数", Integer.toString(acceptReferral));
        result.put("转入退回且出案数",Integer.toString(refuseReferral.size()));
        result.put("转入退回有效次数",Integer.toString(validTrans));
        result.put("有效转入率",df.format(accuracy_num)+"%");
        System.out.println(result);
        List list= new ArrayList<>();
        list.add(result);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByFieldWithPageable,list);
        return  Result.of(pageInfo);
    }



    /**
     * 根据领域查询每个人的转入率
     * @param parameterVo
     * @return
     */
    private Result getEffectiveTransferInRateByField(ParameterVo parameterVo) {
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByFieldWithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo info : classifierCode){
            resultList.add(getPeople(parameterVo.getStartDate(),parameterVo.getEndDate(),info.getClassifiersCode(),info.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        return Result.of(pageInfo);
    }

    /**
     * 获取 处室转入率
     * @param parameterVo
     * @return
     */
    private Result getEffectiveTransferInRateBySectionAll(ParameterVo parameterVo) {
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        PageCondition page = (PageCondition)parameterVo;
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        if(parameterVo.getSecondClassify().length() == 4){
            String dep1 = parameterVo.getSecondClassify().substring(0,2);
            String dep2 = parameterVo.getSecondClassify().substring(2,4);
            Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1,dep2,page.getPageable());
            List classifierInfoCode = new ArrayList();
            classifierCode.forEach((info)->{
                classifierInfoCode.add(info.getClassifiersCode());
            });
            //1.查询所有人员的转入总和
            int sum = transferProcessRepository.getSumOfTheDateAndReceiveIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
            //2.查询该list人员列表中所有的接受转案数量
            int acceptReferral = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"接收转案",classifierInfoCode);
            //3.查找该部门下所有拒绝转案并且出案案件
            List<Object[]> refuseReferral = detailsOfTheCaseExtRepository.getRefuseReferralByReceiveTimeBetweenAndTipeStateAndReceiveIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"拒绝转案",classifierInfoCode);
            List<DetailsOfTheCaseExt> detailOfCaseFinisheds = EntityUtils.castEntity(refuseReferral,DetailsOfTheCaseExt.class);
            //4.查找该部门下所有拒绝转案并且有效案件
            int validTrans = getRefuseReferralNumber(refuseReferral);
            Double accuracy_num = 0d;
            int fenzi =  (acceptReferral + validTrans) *100;
            accuracy_num = Double.valueOf(fenzi/sum);
            result.put("当前所选",parameterVo.getSecondClassify());
            result.put("转入总次数",Integer.toString(sum));
            result.put("转入接收总次数", Integer.toString(acceptReferral));
            result.put("转入退回且出案数",Integer.toString(refuseReferral.size()));
            result.put("转入退回有效次数",Integer.toString(validTrans));
            result.put("有效转入率",df.format(accuracy_num)+"%");
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
     * 查询处室下每个人的转入率
     * @param parameterVo
     * @return
     */
    private Result getEffectiveTransferInRateBySection(ParameterVo parameterVo) {
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        if(parameterVo.getSecondClassify().length() == 4){
            String dep1 = parameterVo.getSecondClassify().substring(0,2);
            String dep2 = parameterVo.getSecondClassify().substring(2,4);
            Page<ClassifierInfo> classifierCode =  classifierInfoRepository.findClassifiersCodeByDep2WithPageable(dep1,dep2,page.getPageable());
            for(ClassifierInfo info : classifierCode){
                resultList.add(getPeople(parameterVo.getStartDate(),parameterVo.getEndDate(),info.getClassifiersCode(),info.getEname()));
            }
            PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
            Result<PageInfo> of = Result.of(pageInfo);
            return of;
        }else {
            return null;
        }
    }

    /**
     *  获取部门转入率
     * @param parameterVo
     * @return
     */
    private Result getEffectiveTransferInRateDepAll(ParameterVo parameterVo){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();
        //0.查询该部门所有人员
        parameterVo.setRows(1000);
        Page<ClassifierInfo> classifiersCodeByDep1WithPageable = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),(Pageable)parameterVo.getPageable());
        List<ClassifierInfo> content = classifiersCodeByDep1WithPageable.getContent();
        List classifierInfoCode = new ArrayList();
        content.forEach((info)->{
            classifierInfoCode.add(info.getClassifiersCode());
        });
        //1.查询该部门全部转入
        int sum = transferProcessRepository.getSumOfTheDateAndReceiveIdList(parameterVo.getStartDate(),parameterVo.getEndDate(),classifierInfoCode);
        //2.查找该部门下所有接受转案
        int acceptReferral = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"接收转案",classifierInfoCode);
        //3.查找该部门下所有拒绝转案并且出案案件
        List<Object[]> refuseReferral = detailsOfTheCaseExtRepository.getRefuseReferralByReceiveTimeBetweenAndTipeStateAndReceiveIds(parameterVo.getStartDate(),parameterVo.getEndDate(),"拒绝转案",classifierInfoCode);
        List<DetailsOfTheCaseExt> detailOfCaseFinisheds = EntityUtils.castEntity(refuseReferral,DetailsOfTheCaseExt.class);
        //4.查找该部门下所有拒绝转案并且有效案件
        int validTrans = getRefuseReferralNumber(refuseReferral);
        Double accuracy_num = 0d;
        int fenzi =  (acceptReferral + validTrans) *100;
        accuracy_num = Double.valueOf(fenzi/sum);
        result.put("当前所选",parameterVo.getSecondClassify());
        result.put("转入总次数",Integer.toString(sum));
        result.put("转入接收总次数", Integer.toString(acceptReferral));
        result.put("转入退回且出案数",Integer.toString(refuseReferral.size()));
        result.put("转入退回有效次数",Integer.toString(validTrans));
        result.put("有效转入率",df.format(accuracy_num)+"%");
        System.out.println(result);
        List list= new ArrayList<>();
        list.add(result);
        PageInfo pageInfo = PageInfo.ofMap(classifiersCodeByDep1WithPageable,list);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 查询部门下每个人的转入率
     * @param parameterVo
     * @return
     */
    private Result getEffectiveTransferInRateByDep1( ParameterVo parameterVo) {
        List<Map<String,String>> resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByDep1WithPageable(parameterVo.getSecondClassify(),page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(getPeople(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 个人转入率
     * @param parameterVo
     * @return
     */
    private Result getEffectiveTransferInRateByPeople( ParameterVo parameterVo){
        List resultList = new ArrayList<>();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByClassifierCode(parameterVo.getSecondClassify() ,page.getPageable());
        for(ClassifierInfo  id : classifierCode.getContent()){
            resultList.add(getPeople(parameterVo.getStartDate(),parameterVo.getEndDate(),id.getClassifiersCode(),id.getEname()));
        }
        PageInfo pageInfo = PageInfo.ofMap(classifierCode,resultList);
        Result<PageInfo> of = Result.of(pageInfo);
        return of;
    }

    /**
     * 返回单个人的比例
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param classifierID 人员ID
     * @return
     */
    private LinkedHashMap getPeople(String startDate,String endDate,String classifierID,String ename ){
        LinkedHashMap<String,String> result = new LinkedHashMap<>();

        double accuracy_num = 0;//有效转入率
        //0.转入总数
        int fengmu = detailOfCaseFinishedRepository.getSumTransferIn(startDate, endDate,classifierID);
        //int fengmu = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndReceiveId(startDate, endDate,classifierID);
        if(fengmu ==0){
            result.put("分类员代码",classifierID);
            result.put("分类员姓名",ename);
            result.put("转入总次数","0");
            result.put("转入接收总次数", "0");
            result.put("转入退回且出案数","0");
            result.put("转入退回有效次数","0");
            result.put("有效转入率","0%");
        }else{
            //1 按照处理转案时间、提示状态为接受转入的集合
            int case_1 = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId(startDate, endDate, "接收转案", classifierID);

            //2.拒绝转入并且在已出案中的所有ipc
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
            accuracy_num = (case_1 + validTrans)*100/fengmu;
            result.put("分类员代码",classifierID);
            result.put("分类员姓名",ename);
            result.put("转入总次数",Integer.toString(fengmu));
            result.put("转入接收总次数", Integer.toString(case_1));
            result.put("转入退回且出案数",Integer.toString( ipc.size()));
            result.put("转入退回有效次数",Integer.toString(validTrans));
            result.put("有效转入率",df.format(accuracy_num)+"%");
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
        //4.查找该部门下所有拒绝转案并且有效案件
        int validTrans = 0; //有效转案
        for(DetailsOfTheCaseExt detailsOfTheCaseExt : detailOfCaseFinisheds){
            List<String> detailCodes = CountUtil.CLASSIFIERS_AND_CODE.get(detailsOfTheCaseExt.getReceiveId());
            String[] finishCodes = detailsOfTheCaseExt.getIpc().split(",");
            for(int j=0;j<finishCodes.length;j++) {
                if (finishCodes[j] != null && finishCodes[j].length()>=4){
                    finishCodes[j] = finishCodes[j].substring(0, 4);
                }
            }
            if(!Collections.disjoint(Arrays.asList(finishCodes),detailCodes)){
                validTrans++;
            }
        }
        return validTrans;
    }
}
