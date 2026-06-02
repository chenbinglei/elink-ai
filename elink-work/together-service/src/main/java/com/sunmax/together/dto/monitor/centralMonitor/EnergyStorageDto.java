package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "EnergyStorageDto", description = "储能返回实体类")
public class EnergyStorageDto {

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 系统id
     */
    @ApiModelProperty(value = "系统id")
    private String systemId;

    /**
     * 系统名称
     */
    @ApiModelProperty(value = "系统名称")
    private String systemName;

    /**
     * 场站名称
     */
    @ApiModelProperty(value = "场站名称")
    private String siteName;

    /**
     * 能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)
     */
    @ApiModelProperty(value = "能源场景类型 1-光伏 2-储能 3-充电桩 4-用能 5-配电 6-换电(可存储多个，以逗号分割)")
    private String scenarioTypes;

    /**
     * 所属省市
     */
    @ApiModelProperty(value = "所属省市")
    private String location;

    /**
     * 储能类型
     */
    @ApiModelProperty(value = "储能类型")
    private String storageType;

    /**
     * 储能装机容量-设备总功率
     */
    @ApiModelProperty(value = "储能装机容量-设备总功率")
    private Double pcsPower;

    /**
     * 储能装机容量-电池簇设备额定容量
     */
    @ApiModelProperty(value = "储能装机容量-电池簇设备额定容量")
    private Double pcsRatedCap;

    /**
     * 储能实时功率,2位小数
     */
    @ApiModelProperty(value = "储能实时功率")
    private Double pcsActivepower;

    /**
     * 系统功率
     */
    @ApiModelProperty(value = "系统功率")
    private List<SystemStatisticsDto> devices;

    /**
     * 今日循环次数
     */
    @ApiModelProperty(value = "今日循环次数")
    private Double loopTimes = 0.0;

    /**
     * 储能SOC,2位小数
     */
    @ApiModelProperty(value = "储能SOC")
    private Double soc;

    /**
     * 储能今日充电量
     */
    @ApiModelProperty(value = "储能今日充电量")
    private Double totalBatteryCharge = 0.0;

    /**
     * 储能今日放电量
     */
    @ApiModelProperty(value = "储能今日放电量")
    private Double totalBatteryDischarge = 0.0;

    /**
     * 光伏并网等级类型 0-0.4kV 1-10kV 2-20kV 3-35kV 4-110kV 5-220kV
     */
    @ApiModelProperty(value = "储能并网等级类型 0-0.4kV 1-10kV 2-20kV 3-35kV 4-110kV 5-220kV")
    private String tiedGrade;

}
