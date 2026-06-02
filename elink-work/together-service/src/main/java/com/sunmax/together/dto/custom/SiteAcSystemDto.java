package com.sunmax.together.dto.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "SiteAcSystemDto", description = "电站VS交流系统返回实体类")
public class SiteAcSystemDto {

    /**
     * 直流系统用电成本(元)
     */
    @ApiModelProperty(value = "直流系统用电成本(元)")
    private BigDecimal dcSystemCost = BigDecimal.ZERO;

    /**
     * 直流系统用电成本占比(%)
     */
    @ApiModelProperty(value = "直流系统用电成本占比(%)")
    private Double dcSystemCostPercent;

    /**
     * 交流系统用电成本(元)
     */
    @ApiModelProperty(value = "交流系统用电成本(元)")
    private BigDecimal acSystemCost = BigDecimal.ZERO;

    /**
     * 交流系统用电成本占比(%)
     */
    @ApiModelProperty(value = "交流系统用电成本占比(%)")
    private Double acSystemCostPercent;

    /**
     * 直流系统系统损耗(kWh)
     */
    @ApiModelProperty(value = "直流系统系统损耗(kWh)")
    private Double dcSystemLoss = 0.0;

    /**
     * 直流系统系统损耗占比(%)
     */
    @ApiModelProperty(value = "直流系统系统损耗占比(%)")
    private Double dcSystemLossPercent;

    /**
     * 交流系统系统损耗(kWh)
     */
    @ApiModelProperty(value = "交流系统系统损耗(kWh)")
    private Double acSystemLoss = 0.0;

    /**
     * 交流系统系统损耗占比(%)
     */
    @ApiModelProperty(value = "交流系统系统损耗占比(%)")
    private Double acSystemLossPercent;

}
