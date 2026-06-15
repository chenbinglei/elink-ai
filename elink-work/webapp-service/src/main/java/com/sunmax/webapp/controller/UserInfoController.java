package com.sunmax.webapp.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.SecretUtil;
import com.sunmax.common.vo.together.AppletUserChangeVo;
import com.sunmax.webapp.dto.AppletDisWalletDto;
import com.sunmax.webapp.dto.AppletTradeListDto;
import com.sunmax.webapp.dto.WechatMchTransferDto;
import com.sunmax.webapp.service.UserInfoService;
import com.sunmax.webapp.service.feign.SystemService;
import com.sunmax.webapp.vo.AppletTradeQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("userInfo")
@Tag(name = "用户信息管理")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private SystemService systemService;

    @PostMapping("queryAppletUserInfoById")
    @Operation(summary = "根据小程序用户id查询基本信息")
    
    @Parameters({
            @Parameter(name = "appletUserId", description = "小程序用户id")
    })
    public ResponseResult<AppletUserInfoDto> queryAppletUserInfoById(String appletUserId) {
        /*appletUserId = SecretUtil.desEncrypt(appletUserId);
        if (StringUtil.isEmpty(appletUserId)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }*/
        return userInfoService.queryAppletUserInfoById(appletUserId);
    }

    @PostMapping("updateAppletUser")
    @Operation(summary = "编辑小程序用户")
    
    public ResponseResult<String> updateAppletUser(AppletUserChangeVo appletUserChangeVo) {
        return userInfoService.updateAppletUser(appletUserChangeVo);
    }

    @PostMapping("findAppletByAppletCode")
    @Operation(summary = "根据小程序id查询小程序数据")
    
    public ResponseResult<AppletDto> findAppletByAppletCode(String appletCode) {
        AppletDto appletDto = systemService.findAppletByAppletCode(appletCode).getData();
        appletDto.setAppletSecret(null);
        appletDto.setTencentSecret(null);
        return ResponseResult.ok(appletDto);
    }

    @PostMapping("findAppletDisWalletListById")
    @Operation(summary = "根据小程序用户id查询用户V2G钱包列表")
    
    @Parameter(name = "appletUserId", description = "小程序用户唯一id")
    public ResponseResult<List<AppletDisWalletDto>> findAppletDisWalletListById(String appletUserId) {
        return userInfoService.findAppletDisWalletListById(appletUserId);
    }

    @PostMapping("submitAppletCancel")
    @Operation(summary = "提交小程序用户注销申请")
    
    @Parameters({
            @Parameter(name = "appletUserId", description = "小程序用户id"),
            @Parameter(name = "appletName", description = "小程序名称")
    })
    public ResponseResult<String> submitAppletCancel(String appletUserId, String appletName) {
        return userInfoService.submitAppletCancel(appletUserId, appletName);
    }

    @PostMapping("updateAppletUserPhoneById")
    @Operation(summary = "根据小程序用户id更新手机号")
    
    @Parameters({
            @Parameter(name = "appletUserId", description = "小程序用户id"),
            @Parameter(name = "phoneNum", description = "手机号")
    })
    public ResponseResult<String> updateAppletUserPhoneById(String appletUserId, String phoneNum) {
        phoneNum = SecretUtil.desEncrypt(phoneNum);
        return userInfoService.updateAppletUserPhoneById(appletUserId, phoneNum);
    }

    @PostMapping("withdrawMoney")
    @Operation(summary = "小程序用户提现放电收益")
    
    @Parameters({
            @Parameter(name = "appletUserId", description = "小程序用户id"),
            @Parameter(name = "disWalletId", description = "小程序放电钱包id"),
            @Parameter(name = "money", description = "提现金额")
    })
    public ResponseResult<WechatMchTransferDto> withdrawAppletUserMoney(String appletUserId, String disWalletId, BigDecimal money) {
        return userInfoService.withdrawAppletUserMoney(appletUserId, disWalletId, money);
    }

    @PostMapping("queryAppletTradeList")
    @Operation(summary = "查询小程序用户交易明细列表")
    
    public ResponseResult<List<AppletTradeListDto>> queryAppletTradeList(AppletTradeQueryVo appletTradeQueryVo) {
        return userInfoService.queryAppletTradeList(appletTradeQueryVo);
    }

}
