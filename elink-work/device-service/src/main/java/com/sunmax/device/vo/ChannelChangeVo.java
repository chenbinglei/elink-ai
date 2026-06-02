package com.sunmax.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "ChannelChangeVo", description = "通道信息编辑参数实体")
public class ChannelChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", required = true)
    private String deviceId;

    /**
     * 通道名称
     */
    @ApiModelProperty(value = "通道名称", required = true)
    private String channelName;

    /**
     * 协议类型 MQTT,HTTP,TCP
     */
    @ApiModelProperty(value = "协议类型 MQTT,HTTP,TCP", required = true)
    private String protocolType;

    /**
     * 接入协议
     */
    @ApiModelProperty(value = "接入协议", required = true)
    private String accessProtocol;

    /**
     * IP地址
     */
    @ApiModelProperty(value = "IP地址", required = true)
    private String ip;

    /**
     * 端口号
     */
    @ApiModelProperty(value = "端口号", required = true)
    private Integer port;

}
