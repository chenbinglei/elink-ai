package com.sunmax.device.service.impl;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TopDeviceDto", description = "拓扑节点设备数据实体类")
public class TopDeviceDto {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private String id;

    /**
     * 设备名称
     */
    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    /**
     * 设备序列号
     */
    @ApiModelProperty(value = "设备序列号")
    private String deviceNumber;

    /**
     * 类型名称
     */
    @ApiModelProperty(value = "类型名称")
    private String typeName;

}
