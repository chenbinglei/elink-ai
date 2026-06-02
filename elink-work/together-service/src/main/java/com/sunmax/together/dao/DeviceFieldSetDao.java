package com.sunmax.together.dao;

import com.sunmax.together.entity.DeviceFieldSetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeviceFieldSetDao extends JpaRepository<DeviceFieldSetEntity, String>, JpaSpecificationExecutor<DeviceFieldSetEntity> {
}
