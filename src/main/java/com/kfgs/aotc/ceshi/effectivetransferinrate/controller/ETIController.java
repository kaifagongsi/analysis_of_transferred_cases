package com.kfgs.aotc.ceshi.effectivetransferinrate.controller;

import com.kfgs.aotc.ceshi.effectivetransferinrate.service.ETIService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 有效转入率
 * Effective transfer in rate
 * */
@RestController
@RequestMapping("/etir/")
public class ETIController {

    @Autowired
    ETIService etiService;

    @RequestMapping("init")
    public Result getEffectiveTransferInRate(ParameterVo parameterVo){
        return etiService.getEffectiveTransferInRate(parameterVo);
    }
}
