package com.kfgs.aotc.ceshi.asscoderate.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;

public interface ACRService {

    Result getAssCodeRate(ParameterVo parameterVo);

    Result getAssCodeRateAll(ParameterVo parameterVo);
}
