package com.kfgs.aotc.ceshi.effectivetransferoutrate.controller;
import com.kfgs.aotc.ceshi.effectivetransferoutrate.service.ETOService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 有效转出率
 */
@RestController
@RequestMapping("/ceshi/count/")
public class ETOController {

    @Autowired
    private ETOService ETOService;

    @Autowired
    private ClassifierInfoRepository classifierInfoRepository;

    @GetMapping("")
    public ModelAndView authority(){
        return new ModelAndView("ceshi/ceshi");
    }

    /*@PostMapping("effectivetransferoutrate")
    public Result<List<Map<String,String>>> countAccuracy(@RequestParam(value = "page",required = false)Integer page,@RequestParam(value = "limit",required = false)Integer limit, ParameterVo parameterVo){
        List<String> list = new ArrayList<>();
        for (Map.Entry<String,List<String>> entry : CountUtil.CLASSIFIERS_AND_CODE.entrySet()) {
            list.add(entry.getKey());
        }
        //return ETOService.countAccuracy(list,parameterVo);
        return ETOService.getEffectiveTransferRate(parameterVo);
    }*/

    @PostMapping("countAccuracy")
    public Result countAccuracy(ParameterVo parameterVo){
        /*List<String> list = new ArrayList<>();
        for (Map.Entry<String,List<String>> entry : CountUtil.CLASSIFIERS_AND_CODE.entrySet()) {
            list.add(entry.getKey());
        }*/
        //return ETOService.countAccuracy(list,parameterVo);
        return ETOService.getEffectiveTransferRate(parameterVo);
    }

    @RequestMapping("countAll")
    public Result getEffectiveTransferInRateAll(ParameterVo parameterVo){
        return ETOService.getEffectiveTransferOutRateAll(parameterVo);
    }

    @PostMapping("findAllFieldGroup")
    public Result findAllFieldGroup(){
        return ETOService.getAllFieldGroup();
    }

    /**
     * 个人有效转出率
     * @param classifierID
     * @return
     */
    /*@PostMapping("countone")
    public Map<String,String> countAccuracyByID(String classifierID){
        return ETOService.countAccuracyByID(classifierID);
    }*/
}
