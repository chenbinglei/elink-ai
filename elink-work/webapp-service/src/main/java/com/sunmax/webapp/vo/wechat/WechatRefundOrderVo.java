package com.sunmax.webapp.vo.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "微信退款订单请求参数")
public class WechatRefundOrderVo {

    /**
     * 退款订单号
     */
    @Schema(description = "退款订单号")
    private String refundOrderNum;

    /**
     * 商户号
     */
    @Schema(description = "商户号")
    private String mchId;

    /**
     * API类型 1-平台证书 2-微信支付公钥
     */
    @Schema(description = "API类型 1-平台证书 2-微信支付公钥")
    private Integer apiType;

    /**
     * APIv3密钥
     */
    @Schema(description = "APIv3密钥")
    private String apiV3Key;

    /**
     * 商户证书序列号
     */
    @Schema(description = "商户证书序列号")
    private String serialNo;

    /**
     * 商户key路径
     */
    @Schema(description = "商户key路径")
    private String keyPemPath;

    /**
     * 平台RSA证书序列号(商户公钥id)
     */
    @Schema(description = "平台RSA证书序列号(商户公钥id)")
    private String rsaSerialNo;

    /**
     * 商户公钥路径(pub_key.pem)
     */
    @Schema(description = "商户公钥路径(pub_key.pem)")
    private String pubKeyPath;

}
