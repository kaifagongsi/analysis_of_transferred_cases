package com.kfgs.aotc.pojo.business.vo;

import com.kfgs.aotc.common.pojo.PageCondition;
import lombok.Data;

@Data
public class ParameterVo  extends PageCondition {
    private String startDate; //开始时间
    private String endDate; //结束时间
    private Integer firstClassify; //下拉框的选择
    private String secondClassify; //下拉框选中的值
}
