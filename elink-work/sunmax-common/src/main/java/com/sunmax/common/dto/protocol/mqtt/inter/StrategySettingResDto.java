package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 运行模式 0-充电模式 1-放电模式
     */
    @ApiModelProperty(value = "运行模式", required = true)
    private Integer runMode;

    /**
     * 失败原因 0-成功 1-平台故障 255-其他原因
     */
    @ApiModelProperty(value = "失败原因", required = true)
    private Integer failReason;

    /**
     * 失败详情
     */
    @ApiModelProperty(value = "详情", required = true)
    private Integer failDetail;

}
