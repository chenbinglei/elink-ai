package com.sunmax.together.service.operation.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.UserGroupInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.together.AppletUserChangeVo;
import com.sunmax.together.dao.*;
import com.sunmax.together.dto.operation.appletUser.AppletCancelInfoDto;
import com.sunmax.together.dto.operation.appletUser.AppletUserDetailDto;
import com.sunmax.common.dto.together.UserDisWalletDto;
import com.sunmax.together.dto.operation.appletUser.UserGroupListDto;
import com.sunmax.together.entity.*;
import com.sunmax.together.service.operation.AppletUserService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.vo.operation.appletUser.AppletUserQueryVo;
import com.sunmax.together.vo.operation.appletUser.UserGroupChangeVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DateUtil.localDateTimeToStr;

@Service
@Slf4j
public class AppletUserServiceImpl implements AppletUserService {

    @Autowired
    private AppletUserDao appletUserDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private GroupBeSiteDao groupBeSiteDao;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserDisWalletDao userDisWalletDao;

    @Autowired
    private AppletCancelDao appletCancelDao;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateAppletUser(AppletUserChangeVo appletUserChangeVo, String userId) {
        if (StringUtil.isNotEmpty(appletUserChangeVo)) {
            //根据手机号查询是否重复
            Optional<AppletUserEntity> appletUserDaoOne = appletUserDao.findOne(Example.of(AppletUserEntity.builder().phoneNum(appletUserChangeVo.getPhoneNum()).userState(1).build()));
            if (appletUserDaoOne.isPresent()) {
                //新增小程序用户
                if (StringUtil.isEmpty(appletUserChangeVo.getId())) {
                    return ResponseResult.paramShow(appletUserChangeVo.getPhoneNum(), ResponseResult.PARAM_EXIST);
                } else {
                    //如果和查询到的id不重复，则说明重复
                    if (!appletUserChangeVo.getId().equals(appletUserDaoOne.get().getId())) {
                        return ResponseResult.paramShow(appletUserChangeVo.getPhoneNum(), ResponseResult.PARAM_EXIST);
                    }
                }
            }
            AppletUserEntity appletUserEntity = new AppletUserEntity();
            //新增小程序用户
            if (StringUtil.isEmpty(appletUserChangeVo.getId())) {
                BeanUtils.copyProperties(appletUserChangeVo, appletUserEntity);
                appletUserEntity.setCreateId(userId);
                appletUserEntity.setCreateTime(LocalDateTime.now());
            } else {
                Optional<AppletUserEntity> appletUserDaoById = appletUserDao.findById(appletUserChangeVo.getId());
                if (appletUserDaoById.isPresent()) {
                    AppletUserEntity appletUser = appletUserDaoById.get();
                    BeanUtils.copyProperties(appletUserChangeVo, appletUserEntity);
                    appletUserEntity.setUpdateId(userId);
                    appletUserEntity.setUpdateTime(LocalDateTime.now());
                    appletUserEntity.setCreateId(appletUser.getCreateId());
                    appletUserEntity.setCreateTime(appletUser.getCreateTime());
                }
            }
            appletUserDao.save(appletUserEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 分页查询小程序用户列表
     * @param appletUserQueryVo
     * @return
     */
    @Override
    public ResponseResult<PageDto<AppletUserInfoDto>> queryAppletUserList(AppletUserQueryVo appletUserQueryVo) {
        List<AppletUserInfoDto> resultList = Lists.newArrayList();
        //查询全部非注销状态用户列表
        List<AppletUserEntity> appletUserEntityList = appletUserDao.findAllByUserStateIn(Arrays.asList(1,2));
        if (CollectionUtils.isNotEmpty(appletUserEntityList)) {
            //根据手机号查询
            if (StringUtil.isNotEmpty(appletUserQueryVo.getPhoneNum())) {
                appletUserEntityList = appletUserEntityList.stream().filter(a -> StringUtil.isNotEmpty(a.getPhoneNum()) && a.getPhoneNum().contains(appletUserQueryVo.getPhoneNum())).collect(Collectors.toList());
            }
            //根据分组id查询
            if (StringUtil.isNotEmpty(appletUserQueryVo.getGroupId())) {
                appletUserEntityList = appletUserEntityList.stream().filter(a -> StringUtil.isNotEmpty(a.getGroupId()) && a.getGroupId().equals(appletUserQueryVo.getGroupId())).collect(Collectors.toList());
            }
            //根据用户状态查询
            if (StringUtil.isNotEmpty(appletUserQueryVo.getUserState())) {
                appletUserEntityList = appletUserEntityList.stream().filter(a -> StringUtil.isNotEmpty(a.getUserState()) && a.getUserState().equals(appletUserQueryVo.getUserState())).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(appletUserEntityList)) {
                //根据多个分组id，查询分组信息
                List<String> groupIdList = appletUserEntityList.stream().map(AppletUserEntity::getGroupId).distinct().collect(Collectors.toList());
                List<UserGroupEntity> userGroupEntityList = userGroupDao.findAllById(groupIdList);
                Map<String, String> userGroupMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(userGroupEntityList)) {
                    userGroupMap = userGroupEntityList.stream().collect(Collectors.toMap(UserGroupEntity::getId, UserGroupEntity::getGroupName));
                }
                Map<String, String> finalUserGroupMap = userGroupMap;
                resultList = appletUserEntityList.stream().map(appletUserEntity -> {
                    AppletUserInfoDto appletUserInfoDto = new AppletUserInfoDto();
                    BeanUtils.copyProperties(appletUserEntity, appletUserInfoDto);
                    //获取用户分组名称
                    if (StringUtil.isNotEmpty(appletUserEntity.getGroupId()) && !finalUserGroupMap.isEmpty() && finalUserGroupMap.containsKey(appletUserEntity.getGroupId())) {
                        appletUserInfoDto.setGroupName(finalUserGroupMap.get(appletUserEntity.getGroupId()));
                    }
                    appletUserInfoDto.setCreateTime(localDateTimeToStr(appletUserEntity.getCreateTime()));
                    return appletUserInfoDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getCreateTime()))
                .sorted(Comparator.comparing(AppletUserInfoDto::getCreateTime).reversed()).collect(Collectors.toList()), appletUserQueryVo.getPage(), appletUserQueryVo.getSize()));
    }

    /**
     * 修改小程序用户状态
     * @param id
     * @param userState
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> updateAppletUserState(String id, Integer userState) {
        Optional<AppletUserEntity> appletUserDaoById = appletUserDao.findById(id);
        if (appletUserDaoById.isPresent()) {
            AppletUserEntity appletUserEntity = appletUserDaoById.get();
            appletUserEntity.setUserState(userState);
            appletUserDao.save(appletUserEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 新增或编辑用户分组
     * @param userGroupChangeVo
     * @param userId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateUserGroup(UserGroupChangeVo userGroupChangeVo, String userId) {
        if (StringUtil.isNotEmpty(userGroupChangeVo)) {
            //根据分组名称查询是否重复
            Optional<UserGroupEntity> userGroupDaoOne = userGroupDao.findOne(Example.of(UserGroupEntity.builder().groupName(userGroupChangeVo.getGroupName()).build()));
            if (userGroupDaoOne.isPresent()) {
                //新增用户分组
                if (StringUtil.isEmpty(userGroupChangeVo.getId())) {
                    return ResponseResult.paramShow(userGroupChangeVo.getGroupName(), ResponseResult.PARAM_EXIST);
                } else {
                    //如果和查询到的id不重复，则说明重复
                    if (!userGroupChangeVo.getId().equals(userGroupDaoOne.get().getId())) {
                        return ResponseResult.paramShow(userGroupChangeVo.getGroupName(), ResponseResult.PARAM_EXIST);
                    }
                }
            }
            UserGroupEntity userGroupEntity = new UserGroupEntity();
            BeanUtils.copyProperties(userGroupChangeVo, userGroupEntity);
            //新增
            if (StringUtil.isEmpty(userGroupChangeVo.getId())) {
                userGroupEntity.setCreateId(userId);
                userGroupEntity.setCreateTime(LocalDateTime.now());
            } else {
                Optional<UserGroupEntity> userGroupDaoById = userGroupDao.findById(userGroupChangeVo.getId());
                if (userGroupDaoById.isPresent()) {
                    userGroupEntity.setCreateId(userGroupDaoById.get().getCreateId());
                    userGroupEntity.setCreateTime(userGroupDaoById.get().getCreateTime());
                    userGroupEntity.setUpdateId(userId);
                    userGroupEntity.setUpdateTime(LocalDateTime.now());
                }
            }
            UserGroupEntity save = userGroupDao.save(userGroupEntity);
            groupBeSiteDao.deleteAllByGroupId(save.getId());
            //添加关联应用站点
            if (StringUtil.isNotEmpty(userGroupChangeVo.getApplySiteIds())) {
                groupBeSiteDao.saveAll(Arrays.stream(userGroupChangeVo.getApplySiteIds().split(",")).map(String::trim).collect(Collectors.toList()).stream().map(siteId -> {
                    GroupBeSiteEntity groupBeSiteEntity = new GroupBeSiteEntity();
                    groupBeSiteEntity.setGroupId(save.getId());
                    groupBeSiteEntity.setSiteId(siteId);
                    return groupBeSiteEntity;
                }).collect(Collectors.toList()));
            }
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 根据分组id查询关联小程序用户列表
     * @param groupId
     * @return
     */
    @Override
    public ResponseResult<List<AppletUserInfoDto>> queryAppletUserListByGroupId(String groupId) {
        List<AppletUserInfoDto> resultList = Lists.newArrayList();

        //根据分组id查询小程序用户
        List<AppletUserEntity> appletUserEntityList = appletUserDao.findAllByGroupIdIn(Collections.singletonList(groupId));
        if (CollectionUtils.isNotEmpty(appletUserEntityList)) {
            //过滤掉已注销的用户
            List<AppletUserEntity> userEntityList = appletUserEntityList.stream().filter(appletUserEntity -> appletUserEntity.getUserState() != 3).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(userEntityList)) {
                resultList = userEntityList.stream().map(appletUserEntity -> {
                    AppletUserInfoDto appletUserInfoDto = new AppletUserInfoDto();
                    BeanUtils.copyProperties(appletUserEntity, appletUserInfoDto);
                    return appletUserInfoDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 管理分组下小程序用户
     * @param groupId 用户分组唯一id
     * @param addUserIds 添加小程序用户id(多个以逗号分割)
     * @param removeUserIds 删除小程序用户id(多个以逗号分割)
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> manageGroupUserByGroupId(String groupId, String addUserIds, String removeUserIds) {
        //新增分组关联小程序用户
        if (StringUtil.isNotEmpty(addUserIds)) {
            List<String> addUserIdList = Arrays.stream(addUserIds.split(",")).map(String::trim).collect(Collectors.toList());
            appletUserDao.saveAll(appletUserDao.findAllById(addUserIdList).stream().peek(a -> a.setGroupId(groupId)).collect(Collectors.toList()));
        }
        //删除分组关联小程序用户
        if (StringUtil.isNotEmpty(removeUserIds)) {
            List<String> removeUserIdList = Arrays.stream(removeUserIds.split(",")).map(String::trim).collect(Collectors.toList());
            appletUserDao.saveAll(appletUserDao.findAllById(removeUserIdList).stream().peek(a -> a.setGroupId("")).collect(Collectors.toList()));
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 查询全部用户分组列表
     * @return
     */
    @Override
    public ResponseResult<List<UserGroupInfoDto>> queryAllUserGroupList() {
        List<UserGroupInfoDto> resultList = Lists.newArrayList();
        List<UserGroupEntity> userGroupEntities = userGroupDao.findAll();
        if (CollectionUtils.isNotEmpty(userGroupEntities)) {
            resultList = userGroupEntities.stream().map(userGroupEntity -> {
                UserGroupInfoDto userGroupInfoDto = new UserGroupInfoDto();
                BeanUtils.copyProperties(userGroupEntity, userGroupInfoDto);
                return userGroupInfoDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 分页查询用户分组列表
     * @param page 当前页
     * @param size 当前页条数
     * @param groupName 分组名称模糊查询
     * @return
     */
    @Override
    public ResponseResult<PageDto<UserGroupListDto>> queryUserGroupList(Integer page, Integer size, String groupName) {
        List<UserGroupListDto> resultList = Lists.newArrayList();
        //根据查询条件查询设备数据
        List<UserGroupEntity> userGroupEntityList = userGroupDao.findAll((Specification<UserGroupEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            if (StringUtil.isNotEmpty(groupName)) { //分组名称
                list.add(cb.or(cb.like(root.get("groupName"), "%" + groupName + "%")));
            }
            return cb.and(list.toArray(new Predicate[0]));
        }, Sort.by("createTime").descending());
        if (CollectionUtils.isNotEmpty(userGroupEntityList)) {
            //根据多个用户分组id查询关联站点信息
            List<String> groupIdList = userGroupEntityList.stream().map(UserGroupEntity::getId).collect(Collectors.toList());
            List<GroupBeSiteEntity> groupBeSiteEntityList = groupBeSiteDao.findAllByGroupIdIn(groupIdList);
            Map<String, List<GroupBeSiteEntity>> groupByGroupIdMap = Maps.newHashMap();
            Map<String, SiteInfoDto> siteInfoDtoMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(groupBeSiteEntityList)) {
                //根据分组id分组
                groupByGroupIdMap = groupBeSiteEntityList.stream().collect(Collectors.groupingBy(GroupBeSiteEntity::getGroupId));
                //根据多个站点id，查询站点详情信息
                List<String> siteIdList = groupBeSiteEntityList.stream().map(GroupBeSiteEntity::getSiteId).distinct().collect(Collectors.toList());
                siteInfoDtoMap = deviceService.findSiteBasicInfoByIds(siteIdList).getData();
            }
            //根据多个分组id，查询关联小程序用户信息
            List<AppletUserEntity> appletUserEntityList = appletUserDao.findAllByGroupIdIn(groupIdList);
            Map<String, List<AppletUserEntity>> appletUserMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(appletUserEntityList)) {
                appletUserMap = appletUserEntityList.stream().collect(Collectors.groupingBy(AppletUserEntity::getGroupId));
            }
            //根据多个创建人id查询创建人信息
            List<String> createIdList = userGroupEntityList.stream().map(UserGroupEntity::getCreateId).distinct().collect(Collectors.toList());
            ResponseResult<Map<String, UserDto>> userInfoMap = systemService.findUserInfoByIdsFeign(createIdList);
            Map<String, List<AppletUserEntity>> finalAppletUserMap = appletUserMap;
            Map<String, List<GroupBeSiteEntity>> finalGroupByGroupIdMap = groupByGroupIdMap;
            Map<String, SiteInfoDto> finalSiteInfoDtoMap = siteInfoDtoMap;
            resultList = userGroupEntityList.stream().map(userGroupEntity -> {
                UserGroupListDto userGroupListDto = new UserGroupListDto();
                BeanUtils.copyProperties(userGroupEntity, userGroupListDto);
                userGroupListDto.setCreateTime(localDateTimeToStr(userGroupEntity.getCreateTime()));
                //获取创建人名称
                if (userInfoMap.isSuccess() && !userInfoMap.getData().isEmpty() && userInfoMap.getData().containsKey(userGroupEntity.getCreateId())) {
                    userGroupListDto.setCreateUserName(userInfoMap.getData().get(userGroupEntity.getCreateId()).getFullName());
                }
                //获取分组下关联小程序用户数量
                if (!finalAppletUserMap.isEmpty() && finalAppletUserMap.containsKey(userGroupEntity.getId())) {
                    userGroupListDto.setUserNum(finalAppletUserMap.get(userGroupEntity.getId()).size());
                }
                //获取应用站点信息
                if (!finalGroupByGroupIdMap.isEmpty() && finalGroupByGroupIdMap.containsKey(userGroupEntity.getId())) {
                    List<GroupBeSiteEntity> groupBeSiteEntities = finalGroupByGroupIdMap.get(userGroupEntity.getId());
                    List<UserGroupListDto.SiteDetail> siteDetailList = groupBeSiteEntities.stream().map(groupBeSiteEntity -> {
                        if (!finalSiteInfoDtoMap.isEmpty() && finalSiteInfoDtoMap.containsKey(groupBeSiteEntity.getSiteId())) {
                            UserGroupListDto.SiteDetail siteDetail = new UserGroupListDto.SiteDetail();
                            siteDetail.setId(groupBeSiteEntity.getSiteId());
                            siteDetail.setSiteName(finalSiteInfoDtoMap.get(groupBeSiteEntity.getSiteId()).getSiteName());
                            return siteDetail;
                        }
                        return null;
                    }).collect(Collectors.toList());
                    userGroupListDto.setSiteDetailList(siteDetailList);
                    userGroupListDto.setApplySiteNum(siteDetailList.size());
                }
                return userGroupListDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(resultList,page, size));
    }

    /**
     * 根据站点id删除所有用户分组关联的站点数据
     * @param siteId 站点id
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteAllGroupBeSiteBySiteId(String siteId) {
        groupBeSiteDao.deleteAllBySiteId(siteId);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据分组id删除用户分组信息
     * @param id 分组id
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteUserGroupById(String id) {
        //删除用户分组主体信息
        userGroupDao.deleteById(id);
        //删除分组关联应用站点信息
        groupBeSiteDao.deleteAllByGroupId(id);
        //根据分组id查询分组下所有小程序用户信息，并清空小程序用户信息中的分组id字段
        List<AppletUserEntity> appletUserEntities = appletUserDao.findAllByGroupIdIn(Collections.singletonList(id));
        if (CollectionUtils.isNotEmpty(appletUserEntities)) {
            appletUserDao.saveAll(appletUserEntities.stream().peek(a -> a.setGroupId("")).collect(Collectors.toList()));
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 查询全部站点信息列表
     * @return
     */
    @Override
    public ResponseResult<List<SiteInfoDto>> queryAllSiteInfoList(String groupId) {
        List<SiteInfoDto> resultList = Lists.newArrayList();

        //查询全部站点信息列表
        ResponseResult<List<SiteInfoDto>> allSiteBasicInfoList = deviceService.findAllSiteBasicInfoList(null);
        if (allSiteBasicInfoList.isSuccess() && CollectionUtils.isNotEmpty(allSiteBasicInfoList.getData())) {
            resultList = allSiteBasicInfoList.getData();
            //查询所有分组关联应用站点信息
            List<GroupBeSiteEntity> groupBeSiteEntityList = groupBeSiteDao.findAll();
            if (CollectionUtils.isNotEmpty(groupBeSiteEntityList)) {
                List<String> siteIdList = groupBeSiteEntityList.stream().map(GroupBeSiteEntity::getSiteId).distinct().collect(Collectors.toList());
                resultList = resultList.stream().filter(c -> siteIdList.stream().noneMatch(v -> Objects.equals(c.getId(), v))).collect(Collectors.toList());
                //如果传了分组id，则只过滤该分组之外的站点信息
                if (StringUtil.isNotEmpty(groupId)) {
                    List<String> groupSiteIdList = groupBeSiteEntityList.stream().filter(g -> g.getGroupId().equals(groupId)).map(GroupBeSiteEntity::getSiteId).collect(Collectors.toList());
                    List<SiteInfoDto> siteInfoDtoList = allSiteBasicInfoList.getData().stream().filter(c -> groupSiteIdList.stream().anyMatch(v -> Objects.equals(c.getId(), v))).collect(Collectors.toList());
                    resultList.addAll(siteInfoDtoList);
                }
            }
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 根据小程序用户id查询用户详情数据
     * @param id
     * @return
     */
    @Override
    public ResponseResult<AppletUserDetailDto> queryAppletUserDetailById(String id) {
        AppletUserDetailDto appletUserDetailDto = new AppletUserDetailDto();
        Optional<AppletUserEntity> appletUserDaoById = appletUserDao.findById(id);
        if (appletUserDaoById.isPresent()) {
            AppletUserEntity appletUserEntity = appletUserDaoById.get();
            BeanUtils.copyProperties(appletUserEntity, appletUserDetailDto);
            //根据用户分组id，查询用户分组信息
            if (StringUtil.isNotEmpty(appletUserEntity.getGroupId())) {
                userGroupDao.findById(appletUserEntity.getGroupId()).ifPresent(userGroupEntity -> appletUserDetailDto.setGroupName(userGroupEntity.getGroupName()));
            }
        }
        return ResponseResult.ok(appletUserDetailDto);
    }

    @Override
    public ResponseResult<UserGroupInfoDto> queryUserDiscountByPhoneNum(String pileCode, String phoneNum) {
        UserGroupInfoDto userGroupInfoDto = new UserGroupInfoDto();
        //根据用户手机号，查询小程序用户详情
        AppletUserEntity appletUserEntity = appletUserDao.findByPhoneNum(phoneNum);
        if (StringUtil.isNotEmpty(appletUserEntity) && StringUtil.isNotEmpty(appletUserEntity.getGroupId())) {
            //根据分组id，查询分组应用站点信息
            List<GroupBeSiteEntity> groupBeSiteEntities = groupBeSiteDao.findAllByGroupIdIn(Collections.singletonList(appletUserEntity.getGroupId()));
            if (CollectionUtils.isNotEmpty(groupBeSiteEntities)) {
                //根据电桩编码查询电桩所属站点id
                ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByCodes = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(pileCode));
                if (deviceBasicInfoByCodes.isSuccess() && !deviceBasicInfoByCodes.getData().isEmpty() && StringUtil.isNotEmpty(deviceBasicInfoByCodes.getData().get(pileCode).getSiteId())) {
                    String siteId = deviceBasicInfoByCodes.getData().get(pileCode).getSiteId();
                    //从分组应用站点信息中过滤出当前站点id数据(查看当前站点是否应用了分组折扣)
                    List<GroupBeSiteEntity> groupBeSiteEntityList = groupBeSiteEntities.stream().filter(g -> g.getSiteId().equals(siteId)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(groupBeSiteEntityList)) {
                        //根据分组id查询用户分组信息
                        Optional<UserGroupEntity> userGroupDaoById = userGroupDao.findById(appletUserEntity.getGroupId());
                        userGroupDaoById.ifPresent(userGroupEntity -> BeanUtils.copyProperties(userGroupEntity, userGroupInfoDto));
                    }
                }
            }
        }
        return ResponseResult.ok(userGroupInfoDto);
    }

    /**
     * 根据小程序用户手机号查询用户信息
     * @param phoneNum 小程序登录用户手机号
     * @return
     */
    @Override
    public ResponseResult<AppletUserInfoDto> queryAppletUserInfoByPhoneNum(String phoneNum) {
        AppletUserInfoDto appletUserInfoDto = new AppletUserInfoDto();
        AppletUserEntity appletUserEntity = appletUserDao.findByPhoneNum(phoneNum);
        if (StringUtil.isNotEmpty(appletUserEntity)) {
            BeanUtils.copyProperties(appletUserEntity, appletUserInfoDto);
            appletUserInfoDto.setCreateTime(localDateTimeToStr(appletUserEntity.getCreateTime()));
            //根据分组id查询分组名称
            if (StringUtil.isNotEmpty(appletUserEntity.getGroupId())) {
                Optional<UserGroupEntity> userGroupDaoById = userGroupDao.findById(appletUserEntity.getGroupId());
                userGroupDaoById.ifPresent(userGroupEntity -> appletUserInfoDto.setGroupName(userGroupEntity.getGroupName()));
            }
        }
        return ResponseResult.ok(appletUserInfoDto);
    }

    /**
     * 根据小程序用户手机号修改小程序编码信息(如果根据手机号查不到小程序用户，则创建小程序用户信息)
     * @param phoneNum 小程序用户手机号
     * @param openid 用户在普通商户AppID下的唯一标识
     * @param appletId 小程序编码
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> updateOrSaveAppletUser(String phoneNum, String openid, String appletId) {
        String id;
        //根据小程序用户id查询小程序用户信息，如果查询到则修改，如果查询不到则创建
        AppletUserEntity appletUserEntity = appletUserDao.findByPhoneNum(phoneNum);
        if (StringUtil.isNotEmpty(appletUserEntity)) {
            appletUserEntity.setOpenid(openid);
            appletUserEntity.setAppletId(appletId);
            appletUserEntity.setUpdateTime(LocalDateTime.now());
            AppletUserEntity save = appletUserDao.save(appletUserEntity);
            id = save.getId();
        } else {
            AppletUserEntity appletUser = new AppletUserEntity();
            appletUser.setAppletId(appletId);
            appletUser.setOpenid(openid);
            appletUser.setCreateTime(LocalDateTime.now());
            appletUser.setNickName(phoneNum);
            appletUser.setPhoneNum(phoneNum);
            appletUser.setPlatformType(1);
            appletUser.setUserState(1);
            AppletUserEntity save = appletUserDao.save(appletUser);
            id = save.getId();
        }
        return ResponseResult.ok(id);
    }

    /**
     * 编辑小程序用户
     * @param appletUserChangeVo
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> updateAppletUser(AppletUserChangeVo appletUserChangeVo) {
        if (StringUtil.isNotEmpty(appletUserChangeVo)) {
            //根据手机号查询是否重复
            Optional<AppletUserEntity> appletUserDaoOne = appletUserDao.findOne(Example.of(AppletUserEntity.builder().phoneNum(appletUserChangeVo.getPhoneNum()).userState(1).build()));
            if (appletUserDaoOne.isPresent()) {
                //新增小程序用户
                if (StringUtil.isEmpty(appletUserChangeVo.getId())) {
                    return ResponseResult.paramShow(appletUserChangeVo.getPhoneNum(), ResponseResult.PARAM_EXIST);
                } else {
                    //如果和查询到的id不重复，则说明重复
                    if (!appletUserChangeVo.getId().equals(appletUserDaoOne.get().getId())) {
                        return ResponseResult.paramShow(appletUserChangeVo.getPhoneNum(), ResponseResult.PARAM_EXIST);
                    }
                }
            }
            AppletUserEntity appletUserEntity = new AppletUserEntity();
            Optional<AppletUserEntity> appletUserDaoById = appletUserDao.findById(appletUserChangeVo.getId());
            if (appletUserDaoById.isPresent()) {
                AppletUserEntity appletUser = appletUserDaoById.get();
                BeanUtils.copyProperties(appletUserChangeVo, appletUserEntity);
                appletUserEntity.setUpdateTime(LocalDateTime.now());
                appletUserEntity.setCreateId(appletUser.getCreateId());
                appletUserEntity.setCreateTime(appletUser.getCreateTime());
            }
            appletUserDao.save(appletUserEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<List<UserDisWalletDto>> findUserDisWalletListById(String appletUserId) {
        //返回的集合
        List<UserDisWalletDto> resultList = Lists.newArrayList();

        //根据小程序用户id查询用户V2G钱包数据
        List<UserDisWalletEntity> userDisWalletList = userDisWalletDao.findAllByAppletUserId(appletUserId);
        if (CollectionUtils.isNotEmpty(userDisWalletList)) {
            //根据多个账户id查询商户id和商户名称
            Set<String> accountIds = userDisWalletList.stream().map(UserDisWalletEntity::getAccountId).collect(Collectors.toSet());
            Map<String, AccountDto> accountMap = systemService.findAccountListByAccountIds(accountIds).getData();
            //对数据进行组装
            resultList = userDisWalletList.stream().map(userDisWallet -> {
                UserDisWalletDto result = new UserDisWalletDto();
                BeanUtils.copyProperties(userDisWallet, result);
                if (accountMap.containsKey(userDisWallet.getAccountId())) {
                    AccountDto account = accountMap.get(userDisWallet.getAccountId());
                    result.setMchId(account.getMchId());
                    result.setMchName(account.getMchName());
                }
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> submitAppletCancel(String appletUserId, String appletName) {
        if (StringUtil.isNotEmpty(appletUserId) && StringUtil.isNotEmpty(appletName)) {
            //先查询是否已有当前用户提交的正在审批状态的注销申请，如果有则返回申请失败
            AppletCancelEntity appletCancel = appletCancelDao.findByAppletUserIdAndApplyState(appletUserId, 1);
            if (StringUtil.isNotEmpty(appletCancel)) {
                return ResponseResult.error("申请失败，已有注销申请在审批状态，请不要重复提交");
            }
            AppletCancelEntity appletCancelEntity = new AppletCancelEntity();
            appletCancelEntity.setAppletUserId(appletUserId);
            appletCancelEntity.setAppletName(appletName);
            appletCancelEntity.setApplyState(1);
            appletCancelEntity.setCreateTime(LocalDateTime.now());
            appletCancelDao.save(appletCancelEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 分页查询小程序用户注销申请列表
     * @param phoneNum
     * @param applyState
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult<PageDto<AppletCancelInfoDto>> findAppletCancelListByPage(String phoneNum, Integer applyState, Integer page, Integer size) {
        List<AppletCancelInfoDto> resultList = Lists.newArrayList();
        //根据查询条件查询注销申请数据
        List<AppletCancelEntity> appletCancelEntityList = appletCancelDao.findAll();
        if (CollectionUtils.isNotEmpty(appletCancelEntityList)) {
            //根据状态查询
            if (StringUtil.isNotEmpty(applyState)) {
                appletCancelEntityList = appletCancelEntityList.stream().filter(a -> a.getApplyState().equals(applyState)).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(appletCancelEntityList)) {
                //根据多个小程序用户id，查询小程序用户数据
                Set<String> appletUserIds = appletCancelEntityList.stream().map(AppletCancelEntity::getAppletUserId).collect(Collectors.toSet());
                Map<String, AppletUserEntity> appletUserEntityMap = appletUserDao.findAllById(appletUserIds).stream().collect(Collectors.toMap(AppletUserEntity::getId, appletUserEntity -> appletUserEntity, (k1, k2) -> k1));
                //根据多个分组id，查询分组信息
                Set<String> groupIds = Lists.newArrayList(appletUserEntityMap.values()).stream().filter(a -> StringUtil.isNotEmpty(a.getGroupId())).map(AppletUserEntity::getGroupId).collect(Collectors.toSet());
                Map<String, UserGroupEntity> userGroupEntityMap = userGroupDao.findAllById(groupIds).stream().collect(Collectors.toMap(UserGroupEntity::getId, userGroupEntity -> userGroupEntity, (k1, k2) -> k1));
                resultList = appletCancelEntityList.stream().map(appletCancelEntity -> {
                    AppletCancelInfoDto appletCancelInfoDto = new AppletCancelInfoDto();
                    BeanUtils.copyProperties(appletCancelEntity, appletCancelInfoDto);
                    appletCancelInfoDto.setCreateTime(localDateTimeToStr(appletCancelEntity.getCreateTime()));
                    //获取小程序用户信息
                    if (appletUserEntityMap.containsKey(appletCancelEntity.getAppletUserId())) {
                        AppletUserEntity appletUserEntity = appletUserEntityMap.get(appletCancelEntity.getAppletUserId());
                        appletCancelInfoDto.setPhoneNum(appletUserEntity.getPhoneNum());
                        appletCancelInfoDto.setRefer(appletUserEntity.getRefer());
                        //获取用户分组信息
                        if (StringUtil.isNotEmpty(appletUserEntity.getGroupId()) && userGroupEntityMap.containsKey(appletUserEntity.getGroupId())) {
                            appletCancelInfoDto.setGroupName(userGroupEntityMap.get(appletUserEntity.getGroupId()).getGroupName());
                        }
                    }
                    return appletCancelInfoDto;
                }).collect(Collectors.toList());
                //根据手机号查询
                if (StringUtil.isNotEmpty(phoneNum)) {
                    resultList = resultList.stream().filter(a -> StringUtil.isNotEmpty(a.getPhoneNum()) && a.getPhoneNum().contains(phoneNum)).collect(Collectors.toList());
                }
                resultList = resultList.stream().sorted(Comparator.comparing(AppletCancelInfoDto::getCreateTime).reversed()).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, page, size));
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> cancelAppletUser(String id) {
        //根据唯一id查询注销申请数据
        Optional<AppletCancelEntity> appletCancelDaoById = appletCancelDao.findById(id);
        if (appletCancelDaoById.isPresent()) {
            AppletCancelEntity appletCancelEntity = appletCancelDaoById.get();
            //把注销申请状态修改为已注销
            appletCancelEntity.setApplyState(2);
            appletCancelDao.save(appletCancelEntity);
            //根据小程序用户id查询小程序用户数据,并修改小程序用户状态为注销
            if (StringUtil.isNotEmpty(appletCancelEntity.getAppletUserId())) {
                Optional<AppletUserEntity> appletUserEntityOptional = appletUserDao.findById(appletCancelEntity.getAppletUserId());
                if (appletUserEntityOptional.isPresent()) {
                    AppletUserEntity appletUserEntity = appletUserEntityOptional.get();
                    appletUserEntity.setUserState(3);
                    appletUserDao.save(appletUserEntity);
                }
            }
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 根据小程序用户id更新手机号
     * @param appletUserId 小程序用户id
     * @param phoneNum 手机号
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> updateAppletUserPhoneById(String appletUserId, String phoneNum) {
        Optional<AppletUserEntity> appletUserDaoById = appletUserDao.findById(appletUserId);
        if (appletUserDaoById.isPresent()) {
            AppletUserEntity appletUserEntity = appletUserDaoById.get();
            appletUserEntity.setPhoneNum(phoneNum);
            appletUserDao.save(appletUserEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }
}
