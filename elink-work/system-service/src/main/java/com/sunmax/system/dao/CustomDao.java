package com.sunmax.system.dao;

import com.sunmax.system.entity.CustomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomDao extends JpaRepository<CustomEntity, String>, JpaSpecificationExecutor<CustomEntity> {
}
