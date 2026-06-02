package com.sunmax.webapp.vo.wechat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "WechatRefundOrderVo", description = "微信退款订单请求参数")
public class WechatRefundOrderVo {

    /**
     * 退款订单号
     */
    @ApiModelProperty("退款订单号")
    private String refundOrderNum;

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
