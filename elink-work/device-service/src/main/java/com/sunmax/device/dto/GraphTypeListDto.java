package com.sunmax.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GraphTypeListDto", description = "图形分类列表返回实体类")
public class GraphTypeListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联资产分类id
     */
    @ApiModelProperty(value = "关联资产分类id")
    private String typeId;

    /**
     * 图形分类编码
     */
    @ApiModelProperty(value = "图形分类编码")
    private String code;

    /**
     * 图形分类名称
     */
    @ApiModelProperty(value = "图形分类名称")
    private String name;

}
