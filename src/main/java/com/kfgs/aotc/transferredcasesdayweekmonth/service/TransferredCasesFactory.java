package com.kfgs.aotc.transferredcasesdayweekmonth.service;

import com.kfgs.aotc.transferredcasesdayweekmonth.service.impl.TransferredCasesServiceByDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class TransferredCasesFactory {

    @Autowired
    ApplicationContext applicationContext;

    // 处理转案率 Handling rate of transferred cases

    private static List<ITransferredCasesService> list = new ArrayList<>();

    public  void  init(){
        // 用于放置一些初始化，根据需要选择取舍
        list.clear(); // 每次初始化先清除旧数据
        list.add((ITransferredCasesService)applicationContext.getBean("transferredCasesServiceByDay")); // 这里根据条件判断优先级放入目标对象,先放入的先判断
        list.add((ITransferredCasesService)applicationContext.getBean("transferredCasesServiceByWeek"));
        list.add((ITransferredCasesService)applicationContext.getBean("transferredCasesServiceByMonth"));
    }

    public static ITransferredCasesService createObj(String day){
        ITransferredCasesService p;
        for(int i=0;i<list.size();i++){
            p = list.get(i);
            if(p.choseCurrent(day)){
                return p; // 如果当前条件满足，则直接返回目标对象
            }
        }
        return new TransferredCasesServiceByDay();// 别忘了最后返回一个默认值，对应于最后一个else判断
    }
}
