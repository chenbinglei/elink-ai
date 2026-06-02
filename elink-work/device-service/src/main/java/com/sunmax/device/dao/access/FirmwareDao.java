package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.FirmwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FirmwareDao extends JpaRepository<FirmwareEntity, String>, JpaSpecificationExecutor<FirmwareEntity> {

}
