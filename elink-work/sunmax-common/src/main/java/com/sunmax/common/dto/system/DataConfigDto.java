package com.sunmax.common.dto.system;

import com.sunmax.common.dto.system.dynamic.MqttConfigDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DataConfigDto", description = "数据配置返回实体类")
public class DataConfigDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 数据转发id
     */
    @ApiModelProperty(value = "数据转发id")
    private String forwardId;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 动态配置
     */
    @ApiModelProperty(value = "动态配置")
    private String dynamicConfigs;

    /**
     * 动态配置数据
     */
    @ApiModelProperty(value = "动态配置数据")
    private MqttConfigDto dynamicMqttConfigs;

}
