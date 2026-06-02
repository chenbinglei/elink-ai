package com.sunmax.webapp.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.webapp.service.WechatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信公众号控制层
 */
@CrossOrigin
@RestController
@RequestMapping("wechat")
@Api(tags = "微信公众号管理")
public class WechatController {

    @Autowired
    private WechatService wechatService;

    /**
     * 正确响应微信发送的Token验证,注意 这里是 get请求
     */
    @GetMapping(value = "verifyUrl")
    @ApiOperation("验证url")
    public String verifyUrl(@RequestParam Map<String, String> params) {
        return wechatService.verifyUrl(params);
    }

    @PostMapping(value = "getWechatSignature")
    @ApiOperation("获取微信签名")
    public ResponseResult<Map<String,String>> getWechatSignature(String url, String appletKey) {
        //处理支付后的业务逻辑
        return wechatService.getWechatSignature(url, appletKey);
    }

}
