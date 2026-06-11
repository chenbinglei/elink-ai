package com.sunmax.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "数据配置列表")
public class DataConfigListDto {

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
     * 站点名称
     */
    @Schema(description = "站点名称")
    private String siteName;

    /**
     * 动态配置
     */
    @Schema(description = "动态配置")
    private String dynamicConfigs;

}
