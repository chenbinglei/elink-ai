package com.sunmax.together.vo.operation.orderRecord;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 平台订单人工退款 参数实体类
 */
@Data
@ApiModel(value = "OrderRefundVo", description = "平台订单人工退款返回实体类")
public class OrderRefundVo {

    /**
     * 订单id
     */
    @ApiModelProperty(value = "订单id", required = true)
    private String orderId;

    /**
     * 交易订单类型 1-充放电订单 2-占用订单
     */
    @ApiModelProperty(value = "交易订单类型 1-充放电订单 2-占用订单", required = true)
    private Integer tradeOrderType;

    /**
     * 本次退款金额
     */
    @ApiModelProperty(value = "本次退款金额", required = true)
    private BigDecimal refundMoney;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", required = true)
    private String userId;

}
