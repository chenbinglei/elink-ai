package com.sunmax.webapp.vo.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "微信商户转账参数实体类")
public class WechatMchTransferVo {

    /**
     * 用户唯一标识
     */
    @Schema(description = "用户唯一标识")
    private String openid;

    /**
     * 商家转账订单编号
     */
    @Schema(description = "商家转账订单编号")
    private String transferNum;

    /**
     * 商家转账订单金额
     */
    @Schema(description = "商家转账订单金额")
    private BigDecimal transferMoney;

    /**
     * 小程序id
     */
    @Schema(description = "小程序id")
    private String appletCode;

    /**
     * 商户号
     */
    @Schema(description = "商户号")
    private String mchId;

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

}
