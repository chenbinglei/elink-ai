package com.sunmax.system.dao;

import com.sunmax.system.entity.TenantInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TenantManageDao extends JpaRepository<TenantInfoEntity, String>, JpaSpecificationExecutor<TenantInfoEntity> {

    /**
     * 根据id更新租户状态
     * @param id
     * @param tenantState
     * @return
     */
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    @Modifying
    @Query("update TenantInfoEntity t set t.tenantState =:tenantState where t.id=:id")
    int updateTenantStateById(String id, Integer tenantState);

    /**
     * 根据租户状态查询所有租户列表
     * @param tenantState
     * @return
     */
    List<TenantInfoEntity> findAllByTenantState(int tenantState);
}
