package com.sunmax.together.dto.operation.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRecordExportDto {

    /**
     * 唯一id
     */
    @ApiModelProperty("唯一id")
    private String id;

    /**
     * 订单状态 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中
     */
    @ApiModelProperty("订单状态 0-未进行 1-充电中 2-充电完成 3-启动失败 4-异常 5-订单取消 6-预约中")
    private Integer orderStatus;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String orderNum;

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
     * 电桩类型 5-交流 6-直流 7-V2G
     */
    @ApiModelProperty("电桩类型 5-交流 6-直流 7-V2G")
    private Integer pileType;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 开始充/放电时间
     */
    @ApiModelProperty("开始充/放电时间")
    private String startTime;

    /**
     * 结束充/放电时间
     */
    @ApiModelProperty("结束充/放电时间")
    private String endTime;

    /**
     * 充/放电时长
     */
    @ApiModelProperty("充/放电时长")
    private String duration;

    /**
     * 本次充/放电总电量
     */
    @ApiModelProperty("本次充/放电总电量")
    private Double totalQt = 0.0;

    /**
     * 实付金额
     */
    @ApiModelProperty("实付金额")
    private BigDecimal actualTotalCost = new BigDecimal("0.0");

    /**
     * 实付电费
     */
    @ApiModelProperty("实付电费")
    private BigDecimal actualTotalElect = new BigDecimal("0.0");

    /**
     * 实付服务费
     */
    @ApiModelProperty("实付服务费")
    private BigDecimal actualTotalFee = new BigDecimal("0.0");

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
     * 车牌号
     */
    @ApiModelProperty("车牌号")
    private String plateNumber;

    /**
     * 补单状态 0-挂单 1-自动恢复 2-人工恢复 3-正常
     */
    @ApiModelProperty(value = "补单状态 0-挂单 1-自动恢复 2-人工恢复 3-正常")
    private Integer repairStatus;

    /**
     * 平台名称
     */
    @ApiModelProperty(value = "平台名称")
    private String platformName;

    /**
     * 预付金额
     */
    @ApiModelProperty("预付金额")
    private BigDecimal prepayMoney;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 停止码
     */
    @ApiModelProperty("停止码")
    private String stopReason;

    /**
     * 停止详细原因
     */
    @ApiModelProperty("停止详细原因")
    private String stopDetailReason;

    /**
     * 异常类型码 0-无异常 1-时间异常：订单时长大于24h 2-大额订单：上报的订单总金额大于1000元 3-电量异常：电量大于500kwh 4-无效订单：电量小于1 5-费用异常：订单金额为0
     */
    @ApiModelProperty("异常类型码 0-无异常 1-时间异常：订单时长大于24h 2-大额订单：上报的订单总金额大于1000元 3-电量异常：电量大于500kwh 4-无效订单：电量小于1 5-费用异常：订单金额为0")
    private String abnormalCode;

    /**
     * 账号类型 1-充/放电卡ID 2-VIN码 3-手机号
     */
    @ApiModelProperty("账号类型 1-充/放电卡ID 2-VIN码 3-手机号")
    private Integer accountType;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-离线卡启动
     */
    @ApiModelProperty(value = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制 7-离线卡启动")
    private Integer starter;

    /**
     * 手机号
     */
    @ApiModelProperty("手机号")
    private String phone;

    /**
     * 电卡卡号
     */
    @ApiModelProperty("电卡卡号")
    private String cardNumber;

    /**
     * VIN码
     */
    @ApiModelProperty("VIN码")
    private String busVin;

}
