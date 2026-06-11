package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.27 CMD_RATE_SET_RES,//费率下发响应  27
 * 发送方向：前置服务--->平台服务
 */
@Data
public class RateSetResVo {

    /**
     * 桩编码
     */
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 费率类型 0-充电费率 1-放电费率
     */
    @Schema(description = "费率类型")
    private Integer type;

    /**
     * 费率模型ID
     */
    @Schema(description = "费率模型ID")
    private String rateId;

}
