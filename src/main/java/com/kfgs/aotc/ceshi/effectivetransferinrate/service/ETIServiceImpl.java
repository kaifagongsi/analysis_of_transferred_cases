package com.kfgs.aotc.ceshi.effectivetransferinrate.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.TransferProcessRepository;
import com.kfgs.aotc.util.CountUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public Result getEffectiveTransferInRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1:
                return getEffectiveTransferInRateByPeople(parameterVo.getStartDate(),parameterVo.getEndDate(),parameterVo.getSecondClassify());
                 //return getEffectiveTransferInRateByPeople(parameterVo);
            case 2:
                System.out.println("2");
                break;
            default:

        }



        return null;
    }

    private Result getEffectiveTransferInRateByPeople(String startDate,String endDate,String classifierID){
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        double accuracy_num = 0;//有效转案率
        //0.转入总数
        int fengmu = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndReceiveId(startDate, endDate,classifierID);
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

        Map<String,String> result = new HashMap<>();
        result.put("分类员代码",classifierID);
        result.put("转案总次数",Integer.toString(fengmu));
        result.put("转案接收总次数", Integer.toString(case_1));
        result.put("转案退回且出案数",Integer.toString( ipc.size()));
        result.put("转案退回有效次数",Integer.toString(validTrans));
        result.put("有效转案率",df.format(accuracy_num)+"%");
        System.out.println(result);
        return Result.of(result);

        /*Page<TransferProcess> andTipeTitle = transferProcessRepository.getAllConditionsByReceiveTimeAndTipeTitle(parameterVo.getStartDate(), parameterVo.getEndDate(),"接收转案",page.getPageable());

        //Page<TransferProcess> allConditions = transferProcessRepository.findAllConditionsByReceiveTimeAndTipeTitle(parameterVo.getStartDate(), parameterVo.getEndDate(),"接收转案",page.getPageable());
        //2 按照处理转案时间、提示状态为拒绝转案的分页集合
        //transferProcessRepository.findAllBy
        System.out.println(andTipeTitle);
        return Result.of(andTipeTitle);*/
    }
}
