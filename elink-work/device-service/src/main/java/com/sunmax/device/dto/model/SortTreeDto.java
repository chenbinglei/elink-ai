package com.sunmax.device.dto.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "模型分类公共类")
public class SortTreeDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 分类名称
     */
    @Schema(description = "分类名称")
    private String sortName;

    /**
     * 父节点id
     */
    @Schema(description = "父节点id")
    private String parentId;

}
