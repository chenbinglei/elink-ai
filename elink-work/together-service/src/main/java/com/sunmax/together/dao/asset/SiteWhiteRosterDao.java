package com.sunmax.together.dao.asset;

import com.sunmax.together.entity.assets.SiteWhiteRosterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SiteWhiteRosterDao extends JpaRepository<SiteWhiteRosterEntity, String>, JpaSpecificationExecutor<SiteWhiteRosterEntity> {

    //根据站点id查询白名单信息
    List<SiteWhiteRosterEntity> findAllBySiteId(String siteId);
}
