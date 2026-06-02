package com.sunmax.configure.dao;

import com.sunmax.configure.entity.interflow.ChargeOrderRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargeOrderRecordDao extends JpaRepository<ChargeOrderRecordEntity, String>, JpaSpecificationExecutor<ChargeOrderRecordEntity> {
}
