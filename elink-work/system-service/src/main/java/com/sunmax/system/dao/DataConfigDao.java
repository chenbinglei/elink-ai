package com.sunmax.system.dao;

import com.sunmax.system.entity.DataConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DataConfigDao extends JpaRepository<DataConfigEntity, String>, JpaSpecificationExecutor<DataConfigEntity> {

    //根据多个转发id查询转发数据配置信息
    List<DataConfigEntity> findAllByForwardIdIn(List<String> forwardIds);

    //根据协议编码和多个站点id查询数据配置信息
    List<DataConfigEntity> findAllByProtocolCodeAndSiteIdIn(String protocolCode, List<String> siteIds);

}
