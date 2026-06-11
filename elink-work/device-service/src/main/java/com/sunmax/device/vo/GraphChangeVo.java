package com.sunmax.device.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 图形编辑参数
 */
@Data
@Schema(description = "图形编辑参数")
public class GraphChangeVo {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 设备id
     */
    @Schema(description = "设备id")
    private String deviceId;

    /**
     * 图形分类id
     */
    @Schema(description = "图形分类id")
    private String graphTypeId;

    /**
     * 图形名称
     */
    @Schema(description = "图形名称")
    private String graphName;

    /**
     * 图形URL
     */
    @Schema(description = "图形URL")
    private String graphUrl;

    /**
     * 默认图形 1-默认 2-不默认
     */
    @Schema(description = "默认图形 1-默认 2-不默认")
    private Integer isDefault;

}
