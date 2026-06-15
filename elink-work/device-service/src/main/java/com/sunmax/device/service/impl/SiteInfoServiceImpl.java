package com.sunmax.device.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.AreaDto;
import com.sunmax.common.dto.CityDto;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.ProvinceDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.dto.device.AffiliatesInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.system.OrganStructureTreeDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.NauticalUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.StaticParamVo;
import com.sunmax.common.vo.device.SiteInfoChangeVo;
import com.sunmax.device.constants.DBConstants;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.AssetTypeDao;
import com.sunmax.device.dao.model.ModelDao;
import com.sunmax.device.dao.model.ModelReaDao;
import com.sunmax.device.dao.model.ReaDao;
import com.sunmax.device.dto.*;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.entity.model.AssetTypeEntity;
import com.sunmax.device.entity.model.ModelEntity;
import com.sunmax.device.entity.model.ModelReaEntity;
import com.sunmax.device.mapper.access.SiteInfoMapper;
import com.sunmax.device.service.SiteInfoService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.feign.TogetherService;
import com.sunmax.device.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.sunmax.common.util.oss.FileUtil.getImagePathList;

@Slf4j
@Service
public class SiteInfoServiceImpl implements SiteInfoService {

    @Autowired
    private SiteInfoDao siteInfoDao;

    @Autowired
    private ScenarioTypeDao scenarioTypeDao;

    @Autowired
    private SystemService systemService;

    @Autowired
    private ModelReaDao modelReaDao;

    @Autowired
    private ReaDao reaDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private AffiliatesInfoDao affiliatesInfoDao;

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private CityDao cityDao;

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private ModelDao modelDao;

    @Autowired
    private SiteInfoMapper siteInfoMapper;

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private SiteSetUpDao siteSetUpDao;

    @Autowired
    private SiteTopNodeDao siteTopNodeDao;

    @Autowired
    private SiteTopItemDao siteTopItemDao;

    @Autowired
    private AssetTypeDao assetTypeDao;

