package com.sunmax.device.service.impl;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "拓扑节点设备数据实体类")
public class TopDeviceDto {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
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
     * 类型名称
     */
    @Schema(description = "类型名称")
    private String typeName;

}
