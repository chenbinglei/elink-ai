package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.SiteSetUpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SiteSetUpDao extends JpaRepository<SiteSetUpEntity, String>, JpaSpecificationExecutor<SiteSetUpEntity> {
    SiteSetUpEntity findBySiteId(String siteId);

    //根据站点id查询站点设置
    List<SiteSetUpEntity> findAllBySiteIdIn(List<String> siteIdList);
}
