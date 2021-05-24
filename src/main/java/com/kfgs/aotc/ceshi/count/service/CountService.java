package com.kfgs.aotc.ceshi.count.service;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;

import java.util.List;
import java.util.Map;

public interface CountService {
    //Result<List<Map<String,String>>> countAccuracy(List<String> list, ParameterVo parameterVo);

    Result getEffectiveTransferRate(ParameterVo parameterVo);
    //Map<String,String> countAccuracyByID(String classifierID);
}
