package com.sunmax.webapp.dto;

import com.google.gson.annotations.SerializedName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class WechatTransferOrderDto {

    /**
     * 商户单号
     */
    @SerializedName("mch_id")
    @ApiModelProperty(value = "商户id")
    private String mchId;

    /**
     * 商户单号
     */
    @SerializedName("out_bill_no")
    @ApiModelProperty(value = "商户单号")
    private String outBillNo;

    /**
     * 微信转账单号
     */
    @SerializedName("transfer_bill_no")
    @ApiModelProperty(value = "微信转账单号")
    private String transferBillNo;

    /**
     * 小程序id
     */
    @SerializedName("appid")
    @ApiModelProperty(value = "小程序id")
    private String appid;

    /**
     * 转账单据状态
     * ACCEPTED:  转账已受理
     * PROCESSING:  转账锁定资金中。如果一直停留在该状态，建议检查账户余额是否足够，如余额不足，可充值后再原单重试。
     * WAIT_USER_CONFIRM:  待收款用户确认，可拉起微信收款确认页面进行收款确认
     * TRANSFERING:  转账中，可拉起微信收款确认页面再次重试确认收款
     * SUCCESS:  转账成功
     * FAIL:  转账失败
     * CANCELING:  商户撤销请求受理成功，该笔转账正在撤销中
     * CANCELLED:  转账撤销完成
     */
    @SerializedName("state")
    @ApiModelProperty(value = "转账单据状态")
    private String state;

    /**
     * 转账金额 转账金额单位为“分”。
     */
    @SerializedName("transfer_amount")
    @ApiModelProperty(value = "转账金额")
    private Long transferAmount;

    /**
     * 转账备注 单条转账备注（微信用户会收到该备注），UTF8编码，最多允许32个字符
     */
    @SerializedName("transfer_remark")
    @ApiModelProperty(value = "转账备注")
    private String transferRemark;

    /**
     * 失败原因 订单已失败或者已退资金时，会返回订单失败原因
     */
    @SerializedName("fail_reason")
    @ApiModelProperty(value = "失败原因")
    private String failReason;

    /**
     * 收款用户OpenID 用户在商户appid下的唯一标识。发起转账前需获取到用户的OpenID
     */
    @SerializedName("openid")
    @ApiModelProperty(value = "收款用户OpenID")
    private String openid;

    /**
     * 收款用户姓名
     * 收款方真实姓名。支持标准RSA算法和国密算法，公钥由微信侧提供转账金额 >= 2,000元时，该笔明细必须填写若商户传入收款用户姓名，微信支付会校验用户OpenID与姓名是否一致，并提供电子回单
     */
    @SerializedName("user_name")
    @ApiModelProperty(value = "收款用户姓名")
    private String userName;

    /**
     * 转账单据创建时间
     */
    @SerializedName("create_time")
    @ApiModelProperty(value = "转账单据创建时间")
    private String createTime;

    /**
     * 转账单据修改时间
     */
    @SerializedName("update_time")
    @ApiModelProperty(value = "转账单据修改时间")
    private String updateTime;

}
