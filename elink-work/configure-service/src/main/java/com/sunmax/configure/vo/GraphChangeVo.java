package com.sunmax.configure.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "图模编辑参数实体类")
public class GraphChangeVo {

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
     * 类型 1-文件夹 2-图模文件
     */
    @Schema(description = "类型 1-文件夹 2-图模文件")
    private Integer type;

    /**
     * 状态 0-无 1-有更新 2-已发布
     */
    @Schema(description = "状态 0-无 1-有更新 2-已发布")
    private Integer status;

    /**
     * 锁定状态 0-未锁定 1-锁定
     */
    @Schema(description = "锁定状态 0-未锁定 1-锁定")
    private Integer lockStatus;

    /**
     * 父级id
     */
    @Schema(description = "父级id")
    private String parentId;

    /**
     * 域名id
     */
    @Schema(description = "域名id")
    private String domainId;

    /**
     * 站点id
     */
    @Schema(description = "站点id")
    private String siteId;

    /**
     * 设备变量标识
     */
    @Schema(description = "设备变量标识")
    private String deviceVariables;

    /**
     * 用户id
     */
    @Schema(description = "用户id")
    private String userId;

    /**
     * 编辑类型 1-新增 2-移动 3-重命名 4-锁定状态 5-保存 6-发布 7-关联站点
     */
    @Schema(description = "编辑类型 1-新增 2-移动 3-重命名 4-锁定状态 5-保存 6-发布 7-关联站点")
    private Integer updateType;

}
