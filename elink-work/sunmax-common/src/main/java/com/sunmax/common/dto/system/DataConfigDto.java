package com.sunmax.common.dto.system;

import com.sunmax.common.dto.system.dynamic.MqttConfigDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据配置返回实体类")
public class DataConfigDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 数据转发id
     */
    @Schema(description = "数据转发id")
    private String forwardId;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 动态配置
     */
    @Schema(description = "动态配置")
    private String dynamicConfigs;

    /**
     * 动态配置数据
     */
    @Schema(description = "动态配置数据")
    private MqttConfigDto dynamicMqttConfigs;

}
