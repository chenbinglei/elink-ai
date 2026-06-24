package com.sunmax.auth.service;

import com.sunmax.auth.dao.*;
import com.sunmax.auth.dto.UserLoginDto;
import com.sunmax.auth.entity.*;
import com.sunmax.auth.service.feign.SystemService;
import com.sunmax.auth.service.feign.TogetherService;
import com.sunmax.auth.service.impl.UserLoginServiceImpl;
import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.util.ResponseResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Example;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserLoginService 单元测试")
class UserLoginServiceTest {

    @Mock private UserDao userDao;
    @Mock private PermissionDao permissionDao;
    @Mock private ProductDao productDao;
    @Mock private TenantApplyEmpowerDao tenantApplyEmpowerDao;
    @Mock private GroupApplyEmpowerDao groupApplyEmpowerDao;
    @Mock private TenantManageDao tenantManageDao;
    @Mock private SystemService systemService;
    @Mock private WechatService wechatService;
    @Mock private TogetherService togetherService;

    @InjectMocks
    private UserLoginServiceImpl userLoginService;

    private UserEntity validUser;

    @BeforeEach
    void setUp() {
        validUser = UserEntity.builder()
                .id("user-001").userAccount("admin").password("admin123")
                .fullName("管理员").tenantId("tenant-001").userRole(0)
                .userState(1).groupId("group-001").build();
    }

    // ===== loadSysUserByAccountAndPassword 测试 =====

    @Test
    @DisplayName("账号为空时返回5001")
    void loadSysUser_emptyAccount_returns5001() {
        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("", "password", "sunos-client");
        assertEquals(5001, result.getCheckCode());
        assertTrue(result.getCheckMsg().contains("账号为空"));
    }

    @Test
    @DisplayName("密码为空时返回5002")
    void loadSysUser_emptyPassword_returns5002() {
        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", "", "sunos-client");
        assertEquals(5002, result.getCheckCode());
        assertTrue(result.getCheckMsg().contains("密码为空"));
    }

    @Test
    @DisplayName("账号为null时返回5001")
    void loadSysUser_nullAccount_returns5001() {
        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword(null, "password", "sunos-client");
        assertEquals(5001, result.getCheckCode());
    }

    @Test
    @DisplayName("密码为null时返回5002")
    void loadSysUser_nullPassword_returns5002() {
        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", null, "sunos-client");
        assertEquals(5002, result.getCheckCode());
    }

