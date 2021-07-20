package com.kfgs.aotc.transferredcasesdayweekmonth.controller;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.transferredcasesdayweekmonth.service.ITransferredCasesService;
import com.kfgs.aotc.transferredcasesdayweekmonth.service.TransferredCasesFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 处理转案率，日、周、月
 */
@RestController
@RequestMapping("/tcbd/")
public class TransferredCasesController {


    @Bean(initMethod = "init")
    TransferredCasesFactory factory(){
        return new TransferredCasesFactory();
    }


    @PostMapping("init/{type}")
    public Result getTransferredCasesInit(@PathVariable String type,  ParameterVo parameterVo){
        ITransferredCasesService service = TransferredCasesFactory.createObj(type);
        return service.calculation(parameterVo);
    }

}
