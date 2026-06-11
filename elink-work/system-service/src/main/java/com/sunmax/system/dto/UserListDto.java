package com.sunmax.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户列表返回实体类")
public class UserListDto {

    /**
     * 唯一id
     */
    @Schema(description = "唯一id")
    private String id;

    /**
     * 姓名
     */
    @Schema(description = "姓名")
    private String fullName;

    /**
     * 所属组织id
     */
    @Schema(description = "所属组织id")
    private String organId;

    /**
     * 所属组织名称
     */
    @Schema(description = "所属组织名称")
    private String organName;

    /**
     * 所属用户组id
     */
    @Schema(description = "所属用户组id")
    private String groupId;

    /**
     * 所属用户组名称
     */
    @Schema(description = "所属用户组名称")
    private String groupName;

    /**
     * 角色 0-平台管理员 1-管理员 2-普通用户
     */
    @Schema(description = "角色 0-平台管理员 1-管理员 2-普通用户")
    private Integer userRole;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private String userAccount;

    /**
     * 密码
     */
    @Schema(description = "密码")
    private String password;

    /**
     * 电话
     */
    @Schema(description = "电话")
    private String phone;

    /**
     * 用户头像
     */
    @Schema(description = "用户头像")
    private String userProfile;

    /**
     * 用户状态 0-关闭 1-开启
     */
    @Schema(description = "用户状态 0-关闭 1-开启")
    private Integer userState;

    /**
     * 账号到期日
     */
    @Schema(description = "账号到期日")
    private String expireDate;

    /**
     * 账号日期状态 1-生效中 2-已过期
     */
    @Schema(description = "账号日期状态 1-生效中 2-已过期")
    private Integer expireState;

    /**
     * 是否默认管理员账号 1-是
     */
    @Schema(description = "是否默认管理员账号 1-是")
    private Integer isDefaultAdmin;
}
