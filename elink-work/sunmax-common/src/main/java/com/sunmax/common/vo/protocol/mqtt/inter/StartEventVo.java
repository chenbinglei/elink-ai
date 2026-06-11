package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.7 CMD_START_EVENT,//启动事件 7
 * 发送方向：前置服务--->平台服务
 */
@Data
public class StartEventVo {

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
     * 事件结果 0-成功 1-启动失败 2-预约成功 3-预约失败 255-其他原因
     */
    @Schema(description = "事件结果")
    private Integer eventResult;

    /**
     * 失败详细原因
     */
    @Schema(description = "失败详细原因")
    private Integer failReason;

    /**
     * 结束详细描述
     */
    @Schema(description = "结束详细描述")
    private String stopDetail;

    /**
     * 开始充放电时间
     */
    @Schema(description = "开始充放电时间")
    private Long startTime;

    /**
     * 交易号
     */
    @Schema(description = "交易号")
    private String recordId;

    /**
     * bms信息
     */
    @Schema(description = "bms信息")
    private PileBmsInfoReportVo bmsInfo;

}
