package com.sunmax.together.vo.operation.orderRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 平台订单人工退款 参数实体类
 */
@Data
@Schema(description = "平台订单人工退款返回实体类")
public class OrderRefundVo {

    /**
     * 订单id
     */
    @Schema(description = "订单id")
    private String orderId;

    /**
     * 交易订单类型 1-充放电订单 2-占用订单
     */
    @Schema(description = "交易订单类型 1-充放电订单 2-占用订单")
    private Integer tradeOrderType;

    /**
     * 本次退款金额
     */
    @Schema(description = "本次退款金额")
    private BigDecimal refundMoney;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

}
