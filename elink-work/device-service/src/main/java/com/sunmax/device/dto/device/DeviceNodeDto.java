package com.sunmax.device.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备节点返回实体类
 */
@Data
@ApiModel(value = "DeviceNodeDto", description = "设备节点返回实体类")
public class DeviceNodeDto {

    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id")
    private String nodeId;

    /**
     * 数据编号
     */
    @ApiModelProperty(value = "数据编号")
    private String dataCode;

    /**
     * 数据值
     */
    @ApiModelProperty(value = "数据值")
    private String dataValue;

}
