package com.sunmax.device.dao.model;

import com.sunmax.device.entity.model.ReaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReaDao extends JpaRepository<ReaEntity, String>, JpaSpecificationExecutor<ReaEntity> {

}
