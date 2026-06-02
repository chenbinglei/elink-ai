package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

/**
 * 业务命令响应-停止事件 订阅实体类
 */
@Data
public class PileStopEventSubscribeVo {

    /**
     * 桩编码
     */
    private String pilesCode;

    /**
     *枪口标识
     */
    private Integer gunCode;

    /**
     * 失败详细原因
     */
    private int faileReason;

    /**
     * 停止充放电时间
     */
    private int stopTime;

    /**
     *交易号
     */
    private String recordId;

    /**
     *BMS 信息
     */
    private PileBmsInfoSubscribeVo.BmsInfoVo bmsInfo;

}
