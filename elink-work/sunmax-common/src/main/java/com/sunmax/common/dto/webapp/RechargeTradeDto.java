package com.sunmax.common.dto.webapp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "RechargeTradeDto", description = "充电交易明细实体类")
public class RechargeTradeDto {

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "交易订单号")
    private String orderNum;

    @ApiModelProperty(value = "交易流水号")
    private String flowNum;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal tradeMoney = new BigDecimal("0.0");

    @ApiModelProperty(value = "交易类型 1-充电预付 2-充电退款")
    private Integer tradeType;

    @ApiModelProperty(value = "交易状态 1-处理中 2-处理成功 3-处理失败")
    private Integer tradeStatus;

    @ApiModelProperty(value = "交易方式 1-微信 2-支付宝 3-银联商户")
    private Integer tradeWay;

    @ApiModelProperty(value = "明细类型 1-收入 2-支出")
    private Integer detailType;

    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    @ApiModelProperty(value = "账号id")
    private String accountId;

    @ApiModelProperty(value = "商户id")
    private String mchId;

    @ApiModelProperty(value = "商户名称")
    private String mchName;

    @ApiModelProperty(value = "小程序用户id")
    private String appletUserId;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "小程序用户手机号")
    private String phoneNum;

    @ApiModelProperty(value = "站点名称")
    private String siteName;

    @ApiModelProperty(value = "支付时间")
    private String createTime;

    @ApiModelProperty(value = "支付完成时间")
    private String updateTime;

    @ApiModelProperty(value = "交易状态编码")
    private String tradeState;

    @ApiModelProperty(value = "交易状态描述")
    private String tradeStateDesc;

}
