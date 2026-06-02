package com.sunmax.crontab.dto;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "电桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "枪编号")
    private String gunCode;

    /**
     * 当前功率(kW)
     */
    @ApiModelProperty(value = "当前功率(kW)")
    @Builder.Default
    private BigDecimal curPower = new BigDecimal("0.0");

    /**
     * 向上可调功率
     */
    @ApiModelProperty(value = "向上可调功率")
    @Builder.Default
    private BigDecimal upPower = new BigDecimal("0.0");

    /**
     * 向下可调功率
     */
    @ApiModelProperty(value = "向下可调功率")
    @Builder.Default
    private BigDecimal downPower = new BigDecimal("0.0");

    /**
     * 充电类型 1-快充 2-慢充
     */
    @ApiModelProperty(value = "充电类型 1-快充 2-慢充")
    private Integer type;

    /**
     * 启动时间
     */
    @ApiModelProperty(value = "启动时间")
    private String startTime;

    /**
     * 当前电池SOC
     */
    @ApiModelProperty(value = "当前电池SOC")
    private Integer batterySOC;

    /**
     * 运行模式 -1-未知 0-充电模式 1-放电模式
     */
    @ApiModelProperty(value = "运行模式 -1-未知 0-充电模式 1-放电模式")
    @Builder.Default
    private Integer runMode = -1;

}
