package com.sunmax.together.dao.order;

import com.sunmax.together.entity.order.SettlementRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface SettlementRecordDao extends JpaRepository<SettlementRecordEntity, String>, JpaSpecificationExecutor<SettlementRecordEntity> {

    //根据订单编号查询结算记录
    SettlementRecordEntity findByOrderNum(String orderNum);

    //根据多个订单编码查询结算记录信息
    List<SettlementRecordEntity> findAllByOrderNumIn(Collection<String> orderNumList);

    //根据多个订单编码和支付方式查询结算记录信息
    List<SettlementRecordEntity> findAllByOrderNumInAndPayWay(List<String> orderNumList, Integer payWay);
}
