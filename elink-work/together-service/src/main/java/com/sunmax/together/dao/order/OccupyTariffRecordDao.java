package com.sunmax.together.dao.order;

import com.sunmax.together.entity.order.OccupyTariffRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OccupyTariffRecordDao extends JpaRepository<OccupyTariffRecordEntity, String>, JpaSpecificationExecutor<OccupyTariffRecordEntity> {

    //查询指定占桩订单计费详情历史数据
    List<OccupyTariffRecordEntity> findAllByOccupyId(String occupyId);
}
