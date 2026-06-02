package com.sunmax.together.service.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.UserGroupInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppletUserChangeVo;
import com.sunmax.together.dto.operation.appletUser.AppletCancelInfoDto;
import com.sunmax.together.dto.operation.appletUser.AppletUserDetailDto;
import com.sunmax.common.dto.together.UserDisWalletDto;
import com.sunmax.together.dto.operation.appletUser.UserGroupListDto;
import com.sunmax.together.vo.operation.appletUser.AppletUserQueryVo;
import com.sunmax.together.vo.operation.appletUser.UserGroupChangeVo;

import java.util.List;

public interface AppletUserService {

    /**
     * 新增或编辑小程序用户
     * @param appletUserChangeVo
     * @param userId
     * @return
     */
    ResponseResult<String> saveOrUpdateAppletUser(AppletUserChangeVo appletUserChangeVo, String userId);

    /**
     * 分页查询小程序用户列表
     * @param appletUserQueryVo
     * @return
     */
    ResponseResult<PageDto<AppletUserInfoDto>> queryAppletUserList(AppletUserQueryVo appletUserQueryVo);

    /**
     * 修改小程序用户状态
     * @param id 小程序用户唯一id
     * @param userState 用户状态 1-正常 2-冻结 3-注销
     * @return
     */
    ResponseResult<String> updateAppletUserState(String id, Integer userState);

    /**
     * 新增或编辑用户分组
     * @param userGroupChangeVo
     * @param userId
     * @return
     */
    ResponseResult<String> saveOrUpdateUserGroup(UserGroupChangeVo userGroupChangeVo, String userId);

    /**
     * 根据分组id查询关联小程序用户列表
     * @param groupId
     * @return
     */
    ResponseResult<List<AppletUserInfoDto>> queryAppletUserListByGroupId(String groupId);

    /**
     * 管理分组下小程序用户
     * @param groupId 用户分组唯一id
     * @param addUserIds 添加小程序用户id(多个以逗号分割)
     * @param removeUserIds 删除小程序用户id(多个以逗号分割)
     * @return
     */
    ResponseResult<String> manageGroupUserByGroupId(String groupId, String addUserIds, String removeUserIds);

    /**
     * 查询全部用户分组列表
     * @return
     */
    ResponseResult<List<UserGroupInfoDto>> queryAllUserGroupList();

    /**
     * 分页查询用户分组列表
     * @param page 当前页
     * @param size 当前页条数
     * @param groupName 分组名称模糊查询
     * @return
     */
    ResponseResult<PageDto<UserGroupListDto>> queryUserGroupList(Integer page, Integer size, String groupName);

    /**
     * 根据站点id删除所有用户分组关联的站点数据
     * @param siteId 站点id
     * @return
     */
    ResponseResult<String> deleteAllGroupBeSiteBySiteId(String siteId);

    /**
     * 根据分组id删除用户分组信息
     * @param id 分组id
     * @return
     */
    ResponseResult<String> deleteUserGroupById(String id);

    /**
     * 查询全部站点信息列表
     * @return
     */
    ResponseResult<List<SiteInfoDto>> queryAllSiteInfoList(String groupId);

    /**
     * 根据小程序用户id查询用户详情数据
     * @param id
     * @return
     */
    ResponseResult<AppletUserDetailDto> queryAppletUserDetailById(String id);

    /**
     * 根据电桩编码和用户手机哈查询用户折扣
     * @param pileCode 电桩编码
     * @param phoneNum //用户手机号
     * @return
     */
    ResponseResult<UserGroupInfoDto> queryUserDiscountByPhoneNum(String pileCode, String phoneNum);

    /**
     * 根据小程序用户手机号查询用户信息
     * @param phoneNum 小程序登录用户手机号
     * @return
     */
    ResponseResult<AppletUserInfoDto> queryAppletUserInfoByPhoneNum(String phoneNum);

    /**
     * 根据小程序用户手机号修改小程序编码信息(如果根据手机号查不到小程序用户，则创建小程序用户信息)
     * @param phoneNum 小程序用户手机号
     * @param openid 用户在普通商户AppID下的唯一标识
     * @param appletId 小程序编码
     * @return
     */
    ResponseResult<String> updateOrSaveAppletUser(String phoneNum, String openid, String appletId);

    /**
     * 编辑小程序用户
     * @param appletUserChangeVo
     * @return
     */
    ResponseResult<String> updateAppletUser(AppletUserChangeVo appletUserChangeVo);

    /**
     * 根据小程序用户id查询用户V2G钱包列表
     * @param appletUserId 小程序用户id
     * @return 用户V2G钱包列表
     */
    ResponseResult<List<UserDisWalletDto>> findUserDisWalletListById(String appletUserId);

    /**
     * 提交小程序用户注销申请
     * @param appletUserId 小程序用户id
     * @param appletName 小程序名称
     * @return
     */
    ResponseResult<String> submitAppletCancel(String appletUserId, String appletName);

    /**
     * 分页查询小程序用户注销申请列表
     * @param phoneNum
     * @param applyState
     * @param page
     * @param size
     * @return
     */
    ResponseResult<PageDto<AppletCancelInfoDto>> findAppletCancelListByPage(String phoneNum, Integer applyState, Integer page, Integer size);

    /**
     * 注销小程序用户账号
     * @param id 注销小程序用户账号
     * @return
     */
    ResponseResult<String> cancelAppletUser(String id);

    /**
     * 根据小程序用户id更新手机号
     * @param appletUserId 小程序用户id
     * @param phoneNum 手机号
     * @return
     */
    ResponseResult<String> updateAppletUserPhoneById(String appletUserId, String phoneNum);
}
