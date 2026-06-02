package com.sunmax.webapp.vo.wechat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "WechatTransferOrderVo", description = "微信转账订单入参实体类")
public class WechatTransferOrderVo {

    /**
     * 商家转账订单编号
     */
    @ApiModelProperty(value = "商家转账订单编号")
    private String transferNum;

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
