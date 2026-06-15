package com.sunmax.common.vo.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "订单更新参数实体类")
public class OrderUpdateVo {

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNum;

    /**
     * 订单状态 0-未进行 1-充放电中 2-充放电完成 3-启动失败 4-充放电异常 5-订单取消 7-预约中
     */
    @Schema(description = "订单状态 0-未进行 1-充放电中 2-充放电完成 3-启动失败 4-充放电异常 5-订单取消 7-预约中")
    private Integer orderStatus;

    /**
     * 结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功
     */
    @Schema(description = "结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功")
    private Integer settlementState;

    /**
     * 分账状态 1-分账关闭 2-分账失败 3-分账成功
     */
    @Schema(description = "分账状态 1-分账关闭 2-分账失败 3-分账成功")
    private Integer subAccountState;

    /**
     * 异常码
     */
    @Schema(description = "异常码")
    private String abnormalCode;

    /**
     * 本次充/放电总电量
     */
    @Schema(description = "本次充/放电总电量")
    @Builder.Default
    private Double totalQt = 0.0;

    /**
     * 本次充/放电总费用
     */
    @Schema(description = "本次充/放电总费用")
    @Builder.Default
    private BigDecimal totalCost = new BigDecimal("0.0");

    /**
     * 实付金额
     */
    @Schema(description = "实付金额")
    @Builder.Default
    private BigDecimal actualTotalCost = new BigDecimal("0.0");

    /**
     * 实付电费
     */
    @Schema(description = "实付电费")
    @Builder.Default
    private BigDecimal actualTotalElect = new BigDecimal("0.0");

    /**
     * 实付服务费
     */
    @Schema(description = "实付服务费")
    @Builder.Default
    private BigDecimal actualTotalFee = new BigDecimal("0.0");

    /**
     * 电费减免
     */
    @Schema(description = "电费减免")
    @Builder.Default
    private BigDecimal totalElectReduction = new BigDecimal("0.0");

    /**
     * 服务费减免
     */
    @Schema(description = "服务费减免")
    @Builder.Default
    private BigDecimal totalFeeReduction = new BigDecimal("0.0");

    /**
     * 退款金额
     */
    @Schema(description = "退款金额")
    @Builder.Default
    private BigDecimal refundMoney = new BigDecimal("0.0");

}
