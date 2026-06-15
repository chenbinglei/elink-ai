package com.sunmax.system.dto;

import com.sunmax.system.entity.PermissionEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 权限列表数据返回实体类
 */
@Data
@Schema(description = "permissionListDto")
public class PermissionListDto {

    /**
     * 权限id
     */
    @Schema(description = "权限id")
    private String id;

    /**
     * 权限编号
     */
    @Schema(description = "权限编号")
    private String permissionNumber;

    /**
     * 权限类型 1-页面 2-控件
     */
    @Schema(description = "权限类型 1-页面 2-控件")
    private Integer permissionType;

    /**
     * 权限编码
     */
    @Schema(description = "权限编码")
    private String permissionCode;

    /**
     * 权限名称
     */
    @Schema(description = "权限名称")
    private String permissionName;

    /**
     * URL
     */
    @Schema(description = "URL")
    private String url;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private String parentId;

    /**
     * 目录顺序
     */
    @Schema(description = "目录顺序")
    private Integer directoryDesc;

    /**
     * 图标路径
     */
    @Schema(description = "图标路径")
    private String iconPath;

    /**
     * 说明
     */
    @Schema(description = "说明")
    private String explanation;

    /**
     * 所属模块id
     */
    @Schema(description = "所属模块id")
    private String moduleId;

    /**
     * 权限状态 1-显示 2-不显示
     */
    @Schema(description = "权限状态 1-显示 2-不显示")
    private Integer isHidden;

    /**
     * 是否有界面 1-是 2-否
     */
    @Schema(description = "是否有界面 1-是 2-否")
    private Integer isLayout;

    /**
     * 操作 1-开启 2-关闭
     */
    @Schema(description = "操作 1-开启 2-关闭")
    private Integer operate;

    public PermissionListDto(PermissionEntity entity) {
        BeanUtils.copyProperties(entity,this);
    }

}
