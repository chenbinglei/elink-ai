package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "系统电表返回实体类")
public class SystemMeterDto {

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
     * 通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 设备型号
     */
    @Schema(description = "设备型号")
    private String model;

    /**
     * 设备厂家名称
     */
    @Schema(description = "设备厂家名称")
    private String manufacturerName;

    /**
     * 本月平均功率因数
     */
    @Schema(description = "本月平均功率因数")
    private Double averagePowerFactor;

    /**
     * 三相电压不平衡度
     */
    @Schema(description = "三相电压不平衡度")
    private Double threePhaseVoltImbalance;

    /**
     * 三相电流不平衡度
     */
    @Schema(description = "三相电流不平衡度")
    private Double threePhaseCurImbalance;

    /**
     * 功率平衡度
     */
    @Schema(description = "功率平衡度")
    private Double powerBalance;

    /**
     * 遥测数据列表
     */
    @Schema(description = "遥测数据列表")
    private List<TelemetryDataDto> telemetryDataList = Lists.newArrayList();

}
