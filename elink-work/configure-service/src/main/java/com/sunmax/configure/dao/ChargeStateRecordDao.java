package com.sunmax.configure.dao;

import com.sunmax.configure.entity.interflow.ChargeStateRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargeStateRecordDao extends JpaRepository<ChargeStateRecordEntity, String>, JpaSpecificationExecutor<ChargeStateRecordEntity> {
}
