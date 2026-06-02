package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 停止原因
     */
    @ApiModelProperty(value = "停止原因", required = true)
    private Integer stopReason;

    /**
     * 交易号
     */
    @ApiModelProperty(value = "交易号", required = true)
    private String recordId;

}
