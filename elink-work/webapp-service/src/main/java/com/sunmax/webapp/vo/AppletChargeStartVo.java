package com.sunmax.webapp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "小程序充放电启动参数")
public class AppletChargeStartVo {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 小程序用户id
     */
    @Schema(description = "小程序用户id")
    private String appletUserId;

//    /**
//     * 平台类型 wechat-微信小程序 alipay-支付宝小程序 app-手机App
//     */
//    @Schema(description = "平台类型 wechat-微信小程序 alipay-支付宝小程序 app-手机App")
//    private String platform;

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @Schema(description = "充电枪编号")
    private String gunCode;

    /**
     * 是否有序 1-是 2-否
     */
    @Schema(description = "是否有序 1-是 2-否")
    private Integer isOrderly;

    /**
     * 订单交易类型 1-交易订单 2-非交易订单
     */
    @Schema(description = "订单交易类型 1-交易订单 2-非交易订单")
    private Integer dealType;

    /**
     * 预付金额
     */
    @Schema(description = "预付金额")
    private BigDecimal prepayMoney;

    /**
     * 充放电方式 0-立即充放电 1-定时充放电 2-自动充放电
     */
    @Schema(description = "充放电方式 0-立即充放电 1-定时充放电 2-自动充放电")
    private Integer type;

    /**
     * 定时充放电时间
     */
    @Schema(description = "定时充放电时间")
    private String clockingTime;

    /**
     * 支付方式 1-免支付 2-微信支付 3-支付宝支付
     */
    @Schema(description = "支付方式 1-免支付 2-微信支付 3-支付宝支付")
    private Integer payWay;

    /**
     * 运行模式 0-充电订单 1-放电订单
     */
    @Schema(description = "运行模式 0-充电订单 1-放电订单")
    private Integer runMode;

    /**
     * 账号类型 1-充/放电卡 2-VIN码 3-手机号
     */
    @Schema(description = "账号类型 1-充/放电卡 2-VIN码 3-手机号")
    private Integer accountType;

    /**
     * 账号数据
     */
    @Schema(description = "账号数据")
    private String accountData;

    /**
     * 用户类型 1-个人会员 2-企业会员
     */
    @Schema(description = "用户类型 1-个人会员 2-企业会员")
    private Integer userType;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制
     */
    @Schema(description = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制")
    private Integer starter;

    /**
     * 充放电策略 0-自动充满 1-soc电量 2-金额 3-电量
     */
    @Schema(description = "充放电策略 0-自动充满 1-soc电量 2-金额 3-电量")
    private Integer strategyType;

    /**
     * 充放电策略参数 金额 电量
     */
    @Schema(description = "充放电策略参数")
    private Double strategyCfg;

    /**
     * 停止码
     */
    @Schema(description = "停止码")
    private String stopCode;

    /**
     * 来源平台标识
     */
    @Schema(description = "来源平台标识")
    private String platformLogo;

}
