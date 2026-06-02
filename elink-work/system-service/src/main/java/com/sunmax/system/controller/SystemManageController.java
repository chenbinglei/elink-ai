package com.sunmax.system.controller;

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
import io.swagger.annotations.*;
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
@Api(tags = "系统管理")
public class SystemManageController {

    @Autowired
    private SystemManageService systemManageService;

    @PostMapping("saveOrUpdateUserGroup")
    @ApiOperation("保存或编辑用户组信息")
    @WebLog("角色管理-保存或编辑角色信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> saveOrUpdateUserGroup(UserGroupVo userGroupVo) {
        return systemManageService.saveOrUpdateUserGroup(userGroupVo);
    }

    @PostMapping("deleteUserGroupById")
    @ApiOperation("根据用户组id删除用户组信息")
    @WebLog("角色管理-根据角色id删除角色信息")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "groupId", value = "用户组id", dataType = "String", required = true)
    public ResponseResult<String> deleteUserGroupById(String groupId) {
        return systemManageService.deleteUserGroupById(groupId);
    }

    @PostMapping("findUserGroupListByPage")
    @ApiOperation("分页查询用户组列表信息")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupName", value = "用户组名称(模糊查询)", dataType = "String"),
            @ApiImplicitParam(name = "tenantId", value = "租户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "page", value = "当前页", dataType = "Integer", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", dataType = "Integer", required = true)
    })
    public ResponseResult<PageDto<UserGroupListDto>> findUserGroupListByPage(String groupName, String tenantId, Integer page, Integer size) {
        return systemManageService.findUserGroupListByPage(groupName, tenantId, page, size);
    }

    @PostMapping("saveOrUpdateUserInfo")
    @ApiOperation("保存或编辑用户信息")
    @WebLog("用户管理-保存或编辑用户信息")
    @ApiOperationSupport(order = 4)
    public ResponseResult<String> saveOrUpdateUserInfo(UserVo userVo, MultipartFile imageFile) {
        return systemManageService.saveOrUpdateUserInfo(userVo, imageFile);
    }

    @PostMapping("deleteUserInfoById")
    @ApiOperation("根据用户id删除用户信息")
    @WebLog("用户管理-根据用户id删除用户信息")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParam(name = "id", value = "用户id", dataType = "String", required = true)
    public ResponseResult<String> deleteUserInfoById(String id) {
        return systemManageService.deleteUserInfoById(id);
    }

    @PostMapping("findUserListByPage")
    @ApiOperation("分页查询用户列表数据")
    @ApiOperationSupport(order = 6)
    public ResponseResult<PageDto<UserListDto>> findUserListByPage(UserListQueryVo userListQueryVo) {
        return systemManageService.findUserListByPage(userListQueryVo);
    }

    @PostMapping("updateUserStateById")
    @ApiOperation("根据id更新用户状态")
    @WebLog("用户管理-根据用户id更新用户状态")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "userState", value = "用户状态 0-关闭 1-开启", dataType = "Integer", required = true)
    })
    public ResponseResult<String> updateUserStateById(String id, Integer userState) {
        return systemManageService.updateUserStateById(id, userState);
    }

    @PostMapping("updateUserOrganStructure")
    @ApiOperation("添加/修改组织信息")
    @WebLog("企业信息-添加/修改组织信息")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userIds", value = "用户id(多个以逗号分割)", dataType = "String", required = true),
            @ApiImplicitParam(name = "organId", value = "组织架构id", dataType = "String", required = true)
    })
    public ResponseResult<String> updateUserOrganStructure(String userIds, String organId) {
        return systemManageService.updateUserOrganStructure(Arrays.stream(userIds.split(",")).map(String::trim).collect(Collectors.toList()), organId);
    }

    @PostMapping("updateUserGroup")
    @ApiOperation("添加/修改用户组信息-用户管理")
    @WebLog("用户管理-添加或修改用户组信息")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userIds", value = "用户id(多个以逗号分割)", dataType = "String", required = true),
            @ApiImplicitParam(name = "groupIds", value = "用户组id(多个以逗号分割)", dataType = "String")
    })
    public ResponseResult<String> updateUserGroup(String userIds, String groupIds) {
        return systemManageService.updateUserGroup(Arrays.stream(userIds.split(",")).map(String::trim).collect(Collectors.toList()), groupIds);
    }

    @PostMapping("findUserGroupListById")
    @ApiOperation("查询租户下用户组列表")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParam(name = "tenantId", value = "所属租户id", dataType = "String", required = true)
    public ResponseResult<List<UserGroupListDto>> findUserGroupListById(String tenantId) {
        return systemManageService.findUserGroupListById(tenantId);
    }

    @PostMapping("saveGroupApplyEmpowerInfo")
    @ApiOperation("保存用户组应用授权信息")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupApplyEmpowerVos", value = "配置用户组应用授权数据对象字符串", dataType = "String", required = true)
    })
    public ResponseResult<String> saveGroupApplyEmpowerInfo(String groupApplyEmpowerVos) {
        return systemManageService.saveGroupApplyEmpowerInfo(groupApplyEmpowerVos);
    }

    @PostMapping("saveGroupApplyInfo")
    @ApiOperation("保存用户组应用授权信息(新版)")
    @WebLog("角色管理-保存角色应用授权信息")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "用户组id", dataType = "String", required = true),
            @ApiImplicitParam(name = "moduleId", value = "模块id", dataType = "String", required = true),
            @ApiImplicitParam(name = "groupApplyVos", value = "配置用户组应用授权对象数组字符串 例如[{\"permissionId\": \"权限id1\",\"operate\": \"1\"},{\"permissionId\": \"权限id2\",\"operate\": \"1\"}]", dataType = "String")
    })
    public ResponseResult<Void> saveGroupApplyInfo(String groupId, String moduleId, String groupApplyVos) {
        return systemManageService.saveGroupApplyInfo(groupId, moduleId, groupApplyVos);
    }

    @PostMapping("findGroupApplyEmpowerInfoById")
    @ApiOperation("查询指定用户组指定模块下配置的应用授权数据")
    @ApiOperationSupport(order = 14)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "用户组id", dataType = "String", required = true),
            @ApiImplicitParam(name = "moduleId", value = "模块id", dataType = "String", required = true)
    })
    public ResponseResult<List<PermissionListDto>> findGroupApplyEmpowerInfoById(String groupId, String moduleId) {
        return systemManageService.findGroupApplyEmpowerInfoById(groupId, moduleId);
    }

    @PostMapping("groupManageUser")
    @ApiOperation("用户组管理人员")
    @WebLog("角色管理-角色管理人员")
    @ApiOperationSupport(order = 15)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addUserIds", value = "新增用户id(多个以逗号分割)", dataType = "String", required = true),
            @ApiImplicitParam(name = "deleteUserIds", value = "移除用户id(多个以逗号分割)", dataType = "String", required = true),
            @ApiImplicitParam(name = "groupId", value = "用户组id", dataType = "String", required = true)
    })
    public ResponseResult<String> groupManageUser(String addUserIds, String deleteUserIds, String groupId) {
        return systemManageService.groupManageUser(addUserIds, deleteUserIds, groupId);
    }

    @PostMapping("findUserDetailsById")
    @ApiOperation("根据用户id查询用户详情")
    @ApiOperationSupport(order = 16)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true)
    })
    public ResponseResult<UserDto> findUserDetailsById(String userId) {
        return systemManageService.findUserDetailsById(userId);
    }

    @PostMapping("deleteGroupApplyEmpowerByGroupId")
    @ApiOperation("删除指定用户组下关联的指定权限数据")
    @WebLog("角色管理-删除指定角色下关联的指定权限数据")
    @ApiOperationSupport(order = 17)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "用户组id", dataType = "String", required = true),
            @ApiImplicitParam(name = "permissionIds", value = "权限id（多个以逗号分隔）", dataType = "String", required = true)
    })
    public ResponseResult<String> deleteGroupApplyEmpowerByGroupId(String groupId, String permissionIds) {
        return systemManageService.deleteGroupApplyEmpowerByGroupId(groupId, Arrays.stream(permissionIds.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList()));
    }

    @PostMapping("updateUserByGroupId")
    @ApiOperation("根据用户组id修改用户下面的用户组数据")
    @WebLog("角色管理-根据角色id修改用户下面的角色数据")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "用户组id", dataType = "String", required = true),
            @ApiImplicitParam(name = "userIds", value = "多个用户id(多个以逗号分割)", dataType = "String")
    })
    public ResponseResult<Void> updateUserByGroupId(String groupId, String userIds) {
        return systemManageService.updateUserByGroupId(groupId, Arrays.stream(userIds.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList()));
    }

}
