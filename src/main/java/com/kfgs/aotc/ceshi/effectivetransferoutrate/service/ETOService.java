package com.kfgs.aotc.ceshi.effectivetransferoutrate.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;

public interface ETOService {
    //Result<List<Map<String,String>>> countAccuracy(List<String> list, ParameterVo parameterVo);

    Result getEffectiveTransferRate(ParameterVo parameterVo);
    //Map<String,String> countAccuracyByID(String classifierID);

    Result getAllFieldGroup();

    Result getEffectiveTransferOutRateAll(ParameterVo parameterVo);
}
