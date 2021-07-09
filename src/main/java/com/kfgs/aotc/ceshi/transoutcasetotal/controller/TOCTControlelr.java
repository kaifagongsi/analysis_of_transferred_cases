package com.kfgs.aotc.ceshi.transoutcasetotal.controller;

import com.kfgs.aotc.ceshi.transoutcasetotal.service.TOCTService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 不同时间维度计算出案案件数
 */
@RestController
@RequestMapping("/count/caseout/")
public class TOCTControlelr {
    @Autowired
    private TOCTService toctService;

    @RequestMapping("totalbyday")
    public Result getTotalByDay(ParameterVo parameterVo){
        return toctService.getTotalByDay(parameterVo);
    }

    @RequestMapping("totalbyweek")
    public Result getTotalByWeek(ParameterVo parameterVo){
        return toctService.getTotalByWeek(parameterVo);
    }

    @RequestMapping("totalbymonth")
    public Result getTotalByMonth(ParameterVo parameterVo){
        return toctService.getTotalByMonth(parameterVo);
    }
}
