package com.sunmax.common.vo.protocol.mqtt.web.data;

import lombok.Data;

/**
 * 电桩日志订阅实体类
 */
@Data
public class PileLogSubscribeVo {

    /**
     * 枪标识
     */
    private Integer gunCode;

    /**
     * 日志类型 1-日志 2-告警记录
     */
    private Integer logType;

    /**
     * 日志发生时间
     */
    private Long logTime;

    /**
     * 日志内容
     */
    private String logContent;

}
