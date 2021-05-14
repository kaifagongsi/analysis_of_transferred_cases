package com.kfgs.aotc.ceshi.count.service;

import com.kfgs.aotc.common.pojo.Result;

import java.util.List;
import java.util.Map;

public interface CountService {
    Result<Map<String,Float>> countAccuracy(List<String> list);
}
