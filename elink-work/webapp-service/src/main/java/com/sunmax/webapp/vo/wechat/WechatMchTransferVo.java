package com.sunmax.webapp.vo.wechat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "WechatMchTransferVo", description = "微信商户转账参数实体类")
public class WechatMchTransferVo {

    /**
     * 用户唯一标识
     */
    @ApiModelProperty(value = "用户唯一标识")
    private String openid;

    /**
     * 商家转账订单编号
     */
    @ApiModelProperty(value = "商家转账订单编号")
    private String transferNum;

    /**
     * 商家转账订单金额
     */
    @ApiModelProperty(value = "商家转账订单金额")
    private BigDecimal transferMoney;

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

}
