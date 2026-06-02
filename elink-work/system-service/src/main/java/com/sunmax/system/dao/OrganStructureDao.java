package com.sunmax.system.dao;

import com.sunmax.system.entity.OrganStructureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrganStructureDao extends JpaRepository<OrganStructureEntity, String>, JpaSpecificationExecutor<OrganStructureEntity> {

    /**
     * 根据租户id查询组织架构数据
     * @param tenantId
     * @return
     */
    List<OrganStructureEntity> findAllByTenantId(String tenantId);

    /**
     * 根据租户id删除组织架构数据
     * @param tenantId
     * @return
     */
    void deleteAllByTenantId(String tenantId);

    /**
     * 根据父级id查询组织架构数据
     * @param organId
     * @return
     */
    List<OrganStructureEntity> findAllByParentId(String organId);
}
