package com.sunmax.device.dao.model;

import com.sunmax.device.entity.model.ModelReaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface ModelReaDao extends JpaRepository<ModelReaEntity, String>, JpaSpecificationExecutor<ModelReaEntity> {

    //根据模型扩展属性id查询模型扩展属性关联数据
    List<ModelReaEntity> findAllByReaId(String reaId);

    //根据多个模型id查询模型扩展属性关联数据
    List<ModelReaEntity> findAllByModelIdIn(List<String> modelIds);

    //根据模型id查询模型扩展属性关联数据
    List<ModelReaEntity> findAllByModelId(String modelId);

    //根据模型id和多个扩展属性id查询模型扩展属性关联数据
    List<ModelReaEntity> findAllByModelIdAndReaIdIn(String modelId, List<String> reaIds);

    //根据模型id查询模型扩展属性关联数据
//    List<ModelReaEntity> findAllByModelIdAndReadWriteType(String modelId, Integer readWriteType);
    
    //根据多个扩展属性id删除模型扩展属性关联数据
    void deleteAllByReaIdIn(Set<String> reaIds);

}
