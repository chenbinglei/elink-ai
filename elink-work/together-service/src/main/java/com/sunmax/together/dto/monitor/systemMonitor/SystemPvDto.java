package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光伏系统数据返回实体类")
public class SystemPvDto {

    /**
     * 装机容量
     */
    @Schema(description = "装机容量")
    private Double capacity;

    /**
     * 逆变器数量
     */
    @Schema(description = "逆变器数量")
    private Integer inverterNum;

    /**
     * 阵列面积
     */
    @Schema(description = "阵列面积")
    private Double arrayArea;

    /**
     * 阵列倾角
     */
    @Schema(description = "阵列倾角")
    private Double arrayInclination;

    /**
     * 累计发电量
     */
    @Schema(description = "累计发电量")
    private Double accTotalQt;

    /**
     * 今日发电量
     */
    @Schema(description = "今日发电量")
    private Double dayQt;

    /**
     * 今日等效利用小时数
     */
    @Schema(description = "今日等效利用小时数")
    private Double dayHours;

    /**
     * 昨日发电量
     */
    @Schema(description = "昨日发电量")
    private Double lastDayQt;

    /**
     * 昨日系统效率
     */
    @Schema(description = "昨日系统效率")
    private Double lastDayEff;

    /**
     * 昨日等效利用小时数
     */
    @Schema(description = "昨日等效利用小时数")
    private Double lastDayHours;

    /**
     * 昨日损失电量
     */
    @Schema(description = "昨日损失电量")
    private Double lastDayLossDayQt;

//    /**
//     * 累计停机时长
//     */
//    @Schema(description = "累计停机时长")
//    private Double accDowntime;

    /**
     * 昨日峰值发电功率
     */
    @Schema(description = "昨日峰值发电功率")
    private Double lastDayMaxPower;

    /**
     * 二氧化碳(CO2)减排量(kg)
     */
    @Schema(description = "二氧化碳(CO2)减排量（kg）")
    private Double co2Reduction = 0.0;

    /**
     * 节约标准煤量(kg)
     */
    @Schema(description = "节约标准煤量（kg）")
    private Double standardCoalReduction = 0.0;

    /**
     * 等效植树量(颗)
     */
    @Schema(description = "等效植树量（颗）")
    private Double treeReduction = 0.0;

    /**
     * 总功率
     */
    @Schema(description = "总功率")
    private Double totalPower;

    /**
     * 实时功率归一化
     */
    @Schema(description = "实时功率归一化")
    private Double realPowerNorm;

}
