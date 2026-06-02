package com.sunmax.common.vo.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "SettlementRecordChangeVo", description = "结算记录编辑信息参数")
public class SettlementRecordChangeVo {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private String id;

    /**
     * 所属订单记录id
     */
    @ApiModelProperty("所属订单记录id")
    private String orderId;

    /**
     * 结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功
     */
    @ApiModelProperty("结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功")
    private Integer settlementState;

    /**
     * 支付方式 1-免支付 2-微信支付 3-支付宝支付 4-钱包余额
     */
    @ApiModelProperty("支付方式 1-免支付 2-微信支付 3-支付宝支付 4-钱包余额")
    private Integer payWay;

    /**
     * 实付金额
     */
    @ApiModelProperty("实付金额")
    private BigDecimal actualTotalCost = new BigDecimal("0.0");

    /**
     * 实付电费
     */
    @ApiModelProperty("实付电费")
    private BigDecimal actualTotalElect = new BigDecimal("0.0");

    /**
     * 实付服务费
     */
    @ApiModelProperty("实付服务费")
    private BigDecimal actualTotalFee = new BigDecimal("0.0");

    /**
     * 电费减免
     */
    @ApiModelProperty("电费减免")
    private BigDecimal totalElectReduction = new BigDecimal("0.0");

    /**
     * 服务费减免
     */
    @ApiModelProperty("服务费减免")
    private BigDecimal totalFeeReduction = new BigDecimal("0.0");

    /**
     * 退款金额
     */
    @ApiModelProperty("退款金额")
    private BigDecimal refundMoney = new BigDecimal("0.0");
}
