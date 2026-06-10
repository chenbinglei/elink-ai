package com.sunmax.system.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.log.config.WebLog;
import com.sunmax.system.dto.PermissionListDto;
import com.sunmax.system.dto.UserGroupListDto;
import com.sunmax.system.dto.UserListDto;
import com.sunmax.system.service.SystemManageService;
import com.sunmax.system.vo.UserGroupVo;
import com.sunmax.system.vo.UserListQueryVo;
import com.sunmax.system.vo.UserVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("systemManage")
@Tag(name = "系统管理")
public class SystemManageController {

    @Autowired
    private SystemManageService systemManageService;

    @PostMapping("saveOrUpdateUserGroup")
    @Operation(summary = "保存或编辑用户组信息")
    @WebLog("角色管理-保存或编辑角色信息")
    
    public ResponseResult<String> saveOrUpdateUserGroup(UserGroupVo userGroupVo) {
        return systemManageService.saveOrUpdateUserGroup(userGroupVo);
    }

    @PostMapping("deleteUserGroupById")
    @Operation(summary = "根据用户组id删除用户组信息")
    @WebLog("角色管理-根据角色id删除角色信息")
    
    @Parameter(name = "groupId", description = "用户组id")
    public ResponseResult<String> deleteUserGroupById(String groupId) {
        return systemManageService.deleteUserGroupById(groupId);
    }

    @PostMapping("findUserGroupListByPage")
    @Operation(summary = "分页查询用户组列表信息")
    
    @Parameters({
            @Parameter(name = "groupName", description = "用户组名称(模糊查询)"),
            @Parameter(name = "tenantId", description = "租户id"),
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数")
    })
    public ResponseResult<PageDto<UserGroupListDto>> findUserGroupListByPage(String groupName, String tenantId, Integer page, Integer size) {
        return systemManageService.findUserGroupListByPage(groupName, tenantId, page, size);
    }

    @PostMapping("saveOrUpdateUserInfo")
    @Operation(summary = "保存或编辑用户信息")
    @WebLog("用户管理-保存或编辑用户信息")
    
    public ResponseResult<String> saveOrUpdateUserInfo(UserVo userVo, MultipartFile imageFile) {
        return systemManageService.saveOrUpdateUserInfo(userVo, imageFile);
    }

    @PostMapping("deleteUserInfoById")
    @Operation(summary = "根据用户id删除用户信息")
    @WebLog("用户管理-根据用户id删除用户信息")
    
    @Parameter(name = "id", description = "用户id")
    public ResponseResult<String> deleteUserInfoById(String id) {
        return systemManageService.deleteUserInfoById(id);
    }

    @PostMapping("findUserListByPage")
    @Operation(summary = "分页查询用户列表数据")
    
    public ResponseResult<PageDto<UserListDto>> findUserListByPage(UserListQueryVo userListQueryVo) {
        return systemManageService.findUserListByPage(userListQueryVo);
    }

    @PostMapping("updateUserStateById")
    @Operation(summary = "根据id更新用户状态")
    @WebLog("用户管理-根据用户id更新用户状态")
    
    @Parameters({
            @Parameter(name = "id", description = "用户id"),
            @Parameter(name = "userState", description = "用户状态 0-关闭 1-开启")
    })
    public ResponseResult<String> updateUserStateById(String id, Integer userState) {
        return systemManageService.updateUserStateById(id, userState);
    }

    @PostMapping("updateUserOrganStructure")
    @Operation(summary = "添加/修改组织信息")
    @WebLog("企业信息-添加/修改组织信息")
    
    @Parameters({
            @Parameter(name = "userIds", description = "用户id(多个以逗号分割)"),
            @Parameter(name = "organId", description = "组织架构id")
    })
    public ResponseResult<String> updateUserOrganStructure(String userIds, String organId) {
        return systemManageService.updateUserOrganStructure(Arrays.stream(userIds.split(",")).map(String::trim).collect(Collectors.toList()), organId);
    }

    @PostMapping("updateUserGroup")
    @Operation(summary = "添加/修改用户组信息-用户管理")
    @WebLog("用户管理-添加或修改用户组信息")
    
    @Parameters({
            @Parameter(name = "userIds", description = "用户id(多个以逗号分割)"),
            @Parameter(name = "groupIds", description = "用户组id(多个以逗号分割)")
    })
    public ResponseResult<String> updateUserGroup(String userIds, String groupIds) {
        return systemManageService.updateUserGroup(Arrays.stream(userIds.split(",")).map(String::trim).collect(Collectors.toList()), groupIds);
    }

