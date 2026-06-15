package com.sunmax.common.dto.device;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 资产分类返回实体类
 */
@Data
@Schema(description = "资产分类返回实体类")
public class AssetTypeDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 资产分类名称
     */
    @Schema(description = "资产分类名称")
    private String typeName;

    /**
     * 父节点id
     */
    @Schema(description = "父节点id")
    private String parentId;

    /**
     * 类型 1-目录 2-资产
     */
    @Schema(description = "类型 1-目录 2-资产")
    private Integer type;

}
