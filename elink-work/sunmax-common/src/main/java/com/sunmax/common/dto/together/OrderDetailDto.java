package com.sunmax.common.dto.together;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "订单记录详情返回实体类")
public class OrderDetailDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNum;

    /**
     * 运营商名称
     */
    @Schema(description = "运营商名称")
    private String operateUnitName;

    /**
     * 运营商id
     */
    @Schema(description = "运营商id")
    private String operateUnitId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 站点位置信息
     */
    @Schema(description = "站点位置信息")
    private String siteLocation;

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
     * 创建时间
     */
    @Schema(description = "创建时间")
    private String createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private String updateTime;

    /**
     * 订单状态 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 7-预约中
     */
    @Schema(description = "订单状态 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 7-预约中")
    private Integer orderStatus;

    /**
     * 充放电方式 0-立即充电 1-定时充电 2-自动充电
     */
    @Schema(description = "充放电方式 0-立即充电 1-定时充电 2-自动充电")
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
     * 卡面号
     */
    @Schema(description = "卡面号")
    private String cardNumber;

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
     * 策略类型  0 满充/放空；1 定 SOC; 2 定金额；3 定电量
     */
    @Schema(description = "策略类型  0 满充/放空；1 定 SOC; 2 定金额；3 定电量")
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
     * 开始充/放电时间
     */
    @Schema(description = "开始充/放电时间")
    private String startTime;

    /**
     * 结束充/放电时间
     */
    @Schema(description = "结束充/放电时间")
    private String endTime;

    /**
     * 本次充/放电总电量
     */
    @Schema(description = "本次充/放电总电量")
    private Double totalQt = 0.0;

    /**
     * 本次充/放电总费用
     */
    @Schema(description = "本次充/放电总费用")
    private BigDecimal totalCost = new BigDecimal("0.0");

    /**
     * 本次充/放电总电费
     */
    @Schema(description = "本次充/放电总电费")
    private BigDecimal totalElect = new BigDecimal("0.0");

    /**
     * 本次充/放电总服务费
     */
    @Schema(description = "本次充/放电总服务费")
    private BigDecimal totalFee = new BigDecimal("0.0");

    /**
     * 本次充/放电尖时总电量
     */
    @Schema(description = "本次充/放电尖时总电量")
    private Double jQt = 0.0;

    /**
     * 本次充/放电尖时总电费
     */
    @Schema(description = "本次充/放电尖时总电费")
    private BigDecimal jElect = new BigDecimal("0.0");

    /**
     * 本次充/放电尖时总服务费
     */
    @Schema(description = "本次充/放电尖时总服务费")
    private BigDecimal jFee = new BigDecimal("0.0");

    /**
     * 本次充/放电峰时总电量
     */
    @Schema(description = "本次充/放电峰时总电量")
    private Double fQt = 0.0;

    /**
     * 本次充/放电峰时总电费
     */
    @Schema(description = "本次充/放电峰时总电费")
    private BigDecimal fElect = new BigDecimal("0.0");

    /**
     * 本次充/放电峰时总服务费
     */
    @Schema(description = "本次充/放电峰时总服务费")
    private BigDecimal fFee = new BigDecimal("0.0");

    /**
     * 本次充/放电平时总电量
     */
    @Schema(description = "本次充/放电平时总电量")
    private Double pQt = 0.0;

    /**
     * 本次充/放电平时总电费
     */
    @Schema(description = "本次充/放电平时总电费")
    private BigDecimal pElect = new BigDecimal("0.0");

    /**
     * 本次充/放电平时总服务费
     */
    @Schema(description = "本次充/放电平时总服务费")
    private BigDecimal pFee = new BigDecimal("0.0");

    /**
     * 本次充/放电谷时总电量
     */
    @Schema(description = "本次充/放电谷时总电量")
    private Double gQt = 0.0;

    /**
     * 本次充/放电谷时总电费
     */
    @Schema(description = "本次充/放电谷时总电费")
    private BigDecimal gElect = new BigDecimal("0.0");

    /**
     * 本次充/放电谷时总服务费
     */
    @Schema(description = "本次充/放电谷时总服务费")
    private BigDecimal gFee = new BigDecimal("0.0");

    /**
     * 开始 SOC
     */
    @Schema(description = "开始 SOC")
    private Integer startSoc;

    /**
     * 结束 SOC
     */
    @Schema(description = "结束 SOC")
    private Integer endSoc;

    /**
     * 停止码
     */
    @Schema(description = "停止码")
    private String stopReason;

    /**
     * 停止详细原因
     */
    @Schema(description = "停止详细原因")
    private String stopDetailReason;

    /**
     * 车量 vin 码
     */
    @Schema(description = "车量 vin 码")
    private String busVin;

    /**
     * 费率模型ID
     */
    @Schema(description = "费率模型ID")
    private String rateTemplateId;

    /**
     * 有效时段总数 取值 0～48
     */
    @Schema(description = "有效时段总数 取值 0～48")
    private Integer timeFrameNum;

    /**
     * 关联时段表id
     */
    @Schema(description = "关联时段表id")
    private String timeFrameId;

    /**
     * 充电时长
     */
    @Schema(description = "充电时长")
    private String chargeDuration;

    /**
     * 平台名称
     */
    @Schema(description = "平台名称")
    private String platformName;

    /**
     * 电流曲线列表
     */
    @Schema(description = "电流曲线列表")
    private List<Double> currentList = Lists.newArrayList();

    /**
     * 电压曲线列表
     */
    @Schema(description = "电压曲线列表")
    private List<Double> voltageList = Lists.newArrayList();

    /**
     * 功率曲线列表
     */
    @Schema(description = "功率曲线列表")
    private List<Double> powerList = Lists.newArrayList();

    /**
     * x轴时间列表
     */
    @Schema(description = "x轴时间列表")
    private List<String> xAxisList = Lists.newArrayList();

    /**
     * 计费详情列表
     */
    @Schema(description = "计费详情列表")
    private List<OrderDetailDto.ChargingDetails> chargingDetailsList;

    /**
     * 结算记录详情
     */
    @Schema(description = "结算记录详情")
    private OrderDetailDto.SettlementRecord settlementRecord;

    /**
     * 计费详情信息
     */
    @Data
    public static class ChargingDetails {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

        /**
         * 时段电价
         */
        @Schema(description = "时段电价")
        private BigDecimal electPrice;

        /**
         * 时段服务费价
         */
        @Schema(description = "时段服务费价")
        private BigDecimal servicePrice;

        /**
         * 充/放电量
         */
        @Schema(description = "充/放电量")
        private Double rechargeQt;

        /**
         * 服务费
         */
        @Schema(description = "服务费")
        private BigDecimal electMoney;

        /**
         * 服务费
         */
        @Schema(description = "服务费")
        private BigDecimal serviceMoney;

        /**
         * 充电时长（分钟）
         */
        @Schema(description = "充电时长（分钟）")
        private Double chargeDuration;

        /**
         * 时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天
         */
        @Schema(description = "时段类型 1-尖时 2-峰时 3-平时 4-谷时 6-全天")
        private Integer periodType;
    }

    /**
     * 结算记录信息
     */
    @Data
    public static class SettlementRecord {

        /**
         * 主键id
         */
        @Schema(description = "主键id")
        private String id;

        /**
         * 结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功
         */
        @Schema(description = "结算状态 0-未结算 1-结算关闭 2-结算失败 3-结算成功")
        private Integer settlementState;

        /**
         * 支付方式 1-免支付 2-微信支付 3-支付宝支付 4-钱包余额
         */
        @Schema(description = "支付方式 1-免支付 2-微信支付 3-支付宝支付 4-钱包余额")
        private Integer payWay;

        /**
         * 实付金额
         */
        @Schema(description = "实付金额")
        private BigDecimal actualTotalCost;

        /**
         * 实付电费
         */
        @Schema(description = "实付电费")
        private BigDecimal actualTotalElect;

        /**
         * 实付服务费
         */
        @Schema(description = "实付服务费")
        private BigDecimal actualTotalFee;

        /**
         * 电费减免
         */
        @Schema(description = "电费减免")
        private BigDecimal totalElectReduction;

        /**
         * 服务费减免
         */
        @Schema(description = "服务费减免")
        private BigDecimal totalFeeReduction;

        /**
         * 退款金额
         */
        @Schema(description = "退款金额")
        private BigDecimal refundMoney;
    }
}
