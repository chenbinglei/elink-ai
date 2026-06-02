package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.SiteTopNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SiteTopNodeDao extends JpaRepository<SiteTopNodeEntity, String>, JpaSpecificationExecutor<SiteTopNodeEntity> {

    //根据站点id查询站点拓扑节点数据
    List<SiteTopNodeEntity> findAllBySiteId(String siteId);

    //根据站点id和节点类型查询站点拓扑节点数据
    List<SiteTopNodeEntity> findAllBySiteIdAndNodeType(String siteId, Integer nodeType);
}
