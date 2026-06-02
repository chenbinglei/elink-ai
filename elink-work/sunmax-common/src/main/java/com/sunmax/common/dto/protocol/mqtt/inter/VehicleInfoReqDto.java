package com.sunmax.common.dto.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

}
