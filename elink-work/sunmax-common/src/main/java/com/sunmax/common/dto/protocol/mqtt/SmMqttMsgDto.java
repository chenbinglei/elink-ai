package com.sunmax.common.dto.protocol.mqtt;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SmMqttMsgDto {

    /**
     * 设备编号
     */
    private String deviceId;

    /**
     * 消息标识
     */
    private String msgId;

    /**
     * 消息体
     */
    private Object msgObj;

}
