package com.kfgs.aotc.util;

import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.pojo.business.DetailsOfTheCase;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailsOfTheCaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.ServletContext;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CountUtil {
    @Autowired
    private ClassifierInfoRepository classifierRepository;

    @Autowired
    private DetailsOfTheCaseRepository detailsOfTheCaseRepository;
    /**
     * 分类员与其对应分类号
     * @return
     */
    public static Map<String,List<String>> CLASSIFIERS_AND_CODE = new HashMap<>();

    @PostConstruct
    public void init(){
        System.out.println("加载classifiersCodeMap......");
        List<ClassifierInfo> list = classifierRepository.findAll();
        for (int i=0;i<list.size();i++){
            String classifierCode = list.get(i).getClassifiersCode();
            List<String> classCodes = new ArrayList<>();
            String[] codes = list.get(i).getClassificationField().split(",");
            for (int j=0;j<codes.length;j++){
                if (codes[j].length()>=4){
                    classCodes.add(codes[j].substring(0,4));
                }
            }
            CLASSIFIERS_AND_CODE.put(classifierCode,classCodes);
        }

    }
    @PreDestroy
    public void destroy(){
        System.out.println("运行结束");
    }

}
