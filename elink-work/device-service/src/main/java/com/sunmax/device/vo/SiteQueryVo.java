package com.sunmax.device.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SiteQueryVo", description = "站点查询条件参数")
public class SiteQueryVo {

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
     * 关键字类型 1-站点名称 2-站点ID
     */
    @ApiModelProperty(value = "关键字类型 1-站点名称 2-站点ID")
    private Integer keywordType;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;
}
