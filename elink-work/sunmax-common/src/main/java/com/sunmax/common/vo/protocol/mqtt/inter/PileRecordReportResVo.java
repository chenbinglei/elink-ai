package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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

    /**
     * 失败原因 0-成功 1-失败 255-其他原因
     */
    @ApiModelProperty(value = "失败原因", required = true)
    private Integer failReason;

    /**
     * 正常记录总条数
     */
    @ApiModelProperty(value = "正常记录总条数", required = true)
    private Integer onlineNum;

    /**
     * 离线记录总条数
     */
    @ApiModelProperty(value = "离线记录总条数", required = true)
    private Integer offlineNum;

}
