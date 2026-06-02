package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 停止详细原因
     */
    @ApiModelProperty(value = "停止详细原因", required = true)
    private Integer failReason;

    /**
     * 停止充放电时间
     */
    @ApiModelProperty(value = "停止充放电时间", required = true)
    private Long stopTime;

    /**
     * 交易号
     */
    @ApiModelProperty(value = "交易号", required = true)
    private String recordId;

}