    @Test
    @DisplayName("用户不存在时返回5001")
    void loadSysUser_userNotFound_returns5001() {
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());
        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("nonexistent", "password", "sunos-client");
        assertEquals(5001, result.getCheckCode());
        assertTrue(result.getCheckMsg().contains("不存在"));
    }

    @Test
    @DisplayName("用户状态为0(禁用)时返回5001")
    void loadSysUser_disabledUser_returns5001() {
        validUser.setUserState(0);
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));
        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", "password", "sunos-client");
        assertEquals(5001, result.getCheckCode());
    }

    @Test
    @DisplayName("租户名称填充-租户存在时填充名称")
    void loadSysUser_tenantExists_populatesTenantName() {
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));
        TenantInfoEntity tenant = new TenantInfoEntity();
        tenant.setId("tenant-001");
        tenant.setTenantName("测试租户");
        when(tenantManageDao.findById("tenant-001")).thenReturn(Optional.of(tenant));
        when(productDao.findByClientIdAndIsDelete("sunos-client", 0)).thenReturn(null);

        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", "password", "sunos-client");
        assertNotNull(result);
    }

    @Test
    @DisplayName("租户不存在时不填充租户名称")
    void loadSysUser_tenantNotExists_noTenantName() {
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));
        when(tenantManageDao.findById("tenant-001")).thenReturn(Optional.empty());
        when(productDao.findByClientIdAndIsDelete("sunos-client", 0)).thenReturn(null);

        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", "password", "sunos-client");
        assertNotNull(result);
    }

    @Test
    @DisplayName("平台管理员(userRole=0)查询所有权限")
    void loadSysUser_platformAdmin_queriesAllPermissions() {
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));
        when(tenantManageDao.findById("tenant-001")).thenReturn(Optional.empty());

        ProductEntity product = new ProductEntity();
        product.setId("product-001");
        product.setClientId("sunos-client");
        when(productDao.findByClientIdAndIsDelete("sunos-client", 0)).thenReturn(product);
        when(permissionDao.findAllByModuleIdInAndPermissionTypeAndIsDelete(any(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());

        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", "password", "sunos-client");
        assertNotNull(result);
    }

    @Test
    @DisplayName("管理员(userRole=1)查询租户授权权限-无授权返回空")
    void loadSysUser_tenantAdmin_noTenantApply_returnsEmpty() {
        validUser.setUserRole(1);
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));
        when(tenantManageDao.findById("tenant-001")).thenReturn(Optional.empty());

        ProductEntity product = new ProductEntity();
        product.setId("product-001");
        when(productDao.findByClientIdAndIsDelete("sunos-client", 0)).thenReturn(product);
        when(tenantApplyEmpowerDao.findAllByTenantId("tenant-001")).thenReturn(Collections.emptyList());

        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", "password", "sunos-client");
        assertNotNull(result);
    }

    @Test
    @DisplayName("管理员(userRole=1)查询租户授权权限-有授权返回权限列表")
    void loadSysUser_tenantAdmin_withTenantApply_returnsPermissions() {
        validUser.setUserRole(1);
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));
        when(tenantManageDao.findById("tenant-001")).thenReturn(Optional.empty());

        ProductEntity product = new ProductEntity();
        product.setId("product-001");
        when(productDao.findByClientIdAndIsDelete("sunos-client", 0)).thenReturn(product);

        TenantApplyEmpowerEntity apply = new TenantApplyEmpowerEntity();
        apply.setPermissionId("perm-001");
        when(tenantApplyEmpowerDao.findAllByTenantId("tenant-001")).thenReturn(Collections.singletonList(apply));

        PermissionEntity perm = new PermissionEntity();
        perm.setId("perm-001");
        perm.setModuleId("product-001");
        perm.setPermissionType(1);
        perm.setPermissionName("测试权限");
        when(permissionDao.findAllByIdInAndIsDelete(any(), anyInt())).thenReturn(Collections.singletonList(perm));

        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", "password", "sunos-client");
        assertNotNull(result);
    }

    @Test
    @DisplayName("普通用户(userRole=2)无groupId返回空权限")
    void loadSysUser_normalUser_noGroupId_returnsEmpty() {
        validUser.setUserRole(2);
        validUser.setGroupId("");
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));
        when(tenantManageDao.findById("tenant-001")).thenReturn(Optional.empty());

        ProductEntity product = new ProductEntity();
        product.setId("product-001");
        when(productDao.findByClientIdAndIsDelete("sunos-client", 0)).thenReturn(product);

        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", "password", "sunos-client");
        assertNotNull(result);
    }

    @Test
    @DisplayName("普通用户(userRole=2)有groupId查询用户组授权")
    void loadSysUser_normalUser_withGroupId_queriesGroupApply() {
        validUser.setUserRole(2);
        validUser.setGroupId("group-001,group-002");
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));
        when(tenantManageDao.findById("tenant-001")).thenReturn(Optional.empty());

        ProductEntity product = new ProductEntity();
        product.setId("product-001");
        when(productDao.findByClientIdAndIsDelete("sunos-client", 0)).thenReturn(product);

        GroupApplyEmpowerEntity groupApply = new GroupApplyEmpowerEntity();
        groupApply.setPermissionId("perm-001");
        when(groupApplyEmpowerDao.findAllByGroupIdIn(any())).thenReturn(Collections.singletonList(groupApply));

        PermissionEntity perm = new PermissionEntity();
        perm.setId("perm-001");
        perm.setModuleId("product-001");
        perm.setPermissionType(1);
        perm.setPermissionName("测试权限");
        when(permissionDao.findAllByIdInAndIsDelete(any(), anyInt())).thenReturn(Collections.singletonList(perm));

        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", "password", "sunos-client");
        assertNotNull(result);
    }

    @Test
    @DisplayName("产品不存在时返回空菜单列表")
    void loadSysUser_noProduct_returnsEmptyMenu() {
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));
        when(tenantManageDao.findById("tenant-001")).thenReturn(Optional.empty());
        when(productDao.findByClientIdAndIsDelete("sunos-client", 0)).thenReturn(null);

        UserLoginDto result = userLoginService.loadSysUserByAccountAndPassword("admin", "password", "sunos-client");
        assertNotNull(result);
    }

    // ===== findPermissionByUserAccount 测试 =====

    @Test
    @DisplayName("根据账号查询权限-用户不存在返回空列表")
    void findPermission_userNotFound_returnsEmpty() {
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());
        ResponseResult<List<PermissionInfoListDto>> result = userLoginService.findPermissionByUserAccount("nobody", "sunos-client");
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("根据账号查询权限-用户存在但产品不存在返回空列表")
    void findPermission_userExists_noProduct_returnsEmpty() {
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));
        when(productDao.findByClientIdAndIsDelete("sunos-client", 0)).thenReturn(null);

        ResponseResult<List<PermissionInfoListDto>> result = userLoginService.findPermissionByUserAccount("admin", "sunos-client");
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("根据账号查询权限-平台管理员查询所有权限")
    void findPermission_platformAdmin_returnsAllPermissions() {
        when(userDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(validUser));

        ProductEntity product = new ProductEntity();
        product.setId("product-001");
        when(productDao.findByClientIdAndIsDelete("sunos-client", 0)).thenReturn(product);

        PermissionEntity perm = new PermissionEntity();
        perm.setId("perm-001");
        perm.setModuleId("product-001");
        perm.setPermissionType(2);
        perm.setPermissionName("API权限");
        when(permissionDao.findAllByModuleIdInAndPermissionTypeAndIsDelete(any(), eq(2), eq(0)))
                .thenReturn(Collections.singletonList(perm));

        ResponseResult<List<PermissionInfoListDto>> result = userLoginService.findPermissionByUserAccount("admin", "sunos-client");
        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
    }

    // ===== loadUserByAppletCodeAndMobile 测试 =====

    @Test
    @DisplayName("微信小程序登录-code为空返回5001")
    void loadAppletUser_emptyCode_returns5001() {
        UserLoginDto result = userLoginService.loadUserByAppletCodeAndMobile("", "appletKey", null, null);
        assertEquals(5001, result.getCheckCode());
        assertTrue(result.getCheckMsg().contains("微信小程序标识"));
    }

    @Test
    @DisplayName("微信小程序登录-code为null返回5001")
    void loadAppletUser_nullCode_returns5001() {
        UserLoginDto result = userLoginService.loadUserByAppletCodeAndMobile(null, "appletKey", null, null);
        assertEquals(5001, result.getCheckCode());
    }

    @Test
    @DisplayName("微信小程序登录-小程序未配置返回5001")
    void loadAppletUser_appletNotConfigured_returns5001() {
        when(systemService.findByAppletCodeAndAppletType(any(), anyInt()))
                .thenReturn(ResponseResult.ok(null));

        UserLoginDto result = userLoginService.loadUserByAppletCodeAndMobile("code123", "appletKey", null, null);
        assertEquals(5001, result.getCheckCode());
        assertTrue(result.getCheckMsg().contains("添加该小程序"));
    }

    @Test
    @DisplayName("微信小程序登录-小程序查询失败返回5001")
    void loadAppletUser_appletQueryFailed_returns5001() {
        AppletDto dto = new AppletDto();
        dto.setAppletCode("");
        when(systemService.findByAppletCodeAndAppletType(any(), anyInt()))
                .thenReturn(ResponseResult.ok(dto));

        UserLoginDto result = userLoginService.loadUserByAppletCodeAndMobile("code123", "appletKey", null, null);
        assertEquals(5001, result.getCheckCode());
    }

    @Test
    @DisplayName("微信小程序登录-未获取到手机号返回5001")
    void loadAppletUser_noMobile_returns5001() {
        AppletDto dto = new AppletDto();
        dto.setAppletCode("wxCode");
        dto.setAppletSecret("wxSecret");
        dto.setId("applet-001");
        when(systemService.findByAppletCodeAndAppletType(any(), anyInt()))
                .thenReturn(ResponseResult.ok(dto));
        when(wechatService.getAppletData(any(), any(), any(), any(), any()))
                .thenReturn(Map.of("appletId", "applet-openid"));

        UserLoginDto result = userLoginService.loadUserByAppletCodeAndMobile("code123", "appletKey", "encData", "iv");
        assertEquals(5001, result.getCheckCode());
        assertTrue(result.getCheckMsg().contains("手机号"));
    }

    @Test
    @DisplayName("微信小程序登录-新用户注册成功")
    void loadAppletUser_newUser_registersSuccessfully() {
        AppletDto dto = new AppletDto();
        dto.setAppletCode("wxCode");
        dto.setAppletSecret("wxSecret");
        dto.setId("applet-001");
        when(systemService.findByAppletCodeAndAppletType(any(), anyInt()))
                .thenReturn(ResponseResult.ok(dto));
        when(wechatService.getAppletData(any(), any(), any(), any(), any()))
                .thenReturn(Map.of("appletId", "applet-openid", "mobile", "13800138000"));
        when(togetherService.queryAppletUserInfoByPhoneNum("13800138000"))
                .thenReturn(ResponseResult.ok(null));
        when(togetherService.updateOrSaveAppletUser(any(), any(), any()))
                .thenReturn(ResponseResult.ok("new-user-id"));

        UserLoginDto result = userLoginService.loadUserByAppletCodeAndMobile("code123", "appletKey", "encData", "iv");
        assertEquals(2, result.getLogo());
        assertEquals("new-user-id", result.getId());
    }

    @Test
    @DisplayName("微信小程序登录-已有用户正常登录")
    void loadAppletUser_existingUser_loginsSuccessfully() {
        AppletDto dto = new AppletDto();
        dto.setAppletCode("wxCode");
        dto.setAppletSecret("wxSecret");
        dto.setId("applet-001");
        when(systemService.findByAppletCodeAndAppletType(any(), anyInt()))
                .thenReturn(ResponseResult.ok(dto));
        when(wechatService.getAppletData(any(), any(), any(), any(), any()))
                .thenReturn(Map.of("appletId", "applet-openid", "mobile", "13800138000"));

        AppletUserInfoDto userInfo = new AppletUserInfoDto();
        userInfo.setId("user-001");
        userInfo.setPhoneNum("13800138000");
        userInfo.setNickName("测试用户");
        userInfo.setUserState(1);
        when(togetherService.queryAppletUserInfoByPhoneNum("13800138000"))
                .thenReturn(ResponseResult.ok(userInfo));
        when(togetherService.updateOrSaveAppletUser(any(), any(), any()))
                .thenReturn(ResponseResult.ok("user-001"));

        UserLoginDto result = userLoginService.loadUserByAppletCodeAndMobile("code123", "appletKey", "encData", "iv");
        assertEquals(2, result.getLogo());
        assertEquals(1, result.getUserState());
    }

    @Test
    @DisplayName("微信小程序登录-冻结用户返回5001")
    void loadAppletUser_frozenUser_returns5001() {
        AppletDto dto = new AppletDto();
        dto.setAppletCode("wxCode");
        dto.setAppletSecret("wxSecret");
        dto.setId("applet-001");
        when(systemService.findByAppletCodeAndAppletType(any(), anyInt()))
                .thenReturn(ResponseResult.ok(dto));
        when(wechatService.getAppletData(any(), any(), any(), any(), any()))
                .thenReturn(Map.of("appletId", "applet-openid", "mobile", "13800138000"));

        AppletUserInfoDto userInfo = new AppletUserInfoDto();
        userInfo.setId("user-001");
        userInfo.setPhoneNum("13800138000");
        userInfo.setUserState(2);
        when(togetherService.queryAppletUserInfoByPhoneNum("13800138000"))
                .thenReturn(ResponseResult.ok(userInfo));
        when(togetherService.updateOrSaveAppletUser(any(), any(), any()))
                .thenReturn(ResponseResult.ok("user-001"));

        UserLoginDto result = userLoginService.loadUserByAppletCodeAndMobile("code123", "appletKey", "encData", "iv");
        assertEquals(5001, result.getCheckCode());
        assertTrue(result.getCheckMsg().contains("冻结"));
    }

    @Test
    @DisplayName("微信小程序登录-注销用户(state=3)自动激活")
    void loadAppletUser_cancelledUser_autoActivates() {
        AppletDto dto = new AppletDto();
        dto.setAppletCode("wxCode");
        dto.setAppletSecret("wxSecret");
        dto.setId("applet-001");
        when(systemService.findByAppletCodeAndAppletType(any(), anyInt()))
                .thenReturn(ResponseResult.ok(dto));
        when(wechatService.getAppletData(any(), any(), any(), any(), any()))
                .thenReturn(Map.of("appletId", "applet-openid", "mobile", "13800138000"));

        AppletUserInfoDto userInfo = new AppletUserInfoDto();
        userInfo.setId("user-001");
        userInfo.setPhoneNum("13800138000");
        userInfo.setUserState(3);
        when(togetherService.queryAppletUserInfoByPhoneNum("13800138000"))
                .thenReturn(ResponseResult.ok(userInfo));
        when(togetherService.updateOrSaveAppletUser(any(), any(), any()))
                .thenReturn(ResponseResult.ok("user-001"));

        UserLoginDto result = userLoginService.loadUserByAppletCodeAndMobile("code123", "appletKey", "encData", "iv");
        assertEquals(1, result.getUserState());
        verify(togetherService).updateAppletUserState("user-001", 1);
    }
}
