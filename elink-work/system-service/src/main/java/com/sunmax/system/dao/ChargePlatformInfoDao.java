package com.sunmax.system.dao;

import com.sunmax.system.entity.ChargePlatformInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ChargePlatformInfoDao extends JpaRepository<ChargePlatformInfoEntity, String>, JpaSpecificationExecutor<ChargePlatformInfoEntity> {

    //根据多个平台标识查询数据
    List<ChargePlatformInfoEntity> findAllByPlatformLogoIn(List<String> platformLogoList);
}
