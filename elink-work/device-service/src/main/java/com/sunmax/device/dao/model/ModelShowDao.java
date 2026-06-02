package com.sunmax.device.dao.model;

import com.sunmax.device.entity.model.ModelShowEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ModelShowDao extends JpaRepository<ModelShowEntity, String>, JpaSpecificationExecutor<ModelShowEntity> {

    //根据模型id查询模型设备显示数据
    Optional<ModelShowEntity> findByModelId(String modelId);
}
