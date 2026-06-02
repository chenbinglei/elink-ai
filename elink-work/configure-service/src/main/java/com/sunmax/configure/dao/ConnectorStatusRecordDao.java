package com.sunmax.configure.dao;

import com.sunmax.configure.entity.interflow.ConnectorStatusRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConnectorStatusRecordDao extends JpaRepository<ConnectorStatusRecordEntity, String>, JpaSpecificationExecutor<ConnectorStatusRecordEntity> {
}
