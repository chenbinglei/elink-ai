package com.sunmax.together.dto.operation.operationAnalysis;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "光伏运营运行分析返回实体类")
public class PvOperationAnalysisDto {

    /**
     * 装机容量
     */
    @Schema(description = "装机容量")
    private Double capacity;

    /**
     * 逆变器数量
     */
    @Schema(description = "逆变器数量")
    private Integer inverterNum = 0;

    /**
     * 总发电量
     */
    @Schema(description = "总发电量")
    private Double generation = 0.0;

    /**
     * 上网电量
     */
    @Schema(description = "上网电量")
    private Double netGeneration = 0.0;

    /**
     * 自用电量
     */
    @Schema(description = "自用电量")
    private Double selfGeneration = 0.0;

    /**
     * 总损失电量
     */
    @Schema(description = "总损失电量")
    private Double lossGeneration = 0.0;

    /**
     * 日期列表
     */
    @Schema(description = "日期列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 发电量列表
     */
    @Schema(description = "发电量列表")
    private List<Double> generationList = Lists.newArrayList();

    /**
     * 系统效率PR列表
     */
    @Schema(description = "系统效率PR列表")
    private List<Double> prList = Lists.newArrayList();

    /**
     * 损失电量列表
     */
    @Schema(description = "损失电量列表")
    private List<Double> lossGenerationList = Lists.newArrayList();

    /**
     * 等效发电时长列表
     */
    @Schema(description = "等效发电时长列表")
    private List<Double> generationTimeList = Lists.newArrayList();

    /**
     * 电站排名列表(等效发电时长排名)
     */
    @Schema(description = "电站排名列表(等效发电时长排名)")
    private List<Ranking> rankingList = Lists.newArrayList();

    /**
     * 二氧化碳(CO2)减排量(kg)
     */
    @Schema(description = "二氧化碳(CO2)减排量（kg）")
    private Double co2Reduction;

    /**
     * 节约标准煤量(kg)
     */
    @Schema(description = "节约标准煤量（kg）")
    private Double standardCoalReduction;

    /**
     * 等效植树量(颗)
     */
    @Schema(description = "等效植树量（颗）")
    private Double treeReduction;

    @Data
    @Schema(description = "排行榜")
    public static class Ranking {

        /**
         * 站点名称
         */
        @Schema(description = "站点名称")
        private String siteName;

        /**
         * 总发电时长
         */
        @Schema(description = "总发电时长")
        private Double totalGenerationTime;

    }

}
