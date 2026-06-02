package com.sunmax.together.dto.operation.operationAnalysis;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "OverviewDto", description = "运营总览封装实体类")
public class OverviewDto {

    /**
     * 充电订单金额(元)
     */
    @ApiModelProperty(value = "充电订单金额(元)")
    private BigDecimal chargeOrderMoney = BigDecimal.ZERO;

    /**
     * 充电实付金额(元)
     */
    @ApiModelProperty(value = "充电实付金额(元)")
    private BigDecimal chargePayMoney = BigDecimal.ZERO;

    /**
     * 充电电量(度)
     */
    @ApiModelProperty(value = "充电电量(度)")
    private Double chargeOrderQt = 0.0;

    /**
     * 充电订单数量(笔)
     */
    @ApiModelProperty(value = "充电订单数量(笔)")
    private Integer chargeOrderNum = 0;

    /**
     * V2G订单金额(元)
     */
    @ApiModelProperty(value = "V2G订单金额(元)")
    private BigDecimal dischargeOrderMoney = BigDecimal.ZERO;

    /**
     * V2G放电电量(度)
     */
    @ApiModelProperty(value = "V2G放电电量(度)")
    private Double dischargeOrderQt = 0.0;

    /**
     * 枪均电量(度)
     */
    @ApiModelProperty(value = "枪均电量(度)")
    private Double avgChargeQt;

    /**
     * 时间利用率(%)
     */
    @ApiModelProperty(value = "时间利用率(%)")
    private Double timeRatio;

    /**
     * 充电时长(小时)
     */
    @ApiModelProperty(value = "充电时长(小时)")
    private Double chargeDuration = 0.0;

    /**
     * 度均服务费(元)
     */
    @ApiModelProperty(value = "度均服务费(元)")
    private BigDecimal avgChargeFee;

    /**
     * 功率利用率(%)
     */
    @ApiModelProperty(value = "功率利用率(%)")
    private Double powerRatio;

    /**
     * 一次充电成功率(%)
     */
    @ApiModelProperty(value = "一次充电成功率(%)")
    private Double chargeSuccessRatio;

}
