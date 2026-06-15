package com.sunmax.common.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 权限列表返回实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionInfoListDto {

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private String parentId;

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
     * URL
     */
    @Schema(description = "URL")
    private String url;

    /**
     * 类型 1-页面 2-控件
     */
    @Schema(description = "类型 1-页面 2-控件")
    private Integer type;

    /**
     * 客户端id
     */
    @Schema(description = "客户端id")
    private String clientId;

}
