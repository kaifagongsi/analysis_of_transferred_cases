package com.kfgs.aotc.ceshi.receiverateoftransout.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;

public interface RTOService {
    Result receiveRateOfTransOut(ParameterVo parameterVo);

    Result getReceiveRateOfTransOutAll(ParameterVo parameterVo);
}
