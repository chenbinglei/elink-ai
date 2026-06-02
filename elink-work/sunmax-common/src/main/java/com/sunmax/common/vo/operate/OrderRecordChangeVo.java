package com.sunmax.common.vo.operate;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "OrderRecordChangeVo", description = "订单记录编辑信息参数")
public class OrderRecordChangeVo {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private String id;


    /**
     * 是否有序 1-是 2-否
     */
    @ApiModelProperty("是否有序 1-是 2-否")
    private Integer isOrderly;

    /**
     * 预付金额
     */
    @ApiModelProperty("预付金额")
    private BigDecimal prepayMoney;

    /**
     * 订单类型 1-充电订单 2-放电订单
     */
    @ApiModelProperty("订单类型 1-充电订单 2-放电订单")
    private Integer orderType;

    /**
     * 订单状态 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中
     */
    @ApiModelProperty("订单状态 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中")
    private Integer orderStatus;

    /**
     * 充放电方式 0-立即充电 1-定时充电 2-自动充电
     */
    @ApiModelProperty("充放电方式 0-立即充电 1-定时充电 2-自动充电")
    private Integer powerWay;

    /**
     * 定时充放电时间
     */
    @ApiModelProperty("定时充放电时间")
    private LocalDateTime clockingTime;

    /**
     * 账号类型 1-充/放电卡 2-VIN码 3-手机号
     */
    @ApiModelProperty("账号类型 1-充/放电卡 2-VIN码 3-手机号")
    private Integer accountType;

    /**
     * 账号数据
     */
    @ApiModelProperty("账号数据")
    private String accountData;

    /**
     * 充电桩编号
     */
    @ApiModelProperty("充电桩编号")
    private String pileCode;

    /**
     * 充电枪编号
     */
    @ApiModelProperty("充电枪编号")
    private Integer gunCode;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制、7-离线卡启动
     */
    @ApiModelProperty("发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制、7-离线卡启动")
    private Integer starter;

    /**
     * 渠道
     */
    @ApiModelProperty("渠道")
    private Integer channelSource;

    /**
     * 策略类型  0 满充/放空；1 定 SOC; 2 定金额；3 定电量
     */
    @ApiModelProperty("策略类型  0 满充/放空；1 定 SOC; 2 定金额；3 定电量")
    private Integer strategyType;

    /**
     * 策略启动时间
     */
    @ApiModelProperty("策略启动时间")
    private String strategyTime;

    /**
     * 定量策略值
     *      *  策略类型 1：0～100      精度 1%；
     *      *  策略类型 2：0～100000   精度 0.001 元
     *      *  策略类型 3：0～100000   精度 0.001kW·h
     *
     */
    @ApiModelProperty("定量策略值")
    private Double strategyNum;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始时间")
    private String startTime;

    /**
     * 结束充/放电时间
     */
    @ApiModelProperty("结束充/放电时间")
    private String endTime;

    /**
     * 本次充/放电总电量
     */
    @ApiModelProperty("本次充/放电总电量")
    private Double totalCurQt = 0.0;

    /**
     * 本次充/放电总费用
     */
    @ApiModelProperty("本次充/放电总费用")
    private BigDecimal totalCost = new BigDecimal("0.0");

    /**
     * 本次充/放电总电费
     */
    @ApiModelProperty("本次充/放电总电费")
    private BigDecimal totalElect = new BigDecimal("0.0");

    /**
     * 本次充/放电总服务费
     */
    @ApiModelProperty("本次充/放电总服务费")
    private BigDecimal totalFee = new BigDecimal("0.0");

    /**
     * 开始 SOC
     */
    @ApiModelProperty("开始 SOC")
    private Integer startSOC;

    /**
     * 结束 SOC
     */
    @ApiModelProperty("结束 SOC")
    private Integer endSOC;

    /**
     * 停止详细原因
     */
    @ApiModelProperty("停止详细原因")
    private Integer stopDetailReason;

    /**
     * 车量 vin 码
     */
    @ApiModelProperty("车量 vin 码")
    private String busVin;

    /**
     * 有效时段总数 取值 0～48
     */
    @ApiModelProperty("有效时段总数 取值 0～48")
    private Integer timeFrameNum;

    /**
     * 关联时段表id
     */
    @ApiModelProperty("关联时段表id")
    private String timeFrameId;
}
