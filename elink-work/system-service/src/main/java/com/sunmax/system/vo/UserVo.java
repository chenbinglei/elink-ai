package com.sunmax.system.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "UserVo", description = "用户新增或编辑参数")
public class UserVo {

    /**
     * 唯一id
     */
    @ApiModelProperty(value = "唯一id")
    private String id;

    /**
     * 姓名
     */
    @ApiModelProperty(value = "姓名")
    private String fullName;

    /**
     * 所属租户id
     */
    @ApiModelProperty(value = "所属租户id", required = true)
    private String tenantId;

    /**
     * 所属组织id
     */
    @ApiModelProperty(value = "所属组织id")
    private String organId;

    /**
     * 所属用户组id
     */
    @ApiModelProperty(value = "所属用户组id")
    private String groupId;

    /**
     * 角色 0-平台管理员 1-管理员 2-普通用户
     */
    @ApiModelProperty(value = "角色 0-平台管理员 1-管理员 2-普通用户", required = true)
    private Integer userRole;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号", required = true)
    private String userAccount;

    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    /**
     * 电话
     */
    @ApiModelProperty(value = "电话")
    private String phone;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    private String userProfile;

    /**
     * 用户状态 0-关闭 1-开启
     */
    @ApiModelProperty(value = "用户状态 0-关闭 1-开启", required = true)
    private Integer userState;

    /**
     * 账号到期日
     */
    @ApiModelProperty(value = "账号到期日")
    private String expireDate;

}
