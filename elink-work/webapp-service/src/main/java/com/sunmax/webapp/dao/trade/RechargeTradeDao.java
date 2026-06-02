package com.sunmax.webapp.dao.trade;

import com.sunmax.common.dao.base.BaseDao;
import com.sunmax.webapp.entity.trade.RechargeTradeEntity;

import java.util.Collection;
import java.util.List;

public interface RechargeTradeDao extends BaseDao<RechargeTradeEntity, String> {

    //根据订单号和交易类型查询充电订单交易数据
    RechargeTradeEntity findAllByOrderNumAndTradeTypeAndTradeStatus(String orderNum, Integer tradeType, Integer tradeStatus);

    //根据订单号和交易类型查询充电订单交易数据
    List<RechargeTradeEntity> findAllByOrderNumInAndTradeTypeAndTradeStatus(Collection<String> orderNums, Integer tradeType, Integer tradeStatus);

    //根据订单号和交易类型查询充电订单交易数据
    List<RechargeTradeEntity> findAllByOrderNumAndTradeType(String orderNum, Integer tradeType);

    //根据流水号查询充电订单交易数据
    RechargeTradeEntity findAllByFlowNum(String flowNum);

}
