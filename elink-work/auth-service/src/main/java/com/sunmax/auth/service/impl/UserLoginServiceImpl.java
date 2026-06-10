package com.sunmax.auth.service.impl;

import com.sunmax.auth.dao.*;
import com.sunmax.auth.dto.MenuListDto;
import com.sunmax.auth.dto.UserLoginDto;
import com.sunmax.auth.entity.*;
import com.sunmax.auth.service.UserLoginService;
import com.sunmax.auth.service.WechatService;
import com.sunmax.auth.service.feign.SystemService;
import com.sunmax.auth.service.feign.TogetherService;
import com.sunmax.common.dto.auth.PermissionInfoListDto;
import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.SecretUtil;
import com.sunmax.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户登录服务实现
 */
@Service
@Slf4j
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private ProductDao productDao;

    @Autowired
    private TenantApplyEmpowerDao tenantApplyEmpowerDao;

    @Autowired
    private GroupApplyEmpowerDao groupApplyEmpowerDao;

    @Autowired
    private TenantManageDao tenantManageDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private WechatService wechatService;

    @Autowired
    private TogetherService togetherService;

    @Override
    public UserLoginDto loadSysUserByAccountAndPassword(String userAccount, String password, String clientId) {
        UserLoginDto result = new UserLoginDto();

        if (StringUtil.isEmpty(userAccount)) {
            result.setCheckMsg("账号为空,请填写完整");
            result.setCheckCode(5001);
            return result;
        }
        if (StringUtil.isEmpty(password)) {
            result.setCheckMsg("密码为空,请填写完整");
            result.setCheckCode(5002);
            return result;
        }

        // 查询用户
        Optional<UserEntity> userOptional = userDao.findAll(
                Example.of(UserEntity.builder().userAccount(userAccount).build()))
                .stream().filter(u -> !Objects.equals(0, u.getUserState())).findFirst();

        if (!userOptional.isPresent()) {
            result.setCheckMsg("用户账号不存在,请核对");
            result.setCheckCode(5001);
            return result;
        }

        UserEntity userEntity = userOptional.get();

        // AES-CBC解密前端加密的密码，与数据库明文密码比对
        String decryptedPassword = SecretUtil.desEncrypt(password);
        if (decryptedPassword == null || !decryptedPassword.equals(userEntity.getPassword())) {
            result.setCheckCode(5002);
            result.setCheckMsg("用户密码错误,请核对");
            return result;
        }

        // 账号到期日校验
        if (StringUtil.isNotEmpty(userEntity.getExpireDate())) {
            LocalDate expireDate = DateUtil.strToLocalDate(userEntity.getExpireDate());
            if (expireDate.isBefore(LocalDate.now())) {
                result.setCheckCode(5001);
                result.setCheckMsg("用户账号已到期,请联系管理员开通账号");
                return result;
            }
        }

        BeanUtils.copyProperties(userEntity, result);

        // 填充租户名称
        tenantManageDao.findById(userEntity.getTenantId())
                .ifPresent(tenant -> result.setTenantName(tenant.getTenantName()));

        // 查询菜单权限（过滤掉控件类型type=4）
        List<MenuListDto> menuList = getMenuPermissionByUserId(
                userEntity.getGroupId(), userEntity.getUserRole(), clientId, userEntity.getTenantId(), 1);
        result.setMenuList(menuList.stream().filter(m -> !Objects.equals(m.getType(), 4)).collect(Collectors.toList()));
        result.setLogo(1);

        return result;
    }

    @Override
    public ResponseResult<List<PermissionInfoListDto>> findPermissionByUserAccount(String userAccount, String clientId) {
        List<PermissionInfoListDto> resultList = new ArrayList<>();

        Optional<UserEntity> optional = userDao.findAll(
                Example.of(UserEntity.builder().userAccount(userAccount).build()))
                .stream().filter(u -> !Objects.equals(0, u.getUserState())).findFirst();

        if (optional.isPresent()) {
            UserEntity userEntity = optional.get();
            resultList = getMenuPermissionByUserId(
                    userEntity.getGroupId(), userEntity.getUserRole(), clientId, userEntity.getTenantId(), 2)
                    .stream().map(m -> {
                        PermissionInfoListDto dto = new PermissionInfoListDto();
                        BeanUtils.copyProperties(m, dto);
                        return dto;
                    }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public UserLoginDto loadUserByAppletCodeAndMobile(String code, String appletKey, String encryptedData, String iv) {
        UserLoginDto result = new UserLoginDto();

        if (StringUtil.isEmpty(code)) {
            result.setCheckCode(5001);
            result.setCheckMsg("未获取到微信小程序标识,请重新登录");
            return result;
        }

        // 查询小程序配置
        ResponseResult<AppletDto> appletDtoResult = systemService.findByAppletCodeAndAppletType(appletKey, 1);
        if (!appletDtoResult.isSuccess() || StringUtil.isEmpty(appletDtoResult.getData())
                || StringUtil.isEmpty(appletDtoResult.getData().getAppletCode())) {
            result.setCheckCode(5001);
            result.setCheckMsg("平台上面先添加该小程序");
            return result;
        }

        AppletDto appletDto = appletDtoResult.getData();
        Map<String, String> appletResult = wechatService.getAppletData(
                code, appletDto.getAppletCode(), appletDto.getAppletSecret(), encryptedData, iv);
        String appletId = appletResult.get("appletId");
        String mobile = appletResult.get("mobile");

        if (StringUtil.isEmpty(mobile)) {
            result.setCheckCode(5001);
            result.setCheckMsg("未获取到微信手机号,请重新登录");
            return result;
        }

        result.setAppletKey(appletDto.getId());

        // 查询或注册小程序用户
        ResponseResult<AppletUserInfoDto> appletUserResult = togetherService.queryAppletUserInfoByPhoneNum(mobile);
        if (!appletUserResult.isSuccess() || StringUtil.isEmpty(appletUserResult.getData())
                || StringUtil.isEmpty(appletUserResult.getData().getId())) {
            // 注册新用户
            String id = togetherService.updateOrSaveAppletUser(mobile, appletId, appletDto.getId()).getData();
            result.setId(id);
            result.setAppletUserId(id);
            result.setPhone(SecretUtil.encrypt(mobile));
            result.setAppletId(appletId);
            result.setFullName(SecretUtil.encrypt(mobile));
            result.setUserAccount(SecretUtil.encrypt(mobile));
            result.setUserState(1);
            result.setLogo(2);
        } else {
            AppletUserInfoDto appletUser = appletUserResult.getData();
            togetherService.updateOrSaveAppletUser(mobile, appletId, appletDto.getId());

            if (appletUser.getUserState() == 2) {
                result.setCheckCode(5001);
                result.setCheckMsg("该账号已经冻结，请联系管理人员解冻账号");
                return result;
            } else if (appletUser.getUserState() == 3) {
                togetherService.updateAppletUserState(appletUser.getId(), 1);
            }

            result.setId(appletUser.getId());
            result.setAppletUserId(appletUser.getId());
            result.setUserAccount(SecretUtil.encrypt(appletUser.getPhoneNum()));
            result.setFullName(appletUser.getNickName());
            if (StringUtil.isEmpty(result.getPhone())) {
                result.setPhone(SecretUtil.encrypt(appletUser.getPhoneNum()));
            }
            result.setLogo(2);
            result.setUserState(1);
            result.setAppletId(appletUser.getId());
        }

        return result;
    }

    /**
     * 根据用户角色查询权限数据
     * userRole: 0-平台管理员(查全部) 1-管理员(查租户授权) 2-普通用户(查用户组授权)
     */
    private List<MenuListDto> getMenuPermissionByUserId(String groupIds, Integer userRole,
                                                         String clientId, String tenantId, Integer permissionType) {
        List<MenuListDto> resultList = new ArrayList<>();

        // 查询指定客户端的产品模块
        ProductEntity productEntity = productDao.findByClientIdAndIsDelete(clientId, 0);
        if (productEntity == null) {
            return resultList;
        }

        List<PermissionEntity> permissionEntityList;

        if (userRole == 0) {
            // 平台管理员：查询模块下所有权限
            permissionEntityList = permissionDao.findAllByModuleIdInAndPermissionTypeAndIsDelete(
                    Collections.singletonList(productEntity.getId()), permissionType, 0);
        } else if (userRole == 1) {
            // 管理员：查询租户授权的权限
            List<TenantApplyEmpowerEntity> tenantApplies = tenantApplyEmpowerDao.findAllByTenantId(tenantId);
            if (CollectionUtils.isNotEmpty(tenantApplies)) {
                List<String> permissionIds = tenantApplies.stream()
                        .map(TenantApplyEmpowerEntity::getPermissionId).collect(Collectors.toList());
                permissionEntityList = permissionDao.findAllByIdInAndIsDelete(permissionIds, 0).stream()
                        .filter(p -> Objects.equals(permissionType, p.getPermissionType()))
                        .collect(Collectors.toList());
            } else {
                permissionEntityList = new ArrayList<>();
            }
        } else if (userRole == 2) {
            // 普通用户：查询用户组授权的权限
            if (StringUtil.isNotEmpty(groupIds)) {
                List<String> groupIdList = Arrays.stream(groupIds.split(","))
                        .map(String::trim).collect(Collectors.toList());
                List<GroupApplyEmpowerEntity> groupApplies = groupApplyEmpowerDao.findAllByGroupIdIn(groupIdList)
                        .stream().distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(groupApplies)) {
                    List<String> permissionIds = groupApplies.stream()
                            .map(GroupApplyEmpowerEntity::getPermissionId).collect(Collectors.toList());
                    permissionEntityList = permissionDao.findAllByIdInAndIsDelete(permissionIds, 0).stream()
                            .filter(p -> Objects.equals(permissionType, p.getPermissionType()))
                            .collect(Collectors.toList());
                } else {
                    permissionEntityList = new ArrayList<>();
                }
            } else {
                permissionEntityList = new ArrayList<>();
            }
        } else {
            permissionEntityList = new ArrayList<>();
        }

        // 过滤出当前产品模块下的权限
        if (CollectionUtils.isNotEmpty(permissionEntityList)) {
            permissionEntityList.stream()
                    .filter(p -> Objects.equals(productEntity.getId(), p.getModuleId()))
                    .forEach(p -> {
                        MenuListDto dto = new MenuListDto();
                        BeanUtils.copyProperties(p, dto);
                        dto.setType(permissionType);
                        dto.setName(p.getPermissionName());
                        resultList.add(dto);
                    });
        }

        return resultList;
    }
}
