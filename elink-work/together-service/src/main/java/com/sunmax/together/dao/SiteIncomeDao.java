package com.sunmax.together.dao;

import com.sunmax.together.entity.SiteIncomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface SiteIncomeDao extends JpaRepository<SiteIncomeEntity, String>, JpaSpecificationExecutor<SiteIncomeEntity> {

    //根据站点id查询站点收益测算数据
    List<SiteIncomeEntity> findAllBySiteId(String siteId);

}
