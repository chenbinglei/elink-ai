package com.sunmax.common.dto.device;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 资产分类返回实体类
 */
@Data
@ApiModel(value = "资产分类返回实体类")
public class AssetTypeDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 资产分类名称
     */
    @ApiModelProperty(value = "资产分类名称")
    private String typeName;

    /**
     * 父节点id
     */
    @ApiModelProperty(value = "父节点id")
    private String parentId;

    /**
     * 类型 1-目录 2-资产
     */
    @ApiModelProperty(value = "类型 1-目录 2-资产")
    private Integer type;

}
