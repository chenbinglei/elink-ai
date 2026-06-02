package com.sunmax.configure.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 图元数据新增编辑实体类
 */
@Data
@ApiModel(value = "GraphPelChangeVo", description = "图元数据新增编辑实体类")
public class PelChangeVo {

    /**
     * 主键id
     */
    @ApiModelProperty(value = "主键id")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 图元类型 1-文件上传 2-自定义图元
     */
    @ApiModelProperty(value = "图元类型 1-文件上传 2-自定义图元")
    private Integer pelType;

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型 1-文件夹 2-图元")
    private Integer type;

    /**
     * 父级id
     */
    @ApiModelProperty("父级id")
    private String parentId;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 编辑类型 1-新增 2-正常编辑 3-重命名 4-移动
     */
    @ApiModelProperty(value = "编辑类型 1-新增 2-正常编辑 3-重命名 4-移动")
    private Integer updateType;

}
