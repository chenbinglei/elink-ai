package com.sunmax.webapp.util;

import com.sunmax.webapp.entity.trade.RechargeTradeEntity;

import java.math.BigDecimal;

public class TradeCreateUtil {

    /**
     * 创建会员支付交易明细
     * @param orderNum 订单号
     * @param tradeType 交易类型 1-充电预付 2-充电退款
     * @param tradeWay 交易方式 1-微信 2-支付宝 3-银联商户
     * @param tradeMoney 交易金额
     * @param appletUserId 小程序用户id
     * @param siteId 站点id
     * @return 交易明细
     */
    public static RechargeTradeEntity createTrade(String orderNum, Integer tradeType, Integer tradeWay, BigDecimal tradeMoney,
                                                  String appletUserId, String siteId, String accountId) {
        //创建充电交易明细
        RechargeTradeEntity tradeDetail = new RechargeTradeEntity();
        tradeDetail.setOrderNum(orderNum);
        tradeDetail.setTradeMoney(tradeMoney);
        tradeDetail.setTradeType(tradeType);
        tradeDetail.setTradeWay(tradeWay);
        tradeDetail.setTradeStatus(1);
        tradeDetail.setDetailType(tradeType);
        tradeDetail.setAppletUserId(appletUserId);
        tradeDetail.setSiteId(siteId);
        tradeDetail.setAccountId(accountId);
        return tradeDetail;
    }

}
