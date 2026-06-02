package com.sunmax.device.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GraphListDto", description = "图形列表返回实体类")
public class GraphListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**
     * 图形分类id
     */
    @ApiModelProperty(value = "图形分类id")
    private String graphTypeId;

    /**
     * 图形分类名称
     */
    @ApiModelProperty(value = "图形分类名称")
    private String graphTypeName;

    /**
     * 图形名称
     */
    @ApiModelProperty(value = "图形名称")
    private String graphName;

    /**
     * 图形URL
     */
    @ApiModelProperty(value = "图形URL")
    private String graphUrl;

    /**
     * 默认图形 1-默认 2-不默认
     */
    @ApiModelProperty(value = "默认图形 1-默认 2-不默认")
    private Integer isDefault;

}
