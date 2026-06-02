package com.sunmax.common.config;

import com.alibaba.fastjson.JSON;
import com.sunmax.common.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class SMAuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    private final WebResponseExceptionTranslator<?> exceptionTranslator = new DefaultWebResponseExceptionTranslator();

    @Autowired
    RestTemplate restTemplate;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        try {
            //解析异常，如果是401则处理
            ResponseEntity<?> result = exceptionTranslator.translate(authException);
            if (result.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                response.setStatus(200);
                response.setHeader("Content-Type", "application/json;charset=utf-8");
                response.setHeader("Access-Control-Allow-Origin", "*");
                ResponseResult<Object> responseResult = new ResponseResult<>();
                responseResult.setCode(9999);
                responseResult.setMessage("未登录或登陆失效");
                responseResult.setSuccess(false);
                response.getWriter().print(JSON.toJSONString(responseResult));
                response.getWriter().flush();
            } else {
                //如果不是401异常，则以默认的方法继续处理其他异常
                super.commence(request, response, authException);
            }
        } catch (Exception e) {
            log.error("认证失败", e);
        }

    }
}
