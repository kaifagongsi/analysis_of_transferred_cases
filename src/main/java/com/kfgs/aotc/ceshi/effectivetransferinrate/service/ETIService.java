package com.kfgs.aotc.ceshi.effectivetransferinrate.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;

public interface ETIService {
    public Result getEffectiveTransferInRate(ParameterVo parameterVo);


    Result getEffectiveTransferInRateAll(ParameterVo parameterVo);
}
