package com.kfgs.aotc.ceshi.count.controller;
import com.kfgs.aotc.ceshi.count.service.CountService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.util.CountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ceshi/count/")
public class CountController {

    @Autowired
    private CountService countService;

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;


    //public static HashMap<String,String> classifiers_Code = CountUtil.getClassificationCode(); //分类员id和分类号
    //public static HashMap<String,String> classifiers_Name = CountUtil.getClassificationName(); //分类员id和姓名
    //public static List<ClassifierInfo> classifiersMap = classifierInfoRepository.findAll();

    @GetMapping("")
    public ModelAndView authority(){
        return new ModelAndView("ceshi/ceshi");
    }


    @PostMapping("count")
    public Result<List<Map<String,String>>> countAccuracy(){
        List<String> list = new ArrayList<>();
        List<ClassifierInfo> classifierInfos = classifierInfoRepository.findAll();
        for(int i=0;i<classifierInfos.size();i++){
            String classifierCode = classifierInfos.get(i).getClassifiersCode();
            list.add(classifierCode);
        }
        return countService.countAccuracy(list);
    }

    /**
     * 个人有效转出率
     * @param classifierID
     * @return
     */
    /*@PostMapping("countone")
    public Result<Map<String,String>> countAccuracyByID(String classifierID){
        return countService.countAccuracyByID("220545");
    }*/
}
