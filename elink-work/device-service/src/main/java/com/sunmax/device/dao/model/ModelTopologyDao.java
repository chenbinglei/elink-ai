package com.sunmax.device.dao.model;

import com.sunmax.device.entity.model.ModelTopologyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface ModelTopologyDao extends JpaRepository<ModelTopologyEntity, String>, JpaSpecificationExecutor<ModelTopologyEntity> {

    //根据模型id查询拓扑节点数据
    List<ModelTopologyEntity> findAllByModelId(String modelId);

    //根据多个模型id查询拓扑节点数据
    List<ModelTopologyEntity> findAllByModelIdIn(Set<String> modelIds);

}
