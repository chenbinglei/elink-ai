package com.sunmax.system.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.system.dao.*;
import com.sunmax.system.dto.PermissionListDto;
import com.sunmax.system.dto.UserGroupListDto;
import com.sunmax.system.dto.UserListDto;
import com.sunmax.system.entity.*;
import com.sunmax.system.service.SystemManageService;
import com.sunmax.system.vo.UserGroupVo;
import com.sunmax.system.vo.UserListQueryVo;
import com.sunmax.system.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SystemManageServiceImpl implements SystemManageService {

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private OrganStructureDao organStructureDao;

    @Autowired
    private GroupApplyEmpowerDao groupApplyEmpowerDao;

    @Autowired
    private PermissionDao permissionDao;

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateUserGroup(UserGroupVo userGroupVo) {
        if (StringUtil.isNotEmpty(userGroupVo)) {
            UserGroupEntity userGroupEntity = new UserGroupEntity();
            if (StringUtil.isEmpty(userGroupVo.getId())) {
                BeanUtils.copyProperties(userGroupVo, userGroupEntity);
                userGroupEntity.setCreateTime(LocalDateTime.now());
                userGroupEntity.setUpdateTime(LocalDateTime.now());
            } else {
                Optional<UserGroupEntity> userGroupDaoById = userGroupDao.findById(userGroupVo.getId());
                if (userGroupDaoById.isPresent()) {
                    userGroupEntity = userGroupDaoById.get();
                    userGroupEntity.setGroupName(userGroupVo.getGroupName());
                    userGroupEntity.setTenantId(userGroupVo.getTenantId());
                    userGroupEntity.setRefer(userGroupVo.getRefer());
                    userGroupEntity.setUpdateTime(LocalDateTime.now());
                }
            }
            userGroupDao.save(userGroupEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 根据用户组id删除用户组信息
     *
     * @param groupId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteUserGroupById(String groupId) {
        try {
            if (StringUtil.isEmpty(groupId)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            List<String> groupIds = Arrays.stream(groupId.split(FileUtil.COMMA)).collect(Collectors.toList());

            groupIds.forEach(id -> {
                userGroupDao.deleteById(id);
                //查询出该用户组下所有用户，然后把这些用户的所属用户组置空
                List<UserEntity> userEntityList = userDao.findAllByGroupIdLike("%" + id + "%");
                if (CollectionUtils.isNotEmpty(userEntityList)) {
                    List<UserEntity> userEntities = userEntityList.stream().peek(userEntity -> {
                        String userGroupId = userEntity.getGroupId();
                        if (userGroupId.contains(id + FileUtil.COMMA)) {
                            userGroupId = userGroupId.replace(id + FileUtil.COMMA, FileUtil.separator);
                        } else if (userGroupId.contains(FileUtil.COMMA + id)) {
                            userGroupId = userGroupId.replace(FileUtil.COMMA + id, FileUtil.separator);
                        } else {
                            userGroupId = userGroupId.replace(id, FileUtil.separator);
                        }
                        userEntity.setGroupId(userGroupId);
                    }).collect(Collectors.toList());
                    userDao.saveAll(userEntities);
                }
            });
            //删除用户组关联权限授权数据
            groupApplyEmpowerDao.deleteAllByGroupIdIn(groupIds);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        } catch (RuntimeException e) {
            log.error("删除用户组信息异常: ", e);
            return ResponseResult.error(ResponseResult.FAIL);
        }
    }

    /**
     * 分页查询用户组列表信息
     *
     * @param groupName
     * @param tenantId
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult<PageDto<UserGroupListDto>> findUserGroupListByPage(String groupName, String tenantId, Integer page, Integer size) {
        List<UserGroupListDto> userGroupListDtoList = Lists.newArrayList();
        //根据查询条件查询用户组数据
        List<UserGroupEntity> userGroupEntityList = userGroupDao.findAll((Specification<UserGroupEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            predicates.add(cb.equal(root.get("tenantId"), tenantId));
            if (StringUtil.isNotEmpty(groupName)) {
                predicates.add(cb.like(root.get("groupName"), "%" + groupName + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(userGroupEntityList)) {
            //查询租户下所有人员
            List<UserEntity> userEntityList = userDao.findAllByTenantId(tenantId);
            userGroupListDtoList = userGroupEntityList.stream().map(userGroupEntity -> {
                UserGroupListDto userGroupListDto = new UserGroupListDto();
                BeanUtils.copyProperties(userGroupEntity, userGroupListDto);
                int peopleNumber = 0;
                if (CollectionUtils.isNotEmpty(userEntityList)) {
                    List<UserEntity> userEntities = userEntityList.stream().filter(u -> StringUtil.isNotEmpty(u.getGroupId()) && u.getGroupId().contains(userGroupEntity.getId())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(userEntities)) {
                        peopleNumber = userEntities.size();
                        userGroupListDto.setUserDtoList(userEntities.stream().map(userEntity -> {
                            UserDto userDto = new UserDto();
                            BeanUtils.copyProperties(userEntity, userDto);
                            return userDto;
                        }).collect(Collectors.toList()));
                    }
                }
                userGroupListDto.setPeopleNumber(peopleNumber);
                return userGroupListDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(userGroupListDtoList, page, size));
    }

    /**
     * 保存或编辑用户信息
     *
     * @param userVo
     * @param imageFile
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateUserInfo(UserVo userVo, MultipartFile imageFile) {
        if (StringUtil.isNotEmpty(userVo)) {
            UserEntity userEntity = new UserEntity();
            if (StringUtil.isEmpty(userVo.getId())) {
                //校验用户账号是否重复
                Optional<UserEntity> userDaoOne = userDao.findOne(Example.of(UserEntity.builder().userAccount(userVo.getUserAccount()).build()));
                if (userDaoOne.isPresent()) {
                    return ResponseResult.paramError("账号重复！");
                }
                BeanUtils.copyProperties(userVo, userEntity);
                //上传用户头像
                if (imageFile != null && !imageFile.isEmpty()) {
                    userEntity.setUserProfile(FileUtil.getImagePath(imageFile, null));
                }
                //判断有没有给用户设置组织机构，如果没有，则默认分配到租户本身机构中
                if (StringUtil.isEmpty(userVo.getOrganId())) {
                    //查询租户组织架构信息
                    List<OrganStructureEntity> organStructureEntityList = organStructureDao.findAllByTenantId(userVo.getTenantId()).stream().filter(o -> StringUtil.isEmpty(o.getParentId())).collect(Collectors.toList());
                    //当前租户所属组织架构id
                    String organStructureId = organStructureEntityList.get(0).getId();
                    userEntity.setOrganId(organStructureId);
                }
                userEntity.setCreateTime(LocalDateTime.now());
                userEntity.setUpdateTime(LocalDateTime.now());
            } else {
                Optional<UserEntity> userDaoById = userDao.findById(userVo.getId());
                if (userDaoById.isPresent()) {
                    //校验用户账号是否重复
                    Optional<UserEntity> userDaoOne = userDao.findOne(Example.of(UserEntity.builder().userAccount(userVo.getUserAccount()).build()));
                    if (userDaoOne.isPresent() && !userDaoById.get().getId().equals(userDaoOne.get().getId())) {
                        return ResponseResult.paramError("账号重复！");
                    }
                    BeanUtils.copyProperties(userVo, userEntity);
                    userEntity.setIsDefaultAdmin(userDaoById.get().getIsDefaultAdmin());
                    userEntity.setCreateTime(userDaoById.get().getCreateTime());
                    userEntity.setUpdateTime(LocalDateTime.now());
                    //用户头像上传
                    if (imageFile != null && !imageFile.isEmpty()) {
                        userEntity.setUserProfile(FileUtil.getImagePath(imageFile, userDaoById.get().getUserProfile()));
                    }
                }
            }
            userDao.save(userEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 根据用户id删除用户信息
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteUserInfoById(String userId) {
        try {
            if (StringUtil.isEmpty(userId)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            List<String> userIds = Arrays.stream(userId.split(FileUtil.COMMA)).collect(Collectors.toList());
            userDao.deleteAllByIdIn(userIds);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        } catch (RuntimeException e) {
            log.error("删除用户信息异常：", e);
            return ResponseResult.error(ResponseResult.FAIL);
        }
    }

    /**
     * 分页查询用户列表数据
     *
     * @param userListQueryVo
     * @return
     */
    @Override
    public ResponseResult<PageDto<UserListDto>> findUserListByPage(UserListQueryVo userListQueryVo) {
        List<UserListDto> userListDtoList = Lists.newArrayList();

        //查询出所选租户下所有启用状态用户账号数据
        List<UserEntity> userEntityList = userDao.findAllByTenantId(userListQueryVo.getTenantId());
        //根据用户组查询
        if (StringUtil.isNotEmpty(userListQueryVo.getGroupType())) {
            if (userListQueryVo.getGroupType() == 1) {
                userEntityList = userEntityList.stream().filter(u -> StringUtil.isEmpty(u.getGroupId())).collect(Collectors.toList());
            } else if (userListQueryVo.getGroupType() == 2 && StringUtil.isNotEmpty(userListQueryVo.getGroupId())) {
                userEntityList = userEntityList.stream().filter(u -> StringUtil.isNotEmpty(u.getGroupId()) && u.getGroupId().contains(userListQueryVo.getGroupId())).collect(Collectors.toList());
            }
        }
        //根据组织架构查询
        if (StringUtil.isNotEmpty(userListQueryVo.getOrganId())) {
            userEntityList = userEntityList.stream().filter(u -> StringUtil.isNotEmpty(u.getOrganId()) && u.getOrganId().equals(userListQueryVo.getOrganId())).collect(Collectors.toList());
        }
        //根据关键字查询
        if (StringUtil.isNotEmpty(userListQueryVo.getKeyword())) {
            userEntityList = userEntityList.stream().filter(u -> (StringUtil.isNotEmpty(u.getUserAccount()) && u.getUserAccount().contains(userListQueryVo.getKeyword())) ||
                    (StringUtil.isNotEmpty(u.getFullName()) && u.getFullName().contains(userListQueryVo.getKeyword()))).collect(Collectors.toList());
        }
        //根据角色查询
        if (StringUtil.isNotEmpty(userListQueryVo.getUserRole())) {
            userEntityList = userEntityList.stream().filter(u -> u.getUserRole().equals(userListQueryVo.getUserRole())).collect(Collectors.toList());
        }
        //根据用户账号状态查询
        if (StringUtil.isNotEmpty(userListQueryVo.getUserState())) {
            userEntityList = userEntityList.stream().filter(u -> u.getUserState().equals(userListQueryVo.getUserState())).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(userEntityList)) {
            //获取用户组信息
            List<String> groupIdList = Lists.newArrayList();
            userEntityList.forEach(user -> {
                String groupId = user.getGroupId();
                if (StringUtil.isNotEmpty(groupId)) {
                    groupIdList.addAll(Arrays.stream(groupId.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList()));
                }
            });
            Map<String, String> userGroupEntityMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(groupIdList)) {
                List<UserGroupEntity> userGroupEntityList = userGroupDao.findAllByIdIn(groupIdList.stream().distinct().collect(Collectors.toList()));
                userGroupEntityMap = userGroupEntityList.stream().collect(Collectors.toMap(UserGroupEntity::getId, UserGroupEntity::getGroupName, (k1, k2) -> k1));
            }
            //获取组织信息
            List<String> organIdList = userEntityList.stream().map(UserEntity::getOrganId).distinct().collect(Collectors.toList());
            List<OrganStructureEntity> structureDaoAllById = organStructureDao.findAllById(organIdList);
            Map<String, String> organStructureMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(structureDaoAllById)) {
                organStructureMap = structureDaoAllById.stream().collect(Collectors.toMap(OrganStructureEntity::getId, OrganStructureEntity::getOrganName, (k1, k2) -> k1));
            }
            //循环用户信息，组装数据
            Map<String, String> finalUserGroupEntityMap = userGroupEntityMap;
            Map<String, String> finalOrganStructureMap = organStructureMap;
            userListDtoList = userEntityList.stream().map(user -> {
                UserListDto userListDto = new UserListDto();
                BeanUtils.copyProperties(user, userListDto);
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
                        userListDto.setGroupName(groupName.get().substring(0, groupName.get().length() - 1));
                    }
                }
                //获取组织名称
                String organId = user.getOrganId();
                if (StringUtil.isNotEmpty(organId)) {
                    userListDto.setOrganName(finalOrganStructureMap.get(organId));
                }
                //到期日 1-生效中 2-已过期
                if (StringUtil.isNotEmpty(user.getExpireDate())) {
                    LocalDate expireDate = DateUtil.strToLocalDate(user.getExpireDate());
                    if (LocalDate.now().isAfter(expireDate)) {
                        userListDto.setExpireState(2); //已过期
                    } else {
                        userListDto.setExpireState(1); //生效中
                    }
                }
                return userListDto;
            }).collect(Collectors.toList());

            //账号日期状态查询
            if (StringUtil.isNotEmpty(userListQueryVo.getExpireState())) {
                userListDtoList = userListDtoList.stream().filter(u -> StringUtil.isNotEmpty(u.getExpireState())
                                && Objects.equals(u.getExpireState(), userListQueryVo.getExpireState())).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(new PageDto<>(userListDtoList, userListQueryVo.getPage(), userListQueryVo.getSize()));
    }

    /**
     * 根据id更新用户状态
     *
     * @param userId
     * @param userState
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> updateUserStateById(String userId, Integer userState) {
        Optional<UserEntity> userDaoById = userDao.findById(userId);
        if (userDaoById.isPresent()) {
            UserEntity userEntity = userDaoById.get();
            userEntity.setUserState(userState);
            userDao.save(userEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 添加/修改用户组织信息
     *
     * @param userIdList
     * @param organId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> updateUserOrganStructure(List<String> userIdList, String organId) {
        List<UserEntity> userDaoAllById = userDao.findAllById(userIdList);
        userDao.saveAll(userDaoAllById.stream().peek(userEntity -> userEntity.setOrganId(organId)).collect(Collectors.toList()));
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 添加/修改用户组信息
     *
     * @param userIdList
     * @param groupIds
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> updateUserGroup(List<String> userIdList, String groupIds) {
        List<UserEntity> userDaoAllById = userDao.findAllById(userIdList);
        if (CollectionUtils.isNotEmpty(userDaoAllById)) {
            userDao.saveAll(userDaoAllById.stream().peek(userEntity -> userEntity.setGroupId(groupIds)).collect(Collectors.toList()));
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 查询租户下用户组列表
     *
     * @param tenantId
     * @return
     */
    @Override
    public ResponseResult<List<UserGroupListDto>> findUserGroupListById(String tenantId) {
        List<UserGroupListDto> userGroupListDtoList = Lists.newArrayList();

        //先查询第一层全部人员数量
        List<UserEntity> userEntityList = userDao.findAllByTenantId(tenantId);
        UserGroupListDto allUserListDto = new UserGroupListDto();
        allUserListDto.setGroupName("全部人员");
        allUserListDto.setPeopleNumber(userEntityList.size());
        userGroupListDtoList.add(allUserListDto);
        //过滤出未分组人员数量
        UserGroupListDto notGroupUserListDto = new UserGroupListDto();
        notGroupUserListDto.setGroupName("未分组");
        int notGroupUser = 0;
        if (CollectionUtils.isNotEmpty(userEntityList)) {
            notGroupUser = (int) userEntityList.stream().filter(u -> StringUtil.isEmpty(u.getGroupId())).count();
        }
        notGroupUserListDto.setPeopleNumber(notGroupUser);
        userGroupListDtoList.add(notGroupUserListDto);
        //查询全部用户组
        List<UserGroupEntity> userGroupEntityList = userGroupDao.findAllByTenantId(tenantId);
        if (CollectionUtils.isNotEmpty(userGroupEntityList)) {
            userGroupEntityList.forEach(userGroupEntity -> {
                UserGroupListDto userGroupListDto = new UserGroupListDto();
                BeanUtils.copyProperties(userGroupEntity, userGroupListDto);
                int peopleNumber = 0;
                if (CollectionUtils.isNotEmpty(userEntityList)) {
                    peopleNumber = (int) userEntityList.stream().filter(u -> StringUtil.isNotEmpty(u.getGroupId()) && u.getGroupId().contains(userGroupEntity.getId())).count();
                }
                userGroupListDto.setPeopleNumber(peopleNumber);
                userGroupListDtoList.add(userGroupListDto);
            });
        }
        return ResponseResult.ok(userGroupListDtoList);
    }

    /**
     * 保存用户组应用授权信息
     *
     * @param groupApplyEmpowerVos
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveGroupApplyEmpowerInfo(String groupApplyEmpowerVos) {
        if (StringUtil.isNotEmpty(groupApplyEmpowerVos)) {
            List<GroupApplyEmpowerEntity> groupApplyEmpowerEntityList = JSON.parseArray(groupApplyEmpowerVos, GroupApplyEmpowerEntity.class);
            //先查询当前用户组当前模块下所有配置，如果有则全部删掉重新添加
            List<GroupApplyEmpowerEntity> applyEmpowerEntityList = groupApplyEmpowerDao.findAllByGroupIdAndModuleId(groupApplyEmpowerEntityList.get(0).getGroupId(), groupApplyEmpowerEntityList.get(0).getModuleId());
            if (CollectionUtils.isNotEmpty(applyEmpowerEntityList)) {
                groupApplyEmpowerDao.deleteAll(applyEmpowerEntityList);
            }
            //添加应用授权数据
            groupApplyEmpowerDao.saveAll(groupApplyEmpowerEntityList);
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    @Override
    public ResponseResult<Void> saveGroupApplyInfo(String groupId, String moduleId, String groupApplyVos) {
        if (StringUtil.isEmpty(groupId) || StringUtil.isEmpty(moduleId)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //先查询当前用户组当前模块下所有配置，如果有则全部删掉
        List<GroupApplyEmpowerEntity> applyEmpowerEntityList = groupApplyEmpowerDao.findAllByGroupIdAndModuleId(groupId, moduleId);
        if (CollectionUtils.isNotEmpty(applyEmpowerEntityList)) {
            groupApplyEmpowerDao.deleteAll(applyEmpowerEntityList);
        }
        //重新添加
        if (StringUtil.isNotEmpty(groupApplyVos)) {
            List<GroupApplyEmpowerEntity> groupApplyEmpowerList = JSON.parseArray(groupApplyVos, GroupApplyEmpowerEntity.class);
            if (CollectionUtils.isNotEmpty(groupApplyEmpowerList)) {
                groupApplyEmpowerDao.saveAll(groupApplyEmpowerList.stream().peek(groupApplyEmpowerEntity -> {
                    groupApplyEmpowerEntity.setGroupId(groupId);
                    groupApplyEmpowerEntity.setModuleId(moduleId);
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok();
    }

    /**
     * 查询指定用户组指定模块下配置的应用授权数据
     *
     * @param groupId
     * @param moduleId
     * @return
     */
    @Override
    public ResponseResult<List<PermissionListDto>> findGroupApplyEmpowerInfoById(String groupId, String moduleId) {
        List<PermissionListDto> permissionListDtoList = Lists.newArrayList();

        //先查询当前模块下所有权限数据
        List<PermissionEntity> permissionList = permissionDao.findAll(Example.of(PermissionEntity.builder().moduleId(moduleId).build()));
        if (CollectionUtils.isNotEmpty(permissionList)) {
            //过滤出删除状态为正常的数据
            List<PermissionEntity> permissionEntities = permissionList.stream().filter(p -> Objects.equals(0, p.getIsDelete())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(permissionEntities)) {
                //再查询当前租户当前模块下配置的所有权限操作数据
                List<GroupApplyEmpowerEntity> allByGroupIdAndModuleId = groupApplyEmpowerDao.findAllByGroupIdAndModuleId(groupId, moduleId);
                Map<String, GroupApplyEmpowerEntity> groupApplyEmpowerEntityMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(allByGroupIdAndModuleId)) {
                    groupApplyEmpowerEntityMap = allByGroupIdAndModuleId.stream().collect(Collectors.toMap(GroupApplyEmpowerEntity::getPermissionId, GroupApplyEmpowerEntity -> GroupApplyEmpowerEntity, (k1, k2) -> k1));
                }
                Map<String, GroupApplyEmpowerEntity> finalGroupApplyEmpowerEntityMap = groupApplyEmpowerEntityMap;
                permissionListDtoList = permissionEntities.stream().map(permissionEntity -> {
                    PermissionListDto permissionListDto = new PermissionListDto(permissionEntity);
                    GroupApplyEmpowerEntity groupApplyEmpowerEntity = finalGroupApplyEmpowerEntityMap.get(permissionEntity.getId());
                    if (StringUtil.isNotEmpty(groupApplyEmpowerEntity)) {
                        permissionListDto.setOperate(groupApplyEmpowerEntity.getOperate());
                    }
                    return permissionListDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(permissionListDtoList);
    }

    /**
     * 用户组管理人员
     *
     * @param addUserIds
     * @param deleteUserIds
     * @param groupId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> groupManageUser(String addUserIds, String deleteUserIds, String groupId) {
        //处理新增加的用户
        if (StringUtil.isNotEmpty(addUserIds)) {
            List<UserEntity> userEntityList = userDao.findAllById(Arrays.stream(addUserIds.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList()));
            List<UserEntity> userEntities = userEntityList.stream().peek(userEntity -> userEntity.setGroupId(userEntity.getGroupId() + groupId + FileUtil.COMMA)).collect(Collectors.toList());
            userDao.saveAll(userEntities);
        }
        //处理移除用户组人员
        if (StringUtil.isNotEmpty(deleteUserIds)) {
            List<UserEntity> userEntityList = userDao.findAllById(Arrays.stream(deleteUserIds.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList()));
            List<UserEntity> userEntities = userEntityList.stream().peek(userEntity -> {
                String userGroupId = userEntity.getGroupId();
                if (userGroupId.contains(groupId + FileUtil.COMMA)) {
                    userGroupId = userGroupId.replace(groupId + FileUtil.COMMA, FileUtil.separator);
                } else if (userGroupId.contains(FileUtil.COMMA + groupId)) {
                    userGroupId = userGroupId.replace(FileUtil.COMMA + groupId, FileUtil.separator);
                } else {
                    userGroupId = userGroupId.replace(groupId, FileUtil.separator);
                }
                userEntity.setGroupId(userGroupId);
            }).collect(Collectors.toList());
            userDao.saveAll(userEntities);
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据用户id查询用户详情
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult<UserDto> findUserDetailsById(String userId) {
        UserDto userDto = new UserDto();

        Optional<UserEntity> userDaoById = userDao.findById(userId);
        if (userDaoById.isPresent()) {
            UserEntity userEntity = userDaoById.get();
            BeanUtils.copyProperties(userEntity, userDto);

            //获取用户组信息
            String groupId = userEntity.getGroupId();
            if (StringUtil.isNotEmpty(groupId)) {
                List<String> groupIds = Arrays.stream(groupId.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList());
                List<UserGroupEntity> userGroupEntityList = userGroupDao.findAllByIdIn(groupIds.stream().distinct().collect(Collectors.toList()));
                Map<String, String> userGroupEntityMap = userGroupEntityList.stream().collect(Collectors.toMap(UserGroupEntity::getId, UserGroupEntity::getGroupName, (k1, k2) -> k1));
                //拼装用户组名称
                AtomicReference<String> groupName = new AtomicReference<>(FileUtil.separator);
                groupIds.forEach(id -> {
                    String name = userGroupEntityMap.get(id);
                    if (StringUtil.isNotEmpty(name)) {
                        groupName.set(groupName + name + FileUtil.COMMA);
                    }
                });
                if (StringUtil.isNotEmpty(groupName.get())) {
                    userDto.setGroupName(groupName.get().substring(0, groupName.get().length() - 1));
                }
            }
            //获取组织信息
            String organId = userEntity.getOrganId();
            if (StringUtil.isNotEmpty(organId)) {
                List<OrganStructureEntity> structureDaoAllById = organStructureDao.findAllById(Collections.singletonList(organId));
                if (CollectionUtils.isNotEmpty(structureDaoAllById)) {
                    Map<String, String> organStructureMap = structureDaoAllById.stream().collect(Collectors.toMap(OrganStructureEntity::getId, OrganStructureEntity::getOrganName, (k1, k2) -> k1));
                    userDto.setOrganName(organStructureMap.get(organId));
                }
            }
        }
        return ResponseResult.ok(userDto);
    }

    /**
     * permissionIdList
     *
     * @param groupId
     * @param permissionIdList
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteGroupApplyEmpowerByGroupId(String groupId, List<String> permissionIdList) {
        groupApplyEmpowerDao.deleteAllByGroupIdAndPermissionIdIn(groupId, permissionIdList);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> updateUserByGroupId(String groupId, List<String> userIds) {
        if (StringUtil.isEmpty(groupId)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //根据用户组id查询用户数据 先删除旧数据
        List<UserEntity> deleteUserList = userDao.findAllByGroupIdLike(groupId);
        if (CollectionUtils.isNotEmpty(deleteUserList)) {
            userDao.saveAll(deleteUserList.stream().peek(userEntity -> {
                List<String> groupIdList = Arrays.stream(userEntity.getGroupId().split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toList());
                groupIdList.remove(groupId);
                userEntity.setGroupId(String.join(FileUtil.COMMA, groupIdList));
            }).collect(Collectors.toList()));
        }
        if (CollectionUtils.isNotEmpty(userIds)) {
            //根据多个用户id查询用户数据 添加新数据
            List<UserEntity> addUserList = userDao.findAllById(userIds);
            if (CollectionUtils.isNotEmpty(addUserList)) {
                userDao.saveAll(addUserList.stream().peek(userEntity -> {
                    if (StringUtil.isEmpty(userEntity.getGroupId())) {
                        userEntity.setGroupId(groupId);
                    } else {
                        userEntity.setGroupId(userEntity.getGroupId() + FileUtil.COMMA + groupId);
                    }
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok();
    }

}
