package com.sunmax.together.dto.operation.operationAnalysis;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "PvOperationAnalysisDto", description = "光伏运营运行分析返回实体类")
public class PvOperationAnalysisDto {

    /**
     * 装机容量
     */
    @ApiModelProperty(value = "装机容量")
    private Double capacity;

    /**
     * 逆变器数量
     */
    @ApiModelProperty(value = "逆变器数量")
    private Integer inverterNum = 0;

    /**
     * 总发电量
     */
    @ApiModelProperty(value = "总发电量")
    private Double generation = 0.0;

    /**
     * 上网电量
     */
    @ApiModelProperty(value = "上网电量")
    private Double netGeneration = 0.0;

    /**
     * 自用电量
     */
    @ApiModelProperty(value = "自用电量")
    private Double selfGeneration = 0.0;

    /**
     * 总损失电量
     */
    @ApiModelProperty(value = "总损失电量")
    private Double lossGeneration = 0.0;

    /**
     * 日期列表
     */
    @ApiModelProperty(value = "日期列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 发电量列表
     */
    @ApiModelProperty(value = "发电量列表")
    private List<Double> generationList = Lists.newArrayList();

    /**
     * 系统效率PR列表
     */
    @ApiModelProperty(value = "系统效率PR列表")
    private List<Double> prList = Lists.newArrayList();

    /**
     * 损失电量列表
     */
    @ApiModelProperty(value = "损失电量列表")
    private List<Double> lossGenerationList = Lists.newArrayList();

    /**
     * 等效发电时长列表
     */
    @ApiModelProperty(value = "等效发电时长列表")
    private List<Double> generationTimeList = Lists.newArrayList();

    /**
     * 电站排名列表(等效发电时长排名)
     */
    @ApiModelProperty(value = "电站排名列表(等效发电时长排名)")
    private List<Ranking> rankingList = Lists.newArrayList();

    /**
     * 二氧化碳(CO2)减排量(kg)
     */
    @ApiModelProperty(value = "二氧化碳(CO2)减排量（kg）")
    private Double co2Reduction;

    /**
     * 节约标准煤量(kg)
     */
    @ApiModelProperty(value = "节约标准煤量（kg）")
    private Double standardCoalReduction;

    /**
     * 等效植树量(颗)
     */
    @ApiModelProperty(value = "等效植树量（颗）")
    private Double treeReduction;

    @Data
    @ApiModel(value = "Ranking", description = "排行榜")
    public static class Ranking {

        /**
         * 站点名称
         */
        @ApiModelProperty(value = "站点名称")
        private String siteName;

        /**
         * 总发电时长
         */
        @ApiModelProperty(value = "总发电时长")
        private Double totalGenerationTime;

    }

}
