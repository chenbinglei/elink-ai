package com.sunmax.together.controller.operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.UserGroupInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.appletUser.AppletCancelInfoDto;
import com.sunmax.together.dto.operation.appletUser.AppletUserDetailDto;
import com.sunmax.common.dto.together.UserDisWalletDto;
import com.sunmax.together.dto.operation.appletUser.UserGroupListDto;
import com.sunmax.together.service.operation.AppletUserService;
import com.sunmax.common.vo.together.AppletUserChangeVo;
import com.sunmax.together.vo.operation.appletUser.AppletUserQueryVo;
import com.sunmax.together.vo.operation.appletUser.UserGroupChangeVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("appletUser")
@Tag(name = "小程序用户管理")
public class AppletUserController {

    @Autowired
    private AppletUserService appletUserService;

    @PostMapping("saveOrUpdateAppletUser")
    @Operation(summary = "新增或编辑小程序用户")
    @WebLog("小程序用户-新增或编辑小程序用户")
    
    public ResponseResult<String> saveOrUpdateAppletUser(AppletUserChangeVo appletUserChangeVo, String userId) {
        return appletUserService.saveOrUpdateAppletUser(appletUserChangeVo, userId);
    }

    @PostMapping("queryAppletUserList")
    @Operation(summary = "分页查询小程序用户列表")
    
    public ResponseResult<PageDto<AppletUserInfoDto>> queryAppletUserList(AppletUserQueryVo appletUserQueryVo) {
        return appletUserService.queryAppletUserList(appletUserQueryVo);
    }

    @PostMapping("updateAppletUserState")
    @Operation(summary = "修改小程序用户状态")
    @WebLog("小程序用户-修改小程序用户状态")
    
    @Parameters({
            @Parameter(name = "id", description = "小程序用户唯一id"),
            @Parameter(name = "userState", description = "用户状态 1-正常 2-冻结 3-注销")
    })
    public ResponseResult<String> updateAppletUserState(String id, Integer userState) {
        return appletUserService.updateAppletUserState(id, userState);
    }

    @PostMapping("saveOrUpdateUserGroup")
    @Operation(summary = "新增或编辑用户分组")
    @WebLog("用户分组-新增或编辑用户分组")
    
    public ResponseResult<String> saveOrUpdateUserGroup(UserGroupChangeVo userGroupChangeVo, String userId) {
        return appletUserService.saveOrUpdateUserGroup(userGroupChangeVo, userId);
    }

    @PostMapping("queryAppletUserListByGroupId")
    @Operation(summary = "根据分组id查询关联小程序用户列表")
    
    public ResponseResult<List<AppletUserInfoDto>> queryAppletUserListByGroupId(String groupId) {
        return appletUserService.queryAppletUserListByGroupId(groupId);
    }

    @PostMapping("manageGroupUserByGroupId")
    @Operation(summary = "管理分组下小程序用户")
    @WebLog("用户分组-管理分组下小程序用户")
    
    @Parameters({
            @Parameter(name = "groupId", description = "用户分组唯一id"),
            @Parameter(name = "addUserIds", description = "添加小程序用户id(多个以逗号分割)"),
            @Parameter(name = "removeUserIds", description = "删除小程序用户id(多个以逗号分割)")
    })
    public ResponseResult<String> manageGroupUserByGroupId(String groupId, String addUserIds, String removeUserIds) {
        return appletUserService.manageGroupUserByGroupId(groupId, addUserIds, removeUserIds);
    }

    @PostMapping("queryAllUserGroupList")
    @Operation(summary = "查询全部用户分组列表")
    
    public ResponseResult<List<UserGroupInfoDto>> queryAllUserGroupList() {
        return appletUserService.queryAllUserGroupList();
    }

    @PostMapping("queryUserGroupList")
    @Operation(summary = "分页查询用户分组列表")
    
    @Parameters({
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数"),
            @Parameter(name = "groupName", description = "分组名称模糊查询")
    })
    public ResponseResult<PageDto<UserGroupListDto>> queryUserGroupList(Integer page, Integer size, String groupName) {
        return appletUserService.queryUserGroupList(page, size, groupName);
    }

    @PostMapping("deleteUserGroupById")
    @Operation(summary = "根据分组id删除用户分组信息")
    @WebLog("用户分组-根据分组id删除用户分组信息")
    
    public ResponseResult<String> deleteUserGroupById(String id) {
        return appletUserService.deleteUserGroupById(id);
    }

    @PostMapping("queryAllSiteInfoList")
    @Operation(summary = "查询全部站点信息列表")
    
    @Parameters({
            @Parameter(name = "groupId", description = "分组id")
    })
    public ResponseResult<List<SiteInfoDto>> queryAllSiteInfoList(String groupId) {
        return appletUserService.queryAllSiteInfoList(groupId);
    }

    @PostMapping("queryAppletUserDetailById")
    @Operation(summary = "根据小程序用户id查询用户详情数据")
    
    @Parameters({
            @Parameter(name = "id", description = "小程序用户唯一id")
    })
    public ResponseResult<AppletUserDetailDto> queryAppletUserDetailById(String id) {
        return appletUserService.queryAppletUserDetailById(id);
    }

    @PostMapping("findUserDisWalletListById")
    @Operation(summary = "根据小程序用户id查询用户V2G钱包列表")
    
    @Parameter(name = "appletUserId", description = "小程序用户唯一id")
    public ResponseResult<List<UserDisWalletDto>> findUserDisWalletListById(String appletUserId) {
        return appletUserService.findUserDisWalletListById(appletUserId);
    }

    @PostMapping("findAppletCancelListByPage")
    @Operation(summary = "分页查询小程序用户注销申请列表")
    
    @Parameters({
            @Parameter(name = "page", description = "当前页"),
            @Parameter(name = "size", description = "当前页条数"),
            @Parameter(name = "phoneNum", description = "手机号码"),
            @Parameter(name = "applyState", description = "申请状态 1-申请注销 2-已注销")
    })
    public ResponseResult<PageDto<AppletCancelInfoDto>> findAppletCancelListByPage(String phoneNum, Integer applyState, Integer page, Integer size) {
        return appletUserService.findAppletCancelListByPage(phoneNum, applyState, page, size);
    }

    @PostMapping("cancelAppletUser")
    @Operation(summary = "注销小程序用户账号")
    @WebLog("用户分组-注销小程序用户账号")
    
    @Parameter(name = "id", description = "注销申请唯一id")
    public ResponseResult<String> cancelAppletUser(String id) {
        return appletUserService.cancelAppletUser(id);
    }
}
