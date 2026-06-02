package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.SiteTopItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SiteTopItemDao extends JpaRepository<SiteTopItemEntity, String>, JpaSpecificationExecutor<SiteTopItemEntity> {

    //根据多个拓扑节点id查询拓扑节点配置数据
    List<SiteTopItemEntity> findAllByNodeIdIn(List<String> topoIdList);
}
