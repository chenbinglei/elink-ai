package com.sunmax.device.dao.model;

import com.sunmax.device.entity.model.ModelEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ModelEventDao extends JpaRepository<ModelEventEntity, String>, JpaSpecificationExecutor<ModelEventEntity> {

    //根据模型id查询模型事件数据
    List<ModelEventEntity> findAllByModelId(String modelId);

}
