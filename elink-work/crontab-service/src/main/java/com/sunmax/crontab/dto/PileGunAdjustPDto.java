package com.sunmax.crontab.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 电桩运行数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PileGunAdjustPDto {

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
     * 当前功率(kW)
     */
    @Schema(description = "当前功率(kW)")
    @Builder.Default
    private BigDecimal curPower = new BigDecimal("0.0");

    /**
     * 向上可调功率
     */
    @Schema(description = "向上可调功率")
    @Builder.Default
    private BigDecimal upPower = new BigDecimal("0.0");

    /**
     * 向下可调功率
     */
    @Schema(description = "向下可调功率")
    @Builder.Default
    private BigDecimal downPower = new BigDecimal("0.0");

    /**
     * 充电类型 1-快充 2-慢充
     */
    @Schema(description = "充电类型 1-快充 2-慢充")
    private Integer type;

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
    @Builder.Default
    private Integer runMode = -1;

}
