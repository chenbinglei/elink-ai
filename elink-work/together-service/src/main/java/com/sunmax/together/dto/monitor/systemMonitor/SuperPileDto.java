package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "超充桩数据返回实体类")
public class SuperPileDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 运行状态 0-停机 1-待机 2-运行 3-故障
     */
    @Schema(description = "运行状态 0-停机 1-待机 2-运行 3-故障")
    private Integer runState;

    /**
     * 运行状态名称
     */
    @Schema(description = "运行状态名称")
    private String runStateName;

    /**
     * 额定功率
     */
    @Schema(description = "额定功率")
    private Double ratedPower;

    /**
     * 设备厂家名称
     */
    @Schema(description = "设备厂家名称")
    private String manufacturerName;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号")
    private String model;

    /**
     * 有功功率
     */
    @Schema(description = "有功功率")
    private Double activePower;

    /**
     * 电压
     */
    @Schema(description = "电压")
    private Double voltage;

    /**
     * 电流
     */
    @Schema(description = "电流")
    private Double current;

    /**
     * 车辆SOC
     */
    @Schema(description = "SOC")
    private Integer soc;

}
