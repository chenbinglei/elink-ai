package com.sunmax.devops.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "SitePileCurveDataDto",description = "站点电桩概览曲线数据返回实体类")
public class SitePileCurveDataDto {

    /**
     * 电桩充电量
     */
    @ApiModelProperty(value = "电桩充电量")
    private Double chargeQt = 0.0;

    /**
     * 电桩放电量
     */
    @ApiModelProperty(value = "电桩放电量")
    private Double dischargeQt = 0.0;

    /**
     * 电桩充电金额
     */
    @ApiModelProperty(value = "电桩充电金额")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 电桩充电金额
     */
    @ApiModelProperty(value = "电桩放电金额")
    private BigDecimal dischargeMoney = BigDecimal.ZERO;

    /**
     * 电桩曲线数据1 日-(前一日功率) 月、年、总 -(电桩充电量)
     */
    @ApiModelProperty(value = "电桩曲线数据1 日-(前一日功率) 月、年、总 -(电桩充电量)")
    private List<Double> curve1List = Lists.newArrayList();

    /**
     * 电桩曲线数据2 日-(当日功率) 月、年、总 -(电桩放电量)
     */
    @ApiModelProperty(value = "电桩曲线数据2 日-(当日功率) 月、年、总 -(电桩放电量)")
    private List<Double> curve2List = Lists.newArrayList();

    /**
     * 时间列表
     */
    @ApiModelProperty(value = "时间列表")
    private List<String> timeList = Lists.newArrayList();

}
