package com.sunmax.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通道信息返回实体类
 */
@Data
@ApiModel(value = "ChannelInfoDto", description = "通道信息返回实体类")
public class ChannelInfoDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**
     * 通道名称
     */
    @ApiModelProperty(value = "通道名称")
    private String channelName;

    /**
     * 协议类型 MQTT,HTTP,TCP
     */
    @ApiModelProperty(value = "协议类型 MQTT,HTTP,TCP")
    private String protocolType;

    /**
     * 接入协议
     */
    @ApiModelProperty(value = "接入协议")
    private String accessProtocol;

    /**
     * IP地址
     */
    @ApiModelProperty(value = "IP地址")
    private String ip;

    /**
     * 端口号
     */
    @ApiModelProperty(value = "端口号")
    private Integer port;

    /**
     * 通信状态 0-连接 1-断开
     */
    @ApiModelProperty(value = "通信状态 0-连接 1-断开")
    private Integer txStatus;

}
