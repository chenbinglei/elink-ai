package com.sunmax.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 图形分类编辑参数
 */
@Data
@ApiModel(value = "GraphTypeChangeVo", description = "图形分类编辑参数")
public class GraphTypeChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 关联资产分类id
     */
    @ApiModelProperty(value = "关联资产分类id", required = true)
    private String typeId;

    /**
     * 图形分类编码
     */
    @ApiModelProperty(value = "图形分类编码", required = true)
    private String code;

    /**
     * 图形分类名称
     */
    @ApiModelProperty(value = "图形分类名称", required = true)
    private String name;

}
