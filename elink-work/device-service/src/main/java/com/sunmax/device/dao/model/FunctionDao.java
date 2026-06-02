package com.sunmax.device.dao.model;

import com.sunmax.device.entity.model.FunctionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface FunctionDao extends JpaRepository<FunctionEntity, String>, JpaSpecificationExecutor<FunctionEntity> {

    //根据伪删除状态查询模型标准功能数据
    List<FunctionEntity> findAllByIsDelete(Integer isDelete);

    //根据多个功能点标识查询标准功能数据
    List<FunctionEntity> findAllByFunctionLogoIn(Set<String> functionLogos);

    //根据多个功能点标识查询标准功能数据
    List<FunctionEntity> findAllByTypeIdAndFunctionLogoInAndIsDelete(String typeId, Set<String> functionLogos, Integer isDelete);

    //根据设备类型id和伪删除状态查询模型标准功能数据
    List<FunctionEntity> findAllByTypeIdAndIsDelete(String typeId, Integer isDelete);

}
