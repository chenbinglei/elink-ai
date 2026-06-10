package com.sunmax.configure.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 图元数据新增编辑实体类
 */
@Data
@Schema(description = "图元数据新增编辑实体类")
public class PelChangeVo {

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
     * 图元类型 1-文件上传 2-自定义图元
     */
    @Schema(description = "图元类型 1-文件上传 2-自定义图元")
    private Integer pelType;

    /**
     * 类型
     */
    @Schema(description = "类型 1-文件夹 2-图元")
    private Integer type;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private String parentId;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 编辑类型 1-新增 2-正常编辑 3-重命名 4-移动
     */
    @Schema(description = "编辑类型 1-新增 2-正常编辑 3-重命名 4-移动")
    private Integer updateType;

}
