package com.sunmax.together.dto.operation.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单交易金额返回实体类")
public class OrderTradeMoneyDto {

    /**
     * 预付金额
     */
    @Schema(description = "预付金额")
    private BigDecimal prepayMoney;

    /**
     * 已退款金额
     */
    @Schema(description = "已退款金额")
    private BigDecimal refundMoney;

    /**
     * 最多可退金额
     */
    @Schema(description = "最多可退金额")
    private BigDecimal maxRefundMoney;

}
