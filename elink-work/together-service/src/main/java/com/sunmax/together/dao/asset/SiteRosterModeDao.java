package com.sunmax.together.dao.asset;

import com.sunmax.together.entity.assets.SiteRosterModeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SiteRosterModeDao extends JpaRepository<SiteRosterModeEntity, String>, JpaSpecificationExecutor<SiteRosterModeEntity> {

    //根据站点id，查询白名单模式
    SiteRosterModeEntity findBySiteId(String siteId);
}
