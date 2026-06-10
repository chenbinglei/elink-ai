package com.sunmax.common.dto.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "充放电价格费率返回实体类")
public class ChargerPriceRateDto {

    /**
     * 价格id
     */
    @Schema(description = "价格id")
    private String priceId;

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
     * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天
     */
    @Schema(description = "时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天")
    private Integer periodType;

    /**
     * 电费
     */
    @Schema(description = "电费")
    private BigDecimal electMoney;

    /**
     * 服务费
     */
    @Schema(description = "服务费")
    private BigDecimal serviceMoney;
}
