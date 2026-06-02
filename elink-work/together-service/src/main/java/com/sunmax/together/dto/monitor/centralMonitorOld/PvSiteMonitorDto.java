package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "PvSiteMonitorDto", description = "光伏电站监测信息返回实体类")
public class PvSiteMonitorDto {

    /**
     * 装机容量
     */
    @ApiModelProperty(value = "装机容量")
    private Double capacity;

    /**
     * 并网点数量
     */
    @ApiModelProperty(value = "并网点数量")
    private Integer parallelNum;

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
    private Double sumQt;

    /**
     * 今日发电量
     */
    @ApiModelProperty(value = "今日发电量")
    private Double dayQt;

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
     * 实际功率
     */
    @ApiModelProperty(value = "实际功率")
    private Double realPower;

    /**
     * 理论发电量
     */
    @ApiModelProperty(value = "理论发电量")
    private Double theoryGeneration;

    /**
     * 功能点实时数据
     */
    @Data
    public static class FunctionRalData {

        /**
         * 功能点标识
         */
        @ApiModelProperty("功能点标识")
        private String functionLogo;

        /**
         * 实时数据
         */
        @ApiModelProperty("实时数据")
        private Object realData;
    }
}
