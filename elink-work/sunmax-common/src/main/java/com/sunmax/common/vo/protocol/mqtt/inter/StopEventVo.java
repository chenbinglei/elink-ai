package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.10 CMD_STOP_EVENT,//停止事件 10
 * 发送方向：前置服务--->平台服务
 */
@Data
public class StopEventVo {

    /**
     * 桩编码
     */
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @Schema(description = "枪标识")
    private Integer gunCode;

    /**
     * 停止详细原因
     */
    @Schema(description = "停止详细原因")
    private Integer failReason;

    /**
     * 停止充放电时间
     */
    @Schema(description = "停止充放电时间")
    private Long stopTime;

    /**
     * 交易号
     */
    @Schema(description = "交易号")
    private String recordId;

}
