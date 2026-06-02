package com.sunmax.together.dao.order;

import com.sunmax.together.entity.order.ChargeTariffRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ChargeTariffRecordDao extends JpaRepository<ChargeTariffRecordEntity, String>, JpaSpecificationExecutor<ChargeTariffRecordEntity> {

    /**
     * 查询指定订单计费详情数据
     * @param orderId
     * @return
     */
    List<ChargeTariffRecordEntity> findAllByOrderNum(String orderId);

    //根据多个订单记录编码查询计费详情数据
    List<ChargeTariffRecordEntity> findAllByOrderNumIn(List<String> orderNumList);

    /**
     * 根据订单编码删除指定订单计费详情数据
     * @param orderNum
     */
    void deleteAllByOrderNum(String orderNum);
}
