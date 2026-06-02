package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
/**
 * CMD_Dev_LogReport,//日志数据上报 52
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PileLogReportVo {

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "枪编号", required = true)
    private Integer gunCode;

    /**
     * 日志类型 1-日志 2-告警记录
     */
    @ApiModelProperty(value = "日志类型 1-日志 2-告警记录", required = true)
    private Integer logType;

    /**
     * 日志内容
     */
    @ApiModelProperty(value = "日志内容", required = true)
    private String logContent;

    /**
     * 日志时间
     */
    @ApiModelProperty(value = "日志时间", required = true)
    private Long logTime;

}
