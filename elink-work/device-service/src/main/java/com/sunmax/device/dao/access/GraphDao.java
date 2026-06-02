package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.GraphEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GraphDao extends JpaRepository<GraphEntity, String>, JpaSpecificationExecutor<GraphEntity> {
}
