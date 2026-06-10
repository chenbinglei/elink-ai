package com.sunmax.protocol.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CostDto {

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
     * 充放电电费价格
     */
    @Schema(description = "充放电电费价格")
    @Builder.Default
    private BigDecimal chargePrice = new BigDecimal("0.0");

    /**
     * 充放电服务费价格
     */
    @Schema(description = "充放电服务费价格")
    @Builder.Default
    private BigDecimal chargeFeePrice = new BigDecimal("0.0");

    /**
     * 总金额
     */
    @Schema(description = "总金额")
    @Builder.Default
    private BigDecimal money = new BigDecimal("0.0");

    /**
     * 充放电量
     */
    @Schema(description = "充放电量")
    @Builder.Default
    private Double chargeQt = 0.0;

    /**
     * 充放电电费金额
     */
    @Schema(description = "充放电电费金额")
    @Builder.Default
    private BigDecimal chargeMoney = new BigDecimal("0.0");

    /**
     * 充放电服务费金额
     */
    @Schema(description = "充放电服务费金额")
    @Builder.Default
    private BigDecimal chargeFeeMoney = new BigDecimal("0.0");

    /**
     * 下标
     */
    @Schema(description = "下标")
    private int index;

}
