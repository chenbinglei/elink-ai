package com.sunmax.device.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备列表返回实体类
 */
@Data
@Schema(description = "设备列表返回实体类")
public class DeviceListDto {

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
     * 设备类型id
     */
    @Schema(description = "设备类型id")
    private String typeId;

    /**
     * 设备类型名称
     */
    @Schema(description = "设备类型名称")
    private String typeName;

    /**
     * 模型id
     */
    @Schema(description = "模型id")
    private String modelId;

    /**
     * 模型名称
     */
    @Schema(description = "模型名称")
    private String modelName;

    /**
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @Schema(description = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    private Integer accessType;

    /**
     * 通信状态 0-未注册 1-在线 2-故障 88-离线
     */
    @Schema(description = "通信状态 0-未注册 1-在线 2-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 告警状态 1-无告警 2-有告警
     */
    @Schema(description = "告警状态 1-无告警 2-有告警")
    private Integer alarmStatus;

    /**
     * 模型logo路径
     */
    @Schema(description = "模型logo路径")
    private String logoPath;

}
