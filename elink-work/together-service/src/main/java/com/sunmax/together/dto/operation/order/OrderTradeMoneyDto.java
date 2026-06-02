package com.sunmax.together.dto.operation.order;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "OrderTradeMoneyDto", description = "订单交易金额返回实体类")
public class OrderTradeMoneyDto {

    /**
     * 预付金额
     */
    @ApiModelProperty(value = "预付金额")
    private BigDecimal prepayMoney;

    /**
     * 已退款金额
     */
    @ApiModelProperty(value = "已退款金额")
    private BigDecimal refundMoney;

    /**
     * 最多可退金额
     */
    @ApiModelProperty(value = "最多可退金额")
    private BigDecimal maxRefundMoney;

}
