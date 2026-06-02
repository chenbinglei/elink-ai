package com.sunmax.devops.service;

import com.sunmax.common.util.ResponseResult;

import java.util.Map;

public interface UserInfoService {

    /**
     * 微信小程序登录
     * @param parameters 登录参数
     * @return
     */
    ResponseResult<Map<String, Object>> postAccessToken(Map<String, String> parameters);

}
