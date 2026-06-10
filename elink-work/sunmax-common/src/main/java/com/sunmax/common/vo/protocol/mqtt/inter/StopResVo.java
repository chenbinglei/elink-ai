package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.9 CMD_STOP_RES,//停止响应 9
 * 前置服务--->平台服务
 */
@Data
public class StopResVo {

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
     * 响应结果 0-成功 1-失败 255-其他原因
     */
    @Schema(description = "响应结果")
    private Integer responseResult;

    /**
     * 失败详细原因
     */
    @Schema(description = "失败详细原因")
    private Integer failReason;

    /**
     * 交易号
     */
    @Schema(description = "交易号")
    private String recordId;

}
