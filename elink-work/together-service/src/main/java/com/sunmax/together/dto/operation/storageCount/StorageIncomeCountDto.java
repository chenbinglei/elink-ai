package com.sunmax.together.dto.operation.storageCount;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "储能收益分析统计返回实体类")
public class StorageIncomeCountDto {

    /**
     * 充电成本(总)
     */
    @Schema(description = "充电成本(总)")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 充电成本(尖)
     */
    @Schema(description = "充电成本(尖)")
    private BigDecimal topChargeMoney = BigDecimal.ZERO;

    /**
     * 充电成本(峰)
     */
    @Schema(description = "充电成本(峰)")
    private BigDecimal peakChargeMoney = BigDecimal.ZERO;

    /**
     * 充电成本(平)
     */
    @Schema(description = "充电成本(平)")
    private BigDecimal plainChargeMoney = BigDecimal.ZERO;

    /**
     * 充电成本(谷)
     */
    @Schema(description = "充电成本(谷)")
    private BigDecimal valleyChargeMoney = BigDecimal.ZERO;

    /**
     * 充电成本(深谷)
     */
    @Schema(description = "充电成本(深谷)")
    private BigDecimal deepChargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(总)
     */
    @Schema(description = "放电收入(总)")
    private BigDecimal dischargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(尖)
     */
    @Schema(description = "放电收入(尖)")
    private BigDecimal topDischargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(峰)
     */
    @Schema(description = "放电收入(峰)")
    private BigDecimal peakDischargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(平)
     */
    @Schema(description = "放电收入(平)")
    private BigDecimal plainDischargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(谷)
     */
    @Schema(description = "放电收入(谷)")
    private BigDecimal valleyDischargeMoney = BigDecimal.ZERO;

    /**
     * 放电收入(深谷)
     */
    @Schema(description = "放电收入(深谷)")
    private BigDecimal deepDischargeMoney = BigDecimal.ZERO;

    /**
     * 日期列表
     */
    @Schema(description = "日期列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 充电成本(总)列表
     */
    @Schema(description = "充电成本(总)列表")
    private List<BigDecimal> chargeMoneyList = Lists.newArrayList();

    /**
     * 放电收入(总)列表
     */
    @Schema(description = "放电收入(总)列表")
    private List<BigDecimal> dischargeMoneyList = Lists.newArrayList();

    /**
     * 收益列表
     */
    @Schema(description = "收益列表")
    private List<BigDecimal> incomeList = Lists.newArrayList();

    /**
     * 累计收益列表
     */
    @Schema(description = "累计收益列表")
    private List<BigDecimal> totalIncomeList = Lists.newArrayList();

}
