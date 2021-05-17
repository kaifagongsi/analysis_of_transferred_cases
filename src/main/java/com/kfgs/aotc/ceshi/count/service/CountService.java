package com.kfgs.aotc.ceshi.count.service;

import com.kfgs.aotc.common.pojo.Result;

import java.util.List;
import java.util.Map;

public interface CountService {
    Result<List<Map<String,String>>> countAccuracy(List<String> list);

    //Result<Map<String,String>> countAccuracyByID(String classifierID);
}
