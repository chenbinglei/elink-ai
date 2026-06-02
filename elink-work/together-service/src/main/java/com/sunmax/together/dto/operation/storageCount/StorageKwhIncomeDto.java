package com.sunmax.together.dto.operation.storageCount;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "StorageKwhIncomeDto", description = "储能度电收益返回实体类")
public class StorageKwhIncomeDto {

    /**
     * 充电量(总)
     */
    @ApiModelProperty(value = "充电量(总)")
    private Double chargeQt = 0.0;

    /**
     * 充电成本(总)
     */
    @ApiModelProperty(value = "充电成本(总)")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 放电量(总)
     */
    @ApiModelProperty(value = "放电量(总)")
    private Double dischargeQt = 0.0;

    /**
     * 放电收入(总)
     */
    @ApiModelProperty(value = "放电收入(总)")
    private BigDecimal dischargeMoney = BigDecimal.ZERO;

    /**
     * 合计收益
     */
    @ApiModelProperty(value = "合计收益")
    private BigDecimal totalIncome = BigDecimal.ZERO;

    /**
     * 合计度电收益
     */
    @ApiModelProperty(value = "合计度电收益")
    private BigDecimal totalKwhIncome = BigDecimal.ZERO;

    /**
     * 日期列表
     */
    @ApiModelProperty(value = "日期列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 度电收益列表
     */
    @ApiModelProperty(value = "度电收益列表")
    private List<BigDecimal> kwhIncomeList = Lists.newArrayList();

}
