package com.sunmax.gateway.controller;

import com.sunmax.gateway.util.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("defaultFallback")
    public ResponseResult<String> defaultFallback() {
        return new ResponseResult<>(400,"服务正忙,请稍后再试");
   }


}
