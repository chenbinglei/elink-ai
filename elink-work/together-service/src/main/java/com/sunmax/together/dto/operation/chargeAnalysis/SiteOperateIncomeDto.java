package com.sunmax.together.dto.operation.chargeAnalysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "SiteOperateIncomeDto", description = "站点经营收入返回实体类")
public class SiteOperateIncomeDto {

    /**
     * 日期列表
     */
    @ApiModelProperty(value = "日期列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 收益列表(元)
     */
    @ApiModelProperty(value = "收益列表")
    private List<BigDecimal> incomeList = Lists.newArrayList();

    /**
     * 累计收益列表(万元)
     */
    @ApiModelProperty(value = "累计收益列表(万元)")
    private List<BigDecimal> totalIncomeList = Lists.newArrayList();

    /**
     * 累计收益率列表(%)
     */
    @ApiModelProperty(value = "累计收益率列表(%)")
    private List<Double> totalIncomeRateList = Lists.newArrayList();

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

}
