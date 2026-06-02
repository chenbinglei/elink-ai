package com.sunmax.together.dao.asset;

import com.sunmax.together.entity.assets.ElectConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface ElectConfigDao extends JpaRepository<ElectConfigEntity, String>, JpaSpecificationExecutor<ElectConfigEntity> {

    //根据多个站点id查询电价策略配置数据列表
    List<ElectConfigEntity> findAllBySiteIdIn(Collection<String> siteIds);

    @Query("SELECT ec FROM ElectConfigEntity ec WHERE ec.siteId = :siteId AND ec.moduleType IN :moduleTypes AND :startDate < ec.endDate AND ec.startDate < :endDate")
    List<ElectConfigEntity> findElectConfigList(String siteId, List<Integer> moduleTypes, String startDate, String endDate);

    @Query("SELECT ec FROM ElectConfigEntity ec WHERE ec.siteId IN :siteIds AND ec.moduleType IN :moduleTypes AND :startDate < ec.endDate AND ec.startDate < :endDate")
    List<ElectConfigEntity> findElectConfigListBySiteIds(List<String> siteIds, List<Integer> moduleTypes, String startDate, String endDate);

}
