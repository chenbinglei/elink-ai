package com.sunmax.device.dao.model;

import com.sunmax.device.entity.model.PileFaultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface PileFaultDao extends JpaRepository<PileFaultEntity, String>, JpaSpecificationExecutor<PileFaultEntity> {

    //根据模型id查询电桩告警数据
    List<PileFaultEntity> findAllByModelId(String modelId);

    //根据模型id和告警码查询告警定义数据
    List<PileFaultEntity> findAllByModelIdAndFaultCode(String modelId, Integer faultCode);

    //根据模型id和告警码查询告警定义数据
    List<PileFaultEntity> findAllByModelIdAndFaultCodeIn(String modelId, Set<Integer> faultCode);

}
