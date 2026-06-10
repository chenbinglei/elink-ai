package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业账户信息新增参数实体类
 */
@Data
@Schema(description = "AccountVo")
public class AccountVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 关联租户id
     */
    @Schema(description = "关联租户id")
    private String tenantId;

    /**
     * 平台类型 1-微信平台 2-支付宝平台
     */
    @Schema(description = "平台类型 1-微信平台 2-支付宝平台")
    private Integer platformType;

    /**
     * 商户类型 1-商户号 2-个人openid
     */
    @Schema(description = "商户类型 1-商户号 2-个人openid")
    private Integer mchType;

    /**
     * 商户号
     */
    @Schema(description = "商户号")
    private String mchId;

    /**
     * 商户名称
     */
    @Schema(description = "商户名称")
    private String mchName;

    /**
     * 商户密钥
     */
    @Schema(description = "商户密钥")
    private String mchKey;

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
     * 平台RSA证书序列号(商户公钥id)
     */
    @Schema(description = "平台RSA证书序列号(商户公钥id)")
    private String rsaSerialNo;

}
