package com.sunmax.common.dto.webapp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "V2G钱包交易明细实体类")
public class DischargeTradeDto {

    @Schema(description = "主键id")
    private String id;

    @Schema(description = "交易订单号")
    private String orderNum;

    @Schema(description = "交易金额")
    private BigDecimal tradeMoney = new BigDecimal("0.0");

    @Schema(description = "交易类型 1-V2G收益存入 2-余额提现")
    private Integer tradeType;

    @Schema(description = "交易状态 1-处理中 2-处理成功 3-处理失败")
    private Integer tradeStatus;

    @Schema(description = "交易方式 1-微信 2-支付宝 3-银联商户")
    private Integer tradeWay;

    @Schema(description = "租户名称")
    private String tenantName;

    @Schema(description = "商户id")
    private String mchId;

    @Schema(description = "商户名称")
    private String mchName;

    @Schema(description = "小程序用户id")
    private String appletUserId;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "小程序用户手机号")
    private String phoneNum;

    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "修改时间")
    private String updateTime;

}
