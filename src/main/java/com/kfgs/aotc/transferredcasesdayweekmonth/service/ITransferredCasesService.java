package com.kfgs.aotc.transferredcasesdayweekmonth.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;

public interface ITransferredCasesService {

    boolean choseCurrent(String type);

    Result calculation( ParameterVo parameterVo);


}
