package com.sunmax.common.dto.protocol.mqtt.web.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "时段开始时间")
    private Integer startTime;

    /**
     * 时段结束时间 24小时制
     */
    @Schema(description = "时段结束时间")
    private Integer endTime;

    /**
     * 时段电价 0.001元/kwh
     */
    @Schema(description = "时段电价")
    private Double price;

    /**
     * 服务费用 0.001元/kwh
     */
    @Schema(description = "服务费用")
    private Double serviceCharger;

    /**
     * 时段类型 1-尖时段 2-峰时段 3-平时段 4-谷时段
     */
    @Schema(description = "时段类型")
    private Integer type;
}
