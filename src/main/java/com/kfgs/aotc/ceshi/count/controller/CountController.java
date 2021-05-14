package com.kfgs.aotc.ceshi.count.controller;

import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.ceshi.count.service.CountService;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ceshi/count/")
public class CountController {

    @Autowired
    private CountService testService;

    @Autowired
    private ClassifierInfoRepository classifierRepository;


    //public static HashMap<String,String> classifiers_Code = CountUtil.getClassificationCode(); //分类员id和分类号
    //public static HashMap<String,String> classifiers_Name = CountUtil.getClassificationName(); //分类员id和姓名
    //public static HashMap<String,ClassifierInfo> classifiersMap = CountUtil.getUserInfo();



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
