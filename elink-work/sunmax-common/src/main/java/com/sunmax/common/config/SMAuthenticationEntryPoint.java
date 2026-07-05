package com.sunmax.common.config;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.util.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@Slf4j
public class SMAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {
        try {
            // 返回 HTTP 401（保留业务码 9999 供前端兼容）
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("Content-Type", "application/json;charset=utf-8");
            // CORS 由 Gateway 统一处理，下游服务不再设置 Access-Control-Allow-Origin
            // 避免与 Gateway CORS 配置冲突（* 与 credentials=true 不兼容）
            ResponseResult<Object> responseResult = new ResponseResult<>();
            responseResult.setCode(9999);
            responseResult.setMessage("未登录或登录失效");
            responseResult.setSuccess(false);
            response.getWriter().print(JSON.toJSONString(responseResult));
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("认证失败", e);
        }
    }
}
