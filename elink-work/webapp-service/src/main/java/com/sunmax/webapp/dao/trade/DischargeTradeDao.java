package com.sunmax.webapp.dao.trade;

import com.sunmax.common.dao.base.BaseDao;
import com.sunmax.webapp.entity.trade.DischargeTradeEntity;

import java.util.List;

public interface DischargeTradeDao extends BaseDao<DischargeTradeEntity, String> {

    //根据订单号和交易类型查询放电交易数据
    DischargeTradeEntity findByOrderNumAndTradeType(String orderNum, Integer tradeType);

    //根据交易流水号查询放电交易数据
    DischargeTradeEntity findAllByFlowNum(String flowNum);

    //根据账户id和用户id和交易类型查询放电交易数据
    List<DischargeTradeEntity> findByAccountIdAndAppletUserIdAndTradeType(String accountId, String appletUserId, Integer tradeType);

}
