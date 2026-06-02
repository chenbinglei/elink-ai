package com.sunmax.system.service;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.dto.PermissionListDto;
import com.sunmax.system.dto.UserGroupListDto;
import com.sunmax.system.dto.UserListDto;
import com.sunmax.system.vo.UserGroupVo;
import com.sunmax.system.vo.UserListQueryVo;
import com.sunmax.system.vo.UserVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SystemManageService {

    /**
     * 保存或编辑用户组信息
     * @param userGroupVo
     * @return
     */
    ResponseResult<String> saveOrUpdateUserGroup(UserGroupVo userGroupVo);

    /**
     * 根据用户组id删除用户组信息
     * @param groupId
     * @return
     */
    ResponseResult<String> deleteUserGroupById(String groupId);

    /**
     * 分页查询用户组列表信息
     *
     * @param groupName
     * @param tenantId
     * @param page
     * @param size
     * @return
     */
    ResponseResult<PageDto<UserGroupListDto>> findUserGroupListByPage(String groupName, String tenantId, Integer page, Integer size);

    /**
     * 保存或编辑用户信息
     * @param userVo
     * @param imageFile
     * @return
     */
    ResponseResult<String> saveOrUpdateUserInfo(UserVo userVo, MultipartFile imageFile);

    /**
     * 根据用户id删除用户信息
     * @param userId
     * @return
     */
    ResponseResult<String> deleteUserInfoById(String userId);

    /**
     * 分页查询用户列表数据
     * @param userListQueryVo
     * @return
     */
    ResponseResult<PageDto<UserListDto>> findUserListByPage(UserListQueryVo userListQueryVo);

    /**
     * 根据id更新用户状态
     * @param userId
     * @param userState
     * @return
     */
    ResponseResult<String> updateUserStateById(String userId, Integer userState);

    /**
     * 添加/修改用户组织信息
     * @param userIdList
     * @param organId
     * @return
     */
    ResponseResult<String> updateUserOrganStructure(List<String> userIdList, String organId);

    /**
     * 添加/修改用户组信息
     * @param userIdList
     * @param groupIds
     * @return
     */
    ResponseResult<String> updateUserGroup(List<String> userIdList, String groupIds);

    /**
     * 查询租户下用户组列表
     * @param tenantId
     * @return
     */
    ResponseResult<List<UserGroupListDto>> findUserGroupListById(String tenantId);

    /**
     * 保存用户组应用授权信息
     * @param groupApplyEmpowerVos
     * @return
     */
    ResponseResult<String> saveGroupApplyEmpowerInfo(String groupApplyEmpowerVos);

    /**
     * 保存用户组应用授权信息
     * @param groupId 用户组id
     * @param moduleId 模块id
     * @param groupApplyVos 用户组应用授权信息
     * @return
     */
    ResponseResult<Void> saveGroupApplyInfo(String groupId, String moduleId, String groupApplyVos);

    /**
     * 查询指定用户组指定模块下配置的应用授权数据
     * @param groupId
     * @param moduleId
     * @return
     */
    ResponseResult<List<PermissionListDto>> findGroupApplyEmpowerInfoById(String groupId, String moduleId);

    /**
     * 用户组管理人员
     * @param addUserIds
     * @param deleteUserIds
     * @param groupId
     * @return
     */
    ResponseResult<String> groupManageUser(String addUserIds, String deleteUserIds, String groupId);

    /**
     * 根据用户id查询用户详情
     * @param userId
     * @return
     */
    ResponseResult<UserDto> findUserDetailsById(String userId);

    /**
     * 删除指定用户组下关联的指定权限数据
     * @param groupId
     * @param permissionIdList
     * @return
     */
    ResponseResult<String> deleteGroupApplyEmpowerByGroupId(String groupId, List<String> permissionIdList);

    /**
     * 根据用户组id查询用户组详情
     * @param groupId 用户组id
     * @param userIds 多个用户id
     * @return 状态码
     */
    ResponseResult<Void> updateUserByGroupId(String groupId, List<String> userIds);
}
