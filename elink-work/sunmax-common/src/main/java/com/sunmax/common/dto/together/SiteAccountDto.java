package com.sunmax.common.dto.together;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteAccountDto", description = "站点账户返回实体类")
public class SiteAccountDto {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private String tenantId;

    /**
     * 支付平台 1-微信
     */
    @ApiModelProperty(value = "支付平台 1-微信")
    private Integer payPlatform;

    /**
     * 租户账号id
     */
    @ApiModelProperty(value = "租户账号id")
    private String accountId;

    /**
     * 类型 1-收款账户 2-付款账户 3-分帐账户
     */
    @ApiModelProperty(value = "类型 1-收款账户 2-付款账户 3-分帐账户")
    private Integer type;

    /**
     * 比例(%)
     */
    @ApiModelProperty(value = "比例(%)")
    private Double ratio;

    /**
     * 商户号
     */
    @ApiModelProperty(value = "商户号")
    private String mchId;

    /**
     * 商户名称
     */
    @ApiModelProperty(value = "商户名称")
    private String mchName;

    /**
     * 商户密钥
     */
    @ApiModelProperty(value = "商户密钥")
    private String mchKey;

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
