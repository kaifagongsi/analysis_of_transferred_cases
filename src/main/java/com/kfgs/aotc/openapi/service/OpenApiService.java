package com.kfgs.aotc.openapi.service;


import com.kfgs.aotc.common.pojo.Result;

public interface OpenApiService {
    /**
     * open api test测试
     * @return 测试数据
     */
    Result<String> test();
}
