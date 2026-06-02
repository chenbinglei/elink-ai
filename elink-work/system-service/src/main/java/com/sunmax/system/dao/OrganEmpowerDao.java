package com.sunmax.system.dao;

import com.sunmax.system.entity.OrganEmpowerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface OrganEmpowerDao extends JpaRepository<OrganEmpowerEntity, String>, JpaSpecificationExecutor<OrganEmpowerEntity> {

    /**
     * 根据组织机构id查询相关资产授权列表
     * @param organId
     * @return
     */
    List<OrganEmpowerEntity> findAllByOrganId(String organId);

    /**
     * 删除租户下所有关联资产授权数据
     * @param organId
     */
    void deleteAllByTenantId(String organId);

    /**
     * 查询租户下关联所有站点资产授权数据
     * @param tenantId
     * @return
     */
    List<OrganEmpowerEntity> findAllByTenantId(String tenantId);

    /**
     * 根据站点id删除所有关联该站点的资产授权数据
     * @param siteId
     */
    void deleteAllBySiteId(String siteId);

    /**
     * 根据租户id和所属组织架构id查询租户资产授权信息
     * @param tenantId
     * @param organStructureId
     * @return
     */
    List<OrganEmpowerEntity> findAllByTenantIdAndOrganId(String tenantId, String organStructureId);

    //删除指定组织架构id下关联的指定站点数据
    void deleteAllByOrganIdInAndSiteId(List<String> structureIdList, String siteId);

    /**
     * 根据多个子级组织架构id以及站点id查询关联资产信息
     * @param structureIdList
     * @param siteId
     * @return
     */
    List<OrganEmpowerEntity> findAllByOrganIdInAndSiteId(List<String> structureIdList, String siteId);

    /**
     * 根据租户id删除租户下所有指定站点资产授权信息
     * @param tenantId
     * @param siteId
     */
    void deleteAllByTenantIdAndSiteId(String tenantId, String siteId);

    /**
     * 查询所有指定租户指定站点资产授权数据
     * @param tenantId
     * @param siteId
     * @return
     */
    List<OrganEmpowerEntity> findAllByTenantIdAndSiteId(String tenantId, String siteId);

    /**
     * 根据多个租户id查询站点资产授权数据
     */
    List<OrganEmpowerEntity> findAllByTenantIdIn(List<String> tenantIdList);
}
