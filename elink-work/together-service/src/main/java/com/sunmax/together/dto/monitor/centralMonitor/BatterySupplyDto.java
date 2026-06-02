package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "BatterySupplyDto", description = "充电返回实体类")
public class BatterySupplyDto {

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
     * 系统装机容量
     */
    @ApiModelProperty(value = "装机容量")
    private Double capacity;

    /**
     * 系统功率曲线列表
     */
    @ApiModelProperty(value = "系统功率")
    private List<SystemStatisticsDto> devices;

    /**
     * 今日充电量
     */
    @ApiModelProperty(value = "今日充电量")
    private Double chargeOrderQt;

    /**
     * 今日V2G电量
     */
    @ApiModelProperty(value = "今日V2G电量")
    private Double dischargeOrderQt;

    /**
     * 今日充电次数
     */
    @ApiModelProperty(value = "今日充电次数")
    private Integer chargeOrderNum;

    /**
     * 今日V2G次数
     */
    @ApiModelProperty(value = "今日枪均充电量")
    private Double avgChargeQt;

    /**
     * 今日充电成功率
     */
    @ApiModelProperty(value = "一次充电成功率")
    private Double chargeSuccessRatio;

}
