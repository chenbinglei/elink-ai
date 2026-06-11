package com.sunmax.device.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "图形分类列表返回实体类")
public class GraphTypeListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 关联资产分类id
     */
    @Schema(description = "关联资产分类id")
    private String typeId;

    /**
     * 图形分类编码
     */
    @Schema(description = "图形分类编码")
    private String code;

    /**
     * 图形分类名称
     */
    @Schema(description = "图形分类名称")
    private String name;

}
