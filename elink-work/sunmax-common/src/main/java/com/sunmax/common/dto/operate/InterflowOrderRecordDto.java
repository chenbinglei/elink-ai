package com.sunmax.common.dto.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "InterflowOrderRecordDto", description = "城市充电订单记录返回实体类")
public class InterflowOrderRecordDto {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private String orderNum;

    /**
     * 是否有序 1-是 2-否
     */
    @ApiModelProperty(value = "是否有序 1-是 2-否")
    private Integer isOrderly;

    /**
     * 预付金额
     */
    @ApiModelProperty(value = "预付金额")
    private BigDecimal prepayMoney;

    /**
     * 运行模式 0-充电订单 1-放电订单
     */
    @ApiModelProperty(value = "运行模式 0-充电订单 1-放电订单")
    private Integer runMode;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private String createTime;

    /**
     * 订单状态 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 7-预约中
     */
    @ApiModelProperty(value = "订单状态 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 7-预约中")
    private Integer orderStatus;

    /**
     * 充放电方式 0-立即充电 1-定时充电 2-自动充电
     */
    @ApiModelProperty(value = "充放电方式 0-立即充电 1-定时充电 2-自动充电")
    private Integer type;

    /**
     * 定时充放电时间
     */
    @ApiModelProperty(value = "定时充放电时间")
    private String clockingTime;

    /**
     * 账号类型 1-充/放电卡ID 2-VIN码 3-手机号
     */
    @ApiModelProperty(value = "账号类型 1-充/放电卡ID 2-VIN码 3-手机号")
    private Integer accountType;

    /**
     * 账号数据
     */
    @ApiModelProperty(value = "账号数据")
    private String accountData;

    /**
     * 卡面号
     */
    @ApiModelProperty(value = "卡面号")
    private String cardNumber;

