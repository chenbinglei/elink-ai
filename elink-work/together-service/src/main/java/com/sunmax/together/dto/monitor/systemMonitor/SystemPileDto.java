package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SystemPileDto", description = "电桩系统数据返回实体类")
public class SystemPileDto {

    /**
     * 装机容量
     */
    @ApiModelProperty(value = "装机容量")
    private Double capacity;

    /**
     * 电桩数量
     */
    @ApiModelProperty(value = "电桩数量")
    private Integer pileNum = 0;

    /**
     * 直流桩数量
     */
    @ApiModelProperty(value = "直流桩数量")
    private Integer dcPileNum = 0;

    /**
     * 直流枪数量
     */
    @ApiModelProperty(value = "直流枪数量")
    private Integer dcGunNum = 0;

    /**
     * 交流桩数量
     */
    @ApiModelProperty(value = "交流桩数量")
    private Integer acPileNum = 0;

    /**
     * 交流枪数量
     */
    @ApiModelProperty(value = "交流枪数量")
    private Integer acGunNum = 0;

    /**
     * 累计充电量
     */
    @ApiModelProperty(value = "累计充电量")
    private Double sumChargeQt = 0.0;

    /**
     * 今日充电量
     */
    @ApiModelProperty(value = "今日充电量")
    private Double dayChargeQt = 0.0;

    /**
     * 昨日充电量
     */
    @ApiModelProperty(value = "昨日充电量")
    private Double lastDayChargeQt = 0.0;

    /**
     * 累计V2G电量
     */
    @ApiModelProperty(value = "累计V2G电量")
    private Double sumV2gQt = 0.0;

    /**
     * 今日V2G电量
     */
    @ApiModelProperty(value = "今日V2G电量")
    private Double dayV2gQt = 0.0;

    /**
     * 昨日V2G电量
     */
    @ApiModelProperty(value = "昨日V2G电量")
    private Double lastDayV2gQt = 0.0;

    /**
     * 总功率
     */
    @ApiModelProperty(value = "总功率")
    private Double totalPower = 0.0;

    /**
     * 今日枪均电量(度)
     */
    @ApiModelProperty(value = "今日枪均电量(度)")
    private Double dayAvgChargeQt;

    /**
     * 今日时间利用率(%)
     */
    @ApiModelProperty(value = "今日时间利用率(%)")
    private Double dayTimeRatio;

}
