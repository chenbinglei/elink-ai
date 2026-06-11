package com.sunmax.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "通道信息编辑参数实体")
public class ChannelChangeVo {

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

}
