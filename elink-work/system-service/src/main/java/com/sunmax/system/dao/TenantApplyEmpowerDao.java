package com.sunmax.system.dao;

import com.sunmax.system.entity.TenantApplyEmpowerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface TenantApplyEmpowerDao extends JpaRepository<TenantApplyEmpowerEntity, String>, JpaSpecificationExecutor<TenantApplyEmpowerEntity> {

    /**
     * 查询指定租户指定模块下的应用授权数据
     * @param tenantId
     * @param moduleId
     * @return
     */
    List<TenantApplyEmpowerEntity> findAllByTenantIdAndModuleId(String tenantId, String moduleId);

    /**
     * 删除租户下所有关联应用授权信息
     * @param tenantId
     */
    void deleteAllByTenantId(String tenantId);

    /**
     * 查询所属租户下所有应用授权数据
     * @param tenantId
     * @return
     */
    List<TenantApplyEmpowerEntity> findAllByTenantId(String tenantId);

    //根据租户id查询关联应用授权信息
    List<TenantApplyEmpowerEntity> findAllByTenantIdAndOperate(String tenantId, Integer operate);
}
