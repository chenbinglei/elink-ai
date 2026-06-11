package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩数据返回实体类")
public class PileDto {

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
     * 电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线")
    private Integer workStatus = -1;

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
     * 电桩类型 28-交流 29-直流 30-V2G
     */
    @Schema(description = "电桩类型 28-交流 29-直流 30-V2G")
    private String typeId;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号")
    private String model;

    /**
     * 今日充电量
     */
    @Schema(description = "今日充电量")
    private Double dayChargeQt = 0.0;

    /**
     * 今日放电量
     */
    @Schema(description = "今日放电量")
    private Double dayV2gQt = 0.0;

    /**
     * 有功功率
     */
    @Schema(description = "有功功率")
    private Double activePower;

    /**
     * 内部温度(°C)
     */
    @Schema(description = "内部温度(°C)")
    private Double innerTemperature;

}
