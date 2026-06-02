package com.sunmax.auth.dao;

import com.sunmax.auth.entity.PermissionEntity;
import com.sunmax.common.dao.base.BaseDao;

import java.util.List;

public interface PermissionDao extends BaseDao<PermissionEntity, String> {

    List<PermissionEntity> findAllByModuleIdInAndPermissionTypeAndIsDelete(List<String> modelIdList, int permissionType, int isDelete);

    List<PermissionEntity> findAllByIdInAndIsDelete(List<String> ids, int isDelete);
}
