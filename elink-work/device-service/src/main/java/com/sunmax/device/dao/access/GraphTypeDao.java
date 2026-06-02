package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.GraphTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GraphTypeDao extends JpaRepository<GraphTypeEntity, String>, JpaSpecificationExecutor<GraphTypeEntity> {
}
