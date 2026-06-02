package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

/**
 * 业务命令响应-功率控制 订阅实体类
 */
@Data
public class PileCtrlResponseSubscribeVo {

    /**
     * 桩编码
     */
    private String pilesCode;

    /**
     *枪标识
     */
    private Integer gunCode;

    /**
     * 电桩运行模式。
     */
    private Integer runMode;

    /**
     * 响应结果
     */
    private Integer responseResult;
}
