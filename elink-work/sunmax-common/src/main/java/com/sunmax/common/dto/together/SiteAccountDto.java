package com.sunmax.common.dto.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "站点账户返回实体类")
public class SiteAccountDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 租户id
     */
    @Schema(description = "租户id")
    private String tenantId;

    /**
     * 支付平台 1-微信
     */
    @Schema(description = "支付平台 1-微信")
    private Integer payPlatform;

    /**
     * 租户账号id
     */
    @Schema(description = "租户账号id")
    private String accountId;

    /**
     * 类型 1-收款账户 2-付款账户 3-分帐账户
     */
    @Schema(description = "类型 1-收款账户 2-付款账户 3-分帐账户")
    private Integer type;

    /**
     * 比例(%)
     */
    @Schema(description = "比例(%)")
    private Double ratio;

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