    /**
     * 新增或编辑站点数据
     *
     * @param siteChangeVo
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveOrUpdateSiteInfo(SiteInfoChangeVo siteChangeVo) {
        //根据名称查询是否重复
        SiteInfoEntity siteNameEntity = siteInfoDao.findBySiteNameAndIsDelete(siteChangeVo.getSiteName(), 1);
        if (StringUtil.isNotEmpty(siteNameEntity)) {
            //新增站点
            if (StringUtil.isEmpty(siteChangeVo.getId())) {
                return ResponseResult.paramShow(siteChangeVo.getSiteName(), ResponseResult.PARAM_EXIST);
            } else {
                //如果和查询到的id不重复，则说明名称重复
                if (!siteChangeVo.getId().equals(siteNameEntity.getId())) {
                    return ResponseResult.paramShow(siteChangeVo.getSiteName(), ResponseResult.PARAM_EXIST);
                }
            }
        }
        //根据站点编码查询是否重复
        SiteInfoEntity siteCodeEntity = siteInfoDao.findBySiteCodeAndIsDelete(siteChangeVo.getSiteCode(), 1);
        if (StringUtil.isNotEmpty(siteCodeEntity)) {
            //新增站点
            if (StringUtil.isEmpty(siteChangeVo.getId())) {
                return ResponseResult.paramShow(siteChangeVo.getSiteCode(), ResponseResult.PARAM_EXIST);
            } else {
                //如果和查询到的id不重复，则说明重复
                if (!siteChangeVo.getId().equals(siteCodeEntity.getId())) {
                    return ResponseResult.paramShow(siteChangeVo.getSiteCode(), ResponseResult.PARAM_EXIST);
                }
            }
        }
        //新增站点数据
        if (StringUtil.isEmpty(siteChangeVo.getId())) {
            SiteInfoEntity siteInfoEntity = new SiteInfoEntity();
            BeanUtils.copyProperties(siteChangeVo, siteInfoEntity);
            siteInfoEntity.setSourceType(1);
            siteInfoEntity.setCreateTime(LocalDateTime.now());
            siteInfoEntity.setUpdateTime(LocalDateTime.now());
            siteInfoEntity.setCreateId(siteChangeVo.getUserId());
            siteInfoEntity.setUpdateId(siteChangeVo.getUserId());
            siteInfoEntity.setIsDelete(1);
            SiteInfoEntity save = siteInfoDao.save(siteInfoEntity);
            //添加能源场景系统模型属性
            if (StringUtil.isNotEmpty(siteChangeVo.getSiteScenarioTypeDtos())) {
                List<ScenarioTypeEntity> scenarioTypeEntityList = JSON.parseArray(siteChangeVo.getSiteScenarioTypeDtos(), ScenarioTypeEntity.class)
                        .stream().peek(s -> s.setSiteId(save.getId())).collect(Collectors.toList());
                scenarioTypeDao.saveAll(scenarioTypeEntityList);
            }
            //把当前站点添加到所属租户的资产授权下，根据租户id查询下面组织架构
            List<OrganStructureTreeDto> organStructureTreeDtoList = systemService.findOrganStructureByTenantId(save.getTenantId()).getData();
            if (CollectionUtils.isEmpty(organStructureTreeDtoList)) {
                return ResponseResult.error("添加失败，没查询到当前登录用户所属租户父级本身组织架构！");
            }
            OrganStructureTreeDto organStructureTreeDto = organStructureTreeDtoList.stream().filter(o -> StringUtil.isEmpty(o.getParentId())).collect(Collectors.toList()).get(0);
            systemService.addOrganEmpowerInfo(save.getId(), 2, organStructureTreeDto.getId(), save.getTenantId());

            //判断产权方id和运营商id是否是同一个租户，如果是则创建一条关联方信息，否则创建两条(因为新增关联方那里不允许一个站点下关联多条相同租户)
            if (siteChangeVo.getPropertyId().equals(siteChangeVo.getOperatorId())) {
                AffiliatesInfoEntity affiliatesInfoEntity = new AffiliatesInfoEntity();
                affiliatesInfoEntity.setAffiliateTypes("2,3");
                affiliatesInfoEntity.setSiteId(save.getId());
                affiliatesInfoEntity.setTenantId(siteChangeVo.getPropertyId());
                affiliatesInfoDao.save(affiliatesInfoEntity);
            } else {
                //新增产权方关联信息
                AffiliatesInfoEntity affiliatesInfoEntity = new AffiliatesInfoEntity();
                affiliatesInfoEntity.setAffiliateTypes("2");
                affiliatesInfoEntity.setSiteId(save.getId());
                affiliatesInfoEntity.setTenantId(siteChangeVo.getPropertyId());
                affiliatesInfoDao.save(affiliatesInfoEntity);

                //新增运营商关联信息
                AffiliatesInfoEntity affiliatesInfoEntity1 = new AffiliatesInfoEntity();
                affiliatesInfoEntity1.setAffiliateTypes("3");
                affiliatesInfoEntity1.setSiteId(save.getId());
                affiliatesInfoEntity1.setTenantId(siteChangeVo.getOperatorId());
                affiliatesInfoDao.save(affiliatesInfoEntity1);
            }

            //添加站点白名单模式默认值
            togetherService.saveRosterMode(save.getId(), 2);

            //添加站点设置默认值
            SiteSetUpEntity siteSetUpEntity = new SiteSetUpEntity();
            siteSetUpEntity.setSiteId(save.getId());
            siteSetUpEntity.setOperatePassword("0000");
            siteSetUpEntity.setAppShow(1);
            siteSetUpEntity.setCreateTime(LocalDateTime.now());
            siteSetUpEntity.setPvQtSource(20);
            siteSetUpEntity.setReduceCoeff(0.4750);
            siteSetUpEntity.setTceCoeff(0.4000);
            siteSetUpEntity.setTreeCoeff(18.3000);
            siteSetUpDao.save(siteSetUpEntity);
        } else {//编辑站点
            Optional<SiteInfoEntity> siteInfoDaoById = siteInfoDao.findById(siteChangeVo.getId());
            if (siteInfoDaoById.isPresent()) {
                SiteInfoEntity siteInfoEntity = new SiteInfoEntity();
                BeanUtils.copyProperties(siteChangeVo, siteInfoEntity);
                siteInfoEntity.setCreateId(siteInfoDaoById.get().getCreateId());
                siteInfoEntity.setCreateTime(siteInfoDaoById.get().getCreateTime());
                siteInfoEntity.setUpdateId(siteChangeVo.getUserId());
                siteInfoEntity.setUpdateTime(LocalDateTime.now());
                siteInfoEntity.setIsDelete(siteInfoDaoById.get().getIsDelete());
                siteInfoDao.save(siteInfoEntity);
            }
        }
        return ResponseResult.ok();
    }

    /**
     * 分页查询站点列表
     *
     * @param siteQueryVo
     * @param userId
     * @return
     */
    @Override
    public ResponseResult<PageDto<SitePageDto>> querySiteListByPage(SiteQueryVo siteQueryVo, String userId) {
        List<SitePageDto> resultList = Lists.newArrayList();
        //当前用户如果是平台管理员则查询全部站点数据
        UserDto loginUserDto = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
        if (StringUtil.isNotEmpty(loginUserDto)) {
            List<SiteInfoEntity> siteEntityList = findSitesForUser(loginUserDto.getUserRole(), userId);
            Integer keywordType = siteQueryVo.getKeywordType();
            String keyword = siteQueryVo.getKeyword();
            //根据关键字查询
            if (StringUtil.isNotEmpty(keywordType) && StringUtil.isNotEmpty(keyword)) {
                Map<Integer, Predicate<SiteInfoEntity>> filters = new HashMap<>();
                filters.put(1, s -> s.getSiteName().contains(keyword)); // 站点名称查询
                filters.put(2, s -> s.getId().contains(keyword));       // 站点id查询

                siteEntityList = siteEntityList.stream()
                        .filter(filters.getOrDefault(keywordType, s -> false))
                        .collect(Collectors.toList());
            }
            if (CollectionUtils.isNotEmpty(siteEntityList)) {
                //获取多个租户id，查询租户信息
                Map<String, TenantDetailsDto> tenantDetailsDtoMap = Maps.newHashMap();
                ResponseResult<List<TenantDetailsDto>> tenantDetailsByIds = systemService.findTenantDetailsByIds(siteEntityList.stream().map(SiteInfoEntity::getTenantId).distinct().collect(Collectors.toList()));
                if (tenantDetailsByIds.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsByIds.getData())) {
                    tenantDetailsDtoMap = tenantDetailsByIds.getData().stream().collect(Collectors.toMap(TenantDetailsDto::getId, tenantDetailsDto -> tenantDetailsDto));
                }
                //查询修改人信息
                Map<String, UserDto> userDtoMap = systemService.findUserInfoByIdsFeign(siteEntityList.stream().map(SiteInfoEntity::getUpdateId).distinct().collect(Collectors.toList())).getData();
                Map<String, TenantDetailsDto> finalTenantDetailsDtoMap = tenantDetailsDtoMap;
                resultList = siteEntityList.stream().map(siteInfoEntity -> {
                    SitePageDto sitePageDto = new SitePageDto();
                    BeanUtils.copyProperties(siteInfoEntity, sitePageDto);
                    //获取修改人名称
                    if (userDtoMap.containsKey(siteInfoEntity.getUpdateId())) {
                        sitePageDto.setUpdateName(userDtoMap.get(siteInfoEntity.getUpdateId()).getFullName());
                    }
                    //获取租户名称
                    if (finalTenantDetailsDtoMap.containsKey(siteInfoEntity.getTenantId())) {
                        sitePageDto.setTenantName(finalTenantDetailsDtoMap.get(siteInfoEntity.getTenantId()).getTenantName());
                    }
                    return sitePageDto;
                }).collect(Collectors.toList());
                //根据创建时间降序排序
                resultList = resultList.stream().sorted(Comparator.comparing(SitePageDto::getCreateTime).reversed()).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(new PageDto<>(resultList, siteQueryVo.getPage(), siteQueryVo.getSize()));
    }

    /**
     * 根据用户角色查询站点列表
     *
     * @param userRole
     * @param userId
     * @return
     */
    private List<SiteInfoEntity> findSitesForUser(Integer userRole, String userId) {
        if (userRole.equals(0)) {
            return siteInfoDao.findAllByIsDelete(1);
        } else {
            ResponseResult<List<OrganEmpowerListDto>> response = systemService.findAllOrganEmpowerByUserId(userId);
            if (response.isSuccess()) {
                List<OrganEmpowerListDto> organEmpowerList = response.getData();
                if (!organEmpowerList.isEmpty()) {
                    List<String> siteIds = organEmpowerList.stream()
                            .map(OrganEmpowerListDto::getSiteId)
                            .distinct()
                            .collect(Collectors.toList());
                    return siteInfoDao.findAllByIdInAndIsDelete(siteIds, 1);
                }
            }
            return Collections.emptyList();
        }
    }

    /**
     * 根据站点id查询基本详情数据
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult<SiteInfoDto> findSiteInfoById(String id) {
        SiteInfoDto result = new SiteInfoDto();
        //根据站点id查询站点信息
        Optional<SiteInfoEntity> siteInfoEntityOptional = siteInfoDao.findById(id);
        if (siteInfoEntityOptional.isPresent()) {
            SiteInfoEntity siteInfoEntity = siteInfoEntityOptional.get();
            BeanUtils.copyProperties(siteInfoEntity, result);
            //站点读写对象值
            String siteReadwriteObject = siteInfoEntity.getSiteReadwriteObject();
            if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                //获取读写字段扩展属性值
                Map<String, Object> readwriteMap = Maps.newHashMap();
                if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                    readwriteMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                    });
                }
                //根据模型id查询所有模型扩展属性id
                Map<String, ModelReaEntity> modelReaMap = modelReaDao.findAllByModelIdIn(Collections.singletonList(siteInfoEntity.getSiteModelId())).stream().collect(Collectors.toMap(ModelReaEntity::getReaId,
                        modelReaEntity -> modelReaEntity, (k1, k2) -> k2));
                Map<String, Object> finalReadwriteMap = readwriteMap;
                result.setSiteReaList(reaDao.findAllById(modelReaMap.keySet()).stream().map(rea -> {
                    DeviceReaDto deviceReaDto = new DeviceReaDto();
                    BeanUtils.copyProperties(rea, deviceReaDto);
                    deviceReaDto.setReaId(rea.getId());
                    //设备实时值
                    deviceReaDto.setValue(finalReadwriteMap.get(rea.getFieldName()));
                    return deviceReaDto;
                }).collect(Collectors.toList()));
            }
            //查询租户信息
            ResponseResult<List<TenantDetailsDto>> tenantDetailsByIds = systemService.findTenantDetailsByIds(Collections.singletonList(siteInfoEntity.getTenantId()));
            if (tenantDetailsByIds.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsByIds.getData())) {
                TenantDetailsDto tenantDetailsDto = tenantDetailsByIds.getData().get(0);
                result.setTenantName(tenantDetailsDto.getTenantName());
            }
            //查询创建人和修改人信息
            Map<String, UserDto> userDtoMap = systemService.findUserInfoByIdsFeign(Arrays.asList(siteInfoEntity.getCreateId(), siteInfoEntity.getUpdateId())).getData();
            if (StringUtil.isNotEmpty(siteInfoEntity.getCreateId()) && userDtoMap.containsKey(siteInfoEntity.getCreateId())) {
                result.setCreateName(userDtoMap.getOrDefault(siteInfoEntity.getCreateId(), null).getFullName());
            }
            if (StringUtil.isNotEmpty(siteInfoEntity.getUpdateId()) && userDtoMap.containsKey(siteInfoEntity.getUpdateId())) {
                result.setUpdateName(userDtoMap.getOrDefault(siteInfoEntity.getUpdateId(), null).getFullName());
            }
            //查询当前站点下能源场景信息
            List<ScenarioTypeEntity> scenarioTypeEntityList = scenarioTypeDao.findAllBySiteIdIn(Collections.singletonList(id));
            if (CollectionUtils.isNotEmpty(scenarioTypeEntityList)) {
                //存储能源场景信息
                List<SiteScenarioTypeDto> siteScenarioTypeDtos = Lists.newArrayList();
                List<String> modelIdList = scenarioTypeEntityList.stream().map(ScenarioTypeEntity::getModelId).collect(Collectors.toList());
                //根据多个模型id，查询模型信息
                Map<String, ModelEntity> modelEntityMap = modelDao.findAllById(modelIdList).stream().collect(Collectors.toMap(ModelEntity::getId, modelEntity -> modelEntity, (k1, k2) -> k2));
                //根据模型id查询所有模型扩展属性id
                Map<String, List<ModelReaEntity>> groupByModeIdMap = modelReaDao.findAllByModelIdIn(modelIdList).stream().collect(Collectors.groupingBy(ModelReaEntity::getModelId));
                //循环能源场景信息，获取扩展属性读写字段值
                scenarioTypeEntityList.forEach(scenarioTypeEntity -> {
                    SiteScenarioTypeDto siteScenarioTypeDto = new SiteScenarioTypeDto();
                    BeanUtils.copyProperties(scenarioTypeEntity, siteScenarioTypeDto);
                    //获取模型名称
                    if (modelEntityMap.containsKey(scenarioTypeEntity.getModelId())) {
                        siteScenarioTypeDto.setModelName(modelEntityMap.get(scenarioTypeEntity.getModelId()).getModelName());
                    }
                    //读写对象值
                    String readwriteObject = scenarioTypeEntity.getReadwriteObject();
                    if (StringUtil.isNotEmpty(readwriteObject)) {
                        //获取读写字段扩展属性值
                        Map<String, Object> readwriteMap = Maps.newHashMap();
                        if (StringUtil.isNotEmpty(readwriteObject)) {
                            readwriteMap = JSON.parseObject(readwriteObject, new TypeReference<Map<String, Object>>() {
                            });
                        }
                        if (groupByModeIdMap.containsKey(scenarioTypeEntity.getModelId())) {
                            //获取模型扩展属性id
                            Map<String, ModelReaEntity> modelReaMap = groupByModeIdMap.get(scenarioTypeEntity.getModelId()).stream().collect(Collectors.toMap(ModelReaEntity::getReaId,
                                    modelReaEntity -> modelReaEntity, (k1, k2) -> k2));
                            Map<String, Object> finalReadwriteMap = readwriteMap;
                            siteScenarioTypeDto.setReaList(reaDao.findAllById(modelReaMap.keySet()).stream().map(rea -> {
                                DeviceReaDto deviceReaDto = new DeviceReaDto();
                                BeanUtils.copyProperties(rea, deviceReaDto);
                                deviceReaDto.setReaId(rea.getId());
                                //设备实时值
                                deviceReaDto.setValue(finalReadwriteMap.get(rea.getFieldName()));
                                return deviceReaDto;
                            }).collect(Collectors.toList()));
                        }
                    }
                    siteScenarioTypeDtos.add(siteScenarioTypeDto);
                });
                result.setSiteScenarioTypeDtos(siteScenarioTypeDtos);
            }
        }
        return ResponseResult.ok(result);
    }

    /**
     * 根据站点id删除站点相关信息
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<String> deleteSiteInfoById(String id) {
        //根据站点id，查询站点信息，并修改伪删除状态为已删除
        Optional<SiteInfoEntity> siteInfoById = siteInfoDao.findById(id);
        if (siteInfoById.isPresent()) {
            SiteInfoEntity siteInfoEntity = siteInfoById.get();
            siteInfoEntity.setIsDelete(2);
            siteInfoDao.save(siteInfoEntity);
            //根据站点id，查询出站点下所有设备信息，并修改伪删除状态为已删除
            List<DeviceEntity> deviceEntityList = deviceDao.findAllBySiteIdAndIsDelete(id, 1);
            if (CollectionUtils.isNotEmpty(deviceEntityList)) {
                deviceDao.saveAll(deviceEntityList.stream().peek(d -> d.setIsDelete(2)).collect(Collectors.toList()));
            }
            //删除所有关联该站点的资产授权数据
            systemService.deleteAllOrganEmpowerBySiteId(id);
            //删除用户分组中所有关联该站点的关联数据
            togetherService.deleteAllGroupBeSiteBySiteId(id);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 新增或编辑站点能源场景信息数据
     *
     * @param scenarioTypeChangeVo
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveOrUpdateSiteScenarioType(ScenarioTypeChangeVo scenarioTypeChangeVo) {
        //新增或编辑能源信息
        ScenarioTypeEntity scenarioTypeEntity = new ScenarioTypeEntity();
        BeanUtils.copyProperties(scenarioTypeChangeVo, scenarioTypeEntity);
        scenarioTypeDao.save(scenarioTypeEntity);
        //如果是新增，则修改站点能源场景字段
        if (StringUtil.isEmpty(scenarioTypeChangeVo.getId())) {
            Optional<SiteInfoEntity> siteInfoEntityOptional = siteInfoDao.findById(scenarioTypeChangeVo.getSiteId());
            if (siteInfoEntityOptional.isPresent()) {
                SiteInfoEntity siteInfoEntity = siteInfoEntityOptional.get();
                SiteInfoEntity siteInfo = new SiteInfoEntity();
                BeanUtils.copyProperties(siteInfoEntity, siteInfo);
                if (StringUtil.isNotEmpty(scenarioTypeChangeVo.getScenarioType())) {
                    if (StringUtil.isNotEmpty(siteInfoEntity.getScenarioTypes())) {
                        List<Integer> scenarioTypes = Arrays.stream(siteInfoEntity.getScenarioTypes().split(FileUtil.COMMA))
                                .filter(StringUtil::isNotEmpty) // 过滤掉空字符串
                                .map(s -> Integer.parseInt(s.trim()))      // 去除两边空格
                                .collect(Collectors.toList());
                        if (!scenarioTypes.contains(scenarioTypeChangeVo.getScenarioType())) {
                            scenarioTypes.add(scenarioTypeChangeVo.getScenarioType());
                        }
                        siteInfo.setScenarioTypes(StringUtils.join(scenarioTypes, FileUtil.COMMA));
                    } else {
                        siteInfo.setScenarioTypes(String.valueOf(scenarioTypeChangeVo.getScenarioType()));
                    }
                }
                siteInfoDao.save(siteInfo);
            }
        }
        return ResponseResult.ok();
    }

    /**
     * 根据能源场景id删除指定能源信息
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<String> deleteSiteScenarioTypeById(String id) {
        //先查询出要删除的能源场景信息
        Optional<ScenarioTypeEntity> optional = scenarioTypeDao.findById(id);
        if (optional.isPresent()) {
            ScenarioTypeEntity scenarioTypeEntity = optional.get();

            //根据站点id查询所有能源场景信息
            List<ScenarioTypeEntity> scenarioTypeList = scenarioTypeDao.findAllBySiteIdIn(Collections.singleton(scenarioTypeEntity.getSiteId()));
            //移除当前需要删除的站点数据
            scenarioTypeList.remove(scenarioTypeEntity);

            //根据能源信息id删除能源信息
            scenarioTypeDao.deleteById(id);

            //根据能源场景类型存到站点信息里面
            Optional<SiteInfoEntity> siteOptional = siteInfoDao.findById(scenarioTypeEntity.getSiteId());
            if (siteOptional.isPresent()) {
                SiteInfoEntity siteInfo = siteOptional.get();
                siteInfo.setScenarioTypes(null);
                if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                    siteInfo.setScenarioTypes(StringUtils.join(scenarioTypeList.stream().map(ScenarioTypeEntity::getScenarioType).collect(Collectors.toSet()), FileUtil.COMMA));
                }
                siteInfoDao.save(siteInfo);
            }
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 新增或编辑关联方信息
     *
     * @param affiliatesChangeVo
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateAffiliatesInfo(AffiliatesChangeVo affiliatesChangeVo) {
        if (StringUtil.isEmpty(affiliatesChangeVo.getId())) {//新增关联方
            //根据租户id和站点id查询是否已经关联过
            AffiliatesInfoEntity existAffiliatesInfoEntity = affiliatesInfoDao.findByTenantIdAndSiteId(affiliatesChangeVo.getTenantId(), affiliatesChangeVo.getSiteId());
            if (StringUtil.isNotEmpty(existAffiliatesInfoEntity)) {
                return ResponseResult.paramShow("关联失败，不能重复关联！");
            }
            //判断当前编辑信息中是否包含产权单位或运营单位，如果包含则查询数据库中是否已经存在产权单位或运营单位信息，如果存在则不允许新增
            if (affiliatesChangeVo.getAffiliateTypes().contains("2") || affiliatesChangeVo.getAffiliateTypes().contains("3")) {
                List<AffiliatesInfoEntity> allBySiteId = affiliatesInfoDao.findAllBySiteId(affiliatesChangeVo.getSiteId());
                //判断产权单位
                if (affiliatesChangeVo.getAffiliateTypes().contains("2")) {
                    List<AffiliatesInfoEntity> propertyInfoEntityList = allBySiteId.stream().filter(a -> a.getAffiliateTypes().contains("2")).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(propertyInfoEntityList)) {
                        return ResponseResult.paramShow(affiliatesChangeVo.getAffiliateTypes(), "新增失败，已存在产权单位，不能重复添加，请去编辑产权单位信息！");
                    }
                }
                //判断运营单位
                if (affiliatesChangeVo.getAffiliateTypes().contains("3")) {
                    List<AffiliatesInfoEntity> operationInfoEntityList = allBySiteId.stream().filter(a -> a.getAffiliateTypes().contains("3")).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(operationInfoEntityList)) {
                        return ResponseResult.paramShow(affiliatesChangeVo.getAffiliateTypes(), "新增失败，已存在运营单位，不能重复添加，请去编辑运营单位信息！");
                    }
                }
            }
            AffiliatesInfoEntity affiliatesEntity = new AffiliatesInfoEntity();
            BeanUtils.copyProperties(affiliatesChangeVo, affiliatesEntity);
            affiliatesInfoDao.save(affiliatesEntity);
            //判断是否包含运营单位和产权单位，如果包含则说明当前新增关联的所属站点没有配置运营单位或产权单位，所以查询出站点信息，并把运营单位id或产权单位id赋值给站点信息中
            //判断是否包含运营单位或产权单位
            if (affiliatesChangeVo.getAffiliateTypes().contains("2") || affiliatesChangeVo.getAffiliateTypes().contains("3")) {
                Optional<SiteInfoEntity> siteInfoDaoById = siteInfoDao.findById(affiliatesChangeVo.getSiteId());
                if (siteInfoDaoById.isPresent()) {
                    SiteInfoEntity siteInfoEntity = siteInfoDaoById.get();
                    if (affiliatesChangeVo.getAffiliateTypes().contains("2")) {
                        siteInfoEntity.setPropertyId(affiliatesChangeVo.getTenantId());
                    }
                    if (affiliatesChangeVo.getAffiliateTypes().contains("3")) {
                        siteInfoEntity.setOperatorId(affiliatesChangeVo.getTenantId());
                    }
                    siteInfoDao.save(siteInfoEntity);
                }
            }
        } else {
            //判断当前编辑信息中是否包含产权单位或运营单位，如果包含则查询数据库中的产权单位或运营单位信息，并和当前编辑id比对是否相同，如果相同则让编辑，不相同返回编辑失败
            if (affiliatesChangeVo.getAffiliateTypes().contains("2") || affiliatesChangeVo.getAffiliateTypes().contains("3")) {
                List<AffiliatesInfoEntity> allBySiteId = affiliatesInfoDao.findAllBySiteId(affiliatesChangeVo.getSiteId());
                //判断产权单位
                if (affiliatesChangeVo.getAffiliateTypes().contains("2")) {
                    AffiliatesInfoEntity affiliatesInfoEntity = allBySiteId.stream().filter(a -> a.getAffiliateTypes().contains("2")).collect(Collectors.toList()).get(0);
                    if (!affiliatesInfoEntity.getId().equals(affiliatesChangeVo.getId())) {
                        return ResponseResult.paramShow(affiliatesChangeVo.getId(), "修改失败，已存在产权单位，不能重复！");
                    }
                }
                //判断运营单位
                if (affiliatesChangeVo.getAffiliateTypes().contains("3")) {
                    AffiliatesInfoEntity affiliatesInfoEntity = allBySiteId.stream().filter(a -> a.getAffiliateTypes().contains("3")).collect(Collectors.toList()).get(0);
                    if (!affiliatesInfoEntity.getId().equals(affiliatesChangeVo.getId())) {
                        return ResponseResult.paramShow(affiliatesChangeVo.getId(), "修改失败，已存在运营单位，不能重复！");
                    }
                }
            }
            Optional<AffiliatesInfoEntity> affiliatesInfoDaoById = affiliatesInfoDao.findById(affiliatesChangeVo.getId());
            if (affiliatesInfoDaoById.isPresent()) {
                AffiliatesInfoEntity affiliatesInfoEntity = affiliatesInfoDaoById.get();
                //判断当前编辑信息中是否包含产权单位或运营单位，如果包含则修改并修改站点信息表中相关产权和运营id字段
                if (affiliatesChangeVo.getAffiliateTypes().contains("2") || affiliatesChangeVo.getAffiliateTypes().contains("3")) {
                    Optional<SiteInfoEntity> siteInfoDaoById = siteInfoDao.findById(affiliatesChangeVo.getSiteId());
                    if (siteInfoDaoById.isPresent()) {
                        SiteInfoEntity siteInfoEntity = siteInfoDaoById.get();
                        //产权单位
                        if (affiliatesChangeVo.getAffiliateTypes().contains("2")) {
                            siteInfoEntity.setPropertyId(affiliatesChangeVo.getTenantId());
                        }
                        //运营单位
                        if (affiliatesChangeVo.getAffiliateTypes().contains("3")) {
                            siteInfoEntity.setOperatorId(affiliatesChangeVo.getTenantId());
                        }
                        siteInfoDao.save(siteInfoEntity);
                    }
                }
                BeanUtils.copyProperties(affiliatesChangeVo, affiliatesInfoEntity);
                affiliatesInfoDao.save(affiliatesInfoEntity);
            }
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    //资产授权操作
//    private void organEmpowerOperate(AffiliatesChangeVo affiliatesChangeVo) {
//        //查询当前租户下所有资产授权
//        Optional<List<OrganEmpowerListDto>> organEmpowerListOpt = Optional.ofNullable(systemService.findOrganEmpowerListByTenantId(affiliatesChangeVo.getTenantId()).getData());
//
//        if (organEmpowerListOpt.isPresent()) {
//            //过滤出当前站点资产授权
//            List<OrganEmpowerListDto> organEmpowerListDtos = organEmpowerListOpt.get().stream()
//                    .filter(o -> o.getSiteId().equals(affiliatesChangeVo.getSiteId()))
//                    .collect(Collectors.toList());
//            //新增/修改/删除资产授权信息
//            handleOrganEmpower(organEmpowerListDtos, affiliatesChangeVo);
//        } else {
//            addNewOrganEmpower(affiliatesChangeVo);
//        }
//    }
//
//    private void handleOrganEmpower(List<OrganEmpowerListDto> organEmpowerListDtos, AffiliatesChangeVo affiliatesChangeVo) {
//        Integer authorityType = affiliatesChangeVo.getAuthorityType();
//        //如果当前关联的权限为0，则删除租户下所有指定站点资产授权信息
//        if (authorityType == 0 && CollectionUtils.isNotEmpty(organEmpowerListDtos)) {
//            //根据租户id删除租户下所有指定站点资产授权信息
//            organEmpowerListDtos.forEach(o -> systemService.deleteOrganEmpowerByTenantId(affiliatesChangeVo.getTenantId(), affiliatesChangeVo.getSiteId()));
//        } else {
//            //如果当前关联的权限不为0，则新增/修改租户下指定站点资产授权信息
//            if (CollectionUtils.isNotEmpty(organEmpowerListDtos)) {
//                systemService.updateOrganEmpowerByTenantId(affiliatesChangeVo.getTenantId(), affiliatesChangeVo.getSiteId(), authorityType);
//            } else {
//                addNewOrganEmpower(affiliatesChangeVo);
//            }
//        }
//    }
//
//    //新增资产授权信息
//    private void addNewOrganEmpower(AffiliatesChangeVo affiliatesChangeVo) {
//        //查询出当前关联方租户本身的组织结构
//        List<OrganStructureTreeDto> organStructureTreeDtoList = systemService.findOrganStructureByTenantId(affiliatesChangeVo.getTenantId()).getData();
//        Optional<OrganStructureTreeDto> organStructureOpt = organStructureTreeDtoList.stream()
//                .filter(o -> StringUtil.isEmpty(o.getParentId()))
//                .findFirst();
//
//        organStructureOpt.ifPresent(rootOrgan -> systemService.addOrganEmpowerInfo(
//                affiliatesChangeVo.getSiteId(),
//                affiliatesChangeVo.getAuthorityType(),
//                rootOrgan.getId(),
//                affiliatesChangeVo.getTenantId()
//        ));
//    }

    /**
     * 根据站点id查询关联方列表信息
     *
     * @param siteId
     * @param keywordType
     * @param keyword
     * @return
     */
    @Override
    public ResponseResult<List<AffiliatesInfoDto>> findAffiliatesListBySiteId(String siteId, Integer keywordType, String keyword) {
        List<AffiliatesInfoDto> resultList = Lists.newArrayList();
        //根据站点id，查询所有关联方信息
        List<AffiliatesInfoEntity> affiliatesInfoEntityList = affiliatesInfoDao.findAllBySiteId(siteId);
        if (CollectionUtils.isNotEmpty(affiliatesInfoEntityList)) {
            //获取所有关联方信息
            Map<String, TenantDetailsDto> tenantDetailsDtoMap = Maps.newHashMap();
            ResponseResult<List<TenantDetailsDto>> tenantDetailsResult = systemService.findTenantDetailsByIds(affiliatesInfoEntityList.stream().map(AffiliatesInfoEntity::getTenantId).collect(Collectors.toList()));
            if (tenantDetailsResult.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsResult.getData())) {
                tenantDetailsDtoMap = tenantDetailsResult.getData().stream().collect(Collectors.toMap(TenantDetailsDto::getId, tenantDetailsDto -> tenantDetailsDto, (k1, k2) -> k1));
            }
            Map<String, TenantDetailsDto> finalTenantDetailsDtoMap = tenantDetailsDtoMap;
            resultList = affiliatesInfoEntityList.stream().map(affiliatesInfoEntity -> {
                AffiliatesInfoDto affiliatesInfoDto = new AffiliatesInfoDto();
                BeanUtils.copyProperties(affiliatesInfoEntity, affiliatesInfoDto);
                //获取租户信息
                if (!finalTenantDetailsDtoMap.isEmpty() && finalTenantDetailsDtoMap.containsKey(affiliatesInfoEntity.getTenantId())) {
                    affiliatesInfoDto.setTenantName(finalTenantDetailsDtoMap.get(affiliatesInfoEntity.getTenantId()).getTenantName());
                }
                return affiliatesInfoDto;
            }).collect(Collectors.toList());
            //根据关键字类型和关键字进行过滤
            if (StringUtil.isNotEmpty(keywordType) && StringUtil.isNotEmpty(keyword)) {
                if (keywordType == 1) {//根据企业名称查询
                    resultList = resultList.stream().filter(a -> a.getTenantName().contains(keyword)).collect(Collectors.toList());
                }
            }
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 根据关联方id删除指定关联方信息
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<String> deleteAffiliatesInfoById(String id) {
        affiliatesInfoDao.deleteById(id);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据用户id查询站点列表信息
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult<List<SiteInfoDto>> findSiteInfoListByUserId(String userId) {
        List<SiteInfoDto> resultList = Lists.newArrayList();
        //当前用户如果是平台管理员则查询全部站点数据
        UserDto loginUserDto = systemService.findUserInfoByIdsFeign(Collections.singletonList(userId)).getData().get(userId);
        if (StringUtil.isNotEmpty(loginUserDto)) {
            List<SiteInfoEntity> siteEntityList = findSitesForUser(loginUserDto.getUserRole(), userId);
            if (CollectionUtils.isNotEmpty(siteEntityList)) {
                //获取多个租户id，查询租户信息
                Map<String, TenantDetailsDto> tenantDetailsDtoMap = Maps.newHashMap();
                ResponseResult<List<TenantDetailsDto>> tenantDetailsByIds = systemService.findTenantDetailsByIds(siteEntityList.stream().map(SiteInfoEntity::getTenantId).distinct().collect(Collectors.toList()));
                if (tenantDetailsByIds.isSuccess() && CollectionUtils.isNotEmpty(tenantDetailsByIds.getData())) {
                    tenantDetailsDtoMap = tenantDetailsByIds.getData().stream().collect(Collectors.toMap(TenantDetailsDto::getId, tenantDetailsDto -> tenantDetailsDto));
                }
                //查询所有站点下关联能源场景信息
                List<ScenarioTypeEntity> scenarioTypeEntities = scenarioTypeDao.findAllBySiteIdIn(siteEntityList.stream().map(SiteInfoEntity::getId).collect(Collectors.toList()));
                Map<String, List<ScenarioTypeEntity>> groupBySiteIdMap = scenarioTypeEntities.stream().collect(Collectors.groupingBy(ScenarioTypeEntity::getSiteId));
                //获取多个站点模型id以及关联场景类型模型id
                List<String> siteModelIdList = siteEntityList.stream().map(SiteInfoEntity::getSiteModelId).distinct().collect(Collectors.toList());
                List<String> modeIdList = scenarioTypeEntities.stream().map(ScenarioTypeEntity::getModelId).distinct().collect(Collectors.toList());
                siteModelIdList.addAll(modeIdList);
                //查询模型扩展属性信息
                Map<String, List<ModelReaEntity>> groupByModelIdMap = modelReaDao.findAllByModelIdIn(siteModelIdList).stream().collect(Collectors.groupingBy(ModelReaEntity::getModelId));
                Map<String, TenantDetailsDto> finalTenantDetailsDtoMap = tenantDetailsDtoMap;
                resultList = siteEntityList.stream().map(siteInfoEntity -> {
                    SiteInfoDto siteInfoDto = new SiteInfoDto();
                    BeanUtils.copyProperties(siteInfoEntity, siteInfoDto);
                    //获取租户名称
                    if (finalTenantDetailsDtoMap.containsKey(siteInfoEntity.getTenantId())) {
                        siteInfoDto.setTenantName(finalTenantDetailsDtoMap.get(siteInfoEntity.getTenantId()).getTenantName());
                    }
                    //站点读写对象值
                    String siteReadwriteObject = siteInfoEntity.getSiteReadwriteObject();
                    if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                        //获取读写字段扩展属性值
                        Map<String, Object> readwriteMap = Maps.newHashMap();
                        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                            readwriteMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                            });
                        }
                        //根据模型id查询所有模型扩展属性id
                        if (!groupByModelIdMap.isEmpty() && groupByModelIdMap.containsKey(siteInfoEntity.getSiteModelId())) {
                            Map<String, ModelReaEntity> modelReaMap = groupByModelIdMap.get(siteInfoEntity.getSiteModelId()).stream().collect(Collectors.toMap(ModelReaEntity::getReaId,
                                    modelReaEntity -> modelReaEntity, (k1, k2) -> k2));
                            Map<String, Object> finalReadwriteMap = readwriteMap;
                            siteInfoDto.setSiteReaList(reaDao.findAllById(modelReaMap.keySet()).stream().map(rea -> {
                                DeviceReaDto deviceReaDto = new DeviceReaDto();
                                BeanUtils.copyProperties(rea, deviceReaDto);
                                deviceReaDto.setReaId(rea.getId());
                                //设备实时值
                                deviceReaDto.setValue(finalReadwriteMap.get(rea.getFieldName()));
                                return deviceReaDto;
                            }).collect(Collectors.toList()));
                        }
                    }
                    //获取站点下关联能源场景列表
                    if (groupBySiteIdMap.containsKey(siteInfoEntity.getId())) {
                        //存储能源场景信息
                        List<SiteScenarioTypeDto> siteScenarioTypeDtos = Lists.newArrayList();
                        groupBySiteIdMap.get(siteInfoEntity.getId()).forEach(scenarioTypeEntity -> {
                            SiteScenarioTypeDto siteScenarioTypeDto = new SiteScenarioTypeDto();
                            BeanUtils.copyProperties(scenarioTypeEntity, siteScenarioTypeDto);
                            //读写对象值
                            String readwriteObject = scenarioTypeEntity.getReadwriteObject();
                            if (StringUtil.isNotEmpty(readwriteObject)) {
                                //获取读写字段扩展属性值
                                Map<String, Object> readwriteMap = Maps.newHashMap();
                                if (StringUtil.isNotEmpty(readwriteObject)) {
                                    readwriteMap = JSON.parseObject(readwriteObject, new TypeReference<Map<String, Object>>() {
                                    });
                                }
                                //获取模型扩展属性id
                                Map<String, ModelReaEntity> modelReaMap = groupByModelIdMap.get(scenarioTypeEntity.getModelId()).stream().collect(Collectors.toMap(ModelReaEntity::getReaId,
                                        modelReaEntity -> modelReaEntity, (k1, k2) -> k2));
                                Map<String, Object> finalReadwriteMap = readwriteMap;
                                siteScenarioTypeDto.setReaList(reaDao.findAllById(modelReaMap.keySet()).stream().map(rea -> {
                                    DeviceReaDto deviceReaDto = new DeviceReaDto();
                                    BeanUtils.copyProperties(rea, deviceReaDto);
                                    deviceReaDto.setReaId(rea.getId());
                                    //设备实时值
                                    deviceReaDto.setValue(finalReadwriteMap.get(rea.getFieldName()));
                                    return deviceReaDto;
                                }).collect(Collectors.toList()));
                            }
                            siteScenarioTypeDtos.add(siteScenarioTypeDto);
                        });
                        siteInfoDto.setSiteScenarioTypeDtos(siteScenarioTypeDtos);
                    }
                    return siteInfoDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
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
        List<CityDto> cityDtoList = com.google.common.collect.Lists.newArrayList();
        List<CityEntity> cityEntityList = cityDao.findAllByProvinceIdIn(com.google.common.collect.Lists.newArrayList(provinceId));
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
        List<AreaDto> areaDtoList = com.google.common.collect.Lists.newArrayList();
        List<AreaEntity> areaEntityList = areaDao.findAllByCityIdIn(com.google.common.collect.Lists.newArrayList(cityId));
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
            result.setProvinceName(addressMap.get(StaticParamVo.PROVINCE_NAME));
            if (StringUtil.isNotEmpty(addressMap.get(StaticParamVo.CITY_NAME))) {
                result.setCityName(addressMap.get(StaticParamVo.CITY_NAME));
            } else {
                result.setCityName(addressMap.get(StaticParamVo.PROVINCE_NAME));
            }
            result.setAreaName(addressMap.get(StaticParamVo.AREA_NAME));
            result.setAddress(addressMap.get(StaticParamVo.ADDRESS_NAME));
        }
        return ResponseResult.ok(result);
    }

    /**
     * 根据站点id操作站点图片
     *
     * @param id
     * @param deleteImagePaths
     * @param imageFiles
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<String> updateSiteImageById(String id, String deleteImagePaths, MultipartFile[] imageFiles) {
        Optional<SiteInfoEntity> siteInfoDaoById = siteInfoDao.findById(id);
        if (siteInfoDaoById.isPresent()) {
            SiteInfoEntity siteInfoEntity = siteInfoDaoById.get();
            String imagePath = siteInfoEntity.getImagePath();
            List<String> deleteImagePathList = com.google.common.collect.Lists.newArrayList();
            //先处理删除图片数据
            if (StringUtil.isNotEmpty(deleteImagePaths)) {
                deleteImagePathList = Arrays.stream(deleteImagePaths.split(",")).map(String::trim).collect(Collectors.toList());
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
            siteInfoEntity.setImagePath(imagePath);
            siteInfoDao.save(siteInfoEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 批量新增或编辑互联互通站点数据
     *
     * @param siteInfoChangeVos
     * @return
     */
    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveOrUpdateInterflowSite(List<SiteInfoChangeVo> siteInfoChangeVos) {
        //根据站点id查询站点数据
        List<SiteInfoEntity> siteInfoEntities = siteInfoDao.findAllById(siteInfoChangeVos.stream().map(SiteInfoChangeVo::getId).collect(Collectors.toList()));
        if (CollectionUtils.isNotEmpty(siteInfoEntities)) {
            //过滤出已存在站点数据
            List<SiteInfoChangeVo> siteInfoChangeVoList = siteInfoChangeVos.stream().filter(siteInfoChangeVo -> siteInfoEntities.stream().map(SiteInfoEntity::getId).collect(Collectors.toList()).contains(siteInfoChangeVo.getId())).collect(Collectors.toList());
            //如果查询出有数据，则直接赋值并修改查询出的站点信息
            siteInfoDao.saveAll(siteInfoChangeVoList.stream().map(siteInfoChangeVo -> {
                SiteInfoEntity siteInfoEntity = new SiteInfoEntity();
                BeanUtils.copyProperties(siteInfoChangeVo, siteInfoEntity);
                siteInfoEntity.setUpdateTime(LocalDateTime.now());
                return siteInfoEntity;
            }).collect(Collectors.toList()));
            //从站点信息参数中，过滤掉当前已做编辑操作的站点
            siteInfoChangeVos = siteInfoChangeVos.stream().filter(siteInfoChangeVo -> !siteInfoEntities.stream().map(SiteInfoEntity::getId).collect(Collectors.toList()).contains(siteInfoChangeVo.getId())).collect(Collectors.toList());
        }
        //如果siteInfoChangeVos参数还有数据，则全部走sql插入
        if (CollectionUtils.isNotEmpty(siteInfoChangeVos)) {
            siteInfoMapper.batchInsertSiteInfoList(siteInfoChangeVos);
        }
        return ResponseResult.ok();
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<String> updateSiteSetUp(SiteSetUpChangeVo siteSetUpChangeVo) {
        if (StringUtil.isNotEmpty(siteSetUpChangeVo)) {
            SiteSetUpEntity siteSetUpEntity = new SiteSetUpEntity();
            if (StringUtil.isEmpty(siteSetUpChangeVo.getId())) {
                //根据站点id查询当前站点是否已经有设置信息，如果有则返回设置失败
                SiteSetUpEntity siteSetUp = siteSetUpDao.findBySiteId(siteSetUpChangeVo.getSiteId());
                if (StringUtil.isNotEmpty(siteSetUp)) {
                    return ResponseResult.error("设置失败，当前站点已经有设置信息，不允许再次新增");
                }
                BeanUtils.copyProperties(siteSetUpChangeVo, siteSetUpEntity);
                siteSetUpEntity.setCreateTime(LocalDateTime.now());
            } else {
                Optional<SiteSetUpEntity> siteSetUpDaoById = siteSetUpDao.findById(siteSetUpChangeVo.getId());
                if (siteSetUpDaoById.isPresent()) {
                    siteSetUpEntity = siteSetUpDaoById.get();
                    siteSetUpEntity.setSiteId(siteSetUpChangeVo.getSiteId());
                    siteSetUpEntity.setAppShow(siteSetUpChangeVo.getAppShow());
                    siteSetUpEntity.setOperatePassword(siteSetUpChangeVo.getOperatePassword());
                    siteSetUpEntity.setUpdateTime(LocalDateTime.now());
                    siteSetUpEntity.setReadwriteObject(siteSetUpChangeVo.getReadwriteObject());
                    siteSetUpEntity.setPvQtSource(siteSetUpChangeVo.getPvQtSource());
                    siteSetUpEntity.setTceCoeff(siteSetUpChangeVo.getTceCoeff());
                    siteSetUpEntity.setTreeCoeff(siteSetUpChangeVo.getTreeCoeff());
                    siteSetUpEntity.setReduceCoeff(siteSetUpChangeVo.getReduceCoeff());
                    siteSetUpEntity.setSystemName(siteSetUpChangeVo.getSystemName());
                }
            }
            siteSetUpDao.save(siteSetUpEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 根据站点id查询站点设置
     *
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<SiteSetUpDto> findSiteSetUpBySiteId(String siteId) {
        SiteSetUpDto siteSetUpDto = new SiteSetUpDto();
        Optional<SiteInfoEntity> optional = siteInfoDao.findById(siteId);
        if (optional.isPresent()) {
            SiteSetUpEntity siteSetUpEntity = siteSetUpDao.findBySiteId(siteId);
            if (StringUtil.isNotEmpty(siteSetUpEntity)) {
                BeanUtils.copyProperties(siteSetUpEntity, siteSetUpDto);
                //系统名称 为空则存站点名称的前10个字符
                siteSetUpDto.setSystemName(siteSetUpEntity.getSystemName());
                if (StringUtil.isEmpty(siteSetUpEntity.getSystemName()) && StringUtil.isNotEmpty(optional.get().getSiteName())) {
                    if (optional.get().getSiteName().length() > 12) {
                        siteSetUpDto.setSystemName(optional.get().getSiteName().substring(0, 12));
                    } else {
                        siteSetUpDto.setSystemName(optional.get().getSiteName());
                    }
                }
            }
        }
        return ResponseResult.ok(siteSetUpDto);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> saveSiteTopNode(SiteTopNodeChangeVo siteTopNodeVo) {

        if (StringUtil.isEmpty(siteTopNodeVo.getId())) {
            //新增拓扑节点
            SiteTopNodeEntity siteTopNodeEntity = new SiteTopNodeEntity();
            BeanUtils.copyProperties(siteTopNodeVo, siteTopNodeEntity);
            SiteTopNodeEntity save = siteTopNodeDao.save(siteTopNodeEntity);
            //添加拓扑节点数据配置数据
            if (StringUtil.isNotEmpty(siteTopNodeVo.getSiteTopItems())) {
                siteTopItemDao.saveAll(JSON.parseArray(siteTopNodeVo.getSiteTopItems(), SiteTopItemDto.class).stream().map(siteTopItem -> {
                    SiteTopItemEntity siteTopItemEntity = new SiteTopItemEntity();
                    BeanUtils.copyProperties(siteTopItem, siteTopItemEntity);
                    //节点id
                    siteTopItemEntity.setNodeId(save.getId());
                    return siteTopItemEntity;
                }).collect(Collectors.toList()));
            }
            return ResponseResult.ok();
        } else {
            //修改拓扑节点
            //根据拓扑节点id查询拓扑节点数据
            Optional<SiteTopNodeEntity> optional = siteTopNodeDao.findById(siteTopNodeVo.getId());
            if (optional.isPresent()) {
                SiteTopNodeEntity siteTopNodeEntity = new SiteTopNodeEntity();
                BeanUtils.copyProperties(siteTopNodeVo, siteTopNodeEntity);
                siteTopNodeEntity.setCreateTime(optional.get().getCreateTime());
                siteTopNodeDao.save(siteTopNodeEntity);

                //修改拓扑节点数据配置数据
                if (StringUtil.isNotEmpty(siteTopNodeVo.getSiteTopItems())) {
                    //根据节点id删除数据配置数据
//                    siteTopItemDao.deleteAll(siteTopItemDao.findAll(Example.of(SiteTopItemEntity.builder().nodeId(siteTopNodeEntity.getId()).build())));
                    //添加数据配置新的数据
                    siteTopItemDao.saveAll(JSON.parseArray(siteTopNodeVo.getSiteTopItems(), SiteTopItemDto.class).stream().map(siteTopItem -> {
                        SiteTopItemEntity siteTopItemEntity = new SiteTopItemEntity();
                        BeanUtils.copyProperties(siteTopItem, siteTopItemEntity);
                        //节点id
                        siteTopItemEntity.setNodeId(siteTopNodeEntity.getId());
                        return siteTopItemEntity;
                    }).collect(Collectors.toList()));
                }
                return ResponseResult.ok();
            }

        }
        return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
    }

    @Override
    @Transactional(transactionManager = DBConstants.JPA_TX_ACCESS, rollbackFor = Exception.class)
    public ResponseResult<Void> deleteTopNodeInfoById(String topNodeId) {
        List<SiteTopNodeEntity> resultLst = Lists.newArrayList();
        //根据id查询节点本身信息
        Optional<SiteTopNodeEntity> optional = siteTopNodeDao.findById(topNodeId);
        if (optional.isPresent()) {
            SiteTopNodeEntity siteTopNodeEntity = optional.get();
            resultLst.add(siteTopNodeEntity);
            //根据拓扑id查询出下面子节点信息
            resultLst.addAll(setChild(siteTopNodeEntity, siteTopNodeDao.findAll()));

            //根据多个拓扑节点id删除拓扑节点配置数据
            List<String> topNodeIds = resultLst.stream().map(SiteTopNodeEntity::getId).collect(Collectors.toList());
            siteTopItemDao.deleteAll(siteTopItemDao.findAllByNodeIdIn(topNodeIds));

            //删除所有拓扑节点信息
            siteTopNodeDao.deleteAll(resultLst);
            return ResponseResult.ok();
        }
        return ResponseResult.error(ResponseResult.FAIL);
    }

    /**
     * 获取子节点数据
     */
    private static List<SiteTopNodeEntity> setChild(SiteTopNodeEntity parent, List<SiteTopNodeEntity> childrenList) {
        //返回的集合
        List<SiteTopNodeEntity> children = Lists.newArrayList();
//        children.add(parent);
        children.addAll(getChildren(parent.getId(), childrenList));
        return children;
    }

    private static List<SiteTopNodeEntity> getChildren(String id, List<SiteTopNodeEntity> childrenList) {
        List<SiteTopNodeEntity> result = Lists.newArrayList();
        for (SiteTopNodeEntity menu : childrenList) {
            if (id.equals(menu.getParentId())) {
                result.add(menu);
                //childrenList.remove(menu);
                result.addAll(getChildren(menu.getId(), childrenList));
            }
        }
        return result;
    }

    @Override
    public ResponseResult<List<SiteTopNodeListDto>> findTopNodeListBySiteId(String siteId) {
        //返回的集合
        List<SiteTopNodeListDto> resultList = Lists.newArrayList();

        //根据站点id查询拓扑节点列表
        List<SiteTopNodeEntity> siteTopNodeEntityList = siteTopNodeDao.findAllBySiteId(siteId);
        if (CollectionUtils.isNotEmpty(siteTopNodeEntityList)) {
            resultList = siteTopNodeEntityList.stream().map(siteTopNodeEntity -> {
                SiteTopNodeListDto siteTopNodeDto = new SiteTopNodeListDto();
                BeanUtils.copyProperties(siteTopNodeEntity, siteTopNodeDto);
                return siteTopNodeDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<TopNodeInfoDto> findTopNodeInfoById(String topNodeId) {
        //返回的对象
        TopNodeInfoDto result = new TopNodeInfoDto();
        //根据拓扑节点id查询拓扑节点信息
        Optional<SiteTopNodeEntity> optional = siteTopNodeDao.findById(topNodeId);
        if (optional.isPresent()) {
            SiteTopNodeEntity siteTopNode = optional.get();
            BeanUtils.copyProperties(siteTopNode, result);
            //查询设备数据
            if (StringUtil.isNotEmpty(siteTopNode.getDeviceIds())) {
                List<String> deviceIds = Lists.newArrayList();
                if (siteTopNode.getNodeType() != 8) {
                    deviceIds = JSON.parseArray(siteTopNode.getDeviceIds(), String.class);
                } else {
                    //储能柜设备特殊处理
                    for (Object data : JSON.parseArray(siteTopNode.getDeviceIds())) {
                        JSONObject jsonObject = JSON.parseObject(String.valueOf(data));
                        deviceIds.add(jsonObject.getString("pcsId"));
                        deviceIds.add(jsonObject.getString("batteryId"));
                    }
                }
                //根据多个设备id查询设备数据
                List<DeviceEntity> deviceList = deviceDao.findAllByIdInAndIsDelete(deviceIds, 1);
                if (CollectionUtils.isNotEmpty(deviceList)) {
                    //根据多个类型id查询类型名称
                    Set<String> typeIds = deviceList.stream().map(DeviceEntity::getTypeId).collect(Collectors.toSet());
                    Map<String, String> typeNameMap = assetTypeDao.findAllById(typeIds).stream().collect(Collectors.toMap(AssetTypeEntity::getId,
                            AssetTypeEntity::getTypeName, (k1, k2) -> k1));
                    result.setDeviceList(deviceList.stream().map(device -> {
                        TopDeviceDto deviceDto = new TopDeviceDto();
                        BeanUtils.copyProperties(device, deviceDto);
                        deviceDto.setTypeName(typeNameMap.get(device.getTypeId()));
                        return deviceDto;
                    }).collect(Collectors.toList()));
                }

            }

            //根据拓扑点节点id查询拓扑节点数据项配置
            List<SiteTopItemEntity> siteTopItemList = siteTopItemDao.findAll(Example.of(SiteTopItemEntity.builder().nodeId(topNodeId).build()));
            if (CollectionUtils.isNotEmpty(siteTopItemList)) {
                result.setSiteTopItemList(siteTopItemList.stream().map(siteTopItem -> {
                    SiteTopItemDto siteTopItemDto = new SiteTopItemDto();
                    BeanUtils.copyProperties(siteTopItem, siteTopItemDto);
                    return siteTopItemDto;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<TopDeviceDto>> findDeviceListBySiteId(String siteId, Integer deviceType) {
        //返回的集合
        List<TopDeviceDto> resultList = Lists.newArrayList();
        //查询站点下所有设备列表
        List<DeviceEntity> deviceList = deviceDao.findAllBySiteIdAndIsDelete(siteId, 1);
        if (CollectionUtils.isEmpty(deviceList)) {
            return ResponseResult.ok(resultList);
        }
        //设备类型 1-关口表 2-电能表 3-逆变器 4-PCS 5-电池簇 6-充电桩 7-智能断路器 8-换电仓
        switch (deviceType) {
            case 1:
                deviceList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "39")).collect(Collectors.toList());
                break;
            case 2:
                deviceList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "38")).collect(Collectors.toList());
                break;
            case 3:
                deviceList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && (Objects.equals(d.getTypeId(), "20") || Objects.equals(d.getTypeId(), "77"))).collect(Collectors.toList());
                break;
            case 4:
                deviceList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && (Objects.equals(d.getTypeId(), "23") || Objects.equals(d.getTypeId(), "78"))).collect(Collectors.toList());
                break;
            case 5:
                deviceList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "25")).collect(Collectors.toList());
                break;
            case 6:
                List<String> typeIds = Arrays.asList("28", "29", "30", "79");
                deviceList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && typeIds.contains(d.getTypeId())).collect(Collectors.toList());
                break;
            case 7:
                deviceList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "50")).collect(Collectors.toList());
                break;
            case 8:
                deviceList = deviceList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && Objects.equals(d.getTypeId(), "70")).collect(Collectors.toList());
                break;
        }
        if (CollectionUtils.isNotEmpty(deviceList)) {
            resultList = deviceList.stream().map(device -> {
                TopDeviceDto result = new TopDeviceDto();
                BeanUtils.copyProperties(device, result);
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }
}
