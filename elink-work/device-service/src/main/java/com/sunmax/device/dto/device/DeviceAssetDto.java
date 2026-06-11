package com.sunmax.device.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备资产信息")
public class DeviceAssetDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备名称
     */
    @Schema(description = "设备名称")
    private String name;

    /**
     * 父节点id
     */
    @Schema(description = "父节点id")
    private String parentId;
}
