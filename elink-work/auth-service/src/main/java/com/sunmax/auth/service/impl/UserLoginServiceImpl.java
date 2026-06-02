package com.sunmax.auth.service.impl;

import com.google.common.collect.Lists;
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
import com.sunmax.common.util.oss.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户登录服务层实现类
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
        //返回的用户数据
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
        //根据用户账号查询用户是否存在
        Optional<UserEntity> userOptional = userDao.findAll(Example.of(UserEntity.builder().userAccount(userAccount).build()))
                .stream().filter(u -> !Objects.equals(0, u.getUserState())).findFirst();
        if (!userOptional.isPresent()) {
            result.setCheckMsg("用户账号不存在,请核对");
            result.setCheckCode(5001);
            return result;
        }
        UserEntity userEntity = userOptional.get();
        //对密码进行解密
        password = SecretUtil.desEncrypt(password);
        if (!Objects.equals(password, userEntity.getPassword())) {
            result.setCheckCode(5002);
            result.setCheckMsg("用户密码错误,请核对");
            return result;
        }
        //账号到期日校验
        if (StringUtil.isNotEmpty(userEntity.getExpireDate())) {
            LocalDate expireDate = DateUtil.strToLocalDate(userEntity.getExpireDate());
            if (expireDate.isBefore(LocalDate.now())) {
                result.setCheckCode(5001);
                result.setCheckMsg("用户账号已到期,请联系管理员开通账号");
                return result;
            }
        }
        BeanUtils.copyProperties(userEntity, result);
        //根据租户id获取租户信息
        Optional<TenantInfoEntity> tenantManageDaoById = tenantManageDao.findById(userEntity.getTenantId());
        tenantManageDaoById.ifPresent(tenantInfoEntity -> result.setTenantName(tenantInfoEntity.getTenantName()));
        //根据用户id和客户端id查询权限数据(只返回菜单权限)
        result.setMenuList(getMenuPermissionByUserId(userEntity.getGroupId(), userEntity.getUserRole(), clientId, userEntity.getTenantId(), 1).stream().filter(m -> !Objects.equals(m.getType(), 4))
                .collect(Collectors.toList()));
        result.setLogo(1);//登录标识
        return result;
    }

    @Override
    public ResponseResult<List<PermissionInfoListDto>> findPermissionByUserAccount(String userAccount, String clientId) {
        //返回的集合
        List<PermissionInfoListDto> resultList = Lists.newArrayList();

        //根据用户账号查询用户id
        Optional<UserEntity> optional = userDao.findAll(Example.of(UserEntity.builder().userAccount(userAccount).build()))
                .stream().filter(u -> !Objects.equals(0, u.getUserState())).findFirst();
        if (optional.isPresent()) {
            UserEntity userEntity = optional.get();
            resultList = this.getMenuPermissionByUserId(userEntity.getGroupId(), userEntity.getUserRole(), clientId, userEntity.getTenantId(), 2).stream().map(m -> {
                PermissionInfoListDto result = new PermissionInfoListDto();
                BeanUtils.copyProperties(m, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 获取该用户的权限数据
     */
    public List<MenuListDto> getMenuPermissionByUserId(String groupIds, Integer userRole, String clientId, String tenantId, Integer permissionType) {
        //返回的结果
        List<MenuListDto> resultList = Lists.newArrayList();

        //页面数据
        List<PermissionEntity> permissionEntityList = Lists.newArrayList();

        //查询指定客户端菜单数据
        ProductEntity productEntity = productDao.findByClientIdAndIsDelete(clientId, 0);
        if (productEntity == null) {
            return resultList;
        }
        //根据账号类型查询---0-平台管理员查询平台下所有菜单权限数据 1-管理员查询租户中配置的应用授权权限数据 2-普通用户查询用户组配置的权限数据
        //平台管理员
        if (userRole == 0) {
            if (StringUtil.isNotEmpty(productEntity)) {
                //根据模块id查询所有页面数据
                permissionEntityList = permissionDao.findAllByModuleIdInAndPermissionTypeAndIsDelete(Collections.singletonList(productEntity.getId()), permissionType, 0);
            }
        } else if (userRole == 1) {//管理员
            //查询租户配置应用授权数据
            List<TenantApplyEmpowerEntity> tenantApplyEmpowerEntityList = tenantApplyEmpowerDao.findAllByTenantId(tenantId);
            if (CollectionUtils.isNotEmpty(tenantApplyEmpowerEntityList)) {
                //根据权限id查询权限数据，并过滤出页面类型数据
                List<String> permissionIdList = tenantApplyEmpowerEntityList.stream().map(TenantApplyEmpowerEntity::getPermissionId).collect(Collectors.toList());
                permissionEntityList = permissionDao.findAllByIdInAndIsDelete(permissionIdList, 0).stream().filter(p -> Objects.equals(permissionType, p.getPermissionType())).collect(Collectors.toList());
            }
        } else if (userRole == 2) {//普通用户
            //查询所在用户组下配置的应用授权数据
            if (StringUtil.isNotEmpty(groupIds)) {
                List<String> groupIdList = Arrays.stream(groupIds.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList());
                List<GroupApplyEmpowerEntity> groupApplyEmpowerEntityList = groupApplyEmpowerDao.findAllByGroupIdIn(groupIdList)
                        .stream().distinct().collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(groupApplyEmpowerEntityList)) {
                    //根据权限id查询权限数据，并过滤出页面类型数据
                    List<String> permissionIdList = groupApplyEmpowerEntityList.stream().map(GroupApplyEmpowerEntity::getPermissionId).collect(Collectors.toList());
                    permissionEntityList = permissionDao.findAllByIdInAndIsDelete(permissionIdList, 0).stream().filter(p -> Objects.equals(permissionType, p.getPermissionType())).collect(Collectors.toList());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(permissionEntityList)) {
            //过滤出当前client下权限数据
            List<PermissionEntity> permissionEntities = permissionEntityList.stream().filter(p -> StringUtil.isNotEmpty(p.getModuleId()) && Objects.equals(p.getModuleId(), productEntity.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(permissionEntities)) {
                permissionEntities.forEach(permissionEntity -> {
                    MenuListDto permissionListDto = new MenuListDto();
                    BeanUtils.copyProperties(permissionEntity, permissionListDto);
                    permissionListDto.setType(permissionType);
                    permissionListDto.setName(permissionEntity.getPermissionName());
                    resultList.add(permissionListDto);
                });
            }
        }
        return resultList;
    }

    @Override
    public UserLoginDto loadUserByAppletCodeAndMobile(String code, String appletKey, String encryptedData, String iv) {
        //首先判断该微信小程序是否注册过
        // 通过微信小程序code获取微信小程序唯一标识后再去校验数据库该用户信息是否存在 存在直接允许登录成功 不存在则去判断手机号是否为空，为空让用户去填写手机号
        // 不为空的话 则去校验手机号是否存在 如果存在 把该用户微信小程序唯一标识存到数据库中,让用户登录成功 否则让该用户更改登陆方式或者通过微信小程序和手机号去注册
        UserLoginDto result = new UserLoginDto();
        if (StringUtils.isEmpty(code)) {
            result.setCheckCode(5001);
            result.setCheckMsg("未获取到微信小程序标识,请重新登录");
            return result;
        }

        ResponseResult<AppletDto> appletDtoResult = systemService.findByAppletCodeAndAppletType(appletKey, 1);
        if (!appletDtoResult.isSuccess() || StringUtil.isEmpty(appletDtoResult.getData()) || StringUtil.isEmpty(appletDtoResult.getData().getAppletCode())) {
            result.setCheckCode(5001);
            result.setCheckMsg("平台上面先添加该小程序");
            return result;
        }
        AppletDto appletDto = appletDtoResult.getData();
        Map<String, String> appletResult = wechatService.getAppletData(code, appletDto.getAppletCode(), appletDto.getAppletSecret(), encryptedData, iv);
        String appletId = appletResult.get("appletId");
        String mobile = appletResult.get("mobile");

        if (StringUtil.isNotEmpty(mobile)) {
            result.setAppletKey(appletDto.getId());

            ResponseResult<AppletUserInfoDto> appletUserInfoDtoResult = togetherService.queryAppletUserInfoByPhoneNum(mobile);
            if (!appletUserInfoDtoResult.isSuccess() || StringUtil.isEmpty(appletUserInfoDtoResult.getData()) || StringUtil.isEmpty(appletUserInfoDtoResult.getData().getId())) {//没查询到则注册
                String id = togetherService.updateOrSaveAppletUser(mobile, appletId, appletDto.getId()).getData();

                result.setId(id);
                result.setAppletUserId(id);
                result.setPhone(SecretUtil.encrypt(mobile));
                result.setAppletId(appletId);
                result.setFullName(SecretUtil.encrypt(mobile));
                result.setUserAccount(SecretUtil.encrypt(mobile));//用于唯一标识
                result.setUserState(1);
                result.setLogo(2);
            } else {
                AppletUserInfoDto appletUserInfoDto = appletUserInfoDtoResult.getData();
                togetherService.updateOrSaveAppletUser(mobile, appletId, appletDto.getId());

                if (appletUserInfoDto.getUserState() == 2) {
                    result.setCheckCode(5001);
                    result.setCheckMsg("该账号已经冻结，请联系管理人员解冻账号");
                } else if (appletUserInfoDto.getUserState() == 3) {//如果账号已经被注销过了，则把账号状态改为正常后，再正常登录
                    togetherService.updateAppletUserState(appletUserInfoDto.getId(), 1);
                }

                result.setId(appletUserInfoDto.getId());
                result.setAppletUserId(appletUserInfoDto.getId());
                result.setUserAccount(SecretUtil.encrypt(appletUserInfoDto.getPhoneNum()));//用户账号就是手机号码，加密
                result.setFullName(appletUserInfoDto.getNickName());
                if (StringUtil.isEmpty(result.getPhone())) {
                    result.setPhone(SecretUtil.encrypt(appletUserInfoDto.getPhoneNum()));
                }
                result.setLogo(2);
                result.setUserState(1);
                result.setAppletId(appletUserInfoDto.getId());
            }
        } else {
            result.setCheckCode(5001);
            result.setCheckMsg("未获取到微信手机号,请重新登录");
        }
        return result;
    }
}
