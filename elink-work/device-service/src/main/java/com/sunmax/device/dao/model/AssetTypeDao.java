package com.sunmax.device.dao.model;

import com.sunmax.device.entity.model.AssetTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AssetTypeDao extends JpaRepository<AssetTypeEntity, String>, JpaSpecificationExecutor<AssetTypeEntity> {

}