package com.sunmax.together.dto.operation.storageCount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "StorageIncomeCountDto", description = "储能收益分析统计返回实体类")
public class StorageIncomeCountDto {

    /**
     * 充电成本(总)
     */
    @ApiModelProperty(value = "充电成本(总)")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 充电成本(尖)
     */
    @ApiModelProperty(value = "充电成本(尖)")
    private BigDecimal topChargeMoney = BigDecimal.ZERO;

    /**
     * 充电成本(峰)
     */
    @ApiModelProperty(value = "充电成本(峰)")
    private BigDecimal peakChargeMoney = BigDecimal.ZERO;

    /**
     * 充电成本(平)
     */
    @ApiModelProperty(value = "充电成本(平)")
    private BigDecimal plainChargeMoney = BigDecimal.ZERO;

    /**
     * 充电成本(谷)
     */
    @ApiModelProperty(value = "充电成本(谷)")
    private BigDecimal valleyChargeMoney = BigDecimal.ZERO;

    /**
     * 充电成本(深谷)
     */
    @ApiModelProperty(value = "充电成本(深谷)")
    private BigDecimal deepChargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(总)
     */
    @ApiModelProperty(value = "放电收入(总)")
    private BigDecimal dischargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(尖)
     */
    @ApiModelProperty(value = "放电收入(尖)")
    private BigDecimal topDischargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(峰)
     */
    @ApiModelProperty(value = "放电收入(峰)")
    private BigDecimal peakDischargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(平)
     */
    @ApiModelProperty(value = "放电收入(平)")
    private BigDecimal plainDischargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(谷)
     */
    @ApiModelProperty(value = "放电收入(谷)")
    private BigDecimal valleyDischargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(深谷)
     */
    @ApiModelProperty(value = "放电收入(深谷)")
    private BigDecimal deepDischargeMoney = BigDecimal.ZERO;

    /**
     * 日期列表
     */
    @ApiModelProperty(value = "日期列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 充电成本(总)列表
     */
    @ApiModelProperty(value = "充电成本(总)列表")
    private List<BigDecimal> chargeMoneyList = Lists.newArrayList();

    /**
     * 放电收入(总)列表
     */
    @ApiModelProperty(value = "放电收入(总)列表")
    private List<BigDecimal> dischargeMoneyList = Lists.newArrayList();

    /**
     * 收益列表
     */
    @ApiModelProperty(value = "收益列表")
    private List<BigDecimal> incomeList = Lists.newArrayList();

    /**
     * 累计收益列表
     */
    @ApiModelProperty(value = "累计收益列表")
    private List<BigDecimal> totalIncomeList = Lists.newArrayList();

}
