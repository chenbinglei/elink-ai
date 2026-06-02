package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "BatteryChangeDto", description = "换电返回实体类")
public class BatteryChangeDto {

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
     * 充电仓位数
     */
    @ApiModelProperty(value = "充电仓位数")
    private Integer chargePosition = 0;

    /**
     * SOC,2位小数
     */
    @ApiModelProperty(value = "SOC")
    private Double soc;

    /**
     * 今日充电量
     */
    @ApiModelProperty(value = "今日充电量")
    private Double dayChargeQt = 0.0;

    /**
     * 今日用电量
     */
    @ApiModelProperty(value = "今日用电量")
    private Double dayUseQt = 0.0;

    /**
     * 充电能耗占比
     */
    @ApiModelProperty(value = "充电能耗占比")
    private Double chargeEnergyRatio = 0.0;

    /**
     * 今日换电次数
     */
    @ApiModelProperty(value = "今日换电次数")
    private Integer dayChangeNum = 0;

    /**
     * 系统功率曲线列表
     */
    @ApiModelProperty(value = "系统功率曲线列表")
    private List<SystemStatisticsDto> powerList;

}
