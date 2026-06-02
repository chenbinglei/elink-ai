package com.sunmax.configure.dao;

import com.sunmax.configure.entity.CityRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CityRecordDao extends JpaRepository<CityRecordEntity, String>, JpaSpecificationExecutor<CityRecordEntity> {
}
