package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "储能辅助设备数据返回实体类")
public class SystemSeAuxEquipmentDto {

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
     * 制冷状态 0-停止 1-开启
     */
    @Schema(description = "制冷状态 0-停止 1-开启")
    private Integer coolingState;

    /**
     * 加热器状态 0-停止 1-开启
     */
    @Schema(description = "加热器状态 0-停止 1-开启")
    private Integer heatingState;

    /**
     * 内风机状态 0-停止 1-开启
     */
    @Schema(description = "内风机状态 0-停止 1-开启")
    private Integer internalState;

    /**
     * 外风机状态 0-停止 1-开启
     */
    @Schema(description = "外风机状态 0-停止 1-开启")
    private Integer externalState;

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
     * 遥测数据列表
     */
    @Schema(description = "遥测数据列表")
    private List<TelemetryDataDto> telemetryDataList = Lists.newArrayList();

    /**
     * 遥信告警数据
     */
    @Schema(description = "遥信告警数据")
    private List<TelecommuteAlarmDto> telecommuteAlarmList = Lists.newArrayList();

}
