package com.sunmax.together.dto.operation.siteInfo;

import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "光伏电站列表返回实体类")
public class PvSiteListDto {

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
     * 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     */
    @Schema(description = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    private Integer siteStatus;

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
     * 光伏类型 0-分布式商业 1-分布式户用 2-集中式
     */
    @Schema(description = "光伏类型 0-分布式商业 1-分布式户用 2-集中式")
    private Integer pvType;

    /**
     * 消纳方式 0-自发自用 1-余电上网 2-全额上网 3-离网自用
     */
    @Schema(description = "消纳方式 0-自发自用 1-余电上网 2-全额上网 3-离网自用")
    private String consumMode;

    /**
     * 并网等级 0-0.4kV 1-10kV 2-20kV 3-35kV 4-110kV 5-220kV
     */
    @Schema(description = "并网等级 0-0.4kV 1-10kV 2-20kV 3-35kV 4-110kV 5-220kV")
    private Integer tiedGrade;

    /**
     * 实时功率曲线列表
     */
    @Schema(description = "实时功率曲线")
    private List<Double> realPowerList = Lists.newArrayList();

    /**
     * xAxis列表
     */
    @Schema(description = "xAxis列表")
    private List<String> xAxisList = Lists.newArrayList();

    /**
     * 实时功率归一化
     */
    @Schema(description = "实时功率归一化")
    private Double powerAtOne;

    /**
     * 今日发电量
     */
    @Schema(description = "今日发电量")
    private Double dayQt;

    /**
     * 今日发电小时数
     */
    @Schema(description = "今日发电小时数")
    private Double dayQtHours;

    /**
     * 光伏装机容量
     */
    @Schema(description = "光伏装机容量")
    private Double pvCapacity;

    /**
     * 投运时间
     */
    @Schema(description = "投运时间")
    private String officialRunTime;

    /**
     * 实时功率
     */
    @Schema(description = "实时功率")
    private Double realPower;

    /**
     * 站点下逆变器额定功率总和
     */
    @Schema(description = "站点下逆变器额定功率总和")
    private Double sumPower;
}
