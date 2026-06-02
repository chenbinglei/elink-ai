package com.sunmax.auth.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @Author: xiuho
 * @CreateDate: 2021.3.18
 * @Description:
 */
@CrossOrigin
@RestController
//@RequestMapping("/oauth")
@RequestMapping("/oauth")
@Api(tags = "认证管理")
public class OauthController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @GetMapping("/token")
    @ApiOperation("用户登录Get请求方式")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, Object>> getAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return custom(tokenEndpoint.getAccessToken(principal, parameters).getBody());
    }

    @PostMapping("/token")
    @ApiOperation("用户登录Post请求方式")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, Object>> postAccessToken(Principal principal, @RequestParam Map<String, String> parameters) throws HttpRequestMethodNotSupportedException {
        return custom(tokenEndpoint.postAccessToken(principal, parameters).getBody());
    }

    //定制申请返回实体
    private ResponseResult<Map<String, Object>> custom(OAuth2AccessToken accessToken) {
        DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) accessToken;
        Map<String, Object> data = new LinkedHashMap<>(token.getAdditionalInformation());
        String failMsg = (String) data.get("checkMsg");
        if (failMsg == null) {
            data.put("accessToken", token.getValue());
            if (token.getRefreshToken() != null) {
                data.put("refreshToken", token.getRefreshToken().getValue());
            }
        }
        if (StringUtil.isNotEmpty(failMsg)) {
            return ResponseResult.paramError(failMsg,Integer.parseInt(data.get("checkCode").toString()));
        }
        return ResponseResult.ok(data);
    }

}
