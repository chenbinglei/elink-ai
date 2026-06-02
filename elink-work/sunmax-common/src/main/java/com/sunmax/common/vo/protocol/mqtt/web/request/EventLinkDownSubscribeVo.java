package com.sunmax.common.vo.protocol.mqtt.web.request;

import lombok.Data;

/**
 * 设备下线请求
 */
@Data
public class EventLinkDownSubscribeVo {

    /**
     * 设备名称
     */
    private String devName;

    /**
     * 设备编号
     */
    private String devSN;

    /**
     * 断开原因
     */
    private String reason;


}
