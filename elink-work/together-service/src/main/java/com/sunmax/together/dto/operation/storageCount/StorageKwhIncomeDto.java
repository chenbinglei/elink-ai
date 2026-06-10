package com.sunmax.together.dto.operation.storageCount;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "储能度电收益返回实体类")
public class StorageKwhIncomeDto {

    /**
     * 充电量(总)
     */
    @Schema(description = "充电量(总)")
    private Double chargeQt = 0.0;

    /**
     * 充电成本(总)
     */
    @Schema(description = "充电成本(总)")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 放电量(总)
     */
    @Schema(description = "放电量(总)")
    private Double dischargeQt = 0.0;

    /**
     * 放电收入(总)
     */
    @Schema(description = "放电收入(总)")
    private BigDecimal dischargeMoney = BigDecimal.ZERO;

    /**
     * 合计收益
     */
    @Schema(description = "合计收益")
    private BigDecimal totalIncome = BigDecimal.ZERO;

    /**
     * 合计度电收益
     */
    @Schema(description = "合计度电收益")
    private BigDecimal totalKwhIncome = BigDecimal.ZERO;

    /**
     * 日期列表
     */
    @Schema(description = "日期列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 度电收益列表
     */
    @Schema(description = "度电收益列表")
    private List<BigDecimal> kwhIncomeList = Lists.newArrayList();

}
