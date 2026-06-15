package com.sunmax.common.dto.operate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单记录返回实体类")
public class OrderRecordDto {

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
}
