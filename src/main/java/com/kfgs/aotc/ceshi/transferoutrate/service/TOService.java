package com.kfgs.aotc.ceshi.transferoutrate.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;

public interface TOService {

    Result getTransferOutRate(ParameterVo parameterVo);
}
