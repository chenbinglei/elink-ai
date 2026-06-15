package com.sunmax.together.dto.operation.dataReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "光伏电站报表列表返回实体类")
public class PvSiteReportListDto {

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 站点唯一id
     */
    @Schema(description = "站点唯一id")
    private String id;

    /**
     * 所在省份
     */
    @Schema(description = "所在省份")
    private String province;

    /**
     * 所在市
     */
    @Schema(description = "所在市")
    private String city;

    /**
     * 所在区县
     */
    @Schema(description = "所在区县")
    private String county;

    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String address;

    /**
     * 光伏装机容量
     */
    @Schema(description = "光伏装机容量")
    private Double pvCapacity = 0.0;

    /**
     * 总辐照量
     */
    @Schema(description = "总辐照量")
    private Double totalIrradiation;

    /**
     * 平均温度
     */
    @Schema(description = "平均温度")
    private Double avgTemp;

    /**
     * 理论发电量
     */
    @Schema(description = "理论发电量")
    private Double theoryQt;

    /**
     * 逆变器发电量
     */
    @Schema(description = "逆变器发电量")
    private Double inverterQt = 0.0;

    /**
     * 并网点发电量
     */
    @Schema(description = "并网点发电量")
    private Double parallelQt = 0.0;

    /**
     * 上网电量
     */
    @Schema(description = "上网电量")
    private Double internetQt;

    /**
     * 自用电量
     */
    @Schema(description = "自用电量")
    private Double occupiedQt;

    /**
     * 自发自用比例
     */
    @Schema(description = "自发自用比例")
    private Double occupiedRatio;

    /**
     * 损失电量
     */
    @Schema(description = "损失电量")
    private Double lossQt;

    /**
     * 损失收益
     */
    @Schema(description = "损失收益")
    private BigDecimal lossMoney;

    /**
     * 峰值发电功率
     */
    @Schema(description = "峰值发电功率")
    private Double fValuePower;

    /**
     * 负荷率
     */
    @Schema(description = "负荷率")
    private Double loadRatio;

    /**
     * 光伏收益
     */
    @Schema(description = "光伏收益")
    private BigDecimal pvMoney;

    /**
     * 上网收益
     */
    @Schema(description = "上网收益")
    private BigDecimal internetMoney;

    /**
     * 消纳收益
     */
    @Schema(description = "消纳收益")
    private BigDecimal consumMoney;

    /**
     * 补贴收益
     */
    @Schema(description = "补贴收益")
    private BigDecimal subsidyMoney;

    /**
     * 二氧化碳减排量
     */
    @Schema(description = "二氧化碳减排量")
    private Double dioxideReduce  = 0.0;

    /**
     * 节约标煤量
     */
    @Schema(description = "节约标煤量")
    private Double thriftTce  = 0.0;

    /**
     * 等效植树
     */
    @Schema(description = "等效植树")
    private Double equivalentTree = 0.0;

    /**
     * 系统效率
     */
    @Schema(description = "系统效率")
    private Double systemEffi = 0.0;
}
