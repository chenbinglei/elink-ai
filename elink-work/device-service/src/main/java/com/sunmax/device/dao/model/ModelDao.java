package com.sunmax.device.dao.model;

import com.sunmax.device.entity.model.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ModelDao extends JpaRepository<ModelEntity, String>, JpaSpecificationExecutor<ModelEntity> {

    //根据多个设备类型id查询模型数据
    List<ModelEntity> findAllByTypeIdIn(List<String> typeIds);

}
