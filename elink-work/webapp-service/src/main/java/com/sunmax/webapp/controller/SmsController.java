package com.sunmax.webapp.controller;

import com.sunmax.common.config.redis.RedisUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.VerifyCodeUtil;
import com.sunmax.webapp.service.SmsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @author yqz
 */
@CrossOrigin
@RestController
@RequestMapping("sms")
@Api(tags = "短信验证模块")
public class SmsController {

    @Autowired
    private SmsService smsService;

    /**
     * 给手机发送验证码
     * @param mobile
     * @return
     */
    @PostMapping("sendSecurityCode")
    @ApiOperation("发送验证码")
    public ResponseResult sendSecurityCode(@RequestParam String mobile){
        String code= VerifyCodeUtil.generateTextCode(0, 6, null);
        try {
            RedisUtil.set(mobile,code,60*5);
            Boolean flag=smsService.sendSms(mobile,code);
            if(flag){
                return new ResponseResult(ResponseResult.CodeStatus.OK,"发送成功");
            }else{
                return new ResponseResult(ResponseResult.CodeStatus.BREAKING,"发送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseResult(ResponseResult.CodeStatus.BREAKING,"发送失败");
        }
    }


    @PostMapping("checkSecurityCode")
    @ApiOperation("校验验证码")
    public ResponseResult checkSecurityCode(@RequestParam String mobile,@RequestParam String smscode){
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(smscode)) {
            //throw new InvalidGrantException("手机号或短信验证码为空");
            return new ResponseResult<>(ResponseResult.CodeStatus.BREAKING,"手机号或短信验证码为空");
        }
        //从redis 中获取存储的验证码（或者内存中 以及jwt 验证）
        String realAuthCode = RedisUtil.get(mobile).toString();
        //监测验证码是否正确
        if(StringUtils.isEmpty(realAuthCode)){
            //throw new InvalidGrantException("验证码过期");
            return new ResponseResult(ResponseResult.CodeStatus.BREAKING,"验证码过期");
        } else if(!smscode.equals(realAuthCode)) {
            // throw new InvalidGrantException("验证码错误");
            return new ResponseResult(ResponseResult.CodeStatus.BREAKING,"验证码错误");
        }else{
            return new ResponseResult(ResponseResult.CodeStatus.OK,"验证码正确");
        }
    }




}
