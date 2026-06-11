package com.sunmax.crontab.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 电桩运行数据
 */
@Data
public class PileRunDataVo {

    /**
     * 电桩编号
     */
    @Schema(description = "电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "枪编号")
    private String gunCode;

    /**
     * 最大功率（kW）
     */
    @Schema(description = "最大功率（kW）")
    private Double maxP = 0.0;

    /**
     * 最小功率（kW）
     */
    @Schema(description = "最小功率（kW）")
    private Double minP = 0.0;

    /**
     * 充电类型 1-快充 2-慢充
     */
    @Schema(description = "充电类型 1-快充 2-慢充")
    private Integer type;

    /**
     * 控制类型 1-可调控 2-不可调控
     */
    @Schema(description = "控制类型 1-可调控 2-不可调控")
    private Integer controlType = 2;

    /**
     * 当前功率(kW)
     */
    @Schema(description = "当前功率(kW)")
    private Double curPower = 0.0;

    /**
     * 需求功率(kW)
     */
    @Schema(description = "需求功率(kW)")
    private Double reqPower = 0.0;

    /**
     * 启动时间
     */
    @Schema(description = "启动时间")
    private String startTime;

    /**
     * 当前电池SOC
     */
    @Schema(description = "当前电池SOC")
    private Integer batterySOC;

    /**
     * 运行模式 -1-未知 0-充电模式 1-放电模式
     */
    @Schema(description = "运行模式 -1-未知 0-充电模式 1-放电模式")
    private Integer runMode = -1;

}
