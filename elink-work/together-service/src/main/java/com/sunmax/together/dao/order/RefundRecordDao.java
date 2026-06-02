package com.sunmax.together.dao.order;

import com.sunmax.together.entity.order.RefundRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RefundRecordDao extends JpaRepository<RefundRecordEntity, String>, JpaSpecificationExecutor<RefundRecordEntity> {

    //根据订单号查询退款记录列表
    List<RefundRecordEntity> findAllByOrderNum(String orderNum);
}
