package com.sunmax.common.vo.protocol.mqtt.web.request;

import lombok.Data;

import java.util.List;

@Data
public class EventRateReqSubscribeVo {
    /**
     * 设备id
     */
    private String deviceId;
    private List<String> pilesCode;

}
