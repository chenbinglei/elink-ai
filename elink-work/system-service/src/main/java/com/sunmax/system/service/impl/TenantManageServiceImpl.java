package com.sunmax.system.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.*;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.system.dao.*;
import com.sunmax.system.dto.AccountListDto;
import com.sunmax.system.dto.PermissionListDto;
import com.sunmax.system.dto.TenantListDto;
import com.sunmax.system.entity.*;
import com.sunmax.system.service.TenantManageService;
import com.sunmax.system.service.feign.DeviceService;
import com.sunmax.system.vo.AccountVo;
import com.sunmax.system.vo.OrganStructureVo;
import com.sunmax.system.vo.TenantInfoVo;
import com.sunmax.system.vo.TenantListQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.sunmax.common.util.DateUtil.localDateTimeToStr;

@Slf4j
@Service
public class TenantManageServiceImpl implements TenantManageService {

    @Autowired
    private TenantManageDao tenantManageDao;

    @Autowired
    private OrganStructureDao organStructureDao;

    @Autowired
    private OrganEmpowerDao organEmpowerDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserGroupDao userGroupDao;

    @Autowired
    private TenantApplyEmpowerDao tenantApplyEmpowerDao;

    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private GroupApplyEmpowerDao groupApplyEmpowerDao;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private AccountDao accountDao;

