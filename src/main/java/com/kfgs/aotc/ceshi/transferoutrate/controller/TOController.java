package com.kfgs.aotc.ceshi.transferoutrate.controller;

import com.kfgs.aotc.ceshi.transferoutrate.service.TOService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 转出案件率
 */
@RestController
@RequestMapping("/count/transferout/")
public class TOController {

    @Autowired
    private TOService toService;

    public Result getTransferOutRate(ParameterVo parameterVo){
        return toService.getTransferOutRate(parameterVo);
    }
}
