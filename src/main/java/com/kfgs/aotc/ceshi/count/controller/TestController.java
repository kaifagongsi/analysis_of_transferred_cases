package com.kfgs.aotc.ceshi.count.controller;

import com.kfgs.aotc.ceshi.classifier.repository.ClassifierRepository;
import com.kfgs.aotc.ceshi.count.service.TestService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.util.CountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ceshi/")
public class TestController {

    @Autowired
    private TestService testService;

    @Autowired
    private ClassifierRepository classifierRepository;


    //public static HashMap<String,String> classifiers_Code = CountUtil.getClassificationCode(); //分类员id和分类号
    //public static HashMap<String,String> classifiers_Name = CountUtil.getClassificationName(); //分类员id和姓名
    //public static HashMap<String,ClassifierInfo> classifiersMap = CountUtil.getUserInfo();

    @GetMapping("")
    public ModelAndView authority(){
        return new ModelAndView("ceshi/ceshi");
    }

    @PostMapping("count")
    public Map countAccuracy(){
        HashMap<String,ClassifierInfo> map = new HashMap<>();
        List<ClassifierInfo> list = classifierRepository.findAll();
        for (int i=0;i<list.size();i++){
            String classifierCode = list.get(i).getClassifiersCode().toString();
            map.put(classifierCode,list.get(i));
        }
        return map;
    }

}
