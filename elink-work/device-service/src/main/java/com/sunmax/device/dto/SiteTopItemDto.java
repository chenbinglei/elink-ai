package com.sunmax.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据项配置(对象格式)
 */
@Data
@ApiModel(value = "SiteTopItemDto", description = "站点拓扑节点数据项配置返回实体类")
public class SiteTopItemDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 数据编号
     */
    @ApiModelProperty(value = "数据编号", required = true)
    private String dataCode;

    /**
     * 数据名称
     */
    @ApiModelProperty(value = "数据名称", required = true)
    private String dataName;

    /**
     * 数据展示名称
     */
    @ApiModelProperty(value = "数据展示名称", required = true)
    private String showName;

    /**
     * 数据展示类型 1-显示 2-隐藏
     */
    @ApiModelProperty(value = "数据展示类型 1-显示 2-隐藏", required = true)
    private Integer showType;

    /**
     * 数据位置类型 1-上 2-下 3-左 4-右
     */
    @ApiModelProperty(value = "数据位置类型 1-上 2-下 3-左 4-右", required = true)
    private Integer positionType;

}
