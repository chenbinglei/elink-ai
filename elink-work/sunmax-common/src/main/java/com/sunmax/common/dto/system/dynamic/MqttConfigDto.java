package com.sunmax.common.dto.system.dynamic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "mqtt动态配置实体类")
public class MqttConfigDto {

    /**
     * 设备编号
     */
    @Schema(description = "设备编号")
    private String deviceSn;

    /**
     * 资源编号
     */
    @Schema(description = "资源编号")
    private String resourceSn;

    /**
     * 服务标识
     */
    @Schema(description = "服务标识")
    private String identifier;

}
