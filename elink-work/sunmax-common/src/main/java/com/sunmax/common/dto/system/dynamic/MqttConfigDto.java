package com.sunmax.common.dto.system.dynamic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "MqttConfigDto", description = "mqtt动态配置实体类")
public class MqttConfigDto {

    /**
     * 设备编号
     */
    @ApiModelProperty(value = "设备编号")
    private String deviceSn;

    /**
     * 资源编号
     */
    @ApiModelProperty(value = "资源编号")
    private String resourceSn;

    /**
     * 服务标识
     */
    @ApiModelProperty(value = "服务标识")
    private String identifier;

}
