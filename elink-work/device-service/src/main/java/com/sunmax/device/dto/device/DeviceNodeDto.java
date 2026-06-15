package com.sunmax.device.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 设备节点返回实体类
 */
@Data
@Schema(description = "设备节点返回实体类")
public class DeviceNodeDto {

    /**
     * 节点id
     */
    @Schema(description = "节点id")
    private String nodeId;

    /**
     * 数据编号
     */
    @Schema(description = "数据编号")
    private String dataCode;

    /**
     * 数据值
     */
    @Schema(description = "数据值")
    private String dataValue;

}
