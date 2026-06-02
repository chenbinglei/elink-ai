package com.sunmax.together.dto.asset.electConfig;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "ElectTimeFrameDto", description = "电价配置时段参数实体类")
public class ElectTimeFrameDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 所属电价策略id
     */
    @ApiModelProperty(value = "所属电价策略id")
    private String electConfigId;

    /**
     * 时段开始时间
     */
    @ApiModelProperty(value = "时段开始时间", required = true)
    private String startTime;

    /**
     * 时段结束时间
     */
    @ApiModelProperty(value = "时段结束时间", required = true)
    private String endTime;

    /**
     * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天(展示平时)
     */
    @ApiModelProperty(value = "时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天(展示平时)", required = true)
    private Integer periodType;

    /**
     * 电费
     */
    @ApiModelProperty(value = "电费", required = true)
    private BigDecimal electMoney;

}
