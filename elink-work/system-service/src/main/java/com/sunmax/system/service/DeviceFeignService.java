package com.sunmax.system.service;

import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;

import java.util.List;
import java.util.Map;

public interface DeviceFeignService {

    /**
     * 根据用户id查询用户信息
     * @param userIds
     * @return
     */
    ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(List<String> userIds);

    /**
     * 根据站点id删除所有关联的资产授权数据
     * @param siteId
     * @return
     */
    ResponseResult<String> deleteAllOrganEmpowerBySiteId(String siteId);

    /**
     * 根据用户id查询所有关联的资产授权数据
     *
     * @param userId
     * @return
     */
    ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(String userId);

    /**
     * 根据租户id查询资产授权列表信息
     * @param tenantId
     * @return
     */
    ResponseResult<List<OrganEmpowerListDto>> findOrganEmpowerListByTenantId(String tenantId);

    /**
     * 根据租户id删除租户下所有指定站点资产授权信息
     * @param tenantId
     * @param siteId
     * @return
     */
    ResponseResult<String> deleteOrganEmpowerByTenantId(String tenantId, String siteId);

    /**
     * 根据权限id修改指定权限
     * @param tenantId
     * @param siteId
     * @param authority
     * @return
     */
    ResponseResult<String> updateOrganEmpowerByTenantId(String tenantId, String siteId, Integer authority);

    /**
     * 根据账号查询密码
     * @param userAccount 用户账号
     * @return 用户密码
     */
    ResponseResult<String> getPasswordByAccount(String userAccount);

    /**
     * 根据多个用户账号查询用户名称
     * @param userAccounts 多个用户账号
     * @return 用户账号 -> 用户名称
     */
    ResponseResult<Map<String, String>> findUserNameByUserAccounts(List<String> userAccounts);

    /**
     * 根据用户id查询租户下所有的用户
     * @param userId 用户id
     * @return 用户信息
     */
    ResponseResult<List<UserDto>> findUserListByUserId(String userId);

}
