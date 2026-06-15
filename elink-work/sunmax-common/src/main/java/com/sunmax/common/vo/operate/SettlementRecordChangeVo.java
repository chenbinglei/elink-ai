package com.sunmax.common.vo.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "结算记录编辑信息参数")
public class SettlementRecordChangeVo {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 所属订单记录id
     */
    @Schema(description = "所属订单记录id")
    private String orderId;

    /**
     * 结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功
     */
    @Schema(description = "结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功")
    private Integer settlementState;

    /**
     * 支付方式 1-免支付 2-微信支付 3-支付宝支付 4-钱包余额
     */
    @Schema(description = "支付方式 1-免支付 2-微信支付 3-支付宝支付 4-钱包余额")
    private Integer payWay;

    /**
     * 实付金额
     */
    @Schema(description = "实付金额")
    private BigDecimal actualTotalCost = new BigDecimal("0.0");

    /**
     * 实付电费
     */
    @Schema(description = "实付电费")
    private BigDecimal actualTotalElect = new BigDecimal("0.0");

    /**
     * 实付服务费
     */
    @Schema(description = "实付服务费")
    private BigDecimal actualTotalFee = new BigDecimal("0.0");

    /**
     * 电费减免
     */
    @Schema(description = "电费减免")
    private BigDecimal totalElectReduction = new BigDecimal("0.0");

    /**
     * 服务费减免
     */
    @Schema(description = "服务费减免")
    private BigDecimal totalFeeReduction = new BigDecimal("0.0");

    /**
     * 退款金额
     */
    @Schema(description = "退款金额")
    private BigDecimal refundMoney = new BigDecimal("0.0");
}
