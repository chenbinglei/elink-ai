package com.sunmax.together.dao.order;

import com.sunmax.together.entity.order.RepairOrderRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RepairOrderRecordDao extends JpaRepository<RepairOrderRecordEntity, String>, JpaSpecificationExecutor<RepairOrderRecordEntity> {

    //根据订单号查询补单记录信息
    List<RepairOrderRecordEntity> findAllByOrderNum(String orderNum);

    //根据多个订单编码查询补单信息
    List<RepairOrderRecordEntity> findAllByOrderNumIn(List<String> orderNumList);
}
