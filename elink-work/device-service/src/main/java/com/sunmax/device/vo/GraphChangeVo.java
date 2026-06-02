package com.sunmax.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 图形编辑参数
 */
@Data
@ApiModel(value = "GraphChangeVo", description = "图形编辑参数")
public class GraphChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "设备id", required = true)
    private String deviceId;

    /**
     * 图形分类id
     */
    @ApiModelProperty(value = "图形分类id", required = true)
    private String graphTypeId;

    /**
     * 图形名称
     */
    @ApiModelProperty(value = "图形名称", required = true)
    private String graphName;

    /**
     * 图形URL
     */
    @ApiModelProperty(value = "图形URL", required = true)
    private String graphUrl;

    /**
     * 默认图形 1-默认 2-不默认
     */
    @ApiModelProperty(value = "默认图形 1-默认 2-不默认", required = true)
    private Integer isDefault;

}
