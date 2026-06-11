package com.sunmax.common.dto.together.ops;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "巡检项数据返回实体类")
public class AppInspectItemDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 巡检项名称
     */
    @Schema(description = "巡检项名称")
    private String name;

    /**
     * 巡检内容描述
     */
    @Schema(description = "巡检内容描述")
    private String description;

    /**
     * 图标地址
     */
    @Schema(description = "图标地址")
    private String iconPath;

}
