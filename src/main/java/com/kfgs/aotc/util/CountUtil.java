package com.kfgs.aotc.util;
import com.kfgs.aotc.pojo.business.ClassifierInfo;
import com.kfgs.aotc.repository.ClassifierInfoRepository;
import com.kfgs.aotc.repository.DetailsOfTheCaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

@Component
@Slf4j
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
    // 需要排除的领域
    public static List<String> SEND_NAME_NEED_TO_EXCLUDE = new ArrayList<>();

    @Value("${SEND_NAME_NEED_TO_EXCLUDE}")
    private String need_to_exclude;

    @PostConstruct
    public void init(){
        log.info("加载classifiersCodeMap......");
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
        log.info("开始加载需要排除的领域");
        SEND_NAME_NEED_TO_EXCLUDE.addAll(Arrays.asList(need_to_exclude.split(",")));
    }
    @PreDestroy
    public void destroy(){
        System.out.println("运行结束");
    }

}
