package com.sunmax.webapp.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "微信商户转账返回实体类")
public class WechatMchTransferDto {

    /**
     * 商户单号
     */
    @SerializedName("out_bill_no")
    @Schema(description = "商户单号")
    private String outBillNo;

    /**
     * 微信转账单号
     */
    @SerializedName("transfer_bill_no")
    @Schema(description = "微信转账单号")
    private String transferBillNo;

    /**
     * 转账单据创建时间
     */
    @SerializedName("create_time")
    @Schema(description = "转账单据创建时间")
    private String createTime;

    /**
     * 转账单据状态
     */
    @SerializedName("state")
    @Schema(description = "转账单据状态")
    private String state;

    /**
     * 跳转领取页面的package信息-跳转微信支付收款页的package信息，APP调起用户确认收款或者JSAPI调起用户确认收款 时需要使用的参数
     */
    @SerializedName("package_info")
    @Schema(description = "跳转领取页面的package信息-跳转微信支付收款页的package信息，APP调起用户确认收款或者JSAPI调起用户确认收款 时需要使用的参数")
    private String packageInfo;

}
