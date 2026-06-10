package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.32 CMD_VehicleInfoRequest,//车辆信息请求 32
 * 发送方向：前置服务<---平台服务
 */
@Data
public class VehicleInfoReqDto {

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

}
