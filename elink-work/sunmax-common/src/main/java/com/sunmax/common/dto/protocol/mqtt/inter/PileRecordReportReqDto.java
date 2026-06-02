package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 订单序号
     */
    @ApiModelProperty(value = "订单序号", required = true)
    private Integer recordSeq;

    /**
     * 记录上报类型 0-正常 1-离线 2-当前
     */
    @ApiModelProperty(value = "记录上报类型", required = true)
    private Integer recordReportType;

}
