package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.8 CMD_STOP,//停止命令 8
 * 发送方向：前置服务<---平台服务
 */
@Data
public class StopCmdDto {

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
     * 停止原因
     */
    @Schema(description = "停止原因")
    private Integer stopReason;

    /**
     * 交易号
     */
    @Schema(description = "交易号")
    private String recordId;

}
