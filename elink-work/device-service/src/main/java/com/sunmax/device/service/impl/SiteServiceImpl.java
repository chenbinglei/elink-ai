package com.sunmax.device.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.AreaDto;
import com.sunmax.common.dto.CityDto;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.ProvinceDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.OrganStructureTreeDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.NauticalUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.StaticParamVo;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.*;
import com.sunmax.common.dto.device.SiteBasicInfoDto;
import com.sunmax.device.dto.AreaAddressDto;
import com.sunmax.common.dto.device.SiteEnergyInfoDto;
import com.sunmax.device.dto.SiteListDto;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.service.SiteService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.vo.SiteChangeVo;
import com.sunmax.device.vo.SiteEnergyInfoVo;
import com.sunmax.device.vo.SiteQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.oss.FileUtil.getImagePathList;

@Slf4j
@Service
public class SiteServiceImpl implements SiteService {

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private SiteEnergyInfoDao siteEnergyInfoDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private CityDao cityDao;

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private SiteInfoDao siteInfoDao;

    /**
     * 新增或编辑站点数据
     * @param siteChangeVo
     * @param imageFiles
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveOrUpdateSite(SiteChangeVo siteChangeVo, MultipartFile[] imageFiles) {
        //新增站点数据
        if (StringUtil.isEmpty(siteChangeVo.getId())) {
            //根据名称查询是否重复
            Optional<SiteEntity> siteDaoOne = siteDao.findOne(Example.of(SiteEntity.builder().siteName(siteChangeVo.getSiteName()).isDelete(1).build()));
            if (siteDaoOne.isPresent()) {
                return ResponseResult.error("添加失败，站点名称重复");
            }

            SiteEntity siteEntity = new SiteEntity();
            BeanUtils.copyProperties(siteChangeVo, siteEntity);
            siteEntity.setCreateTime(LocalDateTime.now());
            siteEntity.setUpdateTime(LocalDateTime.now());
            siteEntity.setCreateId(siteChangeVo.getUserId());
            siteEntity.setUpdateId(siteChangeVo.getUserId());
            SiteEntity save = siteDao.save(siteEntity);
            //给当前登录用户所属租户、运营单位、业主单位相关租户添加资产授权
            List<String> tenantIdList = Lists.newArrayList();
            tenantIdList.add(siteChangeVo.getTenantId());
            tenantIdList.add(save.getOperateUnit());
            tenantIdList.add(save.getOwnerUnit());
            if (CollectionUtils.isNotEmpty(tenantIdList)) {
                tenantIdList.stream().distinct().collect(Collectors.toList()).forEach(tenantId -> {
                    //根据租户id查询下面组织架构
                    List<OrganStructureTreeDto> organStructureTreeDtoList = systemService.findOrganStructureByTenantId(tenantId).getData();
                    OrganStructureTreeDto organStructureTreeDto = organStructureTreeDtoList.stream().filter(o -> StringUtil.isEmpty(o.getParentId())).collect(Collectors.toList()).get(0);
                    systemService.addOrganEmpowerInfo(save.getId(), 2, organStructureTreeDto.getId(), tenantId);
                });
            }
            return ResponseResult.ok();
        } else {//编辑站点数据
            //根据名称查询是否重复
            Optional<SiteEntity> siteDaoOne = siteDao.findOne(Example.of(SiteEntity.builder().siteName(siteChangeVo.getSiteName()).isDelete(1).build()));
            if (siteDaoOne.isPresent()) {
                return ResponseResult.error("添加失败，站点名称重复");
            }
            Optional<SiteEntity> siteDaoById = siteDao.findById(siteChangeVo.getId());
            if (siteDaoById.isPresent()) {
                SiteEntity siteEntity = new SiteEntity();
                BeanUtils.copyProperties(siteChangeVo, siteEntity);
                siteEntity.setCreateTime(siteDaoById.get().getCreateTime());
                siteEntity.setUpdateTime(LocalDateTime.now());
                siteEntity.setCreateId(siteDaoById.get().getCreateId());
                siteEntity.setUpdateId(siteChangeVo.getUserId());

                String imagePath = siteDaoById.get().getImagePath();
                List<String> deleteImagePathList = Lists.newArrayList();
                //先处理删除图片数据
                String deleteImagePath = siteChangeVo.getDeleteImagePath();
                if (StringUtil.isNotEmpty(deleteImagePath)) {
                    deleteImagePathList = Arrays.stream(deleteImagePath.split(",")).map(String::trim).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(deleteImagePathList)) {
                        for (String deleteImage : deleteImagePathList) {
                            //删除图片路径
                            if (imagePath.contains(deleteImage + ",")) {
                                imagePath = imagePath.replace(deleteImage + ",", "");
                            } else if (imagePath.contains("," + deleteImage)) {
                                imagePath = imagePath.replace("," + deleteImage, "");
                            } else {
                                imagePath = imagePath.replace(deleteImage, "");
                            }
                        }
                    }
                }
                //上传新的图片
                if (imageFiles != null && CollectionUtils.isNotEmpty(Arrays.asList(imageFiles))) {
                    String imagePathList = getImagePathList(Arrays.asList(imageFiles), deleteImagePathList);
                    if (StringUtil.isNotEmpty(imagePath)) {
                        imagePath = imagePath + "," + imagePathList;
                    } else {
                        imagePath = imagePathList;
                    }
                }
                siteEntity.setImagePath(imagePath);
                siteDao.save(siteEntity);
                return ResponseResult.ok();
            }
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    /**
     * 根据站点id查询基本详情数据
     * @param id
     * @return
     */
    @Override
    public ResponseResult<SiteBasicInfoDto> findSiteBasicInfoById(String id) {
        SiteBasicInfoDto siteBasicInfoDto = new SiteBasicInfoDto();
        Optional<SiteEntity> siteDaoById = siteDao.findById(id);
        if (siteDaoById.isPresent()) {
            SiteEntity siteEntity = siteDaoById.get();
            BeanUtils.copyProperties(siteEntity, siteBasicInfoDto);
            //查询创建和修改人信息
            List<String> userIdList = Lists.newArrayList(siteEntity.getCreateId());
            String updateId = siteEntity.getUpdateId();
            if (StringUtil.isNotEmpty(updateId)) {
                userIdList.add(updateId);
            }
            //获取运营单位和业主单位信息
            List<String> tenantIdList = Lists.newArrayList(siteEntity.getOwnerUnit(), siteEntity.getOperateUnit());
            if (CollectionUtils.isNotEmpty(tenantIdList)) {
                ResponseResult<List<TenantDetailsDto>> tenantDetailsByIds = systemService.findTenantDetailsByIds(tenantIdList);
                if (tenantDetailsByIds.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsByIds.getData())) {
                    Map<String, TenantDetailsDto> tenantDetailsDtoMap = tenantDetailsByIds.getData().stream().collect(Collectors.toMap(TenantDetailsDto::getId, tenantDetailsDto -> tenantDetailsDto));
                    if (StringUtil.isNotEmpty(siteEntity.getOwnerUnit()) && tenantDetailsDtoMap.containsKey(siteEntity.getOwnerUnit())) {
                        siteBasicInfoDto.setOwnerUnitName(tenantDetailsDtoMap.get(siteEntity.getOwnerUnit()).getTenantName());
                    }
                    if (StringUtil.isNotEmpty(siteEntity.getOperateUnit()) && tenantDetailsDtoMap.containsKey(siteEntity.getOperateUnit())) {
                        siteBasicInfoDto.setOperateUnitName(tenantDetailsDtoMap.get(siteEntity.getOperateUnit()).getTenantName());
                    }
                }
            }
            ResponseResult<Map<String, UserDto>> userInfoByIdsFeign = systemService.findUserInfoByIdsFeign(userIdList.stream().distinct().collect(Collectors.toList()));
            if (userInfoByIdsFeign.isSuccess() && !userInfoByIdsFeign.getData().isEmpty()) {
                Map<String, UserDto> userDtoMap = userInfoByIdsFeign.getData();
                UserDto createUserDto = userDtoMap.get(siteEntity.getCreateId());
                if (StringUtil.isNotEmpty(createUserDto)) {
                    siteBasicInfoDto.setCreateName(createUserDto.getFullName());
                }
                UserDto updateUserDto = userDtoMap.get(siteEntity.getUpdateId());
                if (StringUtil.isNotEmpty(updateUserDto)) {
                    siteBasicInfoDto.setUpdateName(updateUserDto.getFullName());
                }
            }
        }
        return ResponseResult.ok(siteBasicInfoDto);
    }

    /**
     * 根据站点id删除数据
     * @param id
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<String> deleteSiteInfoById(String id) {
        Optional<SiteEntity> siteDaoById = siteDao.findById(id);
        if (siteDaoById.isPresent()) {
            SiteEntity siteEntity = siteDaoById.get();
            siteEntity.setIsDelete(2);
            siteDao.save(siteEntity);
            //删除所有租户关联的资产授权关联数据
            systemService.deleteAllOrganEmpowerBySiteId(id);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.PARAM_ERROR);
    }

    /**
     * 分页查询站点列表
     *
     * @param siteQueryVo
     * @param userId
     * @return
     */
    @Override
    public ResponseResult<PageDto<SiteListDto>> querySiteListByPage(SiteQueryVo siteQueryVo, String userId) {
        //返回数据
        List<SiteListDto> siteListDtos = Lists.newArrayList();
        //当前用户如果是平台管理员则查询全部站点数据
        UserDto loginUserDto = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
        if (StringUtil.isNotEmpty(loginUserDto)) {
            Integer userRole = loginUserDto.getUserRole();
            List<SiteEntity> siteEntityList = Lists.newArrayList();
            Map<String, Integer> authorityMap = Maps.newHashMap();
            if (userRole == 0) {
                siteEntityList = siteDao.findAllByIsDelete(1);
            } else {
                //先查询当前所属租户配置的站点资产授权信息
                ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerList = systemService.findAllOrganEmpowerByUserId(userId);
                if (allOrganEmpowerList.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerList.getData())) {
                    List<OrganEmpowerListDto> organEmpowerList = allOrganEmpowerList.getData();
                    List<String> siteIdList = organEmpowerList.stream().map(OrganEmpowerListDto::getSiteId).distinct().collect(Collectors.toList());
                    //根据站点id转成map格式
                    authorityMap = organEmpowerList.stream().collect(Collectors.toMap(OrganEmpowerListDto::getSiteId, OrganEmpowerListDto::getAuthority, (k1, k2) -> k1));
                    siteEntityList = siteDao.findAllByIdInAndIsDelete(siteIdList, 1);
                }
            }
            if (CollectionUtils.isNotEmpty(siteEntityList)) {
                Integer keywordType = siteQueryVo.getKeywordType();
                String keyword = siteQueryVo.getKeyword();
                if (StringUtil.isNotEmpty(keywordType) && StringUtil.isNotEmpty(keyword)) {
                    //站点名称查询
                    if (keywordType == 1) {
                        siteEntityList = siteEntityList.stream().filter(s -> s.getSiteName().contains(keyword)).collect(Collectors.toList());
                    } else if (keywordType == 2) {//站点id查询
                        siteEntityList = siteEntityList.stream().filter(s -> s.getId().contains(keyword)).collect(Collectors.toList());
                    } else if (keywordType == 3) {//业主单位查询
                        siteEntityList = siteEntityList.stream().filter(s -> s.getOwnerUnit().contains(keyword)).collect(Collectors.toList());
                    }
                }
                if (CollectionUtils.isNotEmpty(siteEntityList)) {

                    //查询修改人信息
                    List<String> updateUserIdList = siteEntityList.stream().map(SiteEntity::getUpdateId).filter(StringUtil::isNotEmpty).collect(Collectors.toList());
                    Map<String, UserDto> userDtoMap = Maps.newHashMap();
                    if (CollectionUtils.isNotEmpty(updateUserIdList)) {
                        userDtoMap = systemService.findUserInfoByIdsFeign(updateUserIdList).getData();
                    }
                    //查询能源信息
                    List<SiteEnergyInfoEntity> siteEnergyInfoEntityList = siteEnergyInfoDao.findAllBySiteIdIn(siteEntityList.stream().map(SiteEntity::getId).collect(Collectors.toList()));
                    Map<String, SiteEnergyInfoEntity> siteEnergyInfoEntityMap = Maps.newHashMap();
                    if (CollectionUtils.isNotEmpty(siteEnergyInfoEntityList)) {
                        siteEnergyInfoEntityMap = siteEnergyInfoEntityList.stream().collect(Collectors.toMap(SiteEnergyInfoEntity::getSiteId, SiteEnergyInfoEntity -> SiteEnergyInfoEntity, (k1, k2) -> k1));
                    }
                    //查询租户信息
                    List<String> tenantIdList = Lists.newArrayList();
                    tenantIdList.addAll(siteEntityList.stream().map(SiteEntity::getOwnerUnit).collect(Collectors.toList()));
                    tenantIdList.addAll(siteEntityList.stream().map(SiteEntity::getOperateUnit).collect(Collectors.toList()));
                    Map<String, TenantDetailsDto> tenantDetailsDtoMap = Maps.newHashMap();
                    ResponseResult<List<TenantDetailsDto>> tenantDetailsByIds = systemService.findTenantDetailsByIds(tenantIdList.stream().distinct().collect(Collectors.toList()));
                    if (tenantDetailsByIds.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsByIds.getData())) {
                        tenantDetailsDtoMap = tenantDetailsByIds.getData().stream().collect(Collectors.toMap(TenantDetailsDto::getId, tenantDetailsDto -> tenantDetailsDto));
                    }
                    //循环站点信息组装数据
                    Map<String, UserDto> finalUserDtoMap = userDtoMap;
                    Map<String, SiteEnergyInfoEntity> finalSiteEnergyInfoEntityMap = siteEnergyInfoEntityMap;
                    Map<String, Integer> finalAuthorityMap = authorityMap;
                    Map<String, TenantDetailsDto> finalTenantDetailsDtoMap = tenantDetailsDtoMap;
                    siteListDtos = siteEntityList.stream().map(siteEntity -> {
                        SiteListDto siteListDto = new SiteListDto();
                        BeanUtils.copyProperties(siteEntity, siteListDto);
                        UserDto userDto = finalUserDtoMap.get(siteEntity.getUpdateId());
                        if (StringUtil.isNotEmpty(userDto)) {
                            siteListDto.setUpdateName(userDto.getFullName());
                        }
                        if (userRole == 0) {
                            siteListDto.setAuthority(2);
                        } else {
                            siteListDto.setAuthority(finalAuthorityMap.get(siteEntity.getId()));
                        }
                        //获取业主单位和运营单位信息
                        if (StringUtil.isNotEmpty(siteEntity.getOwnerUnit()) && finalTenantDetailsDtoMap.containsKey(siteEntity.getOwnerUnit())) {
                            siteListDto.setOwnerUnitName(finalTenantDetailsDtoMap.get(siteEntity.getOwnerUnit()).getTenantName());
                        }
                        if (StringUtil.isNotEmpty(siteEntity.getOperateUnit()) && finalTenantDetailsDtoMap.containsKey(siteEntity.getOperateUnit())) {
                            siteListDto.setOperateUnitName(finalTenantDetailsDtoMap.get(siteEntity.getOperateUnit()).getTenantName());
                        }

                        //获取能源信息
                        SiteEnergyInfoEntity siteEnergyInfoEntity = finalSiteEnergyInfoEntityMap.get(siteEntity.getId());
                        if (StringUtil.isNotEmpty(siteEnergyInfoEntity)) {
                            siteListDto.setPowerGridState(siteEnergyInfoEntity.getPowerGridState());
                            siteListDto.setTranState(siteEnergyInfoEntity.getTranState());
                            siteListDto.setPileState(siteEnergyInfoEntity.getPileState());
                            siteListDto.setPvState(siteEnergyInfoEntity.getPvState());
                            siteListDto.setStorageState(siteEnergyInfoEntity.getStorageState());
                        }
                        return siteListDto;
                    }).collect(Collectors.toList());
                    //根据创建时间降序排序
                    siteListDtos = siteListDtos.stream().sorted(Comparator.comparing(SiteListDto::getCreateTime).reversed()).collect(Collectors.toList());
                }
            }
        }
        return ResponseResult.ok(new PageDto<>(siteListDtos, siteQueryVo.getPage(), siteQueryVo.getSize()));
    }

    /**
     * 新增或编辑站点能源信息
     * @param siteEnergyInfoVo
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveOrUpdeteSiteEnergyInfo(SiteEnergyInfoVo siteEnergyInfoVo) {
        if (StringUtil.isNotEmpty(siteEnergyInfoVo)) {
            SiteEnergyInfoEntity siteEnergyInfoEntity = new SiteEnergyInfoEntity();
            String energyInfoVoId = siteEnergyInfoVo.getId();
            //新增
            if (StringUtil.isEmpty(energyInfoVoId)) {
                //先根据站点id查询有没有该站点数据
                Optional<SiteEnergyInfoEntity> siteEnergyInfos = siteEnergyInfoDao.findOne(Example.of(SiteEnergyInfoEntity.builder().siteId(siteEnergyInfoVo.getSiteId()).build()));
                if (siteEnergyInfos.isPresent()) {
                    return ResponseResult.paramShow(String.valueOf(siteEnergyInfoVo.getSiteId()), ResponseResult.PARAM_EXIST);
                }
                BeanUtils.copyProperties(siteEnergyInfoVo, siteEnergyInfoEntity);
                siteEnergyInfoEntity.setCreateTime(LocalDateTime.now());
                siteEnergyInfoEntity.setCreateId(siteEnergyInfoVo.getUserId());
            } else {
                Optional<SiteEnergyInfoEntity> siteEnergyInfoDaoById = siteEnergyInfoDao.findById(energyInfoVoId);
                if (siteEnergyInfoDaoById.isPresent()) {
                    BeanUtils.copyProperties(siteEnergyInfoVo, siteEnergyInfoEntity);
                    siteEnergyInfoEntity.setCreateTime(siteEnergyInfoDaoById.get().getCreateTime());
                    siteEnergyInfoEntity.setUpdateTime(LocalDateTime.now());
                    siteEnergyInfoEntity.setUpdateId(siteEnergyInfoVo.getUserId());
                }
            }
            siteEnergyInfoDao.save(siteEnergyInfoEntity);
            return ResponseResult.ok();
        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    /**
     * 根据站点id查询能源信息数据
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<SiteEnergyInfoDto> findSiteEnergyInfoById(String siteId) {
        SiteEnergyInfoDto siteEnergyInfoDto = new SiteEnergyInfoDto();
        Optional<SiteEnergyInfoEntity> siteEnergyInfos = siteEnergyInfoDao.findOne(Example.of(SiteEnergyInfoEntity.builder().siteId(siteId).build()));
        siteEnergyInfos.ifPresent(siteEnergyInfoEntity -> BeanUtils.copyProperties(siteEnergyInfoEntity, siteEnergyInfoDto));
        return ResponseResult.ok(siteEnergyInfoDto);
    }

    /**
     * 根据用户id查询站点列表信息
     * @param userId
     * @return
     */
    @Override
    public ResponseResult<List<SiteBasicInfoDto>> findSiteInfoListByUserId(String userId) {
        //返回数据
        List<SiteBasicInfoDto> siteBasicInfoDtoList = Lists.newArrayList();
        //当前用户如果是平台管理员则查询全部站点数据
        UserDto loginUserDto = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
        if (StringUtil.isNotEmpty(loginUserDto)) {
            List<SiteInfoEntity> siteEntityList = Lists.newArrayList();
            Map<String, Integer> authorityMap = Maps.newHashMap();
            Integer userRole = loginUserDto.getUserRole();
            if (userRole == 0) {
                siteEntityList = siteInfoDao.findAll(Example.of(SiteInfoEntity.builder().isDelete(1).build()));
            } else {
                //先查询当前所属租户配置的站点资产授权信息
                ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerList = systemService.findAllOrganEmpowerByUserId(userId);
                if (allOrganEmpowerList.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerList.getData())) {
                    List<OrganEmpowerListDto> organEmpowerList = allOrganEmpowerList.getData();
                    List<String> siteIdList = organEmpowerList.stream().map(OrganEmpowerListDto::getSiteId).distinct().collect(Collectors.toList());
                    //根据站点id转成map格式
                    authorityMap = organEmpowerList.stream().collect(Collectors.toMap(OrganEmpowerListDto::getSiteId, OrganEmpowerListDto::getAuthority, (k1, k2) -> k1));
                    siteEntityList = siteInfoDao.findAllById(siteIdList);
                }
            }
            if (CollectionUtils.isNotEmpty(siteEntityList)) {
                //创建用户id
                List<String> createUserIdList = siteEntityList.stream().map(SiteInfoEntity::getCreateId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                //修改用户id
                List<String> updateUserIdList = siteEntityList.stream().map(SiteInfoEntity::getUpdateId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                createUserIdList.addAll(updateUserIdList);
                //查询修改人信息
                Map<String, UserDto> userDtoMap = Maps.newHashMap();
                if (CollectionUtils.isNotEmpty(createUserIdList)) {
                    userDtoMap = systemService.findUserInfoByIdsFeign(createUserIdList).getData();
                }
                //循环站点信息组装数据
                Map<String, UserDto> finalUserDtoMap = userDtoMap;
                Map<String, Integer> finalAuthorityMap = authorityMap;
                siteBasicInfoDtoList = siteEntityList.stream().map(siteEntity -> {
                    SiteBasicInfoDto siteBasicInfoDto = new SiteBasicInfoDto();
                    BeanUtils.copyProperties(siteEntity, siteBasicInfoDto);
                    UserDto updateUserDto = finalUserDtoMap.get(siteEntity.getUpdateId());
                    if (StringUtil.isNotEmpty(updateUserDto)) {
                        siteBasicInfoDto.setUpdateName(updateUserDto.getFullName());
                    }
                    UserDto createUserDto = finalUserDtoMap.get(siteEntity.getCreateId());
                    if (StringUtil.isNotEmpty(createUserDto)) {
                        siteBasicInfoDto.setCreateName(createUserDto.getFullName());
                    }

                    if (userRole == 0) {
                        siteBasicInfoDto.setAuthority(2);
                    } else {
                        siteBasicInfoDto.setAuthority(finalAuthorityMap.get(siteEntity.getId()));
                    }
                    return siteBasicInfoDto;
                }).collect(Collectors.toList());
                //根据创建时间降序排序
                siteBasicInfoDtoList = siteBasicInfoDtoList.stream().sorted(Comparator.comparing(SiteBasicInfoDto::getCreateTime).reversed()).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(siteBasicInfoDtoList);
    }

    /**
     * 查询全国省份列表
     *
     * @return
     */
    @Override
    public ResponseResult<List<ProvinceDto>> queryProvinceData() {
        List<ProvinceDto> provinceDtoList;
        List<ProvinceEntity> provinceDaoAll = provinceDao.findAll();
        provinceDtoList = provinceDaoAll.stream().map(m -> {
            ProvinceDto provinceDto = new ProvinceDto();
            BeanUtils.copyProperties(m, provinceDto);
            return provinceDto;
        }).collect(Collectors.toList());
        return ResponseResult.ok(provinceDtoList);
    }

    /**
     * 根据全国省编码id查询下面市级数据
     *
     * @param provinceId
     * @return
     */
    @Override
    public ResponseResult<List<CityDto>> queryCityDataByProvinceId(String provinceId) {
        List<CityDto> cityDtoList = Lists.newArrayList();
        List<CityEntity> cityEntityList = cityDao.findAllByProvinceIdIn(Lists.newArrayList(provinceId));
        cityDtoList = cityEntityList.stream().map(m -> {
            CityDto cityDto = new CityDto();
            BeanUtils.copyProperties(m, cityDto);
            return cityDto;
        }).collect(Collectors.toList());
        return ResponseResult.ok(cityDtoList);
    }

    /**
     * 根据全国市编码id查询下面区县级数据
     *
     * @param cityId
     * @return
     */
    @Override
    public ResponseResult<List<AreaDto>> queryAreaDataByCityId(String cityId) {
        List<AreaDto> areaDtoList = Lists.newArrayList();
        List<AreaEntity> areaEntityList = areaDao.findAllByCityIdIn(Lists.newArrayList(cityId));
        areaDtoList = areaEntityList.stream().map(m -> {
            AreaDto areaDto = new AreaDto();
            BeanUtils.copyProperties(m, areaDto);
            return areaDto;
        }).collect(Collectors.toList());
        return ResponseResult.ok(areaDtoList);
    }

    @Override
    public ResponseResult<AreaAddressDto> getAreaAddressByCoordinates(String coordinates) {
        AreaAddressDto result = new AreaAddressDto();
        Map<String, String> addressMap = NauticalUtil.getAddressByLatAndLongitude(coordinates);
        if (!addressMap.isEmpty()) {
            StringBuilder areaName = new StringBuilder();
            if (StringUtil.isNotEmpty(addressMap.get(StaticParamVo.PROVINCE_NAME))) {
//                areaName.append(addressMap.get(StaticParamVo.PROVINCE_NAME));
                result.setProvinceName(addressMap.get(StaticParamVo.PROVINCE_NAME));
            }
            if (StringUtil.isNotEmpty(addressMap.get(StaticParamVo.CITY_NAME))) {
//                areaName.append(addressMap.get(StaticParamVo.PROVINCE_NAME));
                result.setCityName(addressMap.get(StaticParamVo.CITY_NAME));
            } else {
                result.setCityName(addressMap.get(StaticParamVo.PROVINCE_NAME));
            }
            if (StringUtil.isNotEmpty(addressMap.get(StaticParamVo.AREA_NAME))) {
                areaName.append(addressMap.get(StaticParamVo.AREA_NAME));
                result.setAreaName(addressMap.get(StaticParamVo.AREA_NAME));
            }
//            if (StringUtil.isNotEmpty(addressMap.get(StaticParamVo.TOWNS_NAME))) {
//                areaName.append(addressMap.get(StaticParamVo.TOWNS_NAME));
//            }
            //result.setAreaName(areaName.toString());
//            result.setAddress(StringUtils.join(addressMap.get(StaticParamVo.ADDRESS_NAME).split(areaName.toString())));
            result.setAddress(addressMap.get(StaticParamVo.ADDRESS_NAME));
        }
        return ResponseResult.ok(result);
    }

    /**
     * 根据多个站点id查询能源信息数据
     * @param siteIdList
     * @return
     */
    @Override
    public ResponseResult<Map<String, SiteEnergyInfoDto>> findSiteEnergyInfoBySiteIds(List<String> siteIdList) {
        Map<String, SiteEnergyInfoDto> resultMap = Maps.newHashMap();
        List<SiteEnergyInfoEntity> allBySiteIdIn = siteEnergyInfoDao.findAllBySiteIdIn(siteIdList);
        if (CollectionUtils.isNotEmpty(allBySiteIdIn)) {
            allBySiteIdIn.forEach(siteEnergyInfoEntity -> {
                SiteEnergyInfoDto siteEnergyInfoDto = new SiteEnergyInfoDto();
                BeanUtils.copyProperties(siteEnergyInfoEntity, siteEnergyInfoDto);
                resultMap.put(siteEnergyInfoEntity.getSiteId(), siteEnergyInfoDto);
            });
        }
        return ResponseResult.ok(resultMap);
    }
}
