package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "枪编号")
    private Integer gunCode;

    /**
     * 日志类型 1-日志 2-告警记录
     */
    @Schema(description = "日志类型 1-日志 2-告警记录")
    private Integer logType;

    /**
     * 日志内容
     */
    @Schema(description = "日志内容")
    private String logContent;

    /**
     * 日志时间
     */
    @Schema(description = "日志时间")
    private Long logTime;

}
