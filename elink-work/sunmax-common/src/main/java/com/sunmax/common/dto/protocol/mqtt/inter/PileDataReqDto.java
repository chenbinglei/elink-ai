package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.30 CMD_PileDataRequest,//电桩运行数据请求  30
 * 发送方向：前置服务<---平台服务
 */
@Data
public class PileDataReqDto {

    /**
     * 桩编码
     */
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 枪编号
     */
    @Schema(description = "枪编号")
    private Integer gunCode;

}
