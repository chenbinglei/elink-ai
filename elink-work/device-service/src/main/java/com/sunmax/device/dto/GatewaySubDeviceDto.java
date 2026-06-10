package com.sunmax.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "网关子设备列表返回实体类")
public class GatewaySubDeviceDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 设备序列号
     */
    @Schema(description = "设备序列号")
    private String deviceNumber;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String deviceName;

    /**
     * 通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     */
    @Schema(description = "通讯状态 0-未注册 1-在线 2-维护 3-故障 88-离线")
    private Integer txStatus;

}
