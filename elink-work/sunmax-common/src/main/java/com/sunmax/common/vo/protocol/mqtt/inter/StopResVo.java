package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识 从1开始
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 响应结果 0-成功 1-失败 255-其他原因
     */
    @ApiModelProperty(value = "响应结果", required = true)
    private Integer responseResult;

    /**
     * 失败详细原因
     */
    @ApiModelProperty(value = "失败详细原因", required = true)
    private Integer failReason;

    /**
     * 交易号
     */
    @ApiModelProperty(value = "交易号", required = true)
    private String recordId;

}
