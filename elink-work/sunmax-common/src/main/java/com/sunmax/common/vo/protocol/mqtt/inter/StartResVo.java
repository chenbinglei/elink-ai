package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 4.6 CMD_START_RES,//启动响应 6
 * 发送方向：前置服务--->平台服务
 */
@Data
public class StartResVo {

    /**
     * 桩编码
     */
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 响应结果 0-成功 1-失败 255-其他原因
     */
    @ApiModelProperty(value = "响应结果 0-成功 1-失败 255-其他原因", required = true)
    private Integer responseResult;

    /**
     * 失败原因
     */
    @ApiModelProperty(value = "失败原因", required = true)
    private Integer failReason;

    /**
     * 失败详细原因
     */
    @ApiModelProperty(value = "失败详细原因", required = true)
    private String stopDetail;

    /**
     * 交易号
     */
    @ApiModelProperty(value = "交易号", required = true)
    private String recordId;

}
