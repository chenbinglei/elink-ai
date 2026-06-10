package com.sunmax.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
@Schema(description = "用户登录信息返回实体类")
public class UserLoginDto implements AuthenticatedPrincipal, Serializable {

    private static final long serialVersionUID = 6576377875206310745L;

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
     * 所属租户名称
     */
    @Schema(description = "所属租户名称")
    private String tenantName;

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
     * 是否默认管理员账号 1-是
     */
    @Schema(description = "是否默认管理员账号 1-是")
    private Integer isDefaultAdmin;

    /**
     * 手机号
     */
    @Schema(description = "手机号")
    private String phone;

    /**
     * 用户登录标识 1-账号密码 2-微信小程序 3-支付宝小程序
     */
    private Integer logo;

    /**
     * 登陆校验信息 与前端交互用的
     */
    private String checkMsg;

    /**
     * 登陆校验编码 与前端交互用的
     */
    private Integer checkCode;

    /**
     * 微信或支付宝小程序用户唯一id
     */
    private String appletId;

    /**
     * 小程序主键id
     */
    private String appletKey;

    /**
     * 小程序用户id
     */
    private String appletUserId;

    private Collection<? extends GrantedAuthority> authorities;

    /**
     * 客户端ID（标识登录平台，用于权限校验）
     */
    private String clientId;

    /**
     * 权限数据
     */
    @Schema(description = "权限数据")
    private List<MenuListDto> menuList;

    @Override
    public String getName() {
        return userAccount;
    }
}
