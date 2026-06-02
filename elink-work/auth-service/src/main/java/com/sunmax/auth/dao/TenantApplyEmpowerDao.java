package com.sunmax.auth.dao;

import com.sunmax.auth.entity.TenantApplyEmpowerEntity;
import com.sunmax.common.dao.base.BaseDao;

import java.util.List;

public interface TenantApplyEmpowerDao extends BaseDao<TenantApplyEmpowerEntity, String> {

    /**
     * 查询租户下配置的应用授权数据
     * @param tenantId
     * @return
     */
    List<TenantApplyEmpowerEntity> findAllByTenantId(String tenantId);
}
