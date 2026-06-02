package com.sunmax.common.vo;

import lombok.Data;

/**
 * 订单序列号常量
 */
@Data
public class OrderSerialParamVo {

    /**
     * 支付序列号
     */
    public static final String PAY_NUMBER = "001";

    /**
     * 退款序列号
     */
    public static final String REFUND_NUMBER = "101";

    /**
     * 分账序列号
     */
    public static final String SUB_ACCOUNT_NUMBER = "201";

    /**
     * 占用序列号
     */
    public static final String HOLDER_NUMBER = "301";

    /**
     * 平台策略序列号
     */
    public static final String PLATFORM_STRATEGY_NUMBER = "401";

    /**
     * 转账序列号
     */
    public static final String TRANSFER_NUMBER = "501";

}
