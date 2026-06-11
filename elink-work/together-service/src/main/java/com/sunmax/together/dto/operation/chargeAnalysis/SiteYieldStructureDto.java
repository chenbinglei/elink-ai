package com.sunmax.together.dto.operation.chargeAnalysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "站点电量结构数据返回实体类")
public class SiteYieldStructureDto {

    /**
     * 正向电量
     */
    @Schema(description = "正向电量")
    private Double positiveQt = 0.0;

    /**
     * 反向电量
     */
    @Schema(description = "反向电量")
    private Double negativeQt = 0.0;

    /**
     * 汽车充电量
     */
    @Schema(description = "汽车充电量")
    private Double pileChargeQt = 0.0;

    /**
     * 汽车放电量
     */
    @Schema(description = "汽车放电量")
    private Double pileDischargeQt = 0.0;

    /**
     * 损耗电量
     */
    @Schema(description = "损耗电量")
    private Double lossQt = 0.0;

    /**
     * 充电收入
     */
    @Schema(description = "充电收入")
    private BigDecimal chargeCost = BigDecimal.ZERO;

    /**
     * 充电购电成本
     */
    @Schema(description = "充电购电成本")
    private BigDecimal chargePurchaseCost = BigDecimal.ZERO;

    /**
     * V2G售电收入
     */
    @Schema(description = "V2G售电收入")
    private BigDecimal dischargeSaleCost = BigDecimal.ZERO;

    /**
     * V2G购电成本
     */
    @Schema(description = "V2G购电成本")
    private BigDecimal dischargePurchaseCost = BigDecimal.ZERO;

    /**
     * 充电收益
     */
    @Schema(description = "充电收益")
    private BigDecimal chargeIncome = BigDecimal.ZERO;

    /**
     * 放电收益
     */
    @Schema(description = "放电收益")
    private BigDecimal dischargeIncome = BigDecimal.ZERO;

    /**
     * 运营补贴
     */
    @Schema(description = "运营补贴")
    private BigDecimal operateSubsidy = BigDecimal.ZERO;

    /**
     * 场地租金
     */
    @Schema(description = "场地租金")
    private BigDecimal siteRent = BigDecimal.ZERO;

    /**
     * 运营成本
     */
    @Schema(description = "运营成本")
    private BigDecimal operateCost = BigDecimal.ZERO;

    /**
     * 运维成本
     */
    @Schema(description = "运维成本")
    private BigDecimal maintainCost = BigDecimal.ZERO;

    /**
     * 总收益
     */
    @Schema(description = "总收益")
    private BigDecimal totalIncome = BigDecimal.ZERO;

    /**
     * 日期
     */
    @Schema(description = "日期")
    private String date;

}
