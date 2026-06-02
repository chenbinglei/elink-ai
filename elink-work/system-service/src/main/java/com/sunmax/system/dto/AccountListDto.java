package com.sunmax.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 企业账户列表返回实体类
 */
@Data
@ApiModel("AccountListDto")
public class AccountListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联租户id
     */
    @ApiModelProperty(value = "关联租户id")
    private String tenantId;

    /**
     * 平台类型 1-微信平台 2-支付宝平台
     */
    @ApiModelProperty(value = "平台类型 1-微信平台 2-支付宝平台")
    private Integer platformType;

    /**
     * 商户类型 1-商户号 2-个人openid
     */
    @ApiModelProperty(value = "商户类型 1-商户号 2-个人openid")
    private Integer mchType;

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
     * 商户key路径
     */
    @ApiModelProperty(value = "商户key路径")
    private String keyPemPath;

    /**
     * 商户证书序列号
     */
    @ApiModelProperty(value = "商户证书序列号")
    private String serialNo;

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
