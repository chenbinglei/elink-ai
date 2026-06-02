package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.AffiliatesInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AffiliatesInfoDao extends JpaRepository<AffiliatesInfoEntity, String>, JpaSpecificationExecutor<AffiliatesInfoEntity> {

    /**
     * 根据站点id，查询所有关联方信息
     * @param siteId
     * @return
     */
    List<AffiliatesInfoEntity> findAllBySiteId(String siteId);

    //根据多个站点id，查询关联方数据
    List<AffiliatesInfoEntity> findAllBySiteIdIn(List<String> siteIdList);

    //根据租户id和站点id，查询关联信息
    AffiliatesInfoEntity findByTenantIdAndSiteId(String tenantId, String siteId);
}
