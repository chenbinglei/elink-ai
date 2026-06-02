package com.sunmax.together.dto.monitor.centralMonitorOld;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "ElecCountCurveDto", description = "电量统计曲线数据返回实体类")
public class ElecCountCurveDto {

    /**
     * 充电电量
     */
    @ApiModelProperty(value = "充电电量")
    private List<Double> chargeQtList = Lists.newArrayList();

    /**
     * 放电电量
     */
    @ApiModelProperty(value = "放电电量")
    private List<Double> dischargeQtList = Lists.newArrayList();

    /**
     * x时间轴数据
     */
    @JsonProperty("xAxisList")
    @ApiModelProperty(value = "x时间轴数据")
    private List<String> xAxisList = Lists.newArrayList();
}
