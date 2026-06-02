package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/10/717:14
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "OperatorListQueryVo", description = "运营商列表查询实体类")
public class OperatorListQueryVo {

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
     * 运营商名称
     */
    @ApiModelProperty(value = "运营商名称")
    private String operatorName;
}
