package com.kfgs.aotc.util;

import com.kfgs.aotc.repository.ClassifierInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class CountUtil {
    @Autowired
    private static ClassifierInfoRepository classifierRepository;

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
