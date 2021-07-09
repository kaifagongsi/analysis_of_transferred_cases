package com.kfgs.aotc.ceshi.transoutcasetotal.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.config.security.PasswordConfig;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;

public interface TOCTService {
    Result getTotalByDay(ParameterVo parameterVo);
    Result getTotalByWeek(ParameterVo parameterVo);
    Result getTotalByMonth(ParameterVo parameterVo);
}
