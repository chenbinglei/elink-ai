package com.sunmax.protocol.dao;


import com.sunmax.protocol.entity.DispatchRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DispatchRecordDao extends JpaRepository<DispatchRecordEntity, Long>, JpaSpecificationExecutor<DispatchRecordEntity> {
}
