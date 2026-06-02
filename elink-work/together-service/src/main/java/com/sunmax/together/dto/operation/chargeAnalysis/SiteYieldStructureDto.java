package com.sunmax.together.dto.operation.chargeAnalysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "SiteYieldStructureDto", description = "站点电量结构数据返回实体类")
public class SiteYieldStructureDto {

    /**
     * 正向电量
     */
    @ApiModelProperty(value = "正向电量")
    private Double positiveQt = 0.0;

    /**
     * 反向电量
     */
    @ApiModelProperty(value = "反向电量")
    private Double negativeQt = 0.0;

    /**
     * 汽车充电量
     */
    @ApiModelProperty(value = "汽车充电量")
    private Double pileChargeQt = 0.0;

    /**
     * 汽车放电量
     */
    @ApiModelProperty(value = "汽车放电量")
    private Double pileDischargeQt = 0.0;

    /**
     * 损耗电量
     */
    @ApiModelProperty(value = "损耗电量")
    private Double lossQt = 0.0;

    /**
     * 充电收入
     */
    @ApiModelProperty(value = "充电收入")
    private BigDecimal chargeCost = BigDecimal.ZERO;

    /**
     * 充电购电成本
     */
    @ApiModelProperty(value = "充电购电成本")
    private BigDecimal chargePurchaseCost = BigDecimal.ZERO;

    /**
     * V2G售电收入
     */
    @ApiModelProperty(value = "V2G售电收入")
    private BigDecimal dischargeSaleCost = BigDecimal.ZERO;

    /**
     * V2G购电成本
     */
    @ApiModelProperty(value = "V2G购电成本")
    private BigDecimal dischargePurchaseCost = BigDecimal.ZERO;

    /**
     * 充电收益
     */
    @ApiModelProperty(value = "充电收益")
    private BigDecimal chargeIncome = BigDecimal.ZERO;

    /**
     * 放电收益
     */
    @ApiModelProperty(value = "放电收益")
    private BigDecimal dischargeIncome = BigDecimal.ZERO;

    /**
     * 运营补贴
     */
    @ApiModelProperty(value = "运营补贴")
    private BigDecimal operateSubsidy = BigDecimal.ZERO;

    /**
     * 场地租金
     */
    @ApiModelProperty(value = "场地租金")
    private BigDecimal siteRent = BigDecimal.ZERO;

    /**
     * 运营成本
     */
    @ApiModelProperty(value = "运营成本")
    private BigDecimal operateCost = BigDecimal.ZERO;

    /**
     * 运维成本
     */
    @ApiModelProperty(value = "运维成本")
    private BigDecimal maintainCost = BigDecimal.ZERO;

    /**
     * 总收益
     */
    @ApiModelProperty(value = "总收益")
    private BigDecimal totalIncome = BigDecimal.ZERO;

    /**
     * 日期
     */
    @ApiModelProperty(value = "日期")
    private String date;

}
