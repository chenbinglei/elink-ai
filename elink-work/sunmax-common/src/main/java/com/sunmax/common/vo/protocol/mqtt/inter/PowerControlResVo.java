package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
     * 响应结果 0-成功 255-其他原因
     */
    @ApiModelProperty(value = "响应结果 0-成功 255-其他原因", required = true)
    private Integer responseResult;

}
