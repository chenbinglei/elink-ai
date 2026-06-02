package com.sunmax.webapp.service;

import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppletUserChangeVo;
import com.sunmax.webapp.dto.AppletDisWalletDto;
import com.sunmax.webapp.dto.AppletTradeListDto;
import com.sunmax.webapp.dto.WechatMchTransferDto;
import com.sunmax.webapp.vo.AppletTradeQueryVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserInfoService {

    /**
     * 根据小程序用户id查询基本信息
     * @param appletUserId 小程序用户id
     * @return
     */
    ResponseResult<AppletUserInfoDto> queryAppletUserInfoById(String appletUserId);

    /**
     * 编辑小程序用户
     * @param appletUserChangeVo
     * @return
     */
    ResponseResult<String> updateAppletUser(AppletUserChangeVo appletUserChangeVo);

    /**
     * 微信小程序登录
     * @param parameters 登录参数
     * @return
     */
    ResponseResult<Map<String, Object>> postAccessToken(Map<String, String> parameters);

    /**
     * 根据小程序用户id查询用户V2G钱包列表
     * @param appletUserId
     * @return
     */
    ResponseResult<List<AppletDisWalletDto>> findAppletDisWalletListById(String appletUserId);

    /**
     * 提交小程序用户注销申请
     * @param appletUserId
     * @param appletName
     * @return
     */
    ResponseResult<String> submitAppletCancel(String appletUserId, String appletName);

    /**
     * 根据小程序用户id更新手机号
     * @param appletUserId
     * @param phoneNum
     * @return
     */
    ResponseResult<String> updateAppletUserPhoneById(String appletUserId, String phoneNum);

    /**
     * 小程序用户提现放电收益
     * @param disWalletId 用户放电收益id
     * @param money 提现金额
     * @return 状态码
     */
    ResponseResult<WechatMchTransferDto> withdrawAppletUserMoney(String appletUserId, String disWalletId, BigDecimal money);

    /**
     * 查询小程序用户交易列表
     * @param appletTradeQueryVo 查询参数
     * @return 交易明细列表
     */
    ResponseResult<List<AppletTradeListDto>> queryAppletTradeList(AppletTradeQueryVo appletTradeQueryVo);

}
