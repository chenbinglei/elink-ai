package com.sunmax.protocol.dao;

import com.sunmax.common.dao.base.BaseDao;
import com.sunmax.protocol.entity.RepairOrderRecordEntity;

import java.util.Collection;
import java.util.List;

public interface RepairOrderRecordDao extends BaseDao<RepairOrderRecordEntity, String> {

    //根据多个订单号查询补单记录数据
    List<RepairOrderRecordEntity> findByOrderNumIn(Collection<String> orderNum);

}
