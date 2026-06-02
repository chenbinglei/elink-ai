package com.sunmax.device.vo.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SortChangeVo", description = "模型分类编辑参数类")
public class SortChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 分类名称
     */
    @ApiModelProperty(value = "分类名称", required = true)
    private String sortName;

    /**
     * 父节点id
     */
    @ApiModelProperty(value = "父节点id")
    private String parentId;
//
//    /**
//     * 分类LOGO
//     */
//    @ApiModelProperty(value = "分类LOGO")
//    private String sortLogo;

}
