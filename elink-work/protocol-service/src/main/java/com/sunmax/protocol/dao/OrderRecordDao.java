package com.sunmax.protocol.dao;

import com.sunmax.common.dao.base.BaseDao;
import com.sunmax.protocol.entity.OrderRecordEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRecordDao extends BaseDao<OrderRecordEntity, String> {

    //根据订单号查询订单记录
    OrderRecordEntity findByOrderNum(String orderNum);

    //根据电桩编号和订单状态查询订单记录
    List<OrderRecordEntity> findAllByPileCodeAndOrderStatus(String pileCode, Integer orderStatus);

    //根据多个订单状态查询订单记录数据
    List<OrderRecordEntity> findAllByOrderStatusIn(List<Integer> orderStatusList);

    //根据多个订单状态查询订单记录数据
    List<OrderRecordEntity> findAllByOrderStatusAndUpdateTimeBetween(Integer orderStatus, LocalDateTime startTime, LocalDateTime endTime);

}
