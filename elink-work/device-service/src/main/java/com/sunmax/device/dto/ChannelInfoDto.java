package com.sunmax.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 通道信息返回实体类
 */
@Data
@Schema(description = "通道信息返回实体类")
public class ChannelInfoDto {

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
     * 通道名称
     */
    @Schema(description = "通道名称")
    private String channelName;

    /**
     * 协议类型 MQTT,HTTP,TCP
     */
    @Schema(description = "协议类型 MQTT,HTTP,TCP")
    private String protocolType;

    /**
     * 接入协议
     */
    @Schema(description = "接入协议")
    private String accessProtocol;

    /**
     * IP地址
     */
    @Schema(description = "IP地址")
    private String ip;

    /**
     * 端口号
     */
    @Schema(description = "端口号")
    private Integer port;

    /**
     * 通信状态 0-连接 1-断开
     */
    @Schema(description = "通信状态 0-连接 1-断开")
    private Integer txStatus;

}
