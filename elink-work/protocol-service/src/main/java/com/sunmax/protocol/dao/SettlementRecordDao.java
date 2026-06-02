package com.sunmax.protocol.dao;

import com.sunmax.common.dao.base.BaseDao;
import com.sunmax.protocol.entity.SettlementRecordEntity;

public interface SettlementRecordDao extends BaseDao<SettlementRecordEntity, String> {

    //根据订单编号查询结算记录数据
    SettlementRecordEntity findByOrderNum(String orderNum);

}
