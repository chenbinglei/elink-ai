package com.sunmax.devops.service.impl;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.devops.service.UserInfoService;
import com.sunmax.devops.service.feign.SauthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private SauthService sauthService;

    @Override
    public ResponseResult<Map<String, Object>> postAccessToken(Map<String, String> parameters) {
        return sauthService.postAccessToken(parameters);
    }
}
