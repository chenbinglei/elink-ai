package com.sunmax.together.dto.operation.dataReport;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ApiModel(value = "PvSiteReportListDto", description = "光伏电站报表列表返回实体类")
public class PvSiteReportListDto {

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 站点唯一id
     */
    @ApiModelProperty(value = "站点唯一id")
    private String id;

    /**
     * 所在省份
     */
    @ApiModelProperty(value = "所在省份")
    private String province;

    /**
     * 所在市
     */
    @ApiModelProperty(value = "所在市")
    private String city;

    /**
     * 所在区县
     */
    @ApiModelProperty(value = "所在区县")
    private String county;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "详细地址")
    private String address;

    /**
     * 光伏装机容量
     */
    @ApiModelProperty(value = "光伏装机容量")
    private Double pvCapacity = 0.0;

    /**
     * 总辐照量
     */
    @ApiModelProperty(value = "总辐照量")
    private Double totalIrradiation;

    /**
     * 平均温度
     */
    @ApiModelProperty(value = "平均温度")
    private Double avgTemp;

    /**
     * 理论发电量
     */
    @ApiModelProperty(value = "理论发电量")
    private Double theoryQt;

    /**
     * 逆变器发电量
     */
    @ApiModelProperty(value = "逆变器发电量")
    private Double inverterQt = 0.0;

    /**
     * 并网点发电量
     */
    @ApiModelProperty(value = "并网点发电量")
    private Double parallelQt = 0.0;

    /**
     * 上网电量
     */
    @ApiModelProperty(value = "上网电量")
    private Double internetQt;

    /**
     * 自用电量
     */
    @ApiModelProperty(value = "自用电量")
    private Double occupiedQt;

    /**
     * 自发自用比例
     */
    @ApiModelProperty(value = "自发自用比例")
    private Double occupiedRatio;

    /**
     * 损失电量
     */
    @ApiModelProperty(value = "损失电量")
    private Double lossQt;

    /**
     * 损失收益
     */
    @ApiModelProperty(value = "损失收益")
    private BigDecimal lossMoney;

    /**
     * 峰值发电功率
     */
    @ApiModelProperty(value = "峰值发电功率")
    private Double fValuePower;

    /**
     * 负荷率
     */
    @ApiModelProperty(value = "负荷率")
    private Double loadRatio;

    /**
     * 光伏收益
     */
    @ApiModelProperty(value = "光伏收益")
    private BigDecimal pvMoney;

    /**
     * 上网收益
     */
    @ApiModelProperty(value = "上网收益")
    private BigDecimal internetMoney;

    /**
     * 消纳收益
     */
    @ApiModelProperty(value = "消纳收益")
    private BigDecimal consumMoney;

    /**
     * 补贴收益
     */
    @ApiModelProperty(value = "补贴收益")
    private BigDecimal subsidyMoney;

    /**
     * 二氧化碳减排量
     */
    @ApiModelProperty(value = "二氧化碳减排量")
    private Double dioxideReduce  = 0.0;

    /**
     * 节约标煤量
     */
    @ApiModelProperty(value = "节约标煤量")
    private Double thriftTce  = 0.0;

    /**
     * 等效植树
     */
    @ApiModelProperty(value = "等效植树")
    private Double equivalentTree = 0.0;

    /**
     * 系统效率
     */
    @ApiModelProperty(value = "系统效率")
    private Double systemEffi = 0.0;
}
