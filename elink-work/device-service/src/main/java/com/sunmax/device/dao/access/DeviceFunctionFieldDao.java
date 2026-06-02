package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.DeviceFunctionFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DeviceFunctionFieldDao extends JpaRepository<DeviceFunctionFieldEntity, String>, JpaSpecificationExecutor<DeviceFunctionFieldEntity> {

}
