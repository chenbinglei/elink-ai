package com.sunmax.common.vo.together;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单新增参数实体类")
public class OrderChangeVo {

    /**
     * 所属站点id
     */
    @Schema(description = "所属站点id")
    private String siteId;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNum;

    /**
     * 是否有序 1-是 2-否
     */
    @Schema(description = "是否有序 1-是 2-否")
    private Integer isOrderly;

    /**
     * 预付金额
     */
    @Schema(description = "预付金额")
    private BigDecimal prepayMoney;

    /**
     * 运行模式 0-充电订单 1-放电订单
     */
    @Schema(description = "运行模式 0-充电订单 1-放电订单")
    private Integer runMode;

    /**
     * 订单状态 0-未进行 1-充放电中 2-充放电完成 3-启动失败 4-充放电异常 5-订单取消 7-预约中
     */
    @Schema(description = "订单状态 0-未进行 1-充放电中 2-充放电完成 3-启动失败 4-充放电异常 5-订单取消 7-预约中")
    private Integer orderStatus;

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
     * 账号类型 1-充/放电卡ID 2-VIN码 3-手机号
     */
    @Schema(description = "账号类型 1-充/放电卡ID 2-VIN码 3-手机号")
    private Integer accountType;

    /**
     * 账号数据
     */
    @Schema(description = "账号数据")
    private String accountData;

    /**
     * 充电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @Schema(description = "充电枪编号")
    private Integer gunCode;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-离线卡启动
     */
    @Schema(description = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-离线卡启动")
    private Integer starter;

    /**
     * 来源平台标识
     */
    @Schema(description = "来源平台标识")
    private String platformLogo;

    /**
     * 策略类型 0-满充/放空 1-定SOC 2-定金额 3-定电量
     */
    @Schema(description = "策略类型 0-满充/放空 1-定SOC 2-定金额 3-定电量")
    private Integer strategyType;

    /**
     * 策略启动时间
     */
    @Schema(description = "策略启动时间")
    private String strategyTime;

    /**
     * 定量策略值
     *      *  策略类型 1：0～100      精度 1%；
     *      *  策略类型 2：0～100000   精度 0.001 元
     *      *  策略类型 3：0～100000   精度 0.001kW·h
     *
     */
    @Schema(description = "定量策略值")
    private Double strategyCfg;

    /**
     * 结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功
     */
    @Schema(description = "结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功")
    private Integer settlementState;

    /**
     * 支付方式 1-免支付 2-微信支付 3-支付宝支付 4-放电钱包支付
     */
    @Schema(description = "支付方式 1-免支付 2-微信支付 3-支付宝支付 4-放电钱包支付")
    private Integer payWay;

    /**
     * 费率模型Id
     */
    @Schema(description = "费率模型Id")
    private String rateTemplateId;

}
