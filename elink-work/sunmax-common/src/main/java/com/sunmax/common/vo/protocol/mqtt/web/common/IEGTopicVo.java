package com.sunmax.common.vo.protocol.mqtt.web.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 融合终端 订阅实体类（固定格式）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IEGTopicVo implements Serializable {

    /**
     * 过期时间
     */
    private Long expire;

    /**
     * 请求id
     */
    private String mid;

    /**
     * 数据(报文内容)
     */
    private Object param;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 消息类型，全大写，单词中间用“_”隔开
     */
    private String type;
}
