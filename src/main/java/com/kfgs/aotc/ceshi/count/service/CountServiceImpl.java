package com.kfgs.aotc.ceshi.count.service;

import com.kfgs.aotc.common.pojo.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CountServiceImpl implements CountService {

    @Override
    public Result<Map<String, Float>> countAccuracy(List<String> list) {
        return null;
    }
}
