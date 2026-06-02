package com.sunmax.system.dao;


import com.sunmax.system.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface PermissionDao extends JpaRepository<PermissionEntity, String>, JpaSpecificationExecutor<PermissionEntity> {

    //根据多个模块id查询权限数据
    List<PermissionEntity> findAllByModuleIdIn(List<String> moduleIds);

    /**
     * 根据权限类型和删除状态查询数据
     * @param permissionType
     * @param isDelete
     * @return
     */
    List<PermissionEntity> findAllByPermissionTypeAndIsDelete(int permissionType, int isDelete);

    /**
     * 根据多个权限id查询权限数据
     * @param permissionIdList
     * @param permissionType
     * @param isDelete
     * @return
     */
    List<PermissionEntity> findAllByIdInAndPermissionTypeAndIsDelete(List<String> permissionIdList, int permissionType, int isDelete);
}
