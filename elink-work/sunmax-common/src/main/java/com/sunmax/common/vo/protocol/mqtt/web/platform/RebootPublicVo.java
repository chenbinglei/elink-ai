package com.sunmax.common.vo.protocol.mqtt.web.platform;

import lombok.Data;

/**
 * 网关重启发布实体类
 */
@Data
public class RebootPublicVo {

    /**
     * 类型 1-重启网关 2-重启服务
     */
    private Integer type;

    /**
     * 服务名称（2-重启服务时有效）
     */
    private String service;

}
