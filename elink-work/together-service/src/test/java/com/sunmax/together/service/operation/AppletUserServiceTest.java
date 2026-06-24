package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.AppletUserChangeVo;
import com.sunmax.together.dao.*;
import com.sunmax.together.entity.*;
import com.sunmax.together.service.operation.impl.AppletUserServiceImpl;
import com.sunmax.together.vo.operation.appletUser.UserGroupChangeVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AppletUserService 单元测试")
class AppletUserServiceTest {

    @Mock private AppletUserDao appletUserDao;
    @Mock private UserGroupDao userGroupDao;
    @Mock private GroupBeSiteDao groupBeSiteDao;
    @Mock private UserDisWalletDao userDisWalletDao;
    @Mock private AppletCancelDao appletCancelDao;

    @InjectMocks private AppletUserServiceImpl appletUserService;

    @Test
    @DisplayName("新增小程序用户-手机号重复返回错误")
    void saveAppletUser_duplicatePhone_returnsError() {
        AppletUserChangeVo vo = new AppletUserChangeVo();
        vo.setPhoneNum("13800138000");

        AppletUserEntity existing = new AppletUserEntity();
        existing.setId("au-001");
        existing.setPhoneNum("13800138000");
        when(appletUserDao.findOne(any(org.springframework.data.domain.Example.class))).thenReturn(Optional.of(existing));

        ResponseResult<String> result = appletUserService.saveOrUpdateAppletUser(vo, "user-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("新增小程序用户-成功")
    void saveAppletUser_new_success() {
        AppletUserChangeVo vo = new AppletUserChangeVo();
        vo.setPhoneNum("13800138000");

        when(appletUserDao.findOne(any(org.springframework.data.domain.Example.class))).thenReturn(Optional.empty());
        when(appletUserDao.save(any(AppletUserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = appletUserService.saveOrUpdateAppletUser(vo, "user-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("编辑小程序用户-成功")
    void saveAppletUser_update_success() {
        AppletUserChangeVo vo = new AppletUserChangeVo();
        vo.setId("au-001");
        vo.setPhoneNum("13800138000");

        AppletUserEntity existing = new AppletUserEntity();
        existing.setId("au-001");
        existing.setPhoneNum("13800138000");
        when(appletUserDao.findOne(any(org.springframework.data.domain.Example.class))).thenReturn(Optional.of(existing));

        AppletUserEntity dbEntity = new AppletUserEntity();
        dbEntity.setId("au-001");
        dbEntity.setCreateId("creator-001");
        dbEntity.setCreateTime(LocalDateTime.now());
        when(appletUserDao.findById("au-001")).thenReturn(Optional.of(dbEntity));
        when(appletUserDao.save(any(AppletUserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = appletUserService.saveOrUpdateAppletUser(vo, "user-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("修改小程序用户状态-成功")
    void updateAppletUserState_success() {
        AppletUserEntity entity = new AppletUserEntity();
        entity.setId("au-001");
        entity.setUserState(1);
        when(appletUserDao.findById("au-001")).thenReturn(Optional.of(entity));
        when(appletUserDao.save(any(AppletUserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = appletUserService.updateAppletUserState("au-001", 2);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("修改小程序用户状态-不存在返回失败")
    void updateAppletUserState_notFound_returnsFail() {
        when(appletUserDao.findById("au-001")).thenReturn(Optional.empty());

        ResponseResult<String> result = appletUserService.updateAppletUserState("au-001", 2);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("新增用户分组-名称重复返回错误")
    void saveUserGroup_duplicateName_returnsError() {
        UserGroupChangeVo vo = new UserGroupChangeVo();
        vo.setGroupName("分组1");

        UserGroupEntity existing = new UserGroupEntity();
        existing.setId("ug-001");
        existing.setGroupName("分组1");
        when(userGroupDao.findOne(any(org.springframework.data.domain.Example.class))).thenReturn(Optional.of(existing));

        ResponseResult<String> result = appletUserService.saveOrUpdateUserGroup(vo, "user-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("新增用户分组-成功")
    void saveUserGroup_new_success() {
        UserGroupChangeVo vo = new UserGroupChangeVo();
        vo.setGroupName("分组1");
        vo.setApplySiteIds("site-001,site-002");

        when(userGroupDao.findOne(any(org.springframework.data.domain.Example.class))).thenReturn(Optional.empty());
        UserGroupEntity saved = new UserGroupEntity();
        saved.setId("ug-001");
        when(userGroupDao.save(any(UserGroupEntity.class))).thenReturn(saved);
        when(groupBeSiteDao.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = appletUserService.saveOrUpdateUserGroup(vo, "user-001");
        assertTrue(result.isSuccess());
        verify(groupBeSiteDao).deleteAllByGroupId("ug-001");
    }

    @Test
    @DisplayName("编辑用户分组-成功")
    void saveUserGroup_update_success() {
        UserGroupChangeVo vo = new UserGroupChangeVo();
        vo.setId("ug-001");
        vo.setGroupName("分组1");

        UserGroupEntity existing = new UserGroupEntity();
        existing.setId("ug-001");
        existing.setGroupName("分组1");
        when(userGroupDao.findOne(any(org.springframework.data.domain.Example.class))).thenReturn(Optional.of(existing));

        UserGroupEntity dbEntity = new UserGroupEntity();
        dbEntity.setId("ug-001");
        dbEntity.setCreateId("creator-001");
        dbEntity.setCreateTime(LocalDateTime.now());
        when(userGroupDao.findById("ug-001")).thenReturn(Optional.of(dbEntity));

        UserGroupEntity saved = new UserGroupEntity();
        saved.setId("ug-001");
        when(userGroupDao.save(any(UserGroupEntity.class))).thenReturn(saved);

        ResponseResult<String> result = appletUserService.saveOrUpdateUserGroup(vo, "user-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据分组id查询用户列表-有数据返回列表")
    void queryAppletUserListByGroupId_withData_returnsList() {
        AppletUserEntity user = new AppletUserEntity();
        user.setId("au-001");
        user.setGroupId("ug-001");
        user.setPhoneNum("13800138000");
        user.setUserState(1);
        user.setCreateTime(LocalDateTime.now());
        when(appletUserDao.findAllByGroupIdIn(List.of("ug-001"))).thenReturn(List.of(user));

        ResponseResult<?> result = appletUserService.queryAppletUserListByGroupId("ug-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询所有用户分组-有数据返回列表")
    void queryAllUserGroupList_withData_returnsList() {
        UserGroupEntity group = new UserGroupEntity();
        group.setId("ug-001");
        group.setGroupName("分组1");
        when(userGroupDao.findAll()).thenReturn(List.of(group));

        ResponseResult<?> result = appletUserService.queryAllUserGroupList();
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除用户分组-成功")
    void deleteUserGroupById_success() {
        UserGroupEntity group = new UserGroupEntity();
        group.setId("ug-001");
        when(userGroupDao.findById("ug-001")).thenReturn(Optional.of(group));

        AppletUserEntity user = new AppletUserEntity();
        user.setId("au-001");
        user.setGroupId("ug-001");
        when(appletUserDao.findAllByGroupIdIn(List.of("ug-001"))).thenReturn(List.of(user));
        when(appletUserDao.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = appletUserService.deleteUserGroupById("ug-001");
        assertTrue(result.isSuccess());
        verify(appletUserDao).saveAll(any());
        verify(userGroupDao).deleteById("ug-001");
    }

    @Test
    @DisplayName("根据站点id删除分组关联站点-成功")
    void deleteAllGroupBeSiteBySiteId_success() {
        doNothing().when(groupBeSiteDao).deleteAllBySiteId("site-001");

        ResponseResult<String> result = appletUserService.deleteAllGroupBeSiteBySiteId("site-001");
        assertTrue(result.isSuccess());
    }
}
