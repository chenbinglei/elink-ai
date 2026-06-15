package com.sunmax.together.dto.monitor.centralMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "充电返回实体类")
public class BatterySupplyDto {

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
     * 系统装机容量
     */
    @Schema(description = "装机容量")
    private Double capacity;

    /**
     * 系统功率曲线列表
     */
    @Schema(description = "系统功率")
    private List<SystemStatisticsDto> devices;

    /**
     * 今日充电量
     */
    @Schema(description = "今日充电量")
    private Double chargeOrderQt;

    /**
     * 今日V2G电量
     */
    @Schema(description = "今日V2G电量")
    private Double dischargeOrderQt;

    /**
     * 今日充电次数
     */
    @Schema(description = "今日充电次数")
    private Integer chargeOrderNum;

    /**
     * 今日V2G次数
     */
    @Schema(description = "今日枪均充电量")
    private Double avgChargeQt;

    /**
     * 今日充电成功率
     */
    @Schema(description = "一次充电成功率")
    private Double chargeSuccessRatio;

}
