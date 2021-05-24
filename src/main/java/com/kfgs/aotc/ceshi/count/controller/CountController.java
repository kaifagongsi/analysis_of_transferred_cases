package com.kfgs.aotc.ceshi.count.controller;
import com.kfgs.aotc.ceshi.count.service.CountService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.util.CountUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ceshi/count/")
public class CountController {

    @Autowired
    private CountService countService;

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;

    @GetMapping("")
    public ModelAndView authority(){
        return new ModelAndView("ceshi/ceshi");
    }

    /*@PostMapping("count")
    public Result<List<Map<String,String>>> countAccuracy(@RequestParam(value = "page",required = false)Integer page,@RequestParam(value = "limit",required = false)Integer limit, ParameterVo parameterVo){
        List<String> list = new ArrayList<>();
        for (Map.Entry<String,List<String>> entry : CountUtil.CLASSIFIERS_AND_CODE.entrySet()) {
            list.add(entry.getKey());
        }
        //return countService.countAccuracy(list,parameterVo);
        return countService.getEffectiveTransferRate(parameterVo);
    }*/

    @PostMapping("count")
    public Result countAccuracy(ParameterVo parameterVo){
        /*List<String> list = new ArrayList<>();
        for (Map.Entry<String,List<String>> entry : CountUtil.CLASSIFIERS_AND_CODE.entrySet()) {
            list.add(entry.getKey());
        }*/
        //return countService.countAccuracy(list,parameterVo);
        return countService.getEffectiveTransferRate(parameterVo);
    }

    /**
     * 个人有效转出率
     * @param classifierID
     * @return
     */
    /*@PostMapping("countone")
    public Map<String,String> countAccuracyByID(String classifierID){
        return countService.countAccuracyByID(classifierID);
    }*/
}
