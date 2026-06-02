package com.sunmax.webapp.vo.wechat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WechatPayOrderVo {

    /**
     * 类型 1-微信支付订单号 2-商户订单号
     */
    @ApiModelProperty(value = "类型 1-微信支付订单号 2-商户订单号")
    private Integer type;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNum;

    /**
     * 小程序id
     */
    @ApiModelProperty(value = "小程序id")
    private String appletId;

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
