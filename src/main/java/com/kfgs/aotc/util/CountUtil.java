package com.kfgs.aotc.util;

import com.kfgs.aotc.ceshi.classifier.repository.ClassifierRepository;
import com.kfgs.aotc.ceshi.classifier.service.ClassifierService;
import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
public class CountUtil {
    @Autowired
    private static ClassifierRepository classifierRepository;

    public static HashMap<String,String> getClassificationCode(){
        HashMap<String,String> map = new HashMap<>();
        return map;
    }

    public static HashMap<String,String> getClassificationName(){
        HashMap<String,String> map = new HashMap<>();
        return map;
    }

    /*public static HashMap<String, ClassifierInfo> getUserInfo(){
        HashMap<String,ClassifierInfo> map = new HashMap<>();
        List<ClassifierInfo> list = classifierRepository.findAll();
        for (int i=0;i<list.size();i++){
            String classifierCode = list.get(i).getClassifiersCode().toString();
            map.put(classifierCode,list.get(i));
        }
        return map;
    }*/
}
