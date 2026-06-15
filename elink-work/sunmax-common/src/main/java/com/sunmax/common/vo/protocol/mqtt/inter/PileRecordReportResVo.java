package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.35 CMD_PileRecordReportResponse,//电桩记录查询命令响应 35
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PileRecordReportResVo {

    /**
     * 桩编码
     */
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 枪标识
     */
    @Schema(description = "枪标识")
    private Integer gunCode;

    /**
     * 订单序号
     */
    @Schema(description = "订单序号")
    private Integer recordSeq;

    /**
     * 记录上报类型 0-正常 1-离线 2-当前
     */
    @Schema(description = "记录上报类型")
    private Integer recordReportType;

    /**
     * 失败原因 0-成功 1-失败 255-其他原因
     */
    @Schema(description = "失败原因")
    private Integer failReason;

    /**
     * 正常记录总条数
     */
    @Schema(description = "正常记录总条数")
    private Integer onlineNum;

    /**
     * 离线记录总条数
     */
    @Schema(description = "离线记录总条数")
    private Integer offlineNum;

}
