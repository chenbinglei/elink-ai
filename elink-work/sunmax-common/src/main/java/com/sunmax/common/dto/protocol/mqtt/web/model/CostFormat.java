package com.sunmax.common.dto.protocol.mqtt.web.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 时段费率格式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostFormat {

    /**
     * 时段开始时间 24小时制
     */
    @ApiModelProperty(value = "时段开始时间", required = true)
    private Integer startTime;

    /**
     * 时段结束时间 24小时制
     */
    @ApiModelProperty(value = "时段结束时间", required = true)
    private Integer endTime;

    /**
     * 时段电价 0.001元/kwh
     */
    @ApiModelProperty(value = "时段电价", required = true)
    private Double price;

    /**
     * 服务费用 0.001元/kwh
     */
    @ApiModelProperty(value = "服务费用", required = true)
    private Double serviceCharger;

    /**
     * 时段类型 1-尖时段 2-峰时段 3-平时段 4-谷时段
     */
    @ApiModelProperty(value = "时段类型", required = true)
    private Integer type;
}
