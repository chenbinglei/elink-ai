package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 企业账户信息新增参数实体类
 */
@Data
@ApiModel("AccountVo")
public class AccountVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联租户id
     */
    @ApiModelProperty(value = "关联租户id", required = true)
    private String tenantId;

    /**
     * 平台类型 1-微信平台 2-支付宝平台
     */
    @ApiModelProperty(value = "平台类型 1-微信平台 2-支付宝平台", required = true)
    private Integer platformType;

    /**
     * 商户类型 1-商户号 2-个人openid
     */
    @ApiModelProperty(value = "商户类型 1-商户号 2-个人openid", required = true)
    private Integer mchType;

    /**
     * 商户号
     */
    @ApiModelProperty(value = "商户号", required = true)
    private String mchId;

    /**
     * 商户名称
     */
    @ApiModelProperty(value = "商户名称", required = true)
    private String mchName;

    /**
     * 商户密钥
     */
    @ApiModelProperty(value = "商户密钥", required = true)
    private String mchKey;

    /**
     * API类型 1-平台证书 2-微信支付公钥
     */
    @ApiModelProperty(value = "API类型 1-平台证书 2-微信支付公钥", required = true)
    private Integer apiType;

    /**
     * APIv3密钥
     */
    @ApiModelProperty(value = "APIv3密钥", required = true)
    private String apiV3Key;

    /**
     * 商户证书序列号
     */
    @ApiModelProperty(value = "商户证书序列号", required = true)
    private String serialNo;

    /**
     * 平台RSA证书序列号(商户公钥id)
     */
    @ApiModelProperty(value = "平台RSA证书序列号(商户公钥id)", required = true)
    private String rsaSerialNo;

}
