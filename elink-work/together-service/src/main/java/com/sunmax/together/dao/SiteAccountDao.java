package com.sunmax.together.dao;

import com.sunmax.together.entity.SiteAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface SiteAccountDao extends JpaRepository<SiteAccountEntity, String>, JpaSpecificationExecutor<SiteAccountEntity> {

    //根据多个站点id和类型查询站点账号信息
    List<SiteAccountEntity> findBySiteIdInAndType(Collection<String> siteIds, Integer type);

    //根据站点id和多个类型查询站点账号信息
    List<SiteAccountEntity> findBySiteIdAndTypeIn(String siteId, Collection<Integer> types);

    //根据账户id和类型查询账户信息
    List<SiteAccountEntity> findAllByAccountIdAndTypeAndPayPlatform(String accountId, Integer type, Integer payPlatform);

    //根据多个站点id和类型和支付平台查询站点账号信息
    List<SiteAccountEntity> findAllBySiteIdInAndTypeAndPayPlatform(List<String> siteIds, Integer type, Integer payPlatform);

    //根据多个站点id和支付平台查询站点账号信息
    List<SiteAccountEntity> findAllBySiteIdInAndPayPlatform(List<String> siteIds, Integer payPlatform);

}
