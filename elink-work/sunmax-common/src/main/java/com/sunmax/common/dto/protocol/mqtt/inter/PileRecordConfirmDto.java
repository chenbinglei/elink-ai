package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 4.17 CMD_PILE_RECORD_CONFIRM,//充电记录上报确认 17
 * 发送方向：前置服务<---平台服务
 */

@Data
public class PileRecordConfirmDto {

    /**
     * 桩编码
     */
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 交易记录号
     */
    @ApiModelProperty(value = "交易记录号", required = true)
    private String recordId;

    /**
     * 记录上报序号
     */
    @ApiModelProperty(value = "记录上报序号", required = true)
    private Integer recordSeq;

    /**
     * 记录上报类型 0-正常记录 1-离网记录
     */
    @ApiModelProperty(value = "记录上报类型 0-正常记录 1-离网记录", required = true)
    private Integer reportType;

    /**
     * 枪标识 从1开始
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

}
