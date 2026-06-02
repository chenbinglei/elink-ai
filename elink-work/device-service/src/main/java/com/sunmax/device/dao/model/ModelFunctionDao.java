package com.sunmax.device.dao.model;

import com.sunmax.device.entity.model.ModelFunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface ModelFunctionDao extends JpaRepository<ModelFunctionEntity, String>, JpaSpecificationExecutor<ModelFunctionEntity> {

    //根据模型id查询模型标准功能关联数据
    List<ModelFunctionEntity> findAllByModelId(String modelId);

    //根据模型id查询模型标准功能关联数据
    List<ModelFunctionEntity> findAllByModelIdAndIsDelete(String modelId, Integer isDelete);

    //根据多个模型id查询模型标准功能关联数据
    List<ModelFunctionEntity> findAllByModelIdInAndIsDelete(Set<String> modelIds, Integer isDelete);

    //根据模型标准功能id查询模型标准功能关联数据
    List<ModelFunctionEntity> findAllByFunctionIdAndIsDelete(String functionId, Integer isDelete);

    //根据模型id和多个标准功能id查询模型标准功能关联数据
    List<ModelFunctionEntity> findAllByModelIdAndFunctionIdIn(String modelId, List<String> functionIds);

    //根据模型id和标准功能点查询模型标准功能关联数据
    ModelFunctionEntity findByModelIdAndFunctionIdAndIsDelete(String modelId, String functionId, Integer isDelete);

}
