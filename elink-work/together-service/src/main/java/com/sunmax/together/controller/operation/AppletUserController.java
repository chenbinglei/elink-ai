package com.sunmax.together.controller.operation;

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
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("appletUser")
@Api(tags = "小程序用户管理")
public class AppletUserController {

    @Autowired
    private AppletUserService appletUserService;

    @PostMapping("saveOrUpdateAppletUser")
    @ApiOperation("新增或编辑小程序用户")
    @WebLog("小程序用户-新增或编辑小程序用户")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> saveOrUpdateAppletUser(AppletUserChangeVo appletUserChangeVo, String userId) {
        return appletUserService.saveOrUpdateAppletUser(appletUserChangeVo, userId);
    }

    @PostMapping("queryAppletUserList")
    @ApiOperation("分页查询小程序用户列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<AppletUserInfoDto>> queryAppletUserList(AppletUserQueryVo appletUserQueryVo) {
        return appletUserService.queryAppletUserList(appletUserQueryVo);
    }

    @PostMapping("updateAppletUserState")
    @ApiOperation("修改小程序用户状态")
    @WebLog("小程序用户-修改小程序用户状态")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "小程序用户唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "userState", value = "用户状态 1-正常 2-冻结 3-注销", paramType = "query")
    })
    public ResponseResult<String> updateAppletUserState(String id, Integer userState) {
        return appletUserService.updateAppletUserState(id, userState);
    }

    @PostMapping("saveOrUpdateUserGroup")
    @ApiOperation("新增或编辑用户分组")
    @WebLog("用户分组-新增或编辑用户分组")
    @ApiOperationSupport(order = 4)
    public ResponseResult<String> saveOrUpdateUserGroup(UserGroupChangeVo userGroupChangeVo, String userId) {
        return appletUserService.saveOrUpdateUserGroup(userGroupChangeVo, userId);
    }

    @PostMapping("queryAppletUserListByGroupId")
    @ApiOperation("根据分组id查询关联小程序用户列表")
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<AppletUserInfoDto>> queryAppletUserListByGroupId(String groupId) {
        return appletUserService.queryAppletUserListByGroupId(groupId);
    }

    @PostMapping("manageGroupUserByGroupId")
    @ApiOperation("管理分组下小程序用户")
    @WebLog("用户分组-管理分组下小程序用户")
    @ApiOperationSupport(order = 6)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "用户分组唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "addUserIds", value = "添加小程序用户id(多个以逗号分割)", paramType = "query"),
            @ApiImplicitParam(name = "removeUserIds", value = "删除小程序用户id(多个以逗号分割)", paramType = "query")
    })
    public ResponseResult<String> manageGroupUserByGroupId(String groupId, String addUserIds, String removeUserIds) {
        return appletUserService.manageGroupUserByGroupId(groupId, addUserIds, removeUserIds);
    }

    @PostMapping("queryAllUserGroupList")
    @ApiOperation("查询全部用户分组列表")
    @ApiOperationSupport(order = 7)
    public ResponseResult<List<UserGroupInfoDto>> queryAllUserGroupList() {
        return appletUserService.queryAllUserGroupList();
    }

    @PostMapping("queryUserGroupList")
    @ApiOperation("分页查询用户分组列表")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", paramType = "query", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", paramType = "query", required = true),
            @ApiImplicitParam(name = "groupName", value = "分组名称模糊查询", paramType = "query")
    })
    public ResponseResult<PageDto<UserGroupListDto>> queryUserGroupList(Integer page, Integer size, String groupName) {
        return appletUserService.queryUserGroupList(page, size, groupName);
    }

    @PostMapping("deleteUserGroupById")
    @ApiOperation("根据分组id删除用户分组信息")
    @WebLog("用户分组-根据分组id删除用户分组信息")
    @ApiOperationSupport(order = 9)
    public ResponseResult<String> deleteUserGroupById(String id) {
        return appletUserService.deleteUserGroupById(id);
    }

    @PostMapping("queryAllSiteInfoList")
    @ApiOperation("查询全部站点信息列表")
    @ApiOperationSupport(order = 10)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "groupId", value = "分组id", paramType = "query")
    })
    public ResponseResult<List<SiteInfoDto>> queryAllSiteInfoList(String groupId) {
        return appletUserService.queryAllSiteInfoList(groupId);
    }

    @PostMapping("queryAppletUserDetailById")
    @ApiOperation("根据小程序用户id查询用户详情数据")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "小程序用户唯一id", paramType = "query", required = true)
    })
    public ResponseResult<AppletUserDetailDto> queryAppletUserDetailById(String id) {
        return appletUserService.queryAppletUserDetailById(id);
    }

    @PostMapping("findUserDisWalletListById")
    @ApiOperation("根据小程序用户id查询用户V2G钱包列表")
    @ApiOperationSupport(order = 12)
    @ApiImplicitParam(name = "appletUserId", value = "小程序用户唯一id", paramType = "query", required = true)
    public ResponseResult<List<UserDisWalletDto>> findUserDisWalletListById(String appletUserId) {
        return appletUserService.findUserDisWalletListById(appletUserId);
    }

    @PostMapping("findAppletCancelListByPage")
    @ApiOperation("分页查询小程序用户注销申请列表")
    @ApiOperationSupport(order = 13)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", paramType = "query", required = true),
            @ApiImplicitParam(name = "size", value = "当前页条数", paramType = "query", required = true),
            @ApiImplicitParam(name = "phoneNum", value = "手机号码", paramType = "query"),
            @ApiImplicitParam(name = "applyState", value = "申请状态 1-申请注销 2-已注销", paramType = "query")
    })
    public ResponseResult<PageDto<AppletCancelInfoDto>> findAppletCancelListByPage(String phoneNum, Integer applyState, Integer page, Integer size) {
        return appletUserService.findAppletCancelListByPage(phoneNum, applyState, page, size);
    }

    @PostMapping("cancelAppletUser")
    @ApiOperation("注销小程序用户账号")
    @WebLog("用户分组-注销小程序用户账号")
    @ApiOperationSupport(order = 14)
    @ApiImplicitParam(name = "id", value = "注销申请唯一id", paramType = "query", required = true)
    public ResponseResult<String> cancelAppletUser(String id) {
        return appletUserService.cancelAppletUser(id);
    }
}
