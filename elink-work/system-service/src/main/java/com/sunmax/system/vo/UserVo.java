package com.sunmax.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户新增或编辑参数")
public class UserVo {

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
     * 所属租户id
     */
    @Schema(description = "所属租户id")
    private String tenantId;

    /**
     * 所属组织id
     */
    @Schema(description = "所属组织id")
    private String organId;

    /**
     * 所属用户组id
     */
    @Schema(description = "所属用户组id")
    private String groupId;

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

}
