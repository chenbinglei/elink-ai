package com.sunmax.system.dao;

import com.sunmax.system.entity.UserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserGroupDao extends JpaRepository<UserGroupEntity, String>, JpaSpecificationExecutor<UserGroupEntity> {

    /**
     * 根据多个id查询用户组信息
     * @param groupIdList
     * @return
     */
    List<UserGroupEntity> findAllByIdIn(List<String> groupIdList);

    /**
     * 查询租户下全部用户组
     * @param tenantId
     * @return
     */
    List<UserGroupEntity> findAllByTenantId(String tenantId);

    /**
     * 根据租户id删除组织架构信息
     * @param tenantId
     */
    void deleteAllByTenantId(String tenantId);
}
