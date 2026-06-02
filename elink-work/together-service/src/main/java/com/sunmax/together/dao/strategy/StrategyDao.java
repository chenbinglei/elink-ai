package com.sunmax.together.dao.strategy;

import com.sunmax.together.entity.strategy.StrategyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface StrategyDao extends JpaRepository<StrategyEntity, String>, JpaSpecificationExecutor<StrategyEntity> {

    //根据多个设备id查询策略数据
    List<StrategyEntity> findAllByDeviceIdIn(List<String> deviceId);

}
