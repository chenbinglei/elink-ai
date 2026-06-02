package com.sunmax.configure.dao;

import com.sunmax.configure.entity.interflow.InterflowStationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InterflowStationDao extends JpaRepository<InterflowStationEntity, String>, JpaSpecificationExecutor<InterflowStationEntity> {
}
