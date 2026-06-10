package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.12 CMD_POWERCONTROL_RES,//功率控制响应 12
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PowerControlResVo {

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
     * 响应结果 0-成功 255-其他原因
     */
    @Schema(description = "响应结果 0-成功 255-其他原因")
    private Integer responseResult;

}
