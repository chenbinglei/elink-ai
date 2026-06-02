package com.sunmax.common.vo.protocol.mqtt.web.from;

import lombok.Data;

/**
 * 网关重启响应订阅实体类
 */
@Data
public class RebootSubscribeVo {

    /**
     * 类型 1-重启网关 2-重启服务
     */
    private Integer type;

    /**
     * 服务名称（2-重启服务时有效）
     */
    private String service;

    /**
     * 重启结果0-失败 1-成功  3-重启完成
     */
    private Integer result;

}
