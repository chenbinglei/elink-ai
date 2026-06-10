package com.sunmax.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 数据项配置(对象格式)
 */
@Data
@Schema(description = "站点拓扑节点数据项配置返回实体类")
public class SiteTopItemDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 数据编号
     */
    @Schema(description = "数据编号")
    private String dataCode;

    /**
     * 数据名称
     */
    @Schema(description = "数据名称")
    private String dataName;

    /**
     * 数据展示名称
     */
    @Schema(description = "数据展示名称")
    private String showName;

    /**
     * 数据展示类型 1-显示 2-隐藏
     */
    @Schema(description = "数据展示类型 1-显示 2-隐藏")
    private Integer showType;

    /**
     * 数据位置类型 1-上 2-下 3-左 4-右
     */
    @Schema(description = "数据位置类型 1-上 2-下 3-左 4-右")
    private Integer positionType;

}
