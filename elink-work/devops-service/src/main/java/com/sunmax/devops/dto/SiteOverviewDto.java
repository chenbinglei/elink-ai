package com.sunmax.devops.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "SiteOverviewDto", description = "电站概览返回实体类")
public class SiteOverviewDto {

    /**
     * 运行天数
     */
    @ApiModelProperty(value = "运行天数")
    private Long runDays;

    /**
     * 最高温度
     */
    @ApiModelProperty(value = "最高温度")
    private String tempMax;

    /**
     * 最低温度
     */
    @ApiModelProperty(value = "最低温度")
    private String tempMin;

    /**
     * 白天天气状况图标标码
     */
    @ApiModelProperty(value = "白天天气状况图标标码")
    private String iconDay;

    /**
     * 变压器安全容量
     */
    @ApiModelProperty(value = "变压器安全容量")
    private Double tranSafeCap = 0.0;

    /**
     * 电网容量
     */
    @ApiModelProperty(value = "电网容量")
    private Double gridCap = 0.0;

    /**
     * 电桩容量
     */
    @ApiModelProperty(value = "电桩容量")
    private Double pileCap = 0.0;

    /**
     * 储能PCS额定功率
     */
    @ApiModelProperty(value = "储能PCS额定功率")
    private Double pcsPower = 0.0;

    /**
     * 储能电池簇额定容量
     */
    @ApiModelProperty(value = "储能电池簇额定容量")
    private Double batteryCap = 0.0;

    /**
     * 光伏容量
     */
    @ApiModelProperty(value = "光伏容量")
    private Double pvCap = 0.0;

    /**
     * 负载容量
     */
    @ApiModelProperty(value = "负载容量")
    private Double loadCap = 0.0;

    /**
     * 储能SOC
     */
    @ApiModelProperty(value = "储能SOC")
    private Double soc = 0.0;

    /**
     * 关口表今日上网电量
     */
    @ApiModelProperty(value = "今日上网电量")
    private Double dayNetQt = 0.0;

    /**
     * 关口表今日下网电量
     */
    @ApiModelProperty(value = "今日下网电量")
    private Double dayLowerQt = 0.0;

    /**
     * 光伏今日发电量
     */
    @ApiModelProperty(value = "光伏今日发电量")
    private Double dayPvQt = 0.0;

    /**
     * 光伏今日收益
     */
    @ApiModelProperty(value = "光伏今日收益")
    private BigDecimal dayPvIncome;

    /**
     * 储能今日充电量
     */
    @ApiModelProperty(value = "储能今日充电量")
    private Double daySeChargeQt = 0.0;

    /**
     * 储能今日放电量
     */
    @ApiModelProperty(value = "储能今日放电量")
    private Double daySeDischargeQt = 0.0;

    /**
     * 储能今日收益
     */
    @ApiModelProperty(value = "储能今日收益")
    private BigDecimal daySeIncome;

    /**
     * 储能累计循环次数
     */
    @ApiModelProperty(value = "储能累计循环次数")
    private Double daySeCycleNum = 0.0;

    /**
     * 电桩今日充电量
     */
    @ApiModelProperty(value = "电桩今日充电量")
    private Double dayPileChargeQt = 0.0;

    /**
     * 电桩今日放电量
     */
    @ApiModelProperty(value = "电桩今日放电量")
    private Double dayPileDischargeQt = 0.0;

    /**
     * 充电订单金额
     */
    @ApiModelProperty(value = "充电订单金额")
    private BigDecimal dayPileChargeMoney = BigDecimal.ZERO;

    /**
     * V2G订单金额
     */
    @ApiModelProperty(value = "V2G订单金额")
    private BigDecimal dayPileDischargeMoney = BigDecimal.ZERO;

}
