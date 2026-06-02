package com.sunmax.webapp.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WechatPayVo {

    /**
     * 用户id
     */
    private long userId;
    /**
     * 用户唯一标识
     */
    private String openId;
    /**
     * 订单描述
     */
    private String details;
    /**
     * 订单名称
     */
    private String name;
    /**
     * 支付金额 单位为分
     */
    private BigDecimal payMoney;

}