    @PostMapping("findUserGroupListById")
    @Operation(summary = "查询租户下用户组列表")
    
    @Parameter(name = "tenantId", description = "所属租户id")
    public ResponseResult<List<UserGroupListDto>> findUserGroupListById(String tenantId) {
        return systemManageService.findUserGroupListById(tenantId);
    }

    @PostMapping("saveGroupApplyEmpowerInfo")
    @Operation(summary = "保存用户组应用授权信息")
    
    @Parameters({
            @Parameter(name = "groupApplyEmpowerVos", description = "配置用户组应用授权数据对象字符串")
    })
    public ResponseResult<String> saveGroupApplyEmpowerInfo(String groupApplyEmpowerVos) {
        return systemManageService.saveGroupApplyEmpowerInfo(groupApplyEmpowerVos);
    }

    @PostMapping("saveGroupApplyInfo")
    @Operation(summary = "保存用户组应用授权信息(新版)")
    @WebLog("角色管理-保存角色应用授权信息")
    
    @Parameters({
            @Parameter(name = "groupId", description = "用户组id"),
            @Parameter(name = "moduleId", description = "模块id"),
            @Parameter(name = "groupApplyVos", description = "配置用户组应用授权对象数组字符串 例如[{\"permissionId\": \"权限id1\",\"operate\": \"1\"},{\"permissionId\": \"权限id2\",\"operate\": \"1\"}]")
    })
    public ResponseResult<Void> saveGroupApplyInfo(String groupId, String moduleId, String groupApplyVos) {
        return systemManageService.saveGroupApplyInfo(groupId, moduleId, groupApplyVos);
    }

    @PostMapping("findGroupApplyEmpowerInfoById")
    @Operation(summary = "查询指定用户组指定模块下配置的应用授权数据")
    
    @Parameters({
            @Parameter(name = "groupId", description = "用户组id"),
            @Parameter(name = "moduleId", description = "模块id")
    })
    public ResponseResult<List<PermissionListDto>> findGroupApplyEmpowerInfoById(String groupId, String moduleId) {
        return systemManageService.findGroupApplyEmpowerInfoById(groupId, moduleId);
    }

    @PostMapping("groupManageUser")
    @Operation(summary = "用户组管理人员")
    @WebLog("角色管理-角色管理人员")
    
    @Parameters({
            @Parameter(name = "addUserIds", description = "新增用户id(多个以逗号分割)"),
            @Parameter(name = "deleteUserIds", description = "移除用户id(多个以逗号分割)"),
            @Parameter(name = "groupId", description = "用户组id")
    })
    public ResponseResult<String> groupManageUser(String addUserIds, String deleteUserIds, String groupId) {
        return systemManageService.groupManageUser(addUserIds, deleteUserIds, groupId);
    }

    @PostMapping("findUserDetailsById")
    @Operation(summary = "根据用户id查询用户详情")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id")
    })
    public ResponseResult<UserDto> findUserDetailsById(String userId) {
        return systemManageService.findUserDetailsById(userId);
    }

    @PostMapping("deleteGroupApplyEmpowerByGroupId")
    @Operation(summary = "删除指定用户组下关联的指定权限数据")
    @WebLog("角色管理-删除指定角色下关联的指定权限数据")
    
    @Parameters({
            @Parameter(name = "groupId", description = "用户组id"),
            @Parameter(name = "permissionIds", description = "权限id（多个以逗号分隔）")
    })
    public ResponseResult<String> deleteGroupApplyEmpowerByGroupId(String groupId, String permissionIds) {
        return systemManageService.deleteGroupApplyEmpowerByGroupId(groupId, Arrays.stream(permissionIds.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList()));
    }

    @PostMapping("updateUserByGroupId")
    @Operation(summary = "根据用户组id修改用户下面的用户组数据")
    @WebLog("角色管理-根据角色id修改用户下面的角色数据")
    
    @Parameters({
            @Parameter(name = "groupId", description = "用户组id"),
            @Parameter(name = "userIds", description = "多个用户id(多个以逗号分割)")
    })
    public ResponseResult<Void> updateUserByGroupId(String groupId, String userIds) {
        return systemManageService.updateUserByGroupId(groupId, Arrays.stream(userIds.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList()));
    }

}
