package com.sunmax.device.dao.access;

import com.sunmax.device.entity.access.SiteEnergyInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SiteEnergyInfoDao extends JpaRepository<SiteEnergyInfoEntity, String>, JpaSpecificationExecutor<SiteEnergyInfoEntity> {

    /**
     * 根据站点id查询能源信息数据
     * @param siteIdList
     * @return
     */
    List<SiteEnergyInfoEntity> findAllBySiteIdIn(List<String> siteIdList);
}
