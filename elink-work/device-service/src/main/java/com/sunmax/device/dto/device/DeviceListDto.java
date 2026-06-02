package com.sunmax.device.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备列表返回实体类
 */
@Data
@ApiModel(value = "DeviceListDto", description = "设备列表返回实体类")
public class DeviceListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
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
     * 设备类型id
     */
    @ApiModelProperty(value = "设备类型id")
    private String typeId;

    /**
     * 设备类型名称
     */
    @ApiModelProperty(value = "设备类型名称")
    private String typeName;

    /**
     * 模型id
     */
    @ApiModelProperty(value = "模型id")
    private String modelId;

    /**
     * 模型名称
     */
    @ApiModelProperty(value = "模型名称")
    private String modelName;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 接入类型 1-直连设备 2-网关设备 3-网关子设备
     */
    @ApiModelProperty(value = "接入类型 1-直连设备 2-网关设备 3-网关子设备")
    private Integer accessType;

    /**
     * 通信状态 0-未注册 1-在线 2-故障 88-离线
     */
    @ApiModelProperty(value = "通信状态 0-未注册 1-在线 2-故障 88-离线")
    private Integer txStatus = 0;

    /**
     * 告警状态 1-无告警 2-有告警
     */
    @ApiModelProperty(value = "告警状态 1-无告警 2-有告警")
    private Integer alarmStatus;

    /**
     * 模型logo路径
     */
    @ApiModelProperty(value = "模型logo路径")
    private String logoPath;

}
