package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.11 CMD_POWERCONTROL,//功率控制 11
 * 发送方向：前置服务<---平台服务
 */
@Data
public class PowerControlCmdDto {

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
     * 充/放电接口运行模式 0-充电模式 1-放电模式
     */
    @Schema(description = "充/放电接口运行模式")
    private Integer runMode;

    /**
     * 控制类型 0-绝对控制 1-相对控制
     */
    @Schema(description = "控制类型")
    private Integer ctrlType;

    /**
     * 输出功率
     */
    @Schema(description = "输出功率")
    private Double out;

}