    /**
     * 保存或编辑租户信息
     *
     * @param tenantInfoVo
     * @param businessLicenseFile
     * @param logoFile
     * @param userId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateTenantInfo(TenantInfoVo tenantInfoVo, MultipartFile businessLicenseFile, MultipartFile logoFile, String userId) {
        if (StringUtil.isNotEmpty(tenantInfoVo)) {
            TenantInfoEntity tenantInfoEntity = new TenantInfoEntity();
            //新增租户信息
            if (StringUtil.isEmpty(tenantInfoVo.getId())) {
                BeanUtils.copyProperties(tenantInfoVo, tenantInfoEntity);
                tenantInfoEntity.setCreateTime(LocalDateTime.now());
                tenantInfoEntity.setUpdateTime(LocalDateTime.now());
                tenantInfoEntity.setCreateId(userId);
                tenantInfoEntity.setUpdateId(userId);
                tenantInfoEntity.setCreateTime(LocalDateTime.now());
                tenantInfoEntity.setUpdateTime(LocalDateTime.now());
                TenantInfoEntity save = tenantManageDao.save(tenantInfoEntity);
                //自动生成第一层组织架构
                OrganStructureEntity organStructureEntity = new OrganStructureEntity();
                organStructureEntity.setTenantId(save.getId());
                organStructureEntity.setOrganName(save.getTenantName());
                organStructureEntity.setSortNumber(1);
                OrganStructureEntity structureEntity = organStructureDao.save(organStructureEntity);
                //校验管理员账号是否重复
                Optional<UserEntity> userDaoOne = userDao.findOne(Example.of(UserEntity.builder().userAccount(save.getSuperAccount()).build()));
                if (userDaoOne.isPresent()) {
                    return ResponseResult.paramError("账号重复！");
                }
                //生成租户默认管理员账号
                UserEntity userEntity = new UserEntity();
                userEntity.setUserAccount(save.getSuperAccount());
                userEntity.setPassword(save.getPassword());
                userEntity.setUserRole(1);
                userEntity.setUserState(1);
                userEntity.setTenantId(save.getId());
                userEntity.setIsDefaultAdmin(1);
                userEntity.setFullName(save.getTenantName());
                userEntity.setCreateTime(save.getCreateTime());
                userEntity.setOrganId(structureEntity.getId());
                userDao.save(userEntity);
                return ResponseResult.ok(ResponseResult.SUCCESS);
            } else {
                Optional<TenantInfoEntity> tenantManageDaoById = tenantManageDao.findById(tenantInfoVo.getId());
                if (tenantManageDaoById.isPresent()) {
                    BeanUtils.copyProperties(tenantInfoVo, tenantInfoEntity);
                    tenantInfoEntity.setCreateTime(tenantManageDaoById.get().getCreateTime());
                    tenantInfoEntity.setUpdateTime(LocalDateTime.now());
                    tenantInfoEntity.setCreateId(tenantManageDaoById.get().getCreateId());
                    tenantInfoEntity.setUpdateId(userId);
                    tenantInfoEntity.setCreateTime(tenantManageDaoById.get().getCreateTime());
                    tenantInfoEntity.setUpdateTime(LocalDateTime.now());
                    //修改用户表中当前租户的账号密码信息
                    UserEntity userEntity = userDao.findByTenantIdAndIsDefaultAdmin(tenantInfoVo.getId(), 1);
                    if (StringUtil.isNotEmpty(userEntity)) {
                        userEntity.setFullName(tenantInfoVo.getTenantName());
                        userEntity.setUserAccount(tenantInfoVo.getSuperAccount());
                        userEntity.setPassword(tenantInfoVo.getPassword());
                        userDao.save(userEntity);
                    }
                    //修改租户下面的组织架构根节点名称
                    List<OrganStructureEntity> organStructureList = organStructureDao.findAllByTenantId(tenantInfoVo.getId())
                            .stream().filter(o -> StringUtil.isEmpty(o.getParentId()) && Objects.equals(o.getSortNumber(), 1))
                            .peek(o -> o.setOrganName(tenantInfoVo.getTenantName())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(organStructureList)) {
                        organStructureDao.saveAll(organStructureList);
                    }
                    //营业执照上传
                    if (businessLicenseFile != null && !businessLicenseFile.isEmpty()) {
                        tenantInfoEntity.setBusinessLicense(FileUtil.getImagePath(businessLicenseFile, tenantManageDaoById.get().getBusinessLicense()));
                    }
                    //企业logo上传
                    if (logoFile != null && !logoFile.isEmpty()) {
                        tenantInfoEntity.setLogo(FileUtil.getImagePath(logoFile, tenantManageDaoById.get().getLogo()));
                    }
                    tenantManageDao.save(tenantInfoEntity);
                    return ResponseResult.ok(ResponseResult.SUCCESS);
                }
            }
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 根据id更新租户状态
     * @param id
     * @param tenantState
     * @return
     */
    @Override
    public ResponseResult<String> updateTenantStateById(String id, Integer tenantState) {
        int i = tenantManageDao.updateTenantStateById(id, tenantState);
        if (i > 0) {
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 根据id删除租户信息
     * @param id
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteTenantInfoById(String id) {
        //删除租户本身
        tenantManageDao.deleteById(id);
        //查询租户下所有用户租信息
        List<UserGroupEntity> allByTenantId = userGroupDao.findAllByTenantId(id);
        if (CollectionUtils.isNotEmpty(allByTenantId)) {
            //删除租户下所有用户组信息
            userGroupDao.deleteAllByTenantId(id);
            //删除用户组关联权限配置信息
            groupApplyEmpowerDao.deleteAllByGroupIdIn(allByTenantId.stream().map(UserGroupEntity::getId).collect(Collectors.toList()));
        }
        //删除租户下所有用户信息
        userDao.deleteAllByTenantId(id);
        //删除租户下所有组织架构信息
        organStructureDao.deleteAllByTenantId(id);
        //删除租户关联站点资产授权关联信息
        organEmpowerDao.deleteAllByTenantId(id);
        //删除租户下关联应用授权关联信息
        tenantApplyEmpowerDao.deleteAllByTenantId(id);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 分页查询租户信息
     * @param tenantListQueryVo
     * @return
     */
    @Override
    public ResponseResult<?> findTenantInfoByPage(TenantListQueryVo tenantListQueryVo) {
        List<TenantListDto> tenantListDtoList = Lists.newLinkedList();
        //根据查询条件查询租户数据
        List<TenantInfoEntity> tenantInfoEntityList = tenantManageDao.findAll((Specification<TenantInfoEntity>) (root, cq, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (StringUtil.isNotEmpty(tenantListQueryVo.getKeywordType()) && StringUtil.isNotEmpty(tenantListQueryVo.getKeyword())) {
                String keyword = tenantListQueryVo.getKeyword();
                if (tenantListQueryVo.getKeywordType() == 1) {
                    predicates.add(cb.like(root.get("tenantName"), "%" + keyword + "%"));
                } else if (tenantListQueryVo.getKeywordType() == 2) {
                    predicates.add(cb.like(root.get("id"), "%" + keyword + "%"));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(tenantInfoEntityList)) {
            //根据多个创建人和修改人id查询用户信息
            List<String> userIdList = tenantInfoEntityList.stream().filter(u -> StringUtil.isNotEmpty(u.getCreateId()) || StringUtil.isNotEmpty(u.getUpdateId()))
                    .flatMap(entity -> Stream.of(entity.getCreateId(), entity.getUpdateId()))
                    .distinct().collect(Collectors.toList());
            Map<String, UserEntity> userEntityMap = userDao.findAllById(userIdList).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));
            tenantListDtoList = tenantInfoEntityList.stream().map(tenantInfoEntity -> {
                TenantListDto tenantListDto = new TenantListDto();
                BeanUtils.copyProperties(tenantInfoEntity, tenantListDto);
                tenantListDto.setCreateTime(localDateTimeToStr(tenantInfoEntity.getCreateTime()));
                if (StringUtil.isNotEmpty(tenantInfoEntity.getCreateId()) && userEntityMap.containsKey(tenantInfoEntity.getCreateId())) {
                    tenantListDto.setCreateUserName(userEntityMap.get(tenantInfoEntity.getCreateId()).getFullName());
                }
                return tenantListDto;
            }).collect(Collectors.toList());
        }
        if (tenantListQueryVo.getSize() > 0) {
            return ResponseResult.ok(new PageDto<>(tenantListDtoList, tenantListQueryVo.getPage(), tenantListQueryVo.getSize()));
        } else {
            return ResponseResult.ok(tenantListDtoList);
        }
    }

    /**
     * 根据id查询租户详情信息
     * @param id
     * @return
     */
    @Override
    public ResponseResult<TenantDetailsDto> findTenantDetailsById(String id) {
        TenantDetailsDto tenantDetailsDto = new TenantDetailsDto();
        Optional<TenantInfoEntity> tenantManageDaoById = tenantManageDao.findById(id);
        if (tenantManageDaoById.isPresent()) {
            BeanUtils.copyProperties(tenantManageDaoById.get(), tenantDetailsDto);
            tenantDetailsDto.setCreateTime(localDateTimeToStr(tenantManageDaoById.get().getCreateTime()));
            if (StringUtil.isNotEmpty(tenantManageDaoById.get().getUpdateTime())) {
                tenantDetailsDto.setUpdateTime(localDateTimeToStr(tenantManageDaoById.get().getUpdateTime()));
            }
            List<UserEntity> userList = userDao.findAll(Example.of(UserEntity.builder().userAccount(tenantManageDaoById.get().getSuperAccount()).build()));
            if (CollectionUtils.isNotEmpty(userList)) {
                tenantDetailsDto.setPhone(userList.get(0).getPhone());
            }
            List<String> userIdList = Arrays.asList(tenantManageDaoById.get().getCreateId(), tenantManageDaoById.get().getUpdateId());
            Map<String, UserEntity> userEntityMap = userDao.findAllById(userIdList).stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));
            //创建人名称
            if (StringUtil.isNotEmpty(tenantManageDaoById.get().getCreateId()) && userEntityMap.containsKey(tenantManageDaoById.get().getCreateId())) {
                tenantDetailsDto.setCreateUserName(userEntityMap.get(tenantManageDaoById.get().getCreateId()).getFullName());
            }
            //修改人名称
            if (StringUtil.isNotEmpty(tenantManageDaoById.get().getUpdateId()) && userEntityMap.containsKey(tenantManageDaoById.get().getUpdateId())) {
                tenantDetailsDto.setUpdateUserName(userEntityMap.get(tenantManageDaoById.get().getUpdateId()).getFullName());
            }
            //查询租户组织架构信息
            List<OrganStructureEntity> organStructureEntityList = organStructureDao.findAllByTenantId(id).stream().filter(o -> StringUtil.isEmpty(o.getParentId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(organStructureEntityList)) {
                tenantDetailsDto.setOrganStructureId(organStructureEntityList.get(0).getId());
            }
        }
        return ResponseResult.ok(tenantDetailsDto);
    }

    /**
     * 保存或编辑组织架构信息
     * @param organStructureVo
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateOrganStructure(OrganStructureVo organStructureVo) {
        if (StringUtil.isNotEmpty(organStructureVo)) {
            OrganStructureEntity organStructureEntity = new OrganStructureEntity();
            BeanUtils.copyProperties(organStructureVo, organStructureEntity);
            organStructureDao.save(organStructureEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
            //新增组织架构
            /*if (StringUtil.isEmpty(organStructureVo.getId())) {
            } else {
                Optional<OrganStructureEntity> organStructureDaoById = organStructureDao.findById(organStructureVo.getId());
                if (organStructureDaoById.isPresent()) {

                }
            }*/
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 根据组织架构id删除指定组织架构信息
     * @param organId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteOrganStructureById(String organId) {
        organStructureDao.deleteById(organId);
        //查询组织下所有用户信息，并把所属组织id置空
        List<UserEntity> userEntityList = userDao.findAllByOrganId(organId);
        if (CollectionUtils.isNotEmpty(userEntityList)) {
            userDao.saveAll(userEntityList.stream().peek(u -> u.setOrganId("")).collect(Collectors.toList()));
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据租户id查询租户下组织架构信息
     * @param tenantId
     * @return
     */
    @Override
    public ResponseResult<List<OrganStructureTreeDto>> findOrganStructureByTenantId(String tenantId) {
        List<OrganStructureTreeDto> organStructureDtoList = Lists.newLinkedList();
        //获取租户下所有人员
        List<UserEntity> userEntityList = userDao.findAllByTenantId(tenantId);
        //根据租户id查询组织架构数据
        List<OrganStructureEntity> organStructureEntityList = organStructureDao.findAllByTenantId(tenantId);
        if (CollectionUtils.isNotEmpty(organStructureEntityList)) {
            List<OrganStructureTreeDto> organStructureDtos;
            organStructureDtos = organStructureEntityList.stream().map(organStructure -> {
                OrganStructureTreeDto organStructureDto = new OrganStructureTreeDto();
                BeanUtils.copyProperties(organStructure, organStructureDto);
                return organStructureDto;
            }).collect(Collectors.toList());
            //先获取到最外层租户信息
            List<OrganStructureTreeDto> structureDtoList = organStructureDtos.stream().filter(o -> StringUtil.isEmpty(o.getParentId())).collect(Collectors.toList());
            OrganStructureTreeDto organStructureDto = structureDtoList.get(0);
            int peopleNumber = 0;
            if (CollectionUtils.isNotEmpty(userEntityList)) {
                List<UserEntity> userEntities = userEntityList.stream().filter(u -> StringUtil.isNotEmpty(u.getOrganId()) && u.getOrganId().contains(organStructureDto.getId())).collect(Collectors.toList());
                peopleNumber = userEntities.size();
                List<UserDto> userDtoList;
                userDtoList = userEntities.stream().map(userEntity -> {
                    UserDto userDto = new UserDto();
                    BeanUtils.copyProperties(userEntity, userDto);
                    return userDto;
                }).collect(Collectors.toList());
                organStructureDto.setUserDtoList(userDtoList);
            }
            organStructureDto.setPeopleNumber(peopleNumber);
            organStructureDtoList.add(organStructureDto);
            //递归添加子节点
            addChildOrgan(organStructureDto, organStructureDtos, userEntityList);
        }

        return ResponseResult.ok(organStructureDtoList);
    }

    /**
     * 递归组装组织架构信息
     * @param organStructureDto
     * @param organStructureDtoList
     * @param userEntityList
     */
    private void addChildOrgan(OrganStructureTreeDto organStructureDto, List<OrganStructureTreeDto> organStructureDtoList, List<UserEntity> userEntityList) {
        //拿到所传租户的部门列表
        List<OrganStructureTreeDto> tempList = organStructureDtoList.stream()
                .filter(organStructure -> organStructureDto.getId().equals(organStructure.getParentId()))
                .collect(Collectors.toList());
        //统计组织下人员数量
        int peopleNumber = 0;
        if (CollectionUtils.isNotEmpty(userEntityList)) {
            List<UserEntity> userEntities = userEntityList.stream().filter(u -> StringUtil.isNotEmpty(u.getOrganId()) && u.getOrganId().contains(organStructureDto.getId())).collect(Collectors.toList());
            peopleNumber = userEntities.size();
            List<UserDto> userDtoList;
            userDtoList = userEntities.stream().map(userEntity -> {
                UserDto userDto = new UserDto();
                BeanUtils.copyProperties(userEntity, userDto);
                return userDto;
            }).collect(Collectors.toList());
            organStructureDto.setUserDtoList(userDtoList);
        }
        organStructureDto.setPeopleNumber(peopleNumber);
        //根据排序号升序排序
        List<OrganStructureTreeDto> structureDtos = tempList.stream().sorted(Comparator.comparing(OrganStructureTreeDto::getSortNumber)).collect(Collectors.toList());
        organStructureDto.setChildrenList(structureDtos);
        tempList.forEach(organStructure -> {
            //添加子节点
            addChildOrgan(organStructure, organStructureDtoList, userEntityList);
        });
    }

    /**
     * 添加资产授权信息
     *
     * @param siteIdList
     * @param authority
     * @param organId
     * @param tenantId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> addOrganEmpowerInfo(List<String> siteIdList, Integer authority, String organId, String tenantId) {
        List<OrganEmpowerEntity> organEmpowerEntityList = Lists.newArrayList();
        siteIdList.forEach(siteId -> {
            OrganEmpowerEntity organEmpowerEntity = new OrganEmpowerEntity();
            organEmpowerEntity.setOrganId(organId);
            organEmpowerEntity.setAuthority(authority);
            organEmpowerEntity.setSiteId(siteId);
            organEmpowerEntity.setTenantId(tenantId);
            organEmpowerEntityList.add(organEmpowerEntity);
        });
        organEmpowerDao.saveAll(organEmpowerEntityList);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 修改资产授权信息
     *
     * @param empowerId
     * @param authority
     * @param empowerType
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> updateEmpowerAuthorityById(String empowerId, Integer authority, Integer empowerType) {
        Optional<OrganEmpowerEntity> organEmpowerDaoById = organEmpowerDao.findById(empowerId);
        if (organEmpowerDaoById.isPresent()) {
            OrganEmpowerEntity organEmpowerEntity = organEmpowerDaoById.get();
            //租户级修改站点权限，如果是修改为只读，则查询子级组织架构关联的该站点，并且全部也修改为只读
            if (empowerType == 1) {
                //如果是修改为只读
                if (authority == 1) {
                    //根据父级组织架构id查询下面所有子级组织架构
                    List<OrganStructureEntity> organStructureEntityList = organStructureDao.findAllByParentId(organEmpowerEntity.getOrganId());
                    if (CollectionUtils.isNotEmpty(organStructureEntityList)) {
                        List<String> structureIdList = organStructureEntityList.stream().map(OrganStructureEntity::getId).collect(Collectors.toList());
                        //根据多个子级组织架构id以及站点id查询关联资产信息
                        List<OrganEmpowerEntity> organEmpowerEntityList = organEmpowerDao.findAllByOrganIdInAndSiteId(structureIdList, organEmpowerEntity.getSiteId());
                        if (CollectionUtils.isNotEmpty(organEmpowerEntityList)) {
                            organEmpowerDao.saveAll(organEmpowerEntityList.stream().peek(o -> o.setAuthority(1)).collect(Collectors.toList()));
                        }
                    }
                }
            } else {//企业级修改权限
                //如果是修改为读写，则查询父级组织架构关联的该站点是否支持读写权限
                if (authority == 2) {
                    Optional<OrganStructureEntity> organStructureDaoById = organStructureDao.findById(organEmpowerEntity.getOrganId());
                    if (organStructureDaoById.isPresent()) {
                        OrganStructureEntity organStructureEntity = organStructureDaoById.get();
                        //根据父级组织架构id以及站点id查询关联资产授权信息
                        List<OrganEmpowerEntity> allByOrganIdInAndSiteId = organEmpowerDao.findAllByOrganIdInAndSiteId(Collections.singletonList(organStructureEntity.getParentId()), organEmpowerEntity.getSiteId());
                        if (CollectionUtils.isNotEmpty(allByOrganIdInAndSiteId)) {
                            OrganEmpowerEntity empowerEntity = allByOrganIdInAndSiteId.get(0);
                            if (empowerEntity.getAuthority() != 2) {
                                return ResponseResult.error("修改失败，租户本身关联的该站点不支持读写权限！");
                            }
                        }
                    }
                }
            }
            organEmpowerEntity.setAuthority(authority);
            organEmpowerDao.save(organEmpowerEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 根据id删除授权信息限
     *
     * @param empowerId
     * @param empowerType
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteEmpowerInfoById(String empowerId, Integer empowerType) {
        //如果是租户类型资产删除，则把租户下子节点组织架构中关联的资产授权该站点一起删除
        if (empowerType == 1) {
            Optional<OrganEmpowerEntity> organEmpowerDaoById = organEmpowerDao.findById(empowerId);
            if (organEmpowerDaoById.isPresent()) {
                OrganEmpowerEntity organEmpowerEntity = organEmpowerDaoById.get();
                //根据父级组织架构id查询下面所有子级组织架构
                List<OrganStructureEntity> organStructureEntityList = organStructureDao.findAllByParentId(organEmpowerEntity.getOrganId());
                if (CollectionUtils.isNotEmpty(organStructureEntityList)) {
                    List<String> structureIdList = organStructureEntityList.stream().map(OrganStructureEntity::getId).collect(Collectors.toList());
                    //根据多个子级组织架构id删除指定站点数据
                    organEmpowerDao.deleteAllByOrganIdInAndSiteId(structureIdList, organEmpowerEntity.getSiteId());
                }
            }
        }
        organEmpowerDao.deleteById(empowerId);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据组织机构id分页查询资产授权列表
     *
     * @param organId
     * @param page
     * @param size
     * @return
     */
    @Override
    public ResponseResult<PageDto<OrganEmpowerListDto>> findEmpowerListByPage(String organId, Integer page, Integer size) {
        List<OrganEmpowerListDto> organEmpowerListDtoList = Lists.newArrayList();
        //根据组织机构id查询相关资产授权列表
        List<OrganEmpowerEntity> organEmpowerEntityList = organEmpowerDao.findAllByOrganId(organId);
        if (CollectionUtils.isNotEmpty(organEmpowerEntityList)) {
            //根据站点id查询站点详情数据
            Map<String, SiteInfoDto> siteBasicInfoDtoMap = deviceService.findSiteBasicInfoByIds(organEmpowerEntityList.stream().map(OrganEmpowerEntity::getSiteId).collect(Collectors.toList())).getData();
            organEmpowerListDtoList = organEmpowerEntityList.stream().map(organEmpowerEntity -> {
                OrganEmpowerListDto organEmpowerListDto = new OrganEmpowerListDto();
                BeanUtils.copyProperties(organEmpowerEntity, organEmpowerListDto);
                SiteInfoDto siteBasicInfoDto = siteBasicInfoDtoMap.get(organEmpowerEntity.getSiteId());
                if (StringUtil.isNotEmpty(siteBasicInfoDto)) {
                    organEmpowerListDto.setSiteName(siteBasicInfoDto.getSiteName());
                }
                return organEmpowerListDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(new PageDto<>(organEmpowerListDtoList, page, size));
    }

    /**
     * 保存租户应用授权信息
     * @param tenantApplyEmpowerVos
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveTenantApplyEmpowerInfo(String tenantApplyEmpowerVos) {
        if (StringUtil.isNotEmpty(tenantApplyEmpowerVos)) {
            List<TenantApplyEmpowerEntity> applyEmpowerEntities = JSONArray.parseArray(tenantApplyEmpowerVos, TenantApplyEmpowerEntity.class);
            //先查询当前租户当前模块下所有配置，如果有则全部删掉重新添加
            List<TenantApplyEmpowerEntity> applyEmpowerEntityList = tenantApplyEmpowerDao.findAllByTenantIdAndModuleId(applyEmpowerEntities.get(0).getTenantId(), applyEmpowerEntities.get(0).getModuleId());
            if (CollectionUtils.isNotEmpty(applyEmpowerEntityList)) {
                tenantApplyEmpowerDao.deleteAll(applyEmpowerEntityList);
            }
            //添加应用授权数据
            tenantApplyEmpowerDao.saveAll(applyEmpowerEntities);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<Void> saveTenantApplyInfo(String tenantId, String moduleId, String tenantApplyVos) {
        if (StringUtil.isEmpty(tenantId) || StringUtil.isEmpty(moduleId)) {
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }
        //先查询当前租户当前模块下所有配置，如果有则全部删掉重新添加
        List<TenantApplyEmpowerEntity> applyEmpowerEntityList = tenantApplyEmpowerDao.findAllByTenantIdAndModuleId(tenantId, moduleId);
        if (CollectionUtils.isNotEmpty(applyEmpowerEntityList)) {
            tenantApplyEmpowerDao.deleteAll(applyEmpowerEntityList);
        }
        if (StringUtil.isNotEmpty(tenantApplyVos)) {
            List<TenantApplyEmpowerEntity> tenantApplyEmpowerList = JSONArray.parseArray(tenantApplyVos, TenantApplyEmpowerEntity.class);
            if (CollectionUtils.isNotEmpty(tenantApplyEmpowerList)) {
                tenantApplyEmpowerDao.saveAll(tenantApplyEmpowerList.stream().peek(tenantApplyEmpowerEntity -> {
                    tenantApplyEmpowerEntity.setTenantId(tenantId);
                    tenantApplyEmpowerEntity.setModuleId(moduleId);
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok();
    }

    /**
     * 查询指定租户指定模块下配置的应用授权数据
     * @param tenantId
     * @param moduleId
     * @return
     */
    @Override
    public ResponseResult<List<PermissionListDto>> findTenantApplyEmpowerInfoById(String tenantId, String moduleId) {
        List<PermissionListDto> resultList = Lists.newArrayList();
        //根据模块id查询所有权限
        List<PermissionEntity> permissionList = permissionDao.findAll(Example.of(PermissionEntity.builder().moduleId(moduleId).isDelete(0).build()));
        if (CollectionUtils.isNotEmpty(permissionList)) {
            Map<String, Integer> tenantOperateMap = tenantApplyEmpowerDao.findAllByTenantIdAndModuleId(tenantId, moduleId).stream()
                    .filter(t -> StringUtil.isNotEmpty(t.getOperate())).collect(Collectors.toMap(TenantApplyEmpowerEntity::getPermissionId,
                            TenantApplyEmpowerEntity::getOperate, (k1, k2) -> k1));
            resultList = permissionList.stream().map(permission -> {
                PermissionListDto result = new PermissionListDto(permission);
                result.setOperate(tenantOperateMap.getOrDefault(permission.getId(), 2));
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 查询资产授权站点列表信息
     *
     * @param tenantId
     * @param siteNameLike
     * @param isQueryAll
     * @return
     */
    @Override
    public ResponseResult<List<SiteInfoDto>> findOrganEmpowerSiteList(String tenantId, String siteNameLike, Integer isQueryAll) {
        //返回数据对象
        List<SiteInfoDto> siteBasicInfoDtoList = Lists.newArrayList();
        //查询全部站点详情列表
        ResponseResult<List<SiteInfoDto>> basicInfoList = deviceService.findAllSiteBasicInfoList(siteNameLike);
        if (basicInfoList.isSuccess() && CollectionUtils.isNotEmpty(basicInfoList.getData())) {
            siteBasicInfoDtoList = basicInfoList.getData();
            //判断是否是查询全部，如果不是，则过滤出未被该租户关联的站点
            if (StringUtil.isNotEmpty(isQueryAll)) {
                return ResponseResult.ok(siteBasicInfoDtoList);
            }
            //查询当前租户已经授权的站点
            List<OrganEmpowerEntity> allByTenantId = organEmpowerDao.findAllByTenantId(tenantId);
            //过滤掉已经关联授权的站点
            if (CollectionUtils.isNotEmpty(allByTenantId)) {
                siteBasicInfoDtoList = siteBasicInfoDtoList.stream().filter(c -> allByTenantId.stream().noneMatch(v -> Objects.equals(c.getId(), v.getSiteId()))).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(siteBasicInfoDtoList);
    }


    /**
     * 根据用户id查询控件权限列表
     * @param userId
     * @return
     */
    @Override
    public ResponseResult<List<PermissionListDto>> findControlPermissionListByUserId(String userId) {
        List<PermissionListDto> permissionListDtos = Lists.newArrayList();
        //当前用户如果是平台管理员则查询全部站点数据
        Optional<UserEntity> userDaoById = userDao.findById(userId);
        if (userDaoById.isPresent()) {
            UserEntity userEntity = userDaoById.get();
            List<PermissionEntity> permissionEntityList = Lists.newArrayList();
            Integer userRole = userEntity.getUserRole();
            if (userRole == 0) {
                permissionEntityList = permissionDao.findAllByPermissionTypeAndIsDelete(2, 0);
            } else if (userRole == 1){
                //根据登录管理员所属租户id查询配置应用授权信息
                String tenantId = userEntity.getTenantId();
                List<TenantApplyEmpowerEntity> tenantApplyEmpowerEntityList = tenantApplyEmpowerDao.findAllByTenantIdAndOperate(tenantId, 1);
                if (CollectionUtils.isNotEmpty(tenantApplyEmpowerEntityList)) {
                    List<String> permissionIdList = tenantApplyEmpowerEntityList.stream().map(TenantApplyEmpowerEntity::getPermissionId).distinct().collect(Collectors.toList());
                    permissionEntityList = permissionDao.findAllByIdInAndPermissionTypeAndIsDelete(permissionIdList, 2, 0);
                }
            } else if (userRole == 2) {
                //先查询当前用户所属用户组配置权限信息
                if (StringUtil.isNotEmpty(userEntity.getGroupId())) {
                    List<String> groupIdList = Arrays.stream(userEntity.getGroupId().split(",")).map(String::trim).collect(Collectors.toList());
                    //查询分组下所有开启状态权限数据
                    List<GroupApplyEmpowerEntity> groupApplyEmpowerEntityList = groupApplyEmpowerDao.findAllByGroupIdInAndOperate(groupIdList, 1);
                    if (CollectionUtils.isNotEmpty(groupApplyEmpowerEntityList)) {
                        List<String> permissionIdList = groupApplyEmpowerEntityList.stream().map(GroupApplyEmpowerEntity::getPermissionId).distinct().collect(Collectors.toList());
                        permissionEntityList = permissionDao.findAllByIdInAndPermissionTypeAndIsDelete(permissionIdList, 2, 0);
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(permissionEntityList)) {
                permissionListDtos = permissionEntityList.stream().map(PermissionListDto::new).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(permissionListDtos);
    }

    /**
     * 根据租户id查询组织架构信息列表
     * @param tenantId
     * @return
     */
    @Override
    public ResponseResult<List<OrganStructureListDto>> findOrganStructureListByTenantId(String tenantId) {
        List<OrganStructureListDto> organStructureListDtos = Lists.newArrayList();
        //根据租户id查询组织架构信息
        List<OrganStructureEntity> organStructureEntities = organStructureDao.findAllByTenantId(tenantId);
        if (CollectionUtils.isNotEmpty(organStructureEntities)) {
            organStructureListDtos = organStructureEntities.stream().map(organStructure -> {
                OrganStructureListDto organStructureListDto = new OrganStructureListDto();
                BeanUtils.copyProperties(organStructure, organStructureListDto);
                return organStructureListDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(organStructureListDtos);
    }

    /**
     * 根据租户id查询用户列表
     * @param tenantId
     * @return
     */
    @Override
    public ResponseResult<List<UserDto>> findUserListByTenantId(String tenantId) {
        List<UserDto> userDtoList = Lists.newArrayList();
        //根据租户id查询用户列表信息
        List<UserEntity> userEntityList = userDao.findAllByTenantIdAndUserState(tenantId, 1);
        if (CollectionUtils.isNotEmpty(userEntityList)) {
            userDtoList = userEntityList.stream().map(userEntity -> {
                UserDto userDto = new UserDto();
                BeanUtils.copyProperties(userEntity, userDto);
                return userDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(userDtoList);
    }

    /**
     * 根据多个租户id查询租户基本信息
     * @param tenantIdList
     * @return
     */
    @Override
    public ResponseResult<List<TenantDetailsDto>> findAllTenantInfoByIds(List<String> tenantIdList) {
        List<TenantDetailsDto> resultList = Lists.newArrayList();
        List<TenantInfoEntity> tenantManageDaoAllById = tenantManageDao.findAllById(tenantIdList);
        if (CollectionUtils.isNotEmpty(tenantManageDaoAllById)) {
            resultList = tenantManageDaoAllById.stream().map(tenantInfoEntity -> {
                TenantDetailsDto tenantDetailsDto = new TenantDetailsDto();
                BeanUtils.copyProperties(tenantInfoEntity, tenantDetailsDto);
                tenantDetailsDto.setCreateTime(localDateTimeToStr(tenantInfoEntity.getCreateTime()));
                if (StringUtil.isNotEmpty(tenantInfoEntity.getUpdateTime())) {
                    tenantDetailsDto.setUpdateTime(localDateTimeToStr(tenantInfoEntity.getUpdateTime()));
                }
                return tenantDetailsDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 查询租户下组织架构资产授权站点列表
     * @param tenantId
     * @param organId
     * @return
     */
    @Override
    public ResponseResult<List<SiteInfoDto>> findTenantOrganSiteList(String tenantId, String organId) {
        List<SiteInfoDto> siteBasicInfoDtoList = Lists.newArrayList();
        //查询租户组织架构信息
        List<OrganStructureEntity> organStructureEntityList = organStructureDao.findAllByTenantId(tenantId).stream().filter(o -> StringUtil.isEmpty(o.getParentId())).collect(Collectors.toList());
        //当前租户所属组织架构id
        String organStructureId = organStructureEntityList.get(0).getId();
        //根据租户id和所属组织架构id查询租户资产授权信息
        List<OrganEmpowerEntity> organEmpowerEntityList = organEmpowerDao.findAllByTenantIdAndOrganId(tenantId, organStructureId);
        if (CollectionUtils.isNotEmpty(organEmpowerEntityList)) {
            //根据组织机构id查询相关资产授权列表
            List<OrganEmpowerEntity> organEmpowerEntitys = organEmpowerDao.findAllByOrganId(organId);
            if (CollectionUtils.isNotEmpty(organEmpowerEntitys)) {
                //过滤出当前组织架构未关联的资产站点
                organEmpowerEntityList = organEmpowerEntityList.stream().filter(o -> organEmpowerEntitys.stream().noneMatch(s -> Objects.equals(o.getSiteId(), s.getSiteId()))).collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(organEmpowerEntityList)) {
                //根据站点id查询站点详情数据
                Map<String, SiteInfoDto> siteBasicInfoDtoMap = deviceService.findSiteBasicInfoByIds(organEmpowerEntityList.stream().map(OrganEmpowerEntity::getSiteId).collect(Collectors.toList())).getData();
                siteBasicInfoDtoList = organEmpowerEntityList.stream().map(organEmpowerEntity -> {
                    SiteInfoDto siteBasicInfoDto = new SiteInfoDto();
                    //获取站点基本信息
                    if (siteBasicInfoDtoMap.containsKey(organEmpowerEntity.getSiteId())) {
                        BeanUtils.copyProperties(siteBasicInfoDtoMap.get(organEmpowerEntity.getSiteId()), siteBasicInfoDto);
                        siteBasicInfoDto.setAuthority(organEmpowerEntity.getAuthority());
                    }
                    return siteBasicInfoDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(siteBasicInfoDtoList);
    }

    /**
     * 批量添加资产授权信息
     * @param organEmpowerInfoStr
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> batchSaveOrganEmpower(String organEmpowerInfoStr) {
        if (StringUtil.isNotEmpty(organEmpowerInfoStr)) {
            List<OrganEmpowerEntity> organEmpowerEntityList = JSONArray.parseArray(organEmpowerInfoStr, OrganEmpowerEntity.class);
            organEmpowerDao.saveAll(organEmpowerEntityList);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> saveTenantAccount(AccountVo accountVo, MultipartFile keyPemFile, MultipartFile pubKeyFile) {
        //根据商户id和平台类型查询租户账户信息
        List<AccountEntity> accountList = accountDao.findAllByMchIdAndPlatformTypeAndIsDelete(accountVo.getMchId(), accountVo.getPlatformType(), 1);
        //新增租户账户
        if (StringUtil.isEmpty(accountVo.getId())) {
            if (CollectionUtils.isNotEmpty(accountList)) {
                return ResponseResult.paramShow(accountVo.getMchId(), ResponseResult.PARAM_EXIST);
            }
            AccountEntity account = new AccountEntity(accountVo);
            if (keyPemFile != null && !keyPemFile.isEmpty()) {
                account.setKeyPemPath(FileUtil.getFilePath(keyPemFile, null));
            }
            if (pubKeyFile != null && !pubKeyFile.isEmpty()) {
                account.setPubKeyPath(FileUtil.getFilePath(pubKeyFile, null));
            }
            account.setIsDelete(1);
            accountDao.save(account);
            return ResponseResult.ok();
        } else {
            //校验商户id是否存在
            accountList = accountList.stream().filter(a -> !Objects.equals(a.getId(), accountVo.getId())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(accountList)) {
                return ResponseResult.paramShow(accountVo.getMchId(), ResponseResult.PARAM_EXIST);
            }
            Optional<AccountEntity> optional = accountDao.findById(accountVo.getId());
            if (optional.isPresent()) {
                AccountEntity account = new AccountEntity(accountVo);
                if (keyPemFile != null && !keyPemFile.isEmpty()) {
                    account.setKeyPemPath(FileUtil.getFilePath(keyPemFile, optional.get().getKeyPemPath()));
                } else {
                    account.setKeyPemPath(optional.get().getKeyPemPath());
                }
                if (pubKeyFile != null && !pubKeyFile.isEmpty()) {
                    account.setPubKeyPath(FileUtil.getFilePath(pubKeyFile, optional.get().getPubKeyPath()));
                } else {
                    account.setPubKeyPath(optional.get().getPubKeyPath());
                }
                account.setCreateTime(optional.get().getCreateTime());
                account.setIsDelete(1);
                accountDao.save(account);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<AccountListDto>> findTenantAccountList(String tenantId) {
        //返回的集合
        List<AccountListDto> resultList = Lists.newArrayList();

        //根据租户id查询账户信息
        List<AccountEntity> accountList = accountDao.findAllByTenantIdAndIsDelete(tenantId, 1);
        if (CollectionUtils.isNotEmpty(accountList)) {
            resultList = accountList.stream().map(account -> {
                AccountListDto result = new AccountListDto();
                BeanUtils.copyProperties(account, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<Void> deleteTenantAccountById(String id) {
        Optional<AccountEntity> optional = accountDao.findById(id);
        if (optional.isPresent()) {
            AccountEntity account = optional.get();
            account.setIsDelete(2);
            accountDao.save(account);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    public ResponseResult<List<AccountDto>> findAccountListByTenantId(String tenantId, Integer platformType) {
        //返回的集合
        List<AccountDto> resultList = Lists.newArrayList();
        List<AccountEntity> accountList;
        if (StringUtil.isEmpty(platformType)) {
            //根据租户id查询账户信息
            accountList = accountDao.findAllByTenantIdAndIsDelete(tenantId, 1);
        } else {
            //根据租户id和平台类型查询账户信息
            accountList = accountDao.findAllByTenantIdAndPlatformTypeAndIsDelete(tenantId, platformType, 1);
        }
        if (CollectionUtils.isNotEmpty(accountList)) {
            resultList = accountList.stream().map(account -> {
                AccountDto result = new AccountDto();
                BeanUtils.copyProperties(account, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<Map<String, AccountDto>> findAccountListByAccountIds(Set<String> accountIds) {
        //返回的集合
        Map<String, AccountDto> resultMap = Maps.newHashMap();

        //根据多个主键id查询账户信息
        List<AccountEntity> accountList = accountDao.findAllById(accountIds);
        if (CollectionUtils.isNotEmpty(accountList)) {
            resultMap = accountList.stream().map(account -> {
                AccountDto result = new AccountDto();
                BeanUtils.copyProperties(account, result);
                return result;
            }).collect(Collectors.toMap(AccountDto::getId, Function.identity()));
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据用户id查询所有租户列表数据
     * @param userId 用户id
     * @return
     */
    @Override
    public ResponseResult<List<TenantDetailsDto>> findTenantListByUserId(String userId) {
        List<TenantDetailsDto> resultList = Lists.newArrayList();
        //根据用户id查询用户信息
        UserEntity userEntity = userDao.findById(userId).orElse(new UserEntity());
        //根据用户角色判断，如果当前用户是超级管理员，则返回所有租户，如果是管理员或普通用户，则返回单条自己所在租户信息
        List<TenantInfoEntity> tenantList = Lists.newArrayList();
        if (StringUtil.isNotEmpty(userEntity.getUserRole()) && userEntity.getUserRole() == 0) {
            tenantList = tenantManageDao.findAllByTenantState(1);
        } else if (StringUtil.isNotEmpty(userEntity.getTenantId())){
            Optional<TenantInfoEntity> tenantManageDaoById = tenantManageDao.findById(userEntity.getTenantId());
            if (tenantManageDaoById.isPresent()) {
                tenantList.add(tenantManageDaoById.get());
            }
        }
        if (CollectionUtils.isNotEmpty(tenantList)) {
            resultList = tenantList.stream().map(tenantInfoEntity -> {
                TenantDetailsDto tenantDetailsDto = new TenantDetailsDto();
                BeanUtils.copyProperties(tenantInfoEntity, tenantDetailsDto);
                tenantDetailsDto.setCreateTime(localDateTimeToStr(tenantInfoEntity.getCreateTime()));
                if (StringUtil.isNotEmpty(tenantInfoEntity.getUpdateTime())) {
                    tenantDetailsDto.setUpdateTime(localDateTimeToStr(tenantInfoEntity.getUpdateTime()));
                }
                return tenantDetailsDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<AccountDto> findAccountByRsaSerialNo(String rsaSerialNo) {
        AccountDto result = null;
        List<AccountEntity> accountList = accountDao.findByPlatformTypeAndRsaSerialNoAndIsDelete(1, rsaSerialNo, 1);
        if (CollectionUtils.isNotEmpty(accountList)) {
            AccountEntity account = accountList.get(0);
            result = new AccountDto();
            BeanUtils.copyProperties(account, result);
        }
        return ResponseResult.ok(result);
    }
}
