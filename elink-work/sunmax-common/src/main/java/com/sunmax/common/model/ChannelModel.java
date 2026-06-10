package com.sunmax.common.model;

import com.google.common.collect.Maps;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Map;

/**
 * 通道缓存实体
 */
@Data
@Schema(description = "ChannelModel")
public class ChannelModel {

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
     * 通讯状态 0-连接 1-未连接
     */
    @Schema(description = "通讯状态 0-连接 1-未连接")
    private Integer txStatus;

    /**
     * 功能点绑定数据 (设备id+:+功能点标识) -> 点号
     */
    @Schema(description = "功能点绑定数据")
    private Map<String, String> functionPointMap = Maps.newConcurrentMap();

    /**
     * 点表数据
     */
    @Schema(description = "点表数据")
    private Map<String, PointTableModel> pointTableMap = Maps.newConcurrentMap();

}
