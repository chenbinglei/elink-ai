package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 权限编辑信息参数
 */
@Data
@ApiModel(value = "permissionChangeVo", description = "权限编辑信息参数")
public class PermissionChangeVo {

    /**
     * 权限id
     */
    @ApiModelProperty("权限id")
    private String id;

    /**
     * 权限类型 1-页面 2-控件
     */
    @ApiModelProperty(value = "权限类型 1-页面 2-控件", required = true)
    private Integer permissionType;

    /**
     * 权限编码
     */
    @ApiModelProperty(value = "权限编码", required = true)
    private String permissionCode;

    /**
     * 权限名称
     */
    @ApiModelProperty(value = "权限名称", required = true)
    private String permissionName;

    /**
     * URL
     */
    @ApiModelProperty(value = "URL", required = true)
    private String url;

    /**
     * 父级id
     */
    @ApiModelProperty("父级id")
    private String parentId;

    /**
     * 目录顺序
     */
    @ApiModelProperty("目录顺序")
    private Integer directoryDesc;

    /**
     * 图标路径
     */
    @ApiModelProperty("图标路径")
    private String iconPath;

    /**
     * 说明
     */
    @ApiModelProperty("说明")
    private String explanation;

    /**
     * 所属模块id
     */
    @ApiModelProperty("所属模块id")
    private String moduleId;

    /**
     * 权限状态 1-显示 2-不显示
     */
    @ApiModelProperty("权限状态 1-显示 2-不显示")
    private Integer isHidden;

    /**
     * 是否有界面 1-是 2-否
     */
    @ApiModelProperty("是否有界面 1-是 2-否")
    private Integer isLayout;

}
