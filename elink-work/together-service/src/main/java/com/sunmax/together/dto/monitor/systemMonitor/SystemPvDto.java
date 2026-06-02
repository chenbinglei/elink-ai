package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SystemPvDto", description = "光伏系统数据返回实体类")
public class SystemPvDto {

    /**
     * 装机容量
     */
    @ApiModelProperty(value = "装机容量")
    private Double capacity;

    /**
     * 逆变器数量
     */
    @ApiModelProperty(value = "逆变器数量")
    private Integer inverterNum;

    /**
     * 阵列面积
     */
    @ApiModelProperty(value = "阵列面积")
    private Double arrayArea;

    /**
     * 阵列倾角
     */
    @ApiModelProperty(value = "阵列倾角")
    private Double arrayInclination;

    /**
     * 累计发电量
     */
    @ApiModelProperty(value = "累计发电量")
    private Double accTotalQt;

    /**
     * 今日发电量
     */
    @ApiModelProperty(value = "今日发电量")
    private Double dayQt;

    /**
     * 今日等效利用小时数
     */
    @ApiModelProperty(value = "今日等效利用小时数")
    private Double dayHours;

    /**
     * 昨日发电量
     */
    @ApiModelProperty(value = "昨日发电量")
    private Double lastDayQt;

    /**
     * 昨日系统效率
     */
    @ApiModelProperty(value = "昨日系统效率")
    private Double lastDayEff;

    /**
     * 昨日等效利用小时数
     */
    @ApiModelProperty(value = "昨日等效利用小时数")
    private Double lastDayHours;

    /**
     * 昨日损失电量
     */
    @ApiModelProperty(value = "昨日损失电量")
    private Double lastDayLossDayQt;

//    /**
//     * 累计停机时长
//     */
//    @ApiModelProperty(value = "累计停机时长")
//    private Double accDowntime;

    /**
     * 昨日峰值发电功率
     */
    @ApiModelProperty(value = "昨日峰值发电功率")
    private Double lastDayMaxPower;

    /**
     * 二氧化碳(CO2)减排量(kg)
     */
    @ApiModelProperty(value = "二氧化碳(CO2)减排量（kg）")
    private Double co2Reduction = 0.0;

    /**
     * 节约标准煤量(kg)
     */
    @ApiModelProperty(value = "节约标准煤量（kg）")
    private Double standardCoalReduction = 0.0;

    /**
     * 等效植树量(颗)
     */
    @ApiModelProperty(value = "等效植树量（颗）")
    private Double treeReduction = 0.0;

    /**
     * 总功率
     */
    @ApiModelProperty(value = "总功率")
    private Double totalPower;

    /**
     * 实时功率归一化
     */
    @ApiModelProperty(value = "实时功率归一化")
    private Double realPowerNorm;

}
