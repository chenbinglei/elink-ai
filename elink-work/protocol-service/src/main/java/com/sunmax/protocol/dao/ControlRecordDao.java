package com.sunmax.protocol.dao;

import com.sunmax.protocol.entity.ControlRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ControlRecordDao extends JpaRepository<ControlRecordEntity, String>, JpaSpecificationExecutor<ControlRecordEntity> {

}
