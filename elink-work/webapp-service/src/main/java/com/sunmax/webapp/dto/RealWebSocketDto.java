package com.sunmax.webapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "实时数据推送返回实体类")
public class RealWebSocketDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNum;

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
     * 预约时间
     */
    @Schema(description = "预约时间")
    private String clockingTime;

    /**
     * 枪工作状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
     */
    @Schema(description = "枪工作状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中")
    private Integer workState;

    /**
     * 预付金额
     */
    @Schema(description = "预付金额")
    private BigDecimal prepayMoney;

    /**
     * 已充时长
     */
    @Schema(description = "已充时长")
    private String chargeTime;

    /**
     * 充放电策略 0-自动充满 1-soc电量 2-金额 3-电量
     */
    @Schema(description = "充放电策略 0-自动充满 1-soc电量 2-金额 3-电量")
    private Integer strategy;

    /**
     * 充放电策略参数
     */
    @Schema(description = "充放电策略参数")
    private Double strategyCfg;

    /**
     * 起始SOC
     */
    @Schema(description = "起始SOC")
    private Integer startSoc;

    /**
     * 电压
     */
    @Schema(description = "电压")
    private Double voltage;

    /**
     * 电流
     */
    @Schema(description = "电流")
    private Double current;

    /**
     * 功率
     */
    @Schema(description = "功率")
    private Double power;

    /**
     * 剩余时间
     */
    @Schema(description = "剩余时间")
    private String remainTime;

    /**
     * 当前soc
     */
    @Schema(description = "当前soc")
    private Integer batterySoc;

    /**
     * 总电量
     */
    @Schema(description = "总电量")
    private Double totalQt;

    /**
     * 总费用
     */
    @Schema(description = "总费用")
    private BigDecimal totalCost;
}
