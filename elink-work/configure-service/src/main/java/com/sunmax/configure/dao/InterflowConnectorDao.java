package com.sunmax.configure.dao;

import com.sunmax.configure.entity.interflow.InterflowConnectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface InterflowConnectorDao extends JpaRepository<InterflowConnectorEntity, String>, JpaSpecificationExecutor<InterflowConnectorEntity> {

    /**
     * 根据设备编码查询设备接口数据
     * @param equipmentIdList
     * @return
     */
    List<InterflowConnectorEntity> findAllByEquipmentIdIn(List<String> equipmentIdList);
}
