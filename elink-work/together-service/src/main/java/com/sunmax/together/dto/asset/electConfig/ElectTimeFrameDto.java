package com.sunmax.together.dto.asset.electConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "电价配置时段参数实体类")
public class ElectTimeFrameDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 所属电价策略id
     */
    @Schema(description = "所属电价策略id")
    private String electConfigId;

    /**
     * 时段开始时间
     */
    @Schema(description = "时段开始时间")
    private String startTime;

    /**
     * 时段结束时间
     */
    @Schema(description = "时段结束时间")
    private String endTime;

    /**
     * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天(展示平时)
     */
    @Schema(description = "时段类型 1-尖时 2-峰时 3-平时 4-谷时 5-深谷 6-全天(展示平时)")
    private Integer periodType;

    /**
     * 电费
     */
    @Schema(description = "电费")
    private BigDecimal electMoney;

}
