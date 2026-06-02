package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "HeartBeatVo", description = "设备心跳实体类")
public class DevHeartBeatVo {

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", required = true)
    private String deviceId;

    /**
     * 时间戳
     */
    @ApiModelProperty(value = "时间戳", required = true)
    private Long timeStamp;

}
