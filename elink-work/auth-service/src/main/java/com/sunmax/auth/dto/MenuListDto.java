package com.sunmax.auth.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 功能菜单数据实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuListDto implements Serializable {

    private static final long serialVersionUID = 6576566875206310745L;

    /**
     * 主键id
     */
    @ApiModelProperty("主键id")
    private String id;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 父级id
     */
    @ApiModelProperty("父级id")
    private String parentId;

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
     * URL
     */
    @ApiModelProperty("URL")
    private String url;

    /**
     * 类型 1-页面 2-控件
     */
    @ApiModelProperty("类型 1-页面 2-控件")
    private Integer type;

}
