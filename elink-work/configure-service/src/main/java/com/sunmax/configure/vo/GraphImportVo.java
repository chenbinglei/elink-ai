package com.sunmax.configure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "GraphImportVo", description = "图模导入参数实体类")
public class GraphImportVo {

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称", required = true)
    private String name;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 状态 0-无 1-有更新 2-已发布
     */
    @ApiModelProperty(value = "状态 0-无 1-有更新 2-已发布")
    private Integer status;

}
