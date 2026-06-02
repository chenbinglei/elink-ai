package com.sunmax.system.dao;

import com.sunmax.system.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserDao extends JpaRepository<UserEntity, String>, JpaSpecificationExecutor<UserEntity> {

    /**
     * 查询出所选租户下所有启用状态用户账号数据
     * @param tenantId
     * @param userState
     * @return
     */
    List<UserEntity> findAllByTenantIdAndUserState(String tenantId, Integer userState);

    /**
     * 查询租户下全部用户信息
     * @param tenantId
     * @return
     */
    List<UserEntity> findAllByTenantId(String tenantId);

    /**
     * 根据用户组id模糊查询
     * @param groupId
     * @return
     */
    List<UserEntity> findAllByGroupIdLike(String groupId);

    /**
     * 删除租户下全部用户信息
     * @param tenantId
     * @return
     */
    void deleteAllByTenantId(String tenantId);

    /**
     * 根据多个用户id删除用户数据
     * @param userIds 多个用户id
     * @return
     */
    void deleteAllByIdIn(List<String> userIds);

    /**
     * 查询组织下所有用户信息
     * @param organId
     * @return
     */
    List<UserEntity> findAllByOrganId(String organId);

    //查询当前租户默认账号密码
    UserEntity findByTenantIdAndIsDefaultAdmin(String tenantId, int isDefaultAdmin);

    //根据多个用户账号查询用户数据
    List<UserEntity> findAllByUserAccountIn(List<String> userAccounts);
}
