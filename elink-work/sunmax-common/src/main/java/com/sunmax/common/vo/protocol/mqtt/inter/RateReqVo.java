package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.1 CMD_RATE_REQ,//费率请求 CMD=1
 * 发送方向：前置服务--->平台服务
 */
@Data
public class RateReqVo {

    /**
     * 费率类型 0-充电费率 1-放电费率
     */
    @Schema(description = "费率类型 0-充电费率 1-放电费率")
    private Integer type;

}
