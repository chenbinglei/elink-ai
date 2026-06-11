package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.34 CMD_PileRecordReportRequest,//电桩记录查询 34
 * 发送方向：前置服务<---平台服务
 */
@Data
public class PileRecordReportReqDto {

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

}
