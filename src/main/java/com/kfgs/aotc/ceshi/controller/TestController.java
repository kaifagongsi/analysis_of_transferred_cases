package com.kfgs.aotc.ceshi.controller;

import com.kfgs.aotc.ceshi.service.TestServiceImpl;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/ceshi/")
public class TestController {

    @Autowired
    private TestServiceImpl testService;

    @GetMapping("")
    public ModelAndView authority(){
        return new ModelAndView("ceshi/ceshi");
    }
    @GetMapping("findAllUser")
    public Result<List<ClassifierInfo>> getUserName(){
        return Result.of(testService.getAllUser());
    }
}
