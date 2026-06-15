package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 交易记录号
     */
    @Schema(description = "交易记录号")
    private String recordId;

    /**
     * 记录上报序号
     */
    @Schema(description = "记录上报序号")
    private Integer recordSeq;

    /**
     * 记录上报类型 0-正常记录 1-离网记录
     */
    @Schema(description = "记录上报类型 0-正常记录 1-离网记录")
    private Integer reportType;

    /**
     * 枪标识 从1开始
     */
    @Schema(description = "枪标识")
    private Integer gunCode;

}
