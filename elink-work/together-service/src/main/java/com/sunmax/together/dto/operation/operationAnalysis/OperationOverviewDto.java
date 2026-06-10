package com.sunmax.together.dto.operation.operationAnalysis;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "运营总览返回实体类")
public class OperationOverviewDto {

    /**
     * 充电订单金额(元)
     */
    @Schema(description = "充电订单金额(元)")
    private BigDecimal chargeOrderMoney = BigDecimal.ZERO;

    /**
     * 充电订单金额比例(%)
     */
    @Schema(description = "充电订单金额比例(%)")
    private Double chargeOrderMoneyRatio;

    /**
     * 充电实付金额(元)
     */
    @Schema(description = "充电实付金额(元)")
    private BigDecimal chargePayMoney = BigDecimal.ZERO;

    /**
     * 充电实付金额比例(%)
     */
    @Schema(description = "充电实付金额比例(%)")
    private Double chargePayMoneyRatio;

    /**
     * 充电电量(度)
     */
    @Schema(description = "充电电量(度)")
    private Double chargeOrderQt = 0.0;

    /**
     * 充电电量比例
     */
    @Schema(description = "充电电量比例")
    private Double chargeOrderQtRatio;

    /**
     * 充电订单数量(笔)
     */
    @Schema(description = "充电订单数量(笔)")
    private Integer chargeOrderNum = 0;

    /**
     * 充电订单数量比例(%)
     */
    @Schema(description = "充电订单数量比例(%)")
    private Double chargeOrderNumRatio;

    /**
     * V2G订单金额(元)
     */
    @Schema(description = "V2G订单金额(元)")
    private BigDecimal dischargeOrderMoney = BigDecimal.ZERO;

    /**
     * V2G订单金额比例(%)
     */
    @Schema(description = "V2G订单金额比例(%)")
    private Double dischargeOrderMoneyRatio;

    /**
     * V2G放电电量(度)
     */
    @Schema(description = "V2G放电电量(度)")
    private Double dischargeOrderQt = 0.0;

    /**
     * V2G放电电量比例(%)
     */
    @Schema(description = "V2G放电电量比例(%)")
    private Double dischargeOrderQtRatio;

    /**
     * 枪均电量(度)
     */
    @Schema(description = "枪均电量(度)")
    private Double avgChargeQt;

    /**
     * 枪均电量比例(%)
     */
    @Schema(description = "枪均电量比例(%)")
    private Double avgChargeQtRatio;

    /**
     * 时间利用率(%)
     */
    @Schema(description = "时间利用率(%)")
    private Double timeRatio;

    /**
     * 时间利用率比例(%)
     */
    @Schema(description = "时间利用率比例(%)")
    private Double timeRatioRatio;

    /**
     * 充电时长(小时)
     */
    @Schema(description = "充电时长(小时)")
    private Double chargeDuration = 0.0;

    /**
     * 充电时长比例(%)
     */
    @Schema(description = "充电时长比例(%)")
    private Double chargeDurationRatio;

    /**
     * 度均服务费(元)
     */
    @Schema(description = "度均服务费(元)")
    private BigDecimal avgChargeFee;

    /**
     * 度均服务费比例(%)
     */
    @Schema(description = "度均服务费比例(%)")
    private Double avgChargeFeeRatio;

    /**
     * 功率利用率(%)
     */
    @Schema(description = "功率利用率(%)")
    private Double powerRatio;

    /**
     * 功率利用率比例(%)
     */
    @Schema(description = "功率利用率比例(%)")
    private Double powerRatioRatio;

    /**
     * 一次充电成功率(%)
     */
    @Schema(description = "一次充电成功率(%)")
    private Double chargeSuccessRatio;

    /**
     * 一次充电成功率比例(%)
     */
    @Schema(description = "一次充电成功率比例(%)")
    private Double chargeSuccessRatioRatio;

}
