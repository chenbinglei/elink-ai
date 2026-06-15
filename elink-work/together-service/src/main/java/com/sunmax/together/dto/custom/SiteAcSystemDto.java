package com.sunmax.together.dto.custom;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "电站VS交流系统返回实体类")
public class SiteAcSystemDto {

    /**
     * 直流系统用电成本(元)
     */
    @Schema(description = "直流系统用电成本(元)")
    private BigDecimal dcSystemCost = BigDecimal.ZERO;

    /**
     * 直流系统用电成本占比(%)
     */
    @Schema(description = "直流系统用电成本占比(%)")
    private Double dcSystemCostPercent;

    /**
     * 交流系统用电成本(元)
     */
    @Schema(description = "交流系统用电成本(元)")
    private BigDecimal acSystemCost = BigDecimal.ZERO;

    /**
     * 交流系统用电成本占比(%)
     */
    @Schema(description = "交流系统用电成本占比(%)")
    private Double acSystemCostPercent;

    /**
     * 直流系统系统损耗(kWh)
     */
    @Schema(description = "直流系统系统损耗(kWh)")
    private Double dcSystemLoss = 0.0;

    /**
     * 直流系统系统损耗占比(%)
     */
    @Schema(description = "直流系统系统损耗占比(%)")
    private Double dcSystemLossPercent;

    /**
     * 交流系统系统损耗(kWh)
     */
    @Schema(description = "交流系统系统损耗(kWh)")
    private Double acSystemLoss = 0.0;

    /**
     * 交流系统系统损耗占比(%)
     */
    @Schema(description = "交流系统系统损耗占比(%)")
    private Double acSystemLossPercent;

}
