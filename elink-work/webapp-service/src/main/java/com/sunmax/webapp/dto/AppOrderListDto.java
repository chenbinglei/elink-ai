package com.sunmax.webapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ApiModel(value = "AppOrderListDto", description = "小程序订单列表返回实体类")
public class AppOrderListDto {

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
     * 运营商名称
     */
    @ApiModelProperty(value = "运营商名称")
    private String operateUnitName;

    /**
     * 运营商id
     */
    @ApiModelProperty(value = "运营商id")
    private String operateUnitId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 预付金额
     */
    @ApiModelProperty(value = "预付金额")
    private BigDecimal prepayMoney;

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
     * 定时充放电时间
     */
    @ApiModelProperty(value = "定时充放电时间")
    private String clockingTime;

    /**
     * 充电时长
     */
    @ApiModelProperty("充电时长")
    private String chargeDuration;

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
     * 停止详细原因
     */
    @ApiModelProperty(value = "停止详细原因")
    private String stopDetailReason;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
