package com.sunmax.auth.service;

import com.sunmax.common.util.ResponseResult;

public interface UserService {

    /**
     * 校验用户手机号是否存在
     * @param phone 手机号
     * @return 状态码
     */
    ResponseResult<String> findUserIsExitByPhone(String userAccount, String phone);

    /**
     * 修改用户密码
     * @param userAccount 用户账号
     * @param phone 用户手机号
     * @param password 密码
     * @return 状态码
     */
    ResponseResult<String> updateUserPassword(String userAccount, String phone, String password);

}
