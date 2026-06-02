package com.sunmax.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunmax.common.util.ResponseResult;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *@brief
 *@author xt
 *@date 2021/6/8 11:48
 */
@Component
public class SMAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ResponseResult<ObjectMapper> responseVo = new ResponseResult<>(ResponseResult.CodeStatus.ACCESS_FAIL,"无权访问,请联系管理员开通权限");
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), responseVo);
    }
}
