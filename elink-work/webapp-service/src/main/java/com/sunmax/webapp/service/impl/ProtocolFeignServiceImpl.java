package com.sunmax.webapp.service.impl;

import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.SiteAccountDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.together.UserDisWalletChangeVo;
import com.sunmax.common.vo.webapp.WalletBalanceVo;
import com.sunmax.webapp.dao.trade.DischargeTradeDao;
import com.sunmax.webapp.entity.trade.DischargeTradeEntity;
import com.sunmax.webapp.service.ProtocolFeignService;
import com.sunmax.webapp.service.feign.TogetherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class ProtocolFeignServiceImpl implements ProtocolFeignService {

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private DischargeTradeDao dischargeTradeDao;

    @Override
    public ResponseResult<Void> updateWalletBalance(WalletBalanceVo walletBalanceVo) {
        try {
            if (StringUtil.isEmpty(walletBalanceVo.getTradeMoney()) || walletBalanceVo.getTradeMoney().compareTo(BigDecimal.ZERO) <= 0) {
                log.info("更新用户V2G收益钱包失败，原因是：交易金额异常, 用户手机号：{}, 交易金额:{}, 站点id：{}", walletBalanceVo.getPhoneNum(),
                        walletBalanceVo.getTradeMoney(), walletBalanceVo.getSiteId());
                return ResponseResult.paramError(ResponseResult.FAIL);
            }
            AppletUserInfoDto appletUser = togetherService.queryAppletUserInfoByPhoneNum(walletBalanceVo.getPhoneNum()).getData();
            if (appletUser != null && StringUtil.isNotEmpty(appletUser.getId())) {
                //1.校验站点配置付款账户是否存在
                String accountId = null;
                List<SiteAccountDto> siteAccountList = togetherService.findSiteAccountListBySiteIds(Collections.singleton(walletBalanceVo.getSiteId()), 1)
                        .getData().get(walletBalanceVo.getSiteId());
                if (CollectionUtils.isNotEmpty(siteAccountList)) {
                    accountId = siteAccountList.get(0).getAccountId();
                }
                if(StringUtil.isEmpty(accountId)) {
                    log.info("更新用户V2G收益钱包失败，原因是：未找到该站点配置的付款账户信息, 用户手机号：{}, 交易金额:{}, 站点id：{}", walletBalanceVo.getPhoneNum(),
                            walletBalanceVo.getTradeMoney(), walletBalanceVo.getSiteId());
                    return ResponseResult.paramError(ResponseResult.FAIL);
                }
                //3.添加放电交易记录
                DischargeTradeEntity dischargeTrade = dischargeTradeDao.findByOrderNumAndTradeType(walletBalanceVo.getOrderNum(), walletBalanceVo.getTradeType());
                //用户余额牵扯交易金额
                BigDecimal tradeMoney;
                if (dischargeTrade == null) {
                    dischargeTrade = new DischargeTradeEntity();
                    tradeMoney = walletBalanceVo.getTradeMoney();
                } else {
                    //记录上报 订单存在 则更新钱包交易金额
                    tradeMoney = walletBalanceVo.getTradeMoney().subtract(dischargeTrade.getTradeMoney());
                }
                //3.更新小程序用户钱包
                BigDecimal tradeBalance = null;
                if (walletBalanceVo.getTradeType() == 1) {
                    tradeBalance = togetherService.updateUserDisWallet(UserDisWalletChangeVo.builder().appletUserId(appletUser.getId())
                            .tradeType(1).tradeMoney(tradeMoney).accountId(accountId).build()).getData();
                }

                dischargeTrade.setOrderNum(walletBalanceVo.getOrderNum());
                dischargeTrade.setTradeMoney(walletBalanceVo.getTradeMoney());
                dischargeTrade.setTradeType(walletBalanceVo.getTradeType());
                dischargeTrade.setTradeStatus(2);
                dischargeTrade.setTradeWay(walletBalanceVo.getTradeWay());
                dischargeTrade.setTradeBalance(tradeBalance);
                dischargeTrade.setAccountId(accountId);
                dischargeTrade.setAppletUserId(appletUser.getId());
                dischargeTrade.setSiteId(walletBalanceVo.getSiteId());
                dischargeTradeDao.save(dischargeTrade);
                return ResponseResult.ok();
            } else {
                log.info("更新用户V2G收益钱包失败，原因是：未找到该用户信息, 用户手机号：{}, 交易金额:{}, 站点id：{}", walletBalanceVo.getPhoneNum(),
                        walletBalanceVo.getTradeMoney(), walletBalanceVo.getSiteId());
            }
        } catch (Exception e) {
            log.error("更新用户V2G收益钱包失败，原因是：{}", e.getMessage());
        }
        return ResponseResult.paramError(ResponseResult.FAIL);
    }

}
