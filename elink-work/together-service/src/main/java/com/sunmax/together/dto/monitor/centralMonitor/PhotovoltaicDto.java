package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "光伏返回实体类")
public class PhotovoltaicDto {

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 系统id
     */
    @Schema(description = "系统id")
    private String systemId;

    /**
     * 系统名称
     */
    @Schema(description = "系统名称")
    private String systemName;

    /**
     * 场站名称
     */
    @Schema(description = "场站名称")
    private String siteName;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)
     */
    @Schema(description = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)")
    private String scenarioTypes;

    /**
     * 所属省市
     */
    @Schema(description = "所属省市")
    private String location;

    /**
     * 光伏电站类型 0-分布式商业 1-分布式户用 2-集中式
     */
    @Schema(description = "光伏类型，0-分布式商业 1-分布式户用 2-集中式")
    private String pvType;

    /**
     * 光伏消纳方式类型 0-自发自用 1-余电上网 2-全额上网 3-离网自用
     */
    @Schema(description = "光伏消纳方式类型 0-自发自用 1-余电上网 2-全额上网 3-离网自用")
    private String consumMode;

    /**
     * 装机容量
     */
    @Schema(description = "装机容量")
    private Double pvcapacity;

    /**
     * 光伏并网等级类型 0-0.4kV 1-10kV 2-20kV 3-35kV 4-110kV 5-220kV
     */
    @Schema(description = "光伏并网等级类型 0-0.4kV 1-10kV 2-20kV 3-35kV 4-110kV 5-220kV")
    private String tiedGrade;

    /**
     * 系统功率
     */
    @Schema(description = "系统功率")
    private List<SystemStatisticsDto> devices;

    /**
     * 光伏实时功率，2位小数
     */
    @Schema(description = "实时功率值")
    private Double activePower;

    /**
     * 光伏实时功率归一化，2位小数
     */
    @Schema(description = "光伏实时功率归一化")
    private Double powerNormalize;

    /**
     * 今日发电电量，3位小数
     */
    @Schema(description = "今日发电电量")
    private Double dailyPowerGeneration;

    /**
     * 今日发电时长，2位小数
     */
    @Schema(description = "今日发电时长")
    private Double dailyPowerDuration;


}
