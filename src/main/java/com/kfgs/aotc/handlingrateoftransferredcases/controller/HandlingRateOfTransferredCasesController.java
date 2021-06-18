package com.kfgs.aotc.handlingrateoftransferredcases.controller;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.handlingrateoftransferredcases.service.HandlingRateOfTransferredCasesService;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理转案率
 */
@RestController
@RequestMapping("/hrotc/")
public class HandlingRateOfTransferredCasesController {

    @Autowired
    HandlingRateOfTransferredCasesService handlingRateOfTransferredCasesService;

    @PostMapping("init")
    public Result getHandlingRateOfTransferredCases(ParameterVo parameterVo){
        return handlingRateOfTransferredCasesService.getHandlingRateOfTransferredCases(parameterVo);
    }

    @PostMapping("initAll")
    public Result getHandlingRateOfTransferredCasesByDepOrField(ParameterVo parameterVo){
        return handlingRateOfTransferredCasesService.getHandlingRateOfTransferredCasesByDepOrField(parameterVo);
    }

}
