package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 充/放电接口运行模式 0-充电模式 1-放电模式
     */
    @ApiModelProperty(value = "充/放电接口运行模式", required = true)
    private Integer runMode;

    /**
     * 控制类型 0-绝对控制 1-相对控制
     */
    @ApiModelProperty(value = "控制类型", required = true)
    private Integer ctrlType;

    /**
     * 输出功率
     */
    @ApiModelProperty(value = "输出功率", required = true)
    private Double out;

}
