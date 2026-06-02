package com.sunmax.system.dao;

import com.sunmax.system.entity.GroupApplyEmpowerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GroupApplyEmpowerDao extends JpaRepository<GroupApplyEmpowerEntity, String>, JpaSpecificationExecutor<GroupApplyEmpowerEntity> {

    /**
     * 查询指定用户组指定模块下应用授权数据
     * @param groupId
     * @param moduleId
     * @return
     */
    List<GroupApplyEmpowerEntity> findAllByGroupIdAndModuleId(String groupId, String moduleId);

    /**
     * 批量删除用户组关联权限授权信息
     * @param groupIdList
     */
    void deleteAllByGroupIdIn(List<String> groupIdList);

    /**
     * 删除指定用户组下关联的指定权限数据
     * @param groupId
     * @param permissionIdList
     */
    void deleteAllByGroupIdAndPermissionIdIn(String groupId, List<String> permissionIdList);

    /**
     * 查询指定用户组权限配置数据
     * @param groupIdList
     * @return
     */
    List<GroupApplyEmpowerEntity> findAllByGroupIdInAndOperate(List<String> groupIdList, Integer operate);
}
