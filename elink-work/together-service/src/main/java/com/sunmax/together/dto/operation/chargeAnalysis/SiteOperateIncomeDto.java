package com.sunmax.together.dto.operation.chargeAnalysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "站点经营收入返回实体类")
public class SiteOperateIncomeDto {

    /**
     * 日期列表
     */
    @Schema(description = "日期列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 收益列表(元)
     */
    @Schema(description = "收益列表")
    private List<BigDecimal> incomeList = Lists.newArrayList();

    /**
     * 累计收益列表(万元)
     */
    @Schema(description = "累计收益列表(万元)")
    private List<BigDecimal> totalIncomeList = Lists.newArrayList();

    /**
     * 累计收益率列表(%)
     */
    @Schema(description = "累计收益率列表(%)")
    private List<Double> totalIncomeRateList = Lists.newArrayList();

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

}
