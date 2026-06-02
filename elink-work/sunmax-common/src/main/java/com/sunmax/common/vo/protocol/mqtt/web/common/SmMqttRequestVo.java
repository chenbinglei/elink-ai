package com.sunmax.common.vo.protocol.mqtt.web.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmMqttRequestVo {

    /**
     * 主题后缀名称
     */
    private String token;

    /**
     * 日期 “yyyy-mm-dd hh:mm:ss”
     */
    private String timestamp;

    /**
     * 数据
     */
    private Object body;

}
