package com.kfgs.aotc.transferredcasesdayweekmonth.controller;

import com.kfgs.aotc.common.pojo.Result;
import com.kfgs.aotc.pojo.business.vo.ParameterVo;
import com.kfgs.aotc.transferredcasesdayweekmonth.service.ITransferredCasesService;
import com.kfgs.aotc.transferredcasesdayweekmonth.service.TransferredCasesFactory;
import com.kfgs.aotc.util.DateUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 处理转案率，日、周、月
 */
@RestController
@RequestMapping("/tcbd/")
public class TransferredCasesController {


    @Bean(initMethod = "init")
    TransferredCasesFactory factory(){
        return new TransferredCasesFactory();
    }


    @PostMapping("init/{type}")
    public Result getTransferredCasesInit(@PathVariable String type,  ParameterVo parameterVo) throws ParseException {
        ITransferredCasesService service = TransferredCasesFactory.createObj(type);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // 如果是周的话判断开始日期是否为周一，结束日期是否为周日
        if("week".equalsIgnoreCase(type)){
            String startDate = parameterVo.getStartDate();
            String endDate = parameterVo.getEndDate();
            String formatStartDate = sdf.format(DateUtil.getThisWeekMonday(sdf.parse(startDate)));
            if(!startDate.equalsIgnoreCase(formatStartDate)){
                return Result.of(null,false,"按周统计时，开始日期请选择周一");
            }
            if(! DateUtil.isLastDayOfWeek(sdf.parse(endDate))){
                return Result.of(null,false,"按周统计时，结束日期请选择周日");
            }
        }
        if("month".equalsIgnoreCase(type)){
            boolean firstDayOfMonth = DateUtil.isFirstDayOfMonth(sdf.parse(parameterVo.getStartDate()));
            if(!firstDayOfMonth){
                return Result.of(null,false,"按月统计时，开始日期请选择本月一号");
            }
            if(!DateUtil.isLastDayOfMonth(sdf.parse(parameterVo.getEndDate()))){
                return Result.of(null,false,"按月统计时，开始日期请选择本月最后一天");
            }
        }
        return service.calculation(parameterVo);
    }

}
