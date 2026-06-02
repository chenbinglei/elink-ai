package com.sunmax.auth.service;

import com.sunmax.auth.dto.UserLoginDto;
import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.util.ResponseResult;

import java.util.List;

/**
 * 用户登录服务层接口
 */
public interface UserLoginService {

    /**
     * web平台用户登录
     *
     * @param userAccount 用户账号
     * @param password    用户密码
     * @param clientId    客户端id 用来标识登录那个平台
     * @return 用户信息
     */
    UserLoginDto loadSysUserByAccountAndPassword(String userAccount, String password, String clientId);

    /**
     * 根据用户账号查询权限数据
     *
     * @param userAccount 用户账号
     * @param clientId 客户端id
     * @return 权限数据
     */
    ResponseResult<List<PermissionInfoListDto>> findPermissionByUserAccount(String userAccount, String clientId);

    /**
     * 微信小程序登录
     *
     * @param code    微信小程序code
     * @param appletKey 小程序id
     * @param encryptedData
     * @param iv
     * @return 用户数据
     */
    UserLoginDto loadUserByAppletCodeAndMobile(String code, String appletKey, String encryptedData, String iv);
}
