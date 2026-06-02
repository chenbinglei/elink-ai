package com.sunmax.webapp.vo.wechat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WechatPayVo {

    /**
     * 用户唯一标识
     */
    @ApiModelProperty(value = "用户唯一标识")
    private String openid;

    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    private String orderNum;

    /**
     * 订单金额
     */
    @ApiModelProperty(value = "订单金额")
    private BigDecimal orderMoney;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String describe;

    /**
     * 小程序id
     */
    @ApiModelProperty(value = "小程序id")
    private String appletCode;

    /**
     * 商户号
     */
    @ApiModelProperty(value = "商户号")
    private String mchId;

    /**
     * API类型 1-平台证书 2-微信支付公钥
     */
    @ApiModelProperty(value = "API类型 1-平台证书 2-微信支付公钥")
    private Integer apiType;

    /**
     * APIv3密钥
     */
    @ApiModelProperty(value = "APIv3密钥")
    private String apiV3Key;

    /**
     * 商户证书序列号
     */
    @ApiModelProperty(value = "商户证书序列号")
    private String serialNo;

    /**
     * 商户key路径
     */
    @ApiModelProperty(value = "商户key路径")
    private String keyPemPath;

    /**
     * 平台RSA证书序列号(商户公钥id)
     */
    @ApiModelProperty(value = "平台RSA证书序列号(商户公钥id)")
    private String rsaSerialNo;

    /**
     * 商户公钥路径(pub_key.pem)
     */
    @ApiModelProperty(value = "商户公钥路径(pub_key.pem)")
    private String pubKeyPath;

}
