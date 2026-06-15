package com.sunmax.auth.controller;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 当前用户信息控制器
 * 提供获取当前登录用户信息的接口
 * CORS 由 Gateway 统一处理，下游服务不再设置 @CrossOrigin
 */
@RestController
@Tag(name = "当前用户信息")
public class MainController {

    /**
     * 获取当前登录用户信息
     * 通过RedisTokenAuthenticationFilter认证后，Authentication的details中包含用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/current-info")
    @Operation(summary = "获取当前登录用户信息")
    public ResponseResult<JSONObject> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseResult.error("未登录或登录已失效");
        }

        JSONObject result = new JSONObject();
        result.put("userAccount", authentication.getName());
        result.put("authorities", authentication.getAuthorities());

        // 如果details是JSONObject（来自RedisTokenAuthenticationFilter），提取完整用户信息
        if (authentication.getDetails() instanceof JSONObject) {
            JSONObject userDetails = (JSONObject) authentication.getDetails();
            result.putAll(userDetails);
        }

        return ResponseResult.ok(result);
    }
}
