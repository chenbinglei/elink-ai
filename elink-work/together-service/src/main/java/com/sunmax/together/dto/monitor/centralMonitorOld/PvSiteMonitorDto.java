package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "光伏电站监测信息返回实体类")
public class PvSiteMonitorDto {

    /**
     * 装机容量
     */
    @Schema(description = "装机容量")
    private Double capacity;

    /**
     * 并网点数量
     */
    @Schema(description = "并网点数量")
    private Integer parallelNum;

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
    private Double sumQt;

    /**
     * 今日发电量
     */
    @Schema(description = "今日发电量")
    private Double dayQt;

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
     * 实际功率
     */
    @Schema(description = "实际功率")
    private Double realPower;

    /**
     * 理论发电量
     */
    @Schema(description = "理论发电量")
    private Double theoryGeneration;

    /**
     * 功能点实时数据
     */
    @Data
    public static class FunctionRalData {

        /**
         * 功能点标识
         */
        @Schema(description = "功能点标识")
        private String functionLogo;

        /**
         * 实时数据
         */
        @Schema(description = "实时数据")
        private Object realData;
    }
}
