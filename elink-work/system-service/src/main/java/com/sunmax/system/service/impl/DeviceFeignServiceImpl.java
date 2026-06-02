package com.sunmax.system.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.SecretUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.system.dao.*;
import com.sunmax.system.entity.*;
import com.sunmax.system.service.DeviceFeignService;
import com.sunmax.system.service.feign.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DeviceFeignServiceImpl implements DeviceFeignService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TenantManageDao tenantManageDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private OrganStructureDao organStructureDao;

    @Autowired
    private OrganEmpowerDao organEmpowerDao;

    @Autowired
    private DeviceService deviceService;

    /**
     * 根据用户id查询用户信息
     *
     * @param userIds
     * @return
     */
    @Override
    public ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(List<String> userIds) {
        //返回数据对象
        Map<String, UserDto> userDtoMap = Maps.newHashMap();

        //根据多个用户id查询用户信息
        List<UserEntity> userDaoAllById = userDao.findAllById(userIds);
        if (CollectionUtils.isNotEmpty(userDaoAllById)) {
            //租户id
            List<String> tenantIdList = Lists.newArrayList();
            //用户组id
            List<String> groupIdList = Lists.newArrayList();
            //组织架构id
            List<String> organIdList = Lists.newArrayList();
            //获取所有用户的用户组id和组织架构id
            userDaoAllById.forEach(userEntity -> {
                if (StringUtil.isNotEmpty(userEntity.getTenantId())) {
                    tenantIdList.add(userEntity.getTenantId());
                }
                if (StringUtil.isNotEmpty(userEntity.getGroupId())) {
                    groupIdList.addAll(Arrays.stream(userEntity.getGroupId().split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList()));
                }
                if (StringUtil.isNotEmpty(userEntity.getOrganId())) {
                    organIdList.add(userEntity.getOrganId());
                }
            });
            //获取租信息
            Map<String, String> tenantEntityMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(tenantIdList)) {
                List<TenantInfoEntity> tenantEntityList = tenantManageDao.findAllById(tenantIdList.stream().distinct().collect(Collectors.toList()));
                tenantEntityMap = tenantEntityList.stream().collect(Collectors.toMap(TenantInfoEntity::getId, TenantInfoEntity::getTenantName, (k1, k2) -> k1));
            }
            //获取用户组信息
            Map<String, String> userGroupEntityMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(groupIdList)) {
                List<UserGroupEntity> userGroupEntityList = userGroupDao.findAllByIdIn(groupIdList.stream().distinct().collect(Collectors.toList()));
                userGroupEntityMap = userGroupEntityList.stream().collect(Collectors.toMap(UserGroupEntity::getId, UserGroupEntity::getGroupName, (k1, k2) -> k1));
            }
            //获取组织信息
            List<OrganStructureEntity> structureDaoAllById = organStructureDao.findAllById(organIdList);
            Map<String, String> organStructureMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(structureDaoAllById)) {
                organStructureMap = structureDaoAllById.stream().collect(Collectors.toMap(OrganStructureEntity::getId, OrganStructureEntity::getOrganName, (k1, k2) -> k1));
            }
            //循环用户信息，组装数据
            Map<String, String> finalTenantEntityMap = tenantEntityMap;
            Map<String, String> finalUserGroupEntityMap = userGroupEntityMap;
            Map<String, String> finalOrganStructureMap = organStructureMap;
            userDaoAllById.forEach(user -> {
                UserDto userDto = new UserDto();
                BeanUtils.copyProperties(user, userDto);
                //获取租户名称
                String tenantId = user.getTenantId();
                if (StringUtil.isNotEmpty(tenantId)) {
                    userDto.setTenantName(finalTenantEntityMap.get(tenantId));
                }
                //获取用户组信息
                String groupId = user.getGroupId();
                if (StringUtil.isNotEmpty(groupId)) {
                    List<String> groupIds = Arrays.stream(groupId.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList());
                    //拼装用户组名称
                    AtomicReference<String> groupName = new AtomicReference<>(FileUtil.separator);
                    groupIds.forEach(id -> {
                        String name = finalUserGroupEntityMap.get(id);
                        if (StringUtil.isNotEmpty(name)) {
                            groupName.set(groupName + name + FileUtil.COMMA);
                        }
                    });
                    if (StringUtil.isNotEmpty(groupName.get())) {
                        userDto.setGroupName(groupName.get().substring(0, groupName.get().length() - 1));
                    }
                }
                //获取组织名称
                String organId = user.getOrganId();
                if (StringUtil.isNotEmpty(organId)) {
                    userDto.setOrganName(finalOrganStructureMap.get(organId));
                }
                userDtoMap.put(user.getId(), userDto);
            });
        }
        return ResponseResult.ok(userDtoMap);
    }

    /**
     * 根据站点id删除所有关联的资产授权数据
     *
     * @param siteId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteAllOrganEmpowerBySiteId(String siteId) {
        organEmpowerDao.deleteAllBySiteId(siteId);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据用户id查询所有关联的资产授权数据
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult<List<OrganEmpowerListDto>> findAllOrganEmpowerByUserId(String userId) {
        List<OrganEmpowerListDto> organEmpowerListDtos = Lists.newArrayList();
        //根据用户id查询用户信息
        Optional<UserEntity> optional = userDao.findById(userId);
        //如果是超级管理员，返回所有站点数据
        if (optional.isPresent()) {
            UserEntity userEntity = optional.get();
            if (userEntity.getUserRole() == 0) {
                //查询所有站点列表
                ResponseResult<List<SiteInfoDto>> allSiteBasicInfoList = deviceService.findAllSiteBasicInfoList(null);
                if (allSiteBasicInfoList.isSuccess() && CollectionUtils.isNotEmpty(allSiteBasicInfoList.getData())) {
                    organEmpowerListDtos = allSiteBasicInfoList.getData().stream().map(siteInfoDto -> {
                        OrganEmpowerListDto organEmpowerListDto = new OrganEmpowerListDto();
                        organEmpowerListDto.setSiteId(siteInfoDto.getId());
                        organEmpowerListDto.setSiteName(siteInfoDto.getSiteName());
                        organEmpowerListDto.setAuthority(2);
                        return organEmpowerListDto;
                    }).collect(Collectors.toList());
                }
            } else {
                //如果是管理员角色则查询租户本身资产授权，如果是普通用户则查询所属组织机构下的资产授权(新增用户时判断了如果没给用户设置组织，则默认分配到租户本身组织机构下，所以这里直接用组织机构id查询)
                if (StringUtil.isNotEmpty(userEntity.getOrganId())) {
                    //查询当前租户下所有资产授权
                    List<OrganEmpowerEntity> allByTenantId = organEmpowerDao.findAllByTenantIdAndOrganId(userEntity.getTenantId(), userEntity.getOrganId());
                    if (CollectionUtils.isNotEmpty(allByTenantId)) {
                        //根据站点id查询站点详情数据
                        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteInfoListByIds(allByTenantId.stream().map(OrganEmpowerEntity::getSiteId).collect(Collectors.toList())).getData().
                                stream().collect(Collectors.toMap(SiteInfoDto::getId, siteInfoDto -> siteInfoDto, (k1, k2) -> k1));

                        organEmpowerListDtos = allByTenantId.stream().map(organEmpowerEntity -> {
                            OrganEmpowerListDto organEmpowerListDto = new OrganEmpowerListDto();
                            BeanUtils.copyProperties(organEmpowerEntity, organEmpowerListDto);
                            if (siteInfoMap.containsKey(organEmpowerEntity.getSiteId())) {
                                organEmpowerListDto.setSiteName(siteInfoMap.get(organEmpowerEntity.getSiteId()).getSiteName());
                            }
                            return organEmpowerListDto;
                        }).collect(Collectors.toList());
                    }
                }
            }
        }
        return ResponseResult.ok(organEmpowerListDtos);
    }

    /**
     * 根据租户id查询资产授权列表信息
     *
     * @param tenantId
     * @return
     */
    @Override
    public ResponseResult<List<OrganEmpowerListDto>> findOrganEmpowerListByTenantId(String tenantId) {
        List<OrganEmpowerListDto> organEmpowerListDtos = Lists.newArrayList();
        List<OrganEmpowerEntity> organEmpowerEntities = organEmpowerDao.findAllByTenantId(tenantId);
        if (CollectionUtils.isNotEmpty(organEmpowerEntities)) {
            organEmpowerListDtos = organEmpowerEntities.stream().map(organEmpowerEntity -> {
                OrganEmpowerListDto organEmpowerListDto = new OrganEmpowerListDto();
                BeanUtils.copyProperties(organEmpowerEntity, organEmpowerListDto);
                return organEmpowerListDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(organEmpowerListDtos);
    }

    /**
     * 根据租户id删除租户下所有指定站点资产授权信息
     *
     * @param tenantId
     * @param siteId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteOrganEmpowerByTenantId(String tenantId, String siteId) {
        organEmpowerDao.deleteAllByTenantIdAndSiteId(tenantId, siteId);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据权限id修改指定权限
     *
     * @param tenantId
     * @param siteId
     * @param authority
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> updateOrganEmpowerByTenantId(String tenantId, String siteId, Integer authority) {
        //查询所有指定租户指定站点资产授权数据
        List<OrganEmpowerEntity> organEmpowerEntityList = organEmpowerDao.findAllByTenantIdAndSiteId(tenantId, siteId);
        if (CollectionUtils.isNotEmpty(organEmpowerEntityList)) {
            organEmpowerDao.saveAll(organEmpowerEntityList.stream().peek(o -> o.setAuthority(authority)).collect(Collectors.toList()));
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<String> getPasswordByAccount(String userAccount) {
        List<UserEntity> userList = userDao.findAll(Example.of(UserEntity.builder().userAccount(userAccount).userState(1).build()));
        if (CollectionUtils.isNotEmpty(userList)) {
            UserEntity user = userList.get(0);
            if (StringUtil.isNotEmpty(user.getPassword())) {
                return ResponseResult.ok(SecretUtil.encrypt(user.getPassword()));
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<Map<String, String>> findUserNameByUserAccounts(List<String> userAccounts) {
        //返回数据对象
        Map<String, String> resultMap = Maps.newHashMap();

        //根据多个用户id查询用户信息
        List<UserEntity> userEntityList = userDao.findAllByUserAccountIn(userAccounts);
        if (CollectionUtils.isNotEmpty(userEntityList)) {
            resultMap = userEntityList.stream().collect(Collectors.toMap(UserEntity::getUserAccount,
                    UserEntity::getFullName, (k1, k2) -> k1));
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<List<UserDto>> findUserListByUserId(String userId) {
        //返回的集合
        List<UserDto> resultList = Lists.newArrayList();
        //根据用户id查询租户id
        Optional<UserEntity> optional = userDao.findById(userId);
        if (optional.isPresent()) {
            UserEntity userEntity = optional.get();
            List<UserEntity> userList = userDao.findAllByTenantId(userEntity.getTenantId());
            if (CollectionUtils.isNotEmpty(userList)) {
                resultList = userList.stream().map(user -> {
                    UserDto result = new UserDto();
                    BeanUtils.copyProperties(user, result);
                    return result;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }
}
