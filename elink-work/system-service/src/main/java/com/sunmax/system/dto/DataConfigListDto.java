package com.sunmax.system.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DataConfigListDto", description = "数据配置列表")
public class DataConfigListDto {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 数据转发id
     */
    @ApiModelProperty(value = "数据转发id")
    private String forwardId;

    /**
     * 站点id
     */
    @ApiModelProperty(value = "站点id")
    private String siteId;

    /**
     * 站点名称
     */
    @ApiModelProperty(value = "站点名称")
    private String siteName;

    /**
     * 动态配置
     */
    @ApiModelProperty(value = "动态配置")
    private String dynamicConfigs;

}
