package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.21 CMD_StrategySettingResponse,//策略设置响应 21
 * 发送方向：前置服务<---平台服务
 */
@Data
public class StrategySettingResDto {

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
     * 运行模式 0-充电模式 1-放电模式
     */
    @Schema(description = "运行模式")
    private Integer runMode;

    /**
     * 失败原因 0-成功 1-平台故障 255-其他原因
     */
    @Schema(description = "失败原因")
    private Integer failReason;

    /**
     * 失败详情
     */
    @Schema(description = "详情")
    private Integer failDetail;

}
