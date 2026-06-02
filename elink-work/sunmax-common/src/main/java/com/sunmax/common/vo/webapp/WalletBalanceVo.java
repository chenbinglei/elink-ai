package com.sunmax.common.vo.webapp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "WalletBalanceVo", description = "小程序放电钱包实体")
public class WalletBalanceVo {

    /**
     * 交易订单号
     */
    @ApiModelProperty(value = "交易订单号")
    private String orderNum;

    /**
     * 交易金额
     */
    @ApiModelProperty(value = "交易金额")
    @Builder.Default
    private BigDecimal tradeMoney = new BigDecimal("0.0");

    /**
     * 交易类型 1-V2G收益存入 2-余额提现
     */
    @ApiModelProperty(value = "交易类型 1-V2G收益存入 2-余额提现")
    private Integer tradeType;

    /**
     * 交易方式 1-微信 2-支付宝 3-银联商户
     */
    @ApiModelProperty(value = "交易方式 1-微信 2-支付宝 3-银联商户")
    private Integer tradeWay;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号")
    private String phoneNum;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

}
