package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备心跳实体类")
public class DevHeartBeatVo {

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 时间戳
     */
    @Schema(description = "时间戳")
    private Long timeStamp;

}
