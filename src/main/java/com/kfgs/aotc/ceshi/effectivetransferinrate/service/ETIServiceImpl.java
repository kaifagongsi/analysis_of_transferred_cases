package com.kfgs.aotc.ceshi.effectivetransferinrate.service;

import com.kfgs.aotc.common.pojo.PageCondition;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.TransferProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ETIServiceImpl implements ETIService{

    @Autowired
    TransferProcessRepository transferProcessRepository;

    @Override
    public Result getEffectiveTransferInRate(ParameterVo parameterVo) {
        switch (parameterVo.getFirstClassify()){
            case 1:
                 return getEffectiveTransferInRateByPeople(parameterVo);
            case 2:
                System.out.println("2");
                break;
            default:

        }



        return null;
    }

    private Result getEffectiveTransferInRateByPeople(ParameterVo parameterVo){
        System.out.println(parameterVo);
        PageCondition page  = (PageCondition)parameterVo;
        //0.转入总数
        int fengmu = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndReceiveId(parameterVo.getStartDate(), parameterVo.getEndDate(),parameterVo.getSecondClassify());
        //1 按照处理转案时间、提示状态为接受转案的集合
        int case_1 = transferProcessRepository.getAcceptReferralCountNumberByReceiveTimeBetweenAndTipeTitleAndReceiveId(parameterVo.getStartDate(), parameterVo.getEndDate(), "接收转案", parameterVo.getSecondClassify());

        //2.拒绝转案并且在已出案中的所有ipc
        List<String> ipc =  transferProcessRepository.getRefuseReferralByReceiveTimeBetweenAndTipeTitleAndReceiveId(parameterVo.getStartDate(), parameterVo.getEndDate(), "拒绝转案", parameterVo.getSecondClassify());


        /*Page<TransferProcess> andTipeTitle = transferProcessRepository.getAllConditionsByReceiveTimeAndTipeTitle(parameterVo.getStartDate(), parameterVo.getEndDate(),"接收转案",page.getPageable());

        //Page<TransferProcess> allConditions = transferProcessRepository.findAllConditionsByReceiveTimeAndTipeTitle(parameterVo.getStartDate(), parameterVo.getEndDate(),"接收转案",page.getPageable());
        //2 按照处理转案时间、提示状态为拒绝转案的分页集合
        //transferProcessRepository.findAllBy
        System.out.println(andTipeTitle);
        return Result.of(andTipeTitle);*/
        return null;
    }
}
