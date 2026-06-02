package com.sunmax.webapp.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "RealWebSocketDto", description = "实时数据推送返回实体类")
public class RealWebSocketDto {

    /**
     * 站点id
     */
    @ApiModelProperty("站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty("站点名称")
    private String siteName;

    /**
     * 订单号
     */
    @ApiModelProperty("订单号")
    private String orderNum;

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
     * 预约时间
     */
    @ApiModelProperty(value = "预约时间")
    private String clockingTime;

    /**
     * 枪工作状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
     */
    @ApiModelProperty(value = "枪工作状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
    private Integer workState;

    /**
     * 预付金额
     */
    @ApiModelProperty(value = "预付金额")
    private BigDecimal prepayMoney;

    /**
     * 已充时长
     */
    @ApiModelProperty(value = "已充时长")
    private String chargeTime;

    /**
     * 充放电策略 0-自动充满 1-soc电量 2-金额 3-电量
     */
    @ApiModelProperty(value = "充放电策略 0-自动充满 1-soc电量 2-金额 3-电量")
    private Integer strategy;

    /**
     * 充放电策略参数
     */
    @ApiModelProperty(value = "充放电策略参数")
    private Double strategyCfg;

    /**
     * 起始SOC
     */
    @ApiModelProperty(value = "起始SOC")
    private Integer startSoc;

    /**
     * 电压
     */
    @ApiModelProperty(value = "电压")
    private Double voltage;

    /**
     * 电流
     */
    @ApiModelProperty(value = "电流")
    private Double current;

    /**
     * 功率
     */
    @ApiModelProperty(value = "功率")
    private Double power;

    /**
     * 剩余时间
     */
    @ApiModelProperty(value = "剩余时间")
    private String remainTime;

    /**
     * 当前soc
     */
    @ApiModelProperty(value = "当前soc")
    private Integer batterySoc;

    /**
     * 总电量
     */
    @ApiModelProperty(value = "总电量")
    private Double totalQt;

    /**
     * 总费用
     */
    @ApiModelProperty(value = "总费用")
    private BigDecimal totalCost;
}
