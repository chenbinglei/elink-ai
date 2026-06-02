package com.sunmax.common.vo.protocol.mqtt.web.from;

import lombok.Data;

import java.util.List;

/**
 * 网关服务列表订阅类
 */
@Data
public class ServiceListSubscribeVo {

    /**
     * 服务名称
     */
    private String name;

    /**
     * 服务状态 1-运行 2-停止
     */
    private Integer status;

    /**
     * 服务版本
     */
    private String version;

    /**
     * 内部端口
     */
    private List<Integer> portList;

    /**
     * 服务启动时间时间戳
     */
    private Long startTime;

    /**
     * 服务上次心跳时间戳
     */
    private Long lastRxTime;

    /**
     * 服务停止时间时间戳
     */
    private Long stopTime;

}
