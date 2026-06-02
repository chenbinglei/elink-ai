package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserGroupVo", description = "用户组新增或编辑参数")
public class UserListQueryVo {

    /**
     * 当前页
     */
    @ApiModelProperty(value = "当前页", required = true)
    private Integer page;

    /**
     * 当前页条数
     */
    @ApiModelProperty(value = "当前页条数", required = true)
    private Integer size;

    /**
     * 用户组类型 1-未分组 2-已分组
     */
    @ApiModelProperty(value = "用户组类型 1-未分组 2-已分组")
    private Integer groupType;

    /**
     * 用户组id(如果类型为2，这里传用户组id)
     */
    @ApiModelProperty(value = "用户组id(如果类型为2，这里传用户组id)")
    private String groupId;

    /**
     * 组织架构id
     */
    @ApiModelProperty(value = "组织架构id")
    private String organId;

    /**
     * 关键字类型 1-用户名 2-用户ID
     */
    @ApiModelProperty(value = "关键字类型 1-用户名 2-用户ID")
    private Integer keywordType;

    /**
     * 关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;

    /**
     * 角色 1-管理员 2-普通用户
     */
    @ApiModelProperty(value = "角色 1-管理员 2-普通用户")
    private Integer userRole;

    /**
     * 用户状态 0-关闭 1-开启
     */
    @ApiModelProperty(value = "用户状态 0-关闭 1-开启")
    private Integer userState;

    /**
     * 账号日期状态 1-生效中 2-已过期
     */
    @ApiModelProperty(value = "账号日期状态 1-生效中 2-已过期")
    private Integer expireState;

    /**
     * 所属租户id
     */
    @ApiModelProperty(value = "所属租户id", required = true)
    private String tenantId;
}
