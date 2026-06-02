package com.sunmax.log.dao;

import com.sunmax.log.entity.AccessLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AccessLogDao extends JpaRepository<AccessLogEntity, String>, JpaSpecificationExecutor<AccessLogEntity> {
}