    /**
     * 充电桩编号
     */
    @ApiModelProperty(value = "充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @ApiModelProperty(value = "充电枪编号")
    private Integer gunCode;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-离线卡启动
     */
    @ApiModelProperty(value = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-离线卡启动")
    private Integer starter;

    /**
     * 来源平台标识
     */
    @ApiModelProperty(value = "来源平台标识")
    private String platformLogo;

    /**
     * 策略类型  0 满充/放空；1 定 SOC; 2 定金额；3 定电量
     */
    @ApiModelProperty(value = "策略类型  0 满充/放空；1 定 SOC; 2 定金额；3 定电量")
    private Integer strategyType;

    /**
     * 策略启动时间
     */
    @ApiModelProperty(value = "策略启动时间")
    private String strategyTime;

    /**
     * 定量策略值
     *      *  策略类型 1：0～100      精度 1%；
     *      *  策略类型 2：0～100000   精度 0.001 元
     *      *  策略类型 3：0～100000   精度 0.001kW·h
     *
     */
    @ApiModelProperty(value = "定量策略值")
    private Double strategyCfg;

    /**
     * 开始充/放电时间
     */
    @ApiModelProperty(value = "开始充/放电时间")
    private String startTime;

    /**
     * 结束充/放电时间
     */
    @ApiModelProperty(value = "结束充/放电时间")
    private String endTime;

    /**
     * 本次充/放电总电量
     */
    @ApiModelProperty(value = "本次充/放电总电量")
    private Double totalQt = 0.0;

    /**
     * 本次充/放电总费用
     */
    @ApiModelProperty(value = "本次充/放电总费用")
    private BigDecimal totalCost = new BigDecimal("0.0");

    /**
     * 本次充/放电总电费
     */
    @ApiModelProperty(value = "本次充/放电总电费")
    private BigDecimal totalElect = new BigDecimal("0.0");

    /**
     * 本次充/放电总服务费
     */
    @ApiModelProperty(value = "本次充/放电总服务费")
    private BigDecimal totalFee = new BigDecimal("0.0");

    /**
     * 本次充/放电尖时总电量
     */
    @ApiModelProperty(value = "本次充/放电尖时总电量")
    private Double jQt = 0.0;

    /**
     * 本次充/放电尖时总电费
     */
    @ApiModelProperty(value = "本次充/放电尖时总电费")
    private BigDecimal jElect = new BigDecimal("0.0");

    /**
     * 本次充/放电尖时总服务费
     */
    @ApiModelProperty(value = "本次充/放电尖时总服务费")
    private BigDecimal jFee = new BigDecimal("0.0");

    /**
     * 本次充/放电峰时总电量
     */
    @ApiModelProperty(value = "本次充/放电峰时总电量")
    private Double fQt = 0.0;

    /**
     * 本次充/放电峰时总电费
     */
    @ApiModelProperty(value = "本次充/放电峰时总电费")
    private BigDecimal fElect = new BigDecimal("0.0");

    /**
     * 本次充/放电峰时总服务费
     */
    @ApiModelProperty(value = "本次充/放电峰时总服务费")
    private BigDecimal fFee = new BigDecimal("0.0");

    /**
     * 本次充/放电平时总电量
     */
    @ApiModelProperty(value = "本次充/放电平时总电量")
    private Double pQt = 0.0;

    /**
     * 本次充/放电平时总电费
     */
    @ApiModelProperty(value = "本次充/放电平时总电费")
    private BigDecimal pElect = new BigDecimal("0.0");

    /**
     * 本次充/放电平时总服务费
     */
    @ApiModelProperty(value = "本次充/放电平时总服务费")
    private BigDecimal pFee = new BigDecimal("0.0");

    /**
     * 本次充/放电谷时总电量
     */
    @ApiModelProperty(value = "本次充/放电谷时总电量")
    private Double gQt = 0.0;

    /**
     * 本次充/放电谷时总电费
     */
    @ApiModelProperty(value = "本次充/放电谷时总电费")
    private BigDecimal gElect = new BigDecimal("0.0");

    /**
     * 本次充/放电谷时总服务费
     */
    @ApiModelProperty(value = "本次充/放电谷时总服务费")
    private BigDecimal gFee = new BigDecimal("0.0");

    /**
     * 开始 SOC
     */
    @ApiModelProperty(value = "开始 SOC")
    private Integer startSoc;

    /**
     * 结束 SOC
     */
    @ApiModelProperty(value = "结束 SOC")
    private Integer endSoc;

    /**
     * 停止码
     */
    @ApiModelProperty(value = "停止码")
    private String stopReason;

    /**
     * 停止详细原因
     */
    @ApiModelProperty(value = "停止详细原因")
    private String stopDetailReason;

    /**
     * 车量 vin 码
     */
    @ApiModelProperty(value = "车量 vin 码")
    private String busVin;

    /**
     * 费率模型ID
     */
    @ApiModelProperty(value = "费率模型ID")
    private String rateTemplateId;

    /**
     * 有效时段总数 取值 0～48
     */
    @ApiModelProperty(value = "有效时段总数 取值 0～48")
    private Integer timeFrameNum;

    /**
     * 关联时段表id
     */
    @ApiModelProperty(value = "关联时段表id")
    private String timeFrameId;

    /**
     * 计费详情列表
     */
    @ApiModelProperty("计费详情列表")
    private List<InterflowOrderRecordDto.ChargingDetails> chargingDetailsList = Lists.newArrayList();

    /**
     * 计费详情信息
     */
    @Data
    public static class ChargingDetails {

        /**
         * 主键id
         */
        @ApiModelProperty("主键id")
        private String id;

        /**
         * 计费类型  1-充电 2-放电
         */
        @ApiModelProperty("计费类型  1-充电 2-放电")
        private Integer tariffType;

        /**
         * 计费时段
         */
        @ApiModelProperty("计费时段")
        private String tariffPeriod;

        /**
         * 时段电价
         */
        @ApiModelProperty("时段电价")
        private BigDecimal electPrice;

        /**
         * 时段服务费价
         */
        @ApiModelProperty("时段服务费价")
        private BigDecimal servicePrice;

        /**
         * 时段充电开始时间
         */
        @ApiModelProperty("时段充电开始时间")
        private String chargeStartTime;

        /**
         * 时段充电结束时间
         */
        @ApiModelProperty("时段充电结束时间")
        private String chargeEndTime;

        /**
         * 充/放电量
         */
        @ApiModelProperty("充/放电量")
        private Double rechargeQt;

        /**
         * 电费
         */
        @ApiModelProperty("电费")
        private BigDecimal electMoney;

        /**
         * 服务费
         */
        @ApiModelProperty("服务费")
        private BigDecimal serviceMoney;
    }
}
