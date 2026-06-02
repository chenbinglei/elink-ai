package com.sunmax.common.vo.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "OrderUpdateVo", description = "订单更新参数实体类")
public class OrderUpdateVo {

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNum;

    /**
     * 订单状态 0-未进行 1-充放电中 2-充放电完成 3-启动失败 4-充放电异常 5-订单取消 7-预约中
     */
    @ApiModelProperty(value = "订单状态 0-未进行 1-充放电中 2-充放电完成 3-启动失败 4-充放电异常 5-订单取消 7-预约中")
    private Integer orderStatus;

    /**
     * 结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功
     */
    @ApiModelProperty(value = "结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功")
    private Integer settlementState;

    /**
     * 分账状态 1-分账关闭 2-分账失败 3-分账成功
     */
    @ApiModelProperty(value = "分账状态 1-分账关闭 2-分账失败 3-分账成功")
    private Integer subAccountState;

    /**
     * 异常码
     */
    @ApiModelProperty(value = "异常码")
    private String abnormalCode;

    /**
     * 本次充/放电总电量
     */
    @ApiModelProperty(value = "本次充/放电总电量")
    @Builder.Default
    private Double totalQt = 0.0;

    /**
     * 本次充/放电总费用
     */
    @ApiModelProperty(value = "本次充/放电总费用")
    @Builder.Default
    private BigDecimal totalCost = new BigDecimal("0.0");

    /**
     * 实付金额
     */
    @ApiModelProperty(value = "实付金额")
    @Builder.Default
    private BigDecimal actualTotalCost = new BigDecimal("0.0");

    /**
     * 实付电费
     */
    @ApiModelProperty(value = "实付电费")
    @Builder.Default
    private BigDecimal actualTotalElect = new BigDecimal("0.0");

    /**
     * 实付服务费
     */
    @ApiModelProperty(value = "实付服务费")
    @Builder.Default
    private BigDecimal actualTotalFee = new BigDecimal("0.0");

    /**
     * 电费减免
     */
    @ApiModelProperty(value = "电费减免")
    @Builder.Default
    private BigDecimal totalElectReduction = new BigDecimal("0.0");

    /**
     * 服务费减免
     */
    @ApiModelProperty(value = "服务费减免")
    @Builder.Default
    private BigDecimal totalFeeReduction = new BigDecimal("0.0");

    /**
     * 退款金额
     */
    @ApiModelProperty(value = "退款金额")
    @Builder.Default
    private BigDecimal refundMoney = new BigDecimal("0.0");

}
