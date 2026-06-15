package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.33 CMD_VehicleInfoResponse,//车辆信息请求响应 33
 * 发送方向：前置服务--->平台服务
 */
@Data
public class VehicleInfoResVo {

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
     * 车辆VIN码
     */
    @Schema(description = "车辆VIN码")
    private String busVin;

    /**
     * SOC值 范围 0～100，精度 1%
     */
    @Schema(description = "SOC值")
    private Integer soc;

}
