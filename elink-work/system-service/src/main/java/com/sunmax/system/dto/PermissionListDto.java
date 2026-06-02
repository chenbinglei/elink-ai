package com.sunmax.system.dto;

import com.sunmax.system.entity.PermissionEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 权限列表数据返回实体类
 */
@Data
@ApiModel("permissionListDto")
public class PermissionListDto {

    /**
     * 权限id
     */
    @ApiModelProperty("权限id")
    private String id;

    /**
     * 权限编号
     */
    @ApiModelProperty("权限编号")
    private String permissionNumber;

    /**
     * 权限类型 1-页面 2-控件
     */
    @ApiModelProperty("权限类型 1-页面 2-控件")
    private Integer permissionType;

    /**
     * 权限编码
     */
    @ApiModelProperty("权限编码")
    private String permissionCode;

    /**
     * 权限名称
     */
    @ApiModelProperty("权限名称")
    private String permissionName;

    /**
     * URL
     */
    @ApiModelProperty("URL")
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

    /**
     * 操作 1-开启 2-关闭
     */
    @ApiModelProperty(value = "操作 1-开启 2-关闭")
    private Integer operate;

    public PermissionListDto(PermissionEntity entity) {
        BeanUtils.copyProperties(entity,this);
    }

}
