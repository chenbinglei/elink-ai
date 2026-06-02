package com.sunmax.common.model;

import com.google.common.collect.Maps;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 通道缓存实体
 */
@Data
@ApiModel(value = "ChannelModel")
public class ChannelModel {

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
     * 通讯状态 0-连接 1-未连接
     */
    @ApiModelProperty(value = "通讯状态 0-连接 1-未连接")
    private Integer txStatus;

    /**
     * 功能点绑定数据 (设备id+:+功能点标识) -> 点号
     */
    @ApiModelProperty(value = "功能点绑定数据")
    private Map<String, String> functionPointMap = Maps.newConcurrentMap();

    /**
     * 点表数据
     */
    @ApiModelProperty(value = "点表数据")
    private Map<String, PointTableModel> pointTableMap = Maps.newConcurrentMap();

}
