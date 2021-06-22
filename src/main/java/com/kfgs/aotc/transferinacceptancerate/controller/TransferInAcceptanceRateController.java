package com.kfgs.aotc.transferinacceptancerate.controller;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.transferinacceptancerate.service.TransferInAcceptanceRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tiar/")
public class TransferInAcceptanceRateController {

    @Autowired
    private TransferInAcceptanceRateService transferInAcceptanceRateService;

    @RequestMapping("init")
    public Result getTransferInAcceptanceRate(ParameterVo parameterVo){
        return transferInAcceptanceRateService.getTransferInAcceptanceRate(parameterVo);
    }

    @RequestMapping("initAll")
    public Result getTransferInAcceptanceRateAll(ParameterVo parameterVo){
        return transferInAcceptanceRateService.getTransferInAcceptanceRateAll(parameterVo);
    }

}
