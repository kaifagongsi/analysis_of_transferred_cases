package com.kfgs.aotc.ceshi.effectivetransferinrate.service;

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

    @Override
    public Result getEffectiveTransferInRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1:
                return getEffectiveTransferInRateByPeople(parameterVo);
                 //return getEffectiveTransferInRateByPeople(parameterVo);
            case 2:
                return getEffectiveTransferInRateByDep1(parameterVo);
            default:

        }
        return null;
    }

    private Result getEffectiveTransferInRateByDep1( ParameterVo parameterVo) {
        List<Map<String,String>> resultList = new ArrayList<>();

        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        PageCondition page = (PageCondition)parameterVo;
        Page<ClassifierInfo> classifierCode = classifierInfoRepository.findClassifiersCodeByDep1(parameterVo.getSecondClassify(),page.getPageable());
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
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        double accuracy_num = 0;//有效转案率
        //0.转入总数
        int fengmu = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndReceiveId(startDate, endDate,classifierID);
        if(fengmu ==0){
            result.put("分类员代码",classifierID);
            result.put("分类员姓名",ename);
            result.put("转案总次数","0");
            result.put("转案接收总次数", "0");
            result.put("转案退回且出案数","0");
            result.put("转案退回有效次数","0");
            result.put("有效转案率","0%");
        }else{
            //1 按照处理转案时间、提示状态为接受转案的集合
            int case_1 = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId(startDate, endDate, "接收转案", classifierID);

            //2.拒绝转案并且在已出案中的所有ipc
            int validTrans = 0; //有效转案
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
            result.put("转案总次数",Integer.toString(fengmu));
            result.put("转案接收总次数", Integer.toString(case_1));
            result.put("转案退回且出案数",Integer.toString( ipc.size()));
            result.put("转案退回有效次数",Integer.toString(validTrans));
            result.put("有效转案率",df.format(accuracy_num)+"%");
            System.out.println(result);
        }
        return result;
    }
}
