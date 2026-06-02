package com.sunmax.configure.dao;

import com.sunmax.configure.entity.interflow.InterflowEquipmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InterflowEquipmentDao extends JpaRepository<InterflowEquipmentEntity, String>, JpaSpecificationExecutor<InterflowEquipmentEntity> {
}
