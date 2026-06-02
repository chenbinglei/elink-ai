package com.sunmax.device.vo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 参数配置查询实体类
 */
@Data
@ApiModel(value = "ConfigQueryVo", description = "参数配置查询实体类")
public class ConfigQueryVo {

    /**
     * 关联资产分类id
     */
    @ApiModelProperty(value = "关联资产分类id", required = true)
    private String typeId;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

}
