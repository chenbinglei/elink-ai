package com.sunmax.webapp.vo.wechat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "微信转账订单入参实体类")
public class WechatTransferOrderVo {

    /**
     * 商家转账订单编号
     */
    @Schema(description = "商家转账订单编号")
    private String transferNum;

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
