package com.kfgs.aotc.ceshi.asscoderate.controller;

import com.kfgs.aotc.ceshi.asscoderate.service.ACRService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/count/asscoderate/")
public class ACRController {

    @Autowired
    private ACRService acrService;

    /**
     * 加副分率
     * @param parameterVo
     * @return
     */
    @RequestMapping("getAssCodeRate")
    public Result getAssCodeRate(ParameterVo parameterVo){
        return acrService.getAssCodeRate(parameterVo);
    }

    @RequestMapping("getAssCodeRateAll")
    public Result getAssCodeRateAll(ParameterVo parameterVo){
        return acrService.getAssCodeRateAll(parameterVo);
    }
}
