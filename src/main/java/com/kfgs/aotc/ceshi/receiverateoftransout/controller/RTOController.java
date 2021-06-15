package com.kfgs.aotc.ceshi.receiverateoftransout.controller;

import com.kfgs.aotc.ceshi.receiverateoftransout.service.RTOService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 转出接收率
 */
@RestController
@RequestMapping("/ceshi/count/")
public class RTOController {

    @Autowired
    private RTOService rtoService;

    @PostMapping("receiveRateOfTransOut")
    public Result receiveRateOfTransOut(ParameterVo parameterVo){
        return rtoService.receiveRateOfTransOut(parameterVo);
    }
}
