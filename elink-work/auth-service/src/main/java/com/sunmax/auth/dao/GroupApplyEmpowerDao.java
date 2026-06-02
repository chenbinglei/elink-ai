package com.sunmax.auth.dao;

import com.sunmax.auth.entity.GroupApplyEmpowerEntity;
import com.sunmax.common.dao.base.BaseDao;

import java.util.List;

public interface GroupApplyEmpowerDao extends BaseDao<GroupApplyEmpowerEntity, String> {

    /**
     * 根据多个用户组id查询权限配置数据
     * @param groupIdList
     * @return
     */
    List<GroupApplyEmpowerEntity> findAllByGroupIdIn(List<String> groupIdList);
}
