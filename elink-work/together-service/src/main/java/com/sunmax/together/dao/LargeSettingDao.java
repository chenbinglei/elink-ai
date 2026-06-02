package com.sunmax.together.dao;

import com.sunmax.together.entity.LargeSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LargeSettingDao extends JpaRepository<LargeSettingEntity, String>, JpaSpecificationExecutor<LargeSettingEntity> {
}
