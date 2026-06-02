package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ChargeSiteMonitorDto", description = "充电站监测信息返回实体类")
public class ChargeSiteMonitorDto {

    /**
     * 装机容量
     */
    @ApiModelProperty(value = "装机容量")
    private Double capacity;

    /**
     * 电桩数量
     */
    @ApiModelProperty(value = "电桩数量")
    private Integer pileNum;

    /**
     * 直流桩数量
     */
    @ApiModelProperty(value = "直流桩数量")
    private Integer acPileNum;

    /**
     * 直流枪数量
     */
    @ApiModelProperty(value = "直流枪数量")
    private Integer acGunNum;

    /**
     * 交流桩数量
     */
    @ApiModelProperty(value = "交流桩数量")
    private Integer dcPileNum;

    /**
     * 交流枪数量
     */
    @ApiModelProperty(value = "交流枪数量")
    private Integer dcGunNum;

    /**
     * 累计充电量
     */
    @ApiModelProperty(value = "累计充电量")
    private Double sumChargeQt;

    /**
     * 今日充电量
     */
    @ApiModelProperty(value = "今日充电量")
    private Double dayChargeQt;

    /**
     * 昨日充电量
     */
    @ApiModelProperty(value = "昨日充电量")
    private Double lastDayChargeQt;

    /**
     * 累计V2G电量
     */
    @ApiModelProperty(value = "累计V2G电量")
    private Double sumV2gQt;

    /**
     * 今日V2G电量
     */
    @ApiModelProperty(value = "今日V2G电量")
    private Double dayV2gQt;

    /**
     * 昨日V2G电量
     */
    @ApiModelProperty(value = "昨日V2G电量")
    private Double lastDayV2gQt;

    /**
     * 实际功率
     */
    @ApiModelProperty(value = "实际功率")
    private Double realPower;
}
