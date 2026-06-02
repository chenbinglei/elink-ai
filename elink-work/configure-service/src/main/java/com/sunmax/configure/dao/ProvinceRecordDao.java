package com.sunmax.configure.dao;

import com.sunmax.configure.entity.ProvinceRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProvinceRecordDao extends JpaRepository<ProvinceRecordEntity, String>, JpaSpecificationExecutor<ProvinceRecordEntity> {


}
