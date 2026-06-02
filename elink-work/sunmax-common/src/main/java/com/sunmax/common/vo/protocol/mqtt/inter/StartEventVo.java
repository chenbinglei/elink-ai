package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 事件结果 0-成功 1-启动失败 2-预约成功 3-预约失败 255-其他原因
     */
    @ApiModelProperty(value = "事件结果", required = true)
    private Integer eventResult;

    /**
     * 失败详细原因
     */
    @ApiModelProperty(value = "失败详细原因", required = true)
    private Integer failReason;

    /**
     * 结束详细描述
     */
    @ApiModelProperty(value = "结束详细描述", required = true)
    private String stopDetail;

    /**
     * 开始充放电时间
     */
    @ApiModelProperty(value = "开始充放电时间", required = true)
    private Long startTime;

    /**
     * 交易号
     */
    @ApiModelProperty(value = "交易号", required = true)
    private String recordId;

    /**
     * bms信息
     */
    @ApiModelProperty(value = "bms信息", required = true)
    private PileBmsInfoReportVo bmsInfo;

}
