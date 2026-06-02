package com.sunmax.configure.dao;

import com.sunmax.configure.entity.ProvinceOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProvinceOrderDao extends JpaRepository<ProvinceOrderEntity, String>, JpaSpecificationExecutor<ProvinceOrderEntity> {
}
