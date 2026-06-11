package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户组新增或编辑参数")
public class UserListQueryVo {

    /**
     * 当前页
     */
    @Schema(description = "当前页")
    private Integer page;

    /**
     * 当前页条数
     */
    @Schema(description = "当前页条数")
    private Integer size;

    /**
     * 用户组类型 1-未分组 2-已分组
     */
    @Schema(description = "用户组类型 1-未分组 2-已分组")
    private Integer groupType;

    /**
     * 用户组id(如果类型为2，这里传用户组id)
     */
    @Schema(description = "用户组id(如果类型为2，这里传用户组id)")
    private String groupId;

    /**
     * 组织架构id
     */
    @Schema(description = "组织架构id")
    private String organId;

    /**
     * 关键字类型 1-用户名 2-用户ID
     */
    @Schema(description = "关键字类型 1-用户名 2-用户ID")
    private Integer keywordType;

    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;

    /**
     * 角色 1-管理员 2-普通用户
     */
    @Schema(description = "角色 1-管理员 2-普通用户")
    private Integer userRole;

    /**
     * 用户状态 0-关闭 1-开启
     */
    @Schema(description = "用户状态 0-关闭 1-开启")
    private Integer userState;

    /**
     * 账号日期状态 1-生效中 2-已过期
     */
    @Schema(description = "账号日期状态 1-生效中 2-已过期")
    private Integer expireState;

    /**
     * 所属租户id
     */
    @Schema(description = "所属租户id")
    private String tenantId;
}
