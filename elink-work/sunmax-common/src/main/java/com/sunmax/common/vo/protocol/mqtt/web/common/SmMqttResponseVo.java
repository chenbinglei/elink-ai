package com.sunmax.common.vo.protocol.mqtt.web.common;

import lombok.Data;

@Data
public class SmMqttResponseVo {

    /**
     * 主题后缀名称
     */
    private String token;

    /**
     * 日期 “yyyy-mm-dd hh:mm:ss”
     */
    private String timestamp;

    /**
     * 状态 OK/FAILURE
     */
    private String status;

    /**
     * 数据
     */
    private Object body;

}
