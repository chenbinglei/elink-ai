package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪标识
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 车辆VIN码
     */
    @ApiModelProperty(value = "车辆VIN码", required = true)
    private String busVin;

    /**
     * SOC值 范围 0～100，精度 1%
     */
    @ApiModelProperty(value = "SOC值", required = true)
    private Integer soc;

}
