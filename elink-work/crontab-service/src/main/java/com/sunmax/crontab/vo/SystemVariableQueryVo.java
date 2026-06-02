package com.sunmax.crontab.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SystemVariableQueryVo", description = "系统变量列表查询信息参数")
public class SystemVariableQueryVo {

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

    /**
     * 关键字
     */
    @ApiModelProperty("关键字")
    private String keyword;

    /**
     * 关键字类型 1-变量名称 2-变量标识
     */
    @ApiModelProperty(value = "关键字类型 1-变量名称 2-变量标识")
    private Integer keywordType;

    /**
     * 变量类型 1-设备类型 2-站点类型
     */
    @ApiModelProperty("变量类型 1-设备类型 2-站点类型")
    private String varType;
}
