package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "TenantListQueryVo", description = "租户列表查询参数")
public class TenantListQueryVo {

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
     * 关键字类型 1-租户名称 2-租户ID
     */
    @ApiModelProperty(value = "关键字类型 1-租户名称 2-租户ID")
    private Integer keywordType;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;
}
