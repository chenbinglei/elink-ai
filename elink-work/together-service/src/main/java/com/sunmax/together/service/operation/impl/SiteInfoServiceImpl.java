package com.sunmax.together.service.operation.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.device.SiteScenarioTypeDto;
import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.operate.OccupyPilePriceDto;
import com.sunmax.common.dto.operate.OccupyPileRateDto;
import com.sunmax.common.dto.operate.SiteWhiteRosterDto;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.dto.protocol.PlatformStatusDto;
import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import com.sunmax.common.dto.system.ChargePlatformInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.together.SiteRosterInfoDto;
import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.*;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.common.vo.protocol.mqtt.web.from.GatewayStatusSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.from.RebootSubscribeVo;
import com.sunmax.common.vo.protocol.mqtt.web.platform.RebootPublicVo;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import com.sunmax.common.vo.together.OrderChangeVo;
import com.sunmax.together.dao.asset.*;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.dto.operation.siteInfo.*;
import com.sunmax.together.entity.assets.*;
import com.sunmax.together.entity.order.OrderRecordEntity;
import com.sunmax.together.service.WebAppFeignService;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.ProtocolService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.operation.SiteInfoService;
import com.sunmax.together.vo.operation.siteInfo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getGatewayRealModel;
import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModel;
import static com.sunmax.common.util.DateUtil.*;
import static com.sunmax.common.util.DoubleUtil.getToDouble;
import static com.sunmax.common.util.StringUtil.getDeviceAssetType;
import static com.sunmax.common.util.StringUtil.isNotEmpty;

@Slf4j
@Service
public class SiteInfoServiceImpl implements SiteInfoService {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private GatWayPlatformDao gatWayPlatformDao;

    @Autowired
    private ChargerPriceInfoDao chargerPriceInfoDao;

    @Autowired
    private ChargerPriceDao chargerPriceDao;

    @Autowired
    private PirceAppliedRangeDao pirceAppliedRangeDao;

    @Autowired
    private OccupyPilePriceDao occupyPilePriceDao;

    @Autowired
    private SiteWhiteRosterDao siteWhiteRosterDao;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private OrderRecordDao orderRecordDao;

    @Autowired
    private DataService dataService;

    @Autowired
    private WebAppFeignService webAppFeignService;

    @Autowired
    private SiteRosterModeDao siteRosterModeDao;

    /**
     * 分页查询站点信息列表
     *
     * @param siteInfoListQueryVo
     * @param userId
     * @return
     */
    @Override
    public ResponseResult<PageDto<SiteInfoListDto>> findSiteInfoListByPage(SiteInfoListQueryVo siteInfoListQueryVo, String userId) {
        List<SiteInfoListDto> resultList = Lists.newArrayList();
        //根据当前登录用户id，查询相关配置资产授权站点数据
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
        if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
            List<OrganEmpowerListDto> organEmpowerListDtos = allOrganEmpowerByTenantId.getData();
            //根据多个站点id查询站点信息
            ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(
                    organEmpowerListDtos.stream().map(OrganEmpowerListDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList()));
            if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty()) {
                Map<String, SiteInfoDto> siteBasicInfoDtoMap = siteBasicInfoByIds.getData();
                List<SiteInfoDto> siteBasicInfoDtoList = Lists.newArrayList();
                siteBasicInfoDtoMap.forEach((k, v) -> siteBasicInfoDtoList.add(v));
                //根据运营商id查询
//                if (StringUtil.isNotEmpty(siteInfoListQueryVo.getOperateUnitId())) {
//                    siteBasicInfoDtoList = siteBasicInfoDtoList.stream().filter(o -> StringUtil.isNotEmpty(o.getOperateUnit()) && o.getOperateUnit().equals(siteInfoListQueryVo.getOperateUnitId())).collect(Collectors.toList());
//                }
                //根据能源场景类型过滤
                List<SiteInfoDto> siteInfoDtoList = siteBasicInfoDtoList.stream().filter(s -> isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains(String.valueOf(siteInfoListQueryVo.getScenarioTypes()))).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                    //根据站点状态查询
                    if (StringUtil.isNotEmpty(siteInfoListQueryVo.getSiteStatus())) {
                        siteInfoDtoList = siteInfoDtoList.stream().filter(o -> StringUtil.isNotEmpty(o.getSiteStatus()) && o.getSiteStatus().equals(siteInfoListQueryVo.getSiteStatus())).collect(Collectors.toList());
                    }
                    //根据区域查询
                    if (StringUtil.isNotEmpty(siteInfoListQueryVo.getAreaType()) && StringUtil.isNotEmpty(siteInfoListQueryVo.getArea())) {
                        siteInfoDtoList = siteInfoDtoList.stream()
                                .filter(siteInfoDto -> {
                                    Map<String, Object> readwriteMap = Maps.newHashMap();
                                    String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                                    if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                                        Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                                        });
                                        if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                            readwriteMap = JSON.parseObject(JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {
                                            });
                                        }
                                    }
                                    return matchesAreaType(readwriteMap, siteInfoListQueryVo);
                                })
                                .collect(Collectors.toList());

                    }
                    //根据关键字查询
                    if (StringUtil.isNotEmpty(siteInfoListQueryVo.getKeywordType()) && StringUtil.isNotEmpty(siteInfoListQueryVo.getKeyword())) {
                        if (siteInfoListQueryVo.getKeywordType() == 1) {//站点名称
                            siteInfoDtoList = siteInfoDtoList.stream().filter(o -> o.getSiteName().contains(siteInfoListQueryVo.getKeyword())).collect(Collectors.toList());
                        }
                    }
                    if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                        List<SiteInfoListDto> finalResultList = resultList;
                        siteInfoDtoList.forEach(siteBasicInfoDto -> {
                            SiteInfoListDto siteInfoListDto = new SiteInfoListDto();
                            BeanUtils.copyProperties(siteBasicInfoDto, siteInfoListDto);
                            //获取站点位置信息
                            String siteReadwriteObject = siteBasicInfoDto.getSiteReadwriteObject();
                            if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                                Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                                });
                                //站点位置信息对象
                                if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                    Map<String, Object> readwriteMap = JSON.parseObject(JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {
                                    });
                                    //省份
                                    if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.PROVINCE) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.PROVINCE))) {
                                        siteInfoListDto.setProvince(readwriteMap.get(SiteFieldParamVo.PROVINCE).toString());
                                    }
                                    //市级
                                    if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.CITY) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.CITY))) {
                                        siteInfoListDto.setCity(readwriteMap.get(SiteFieldParamVo.CITY).toString());
                                    }
                                    //所在区县
                                    if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.COUNTY) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.COUNTY))) {
                                        siteInfoListDto.setCounty(readwriteMap.get(SiteFieldParamVo.COUNTY).toString());
                                    }
                                    //详细地址
                                    if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.ADDRESS) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.ADDRESS))) {
                                        siteInfoListDto.setAddress(readwriteMap.get(SiteFieldParamVo.ADDRESS).toString());
                                    }
                                }
                                //全天开放
                                if (parseObjectMap.containsKey(SiteFieldParamVo.OPEN_ALL_DAY) && StringUtil.isNotEmpty(parseObjectMap.get(SiteFieldParamVo.OPEN_ALL_DAY))) {
                                    siteInfoListDto.setOpenAllDay(Integer.parseInt(parseObjectMap.get(SiteFieldParamVo.OPEN_ALL_DAY).toString()));
                                }
                                //建设场所
                                if (parseObjectMap.containsKey(SiteFieldParamVo.CONSTRUCTION) && StringUtil.isNotEmpty(parseObjectMap.get(SiteFieldParamVo.CONSTRUCTION))) {
                                    siteInfoListDto.setConstruction(Integer.parseInt(parseObjectMap.get(SiteFieldParamVo.CONSTRUCTION).toString()));
                                }
                            }
                            finalResultList.add(siteInfoListDto);
                        });
                        //根据建筑场所查询
                        if (StringUtil.isNotEmpty(siteInfoListQueryVo.getConstruction())) {
                            resultList = resultList.stream().filter(o -> StringUtil.isNotEmpty(o.getConstruction()) && Objects.equals(o.getConstruction(), siteInfoListQueryVo.getConstruction())).collect(Collectors.toList());
                        }
                    }
                }

            }

        }
        return ResponseResult.ok(new PageDto<>(resultList, siteInfoListQueryVo.getPage(), siteInfoListQueryVo.getSize()));
    }

    public static boolean matchesAreaType(Map<String, Object> readwriteMap, SiteInfoListQueryVo siteInfoListQueryVo) {
        String areaKey = siteInfoListQueryVo.getAreaType() == 1 ? SiteFieldParamVo.PROVINCE : SiteFieldParamVo.CITY;
        Object areaValue = readwriteMap.get(areaKey);

        return areaValue != null && areaValue.equals(siteInfoListQueryVo.getArea());
    }

    /**
     * 根据站点id查询网关状态列表数据
     *
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<List<GatewayStatusDto>> findGatewayStatusListById(String siteId) {
        List<GatewayStatusDto> resultList = Lists.newArrayList();

        //根据站点id查询设备数据
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), DeviceTypeParamVo.TX);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
            List<DeviceBasicInfoDto> gatWayDeviceInfoDtos = deviceBasicInfoBySiteIds.getData().get(siteId).stream().filter(d -> StringUtil.isNotEmpty(d.getAccessType()) && d.getAccessType() == 2).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(gatWayDeviceInfoDtos)) {
                ResponseResult<List<GatewayStatusSubscribeVo>> batchGatewayStatus = protocolService.batchGatewayStatus(gatWayDeviceInfoDtos.stream().map(DeviceBasicInfoDto::getDeviceNumber).filter(StringUtil::isNotEmpty).collect(Collectors.toList()));
                Map<String, GatewayStatusSubscribeVo> gatewayStatusSubscribeVoMap = Maps.newHashMap();
                if (batchGatewayStatus.isSuccess() && CollectionUtils.isNotEmpty(batchGatewayStatus.getData())) {
                    gatewayStatusSubscribeVoMap = batchGatewayStatus.getData().stream().collect(Collectors.toMap(GatewayStatusSubscribeVo::getDevId, gatewayStatusSubscribeVo -> gatewayStatusSubscribeVo, (k1, k2) -> k1));
                }
                Map<String, GatewayStatusSubscribeVo> finalGatewayStatusSubscribeVoMap = gatewayStatusSubscribeVoMap;
                resultList = gatWayDeviceInfoDtos.stream().map(deviceBasicInfoDto -> {
                    GatewayStatusDto gatewayStatusDto = new GatewayStatusDto();
                    gatewayStatusDto.setId(deviceBasicInfoDto.getId());
                    gatewayStatusDto.setName(deviceBasicInfoDto.getDeviceName());
                    gatewayStatusDto.setCode(deviceBasicInfoDto.getDeviceNumber());
                    GatewayStatusSubscribeVo statusSubscribeVo = finalGatewayStatusSubscribeVoMap.get(deviceBasicInfoDto.getDeviceNumber());
                    if (StringUtil.isNotEmpty(statusSubscribeVo)) {
                        BeanUtils.copyProperties(statusSubscribeVo, gatewayStatusDto);
                        gatewayStatusDto.setCode(statusSubscribeVo.getDevId());
                    }
                    //获取网关实时数据
                    gatewayStatusDto.setWorkState(3);
                    GatewayRealModel gatewayRealModel = getGatewayRealModel(deviceBasicInfoDto.getDeviceNumber());
                    if (StringUtil.isNotEmpty(gatewayRealModel)) {
                        gatewayStatusDto.setWorkState(gatewayRealModel.getDeviceStatus());
                    }
                    return gatewayStatusDto;
                }).collect(Collectors.toList());
            }
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 根据网关id查询网关子设备列表数据
     *
     * @param gatWayId
     * @return
     */
    @Override
    public ResponseResult<List<GatewayChildDeviceDto>> findGatewayChildDeviceById(String gatWayId) {
        List<GatewayChildDeviceDto> resultList = Lists.newArrayList();
        //根据网关id查询子设备信息
        ResponseResult<List<DeviceBasicInfoDto>> gatewayChildDeviceById = deviceService.findGatewayChildDeviceById(gatWayId);
        if (gatewayChildDeviceById.isSuccess() && CollectionUtils.isNotEmpty(gatewayChildDeviceById.getData())) {
            resultList = gatewayChildDeviceById.getData().stream().map(deviceBasicInfoDto -> {
                GatewayChildDeviceDto gatewayChildDeviceDto = new GatewayChildDeviceDto();
                gatewayChildDeviceDto.setId(deviceBasicInfoDto.getId());
                gatewayChildDeviceDto.setCode(deviceBasicInfoDto.getDeviceNumber());
                gatewayChildDeviceDto.setName(deviceBasicInfoDto.getDeviceName());
                gatewayChildDeviceDto.setTypeName(deviceBasicInfoDto.getTypeName());
                gatewayChildDeviceDto.setTxStatus(Objects.requireNonNull(RedisDeviceUtil.getDevice(deviceBasicInfoDto.getDeviceNumber())).getTxStatus());
                return gatewayChildDeviceDto;
            }).collect(Collectors.toList());

        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 添加网关关联平台信息
     *
     * @param gatWayId    网关唯一id
     * @param gatWayCode  网关编码
     * @param platformIds 关联平台id(多个以逗号分割,空值代表删除所有)
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveGatWayPlatformInfo(String gatWayId, String gatWayCode, String platformIds) {
        //如果有值代表插入，无值代表清除
        if (StringUtil.isNotEmpty(platformIds)) {
            //先把当前站点下关联的所有平台删除再重新插入
            List<GatWayPlatformEntity> allByGatewayId = gatWayPlatformDao.findAllByGatewayId(gatWayId);
            if (CollectionUtils.isNotEmpty(allByGatewayId)) {
                gatWayPlatformDao.deleteAll(allByGatewayId);
            }
            List<String> platformIdList = Arrays.asList(platformIds.split(","));
            gatWayPlatformDao.saveAll(platformIdList.stream().map(platformId -> {
                GatWayPlatformEntity gatWayPlatformEntity = new GatWayPlatformEntity();
                gatWayPlatformEntity.setGatewayId(gatWayId);
                gatWayPlatformEntity.setPlatformId(platformId);
                return gatWayPlatformEntity;
            }).collect(Collectors.toList()));
            //平台下发
            List<PlatformSetVo> platformSetVos = Lists.newArrayList(systemService.findChargePlatformInfoByIds(platformIdList).getData().values()).stream().map(chargePlatformInfoDto -> {
                PlatformSetVo platformSetVo = new PlatformSetVo();
                platformSetVo.setPlatformLogo(chargePlatformInfoDto.getPlatformLogo());
                platformSetVo.setProtocolDriver(chargePlatformInfoDto.getProtocolType());
                platformSetVo.setIp(chargePlatformInfoDto.getIpAddress());
                platformSetVo.setPort(Integer.parseInt(chargePlatformInfoDto.getPortNumber()));
                return platformSetVo;
            }).collect(Collectors.toList());
            protocolService.platformSet(gatWayCode, platformSetVos);
        } else {
            gatWayPlatformDao.deleteAllByGatewayId(gatWayId);
            protocolService.platformSet(gatWayCode, Lists.newArrayList());
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据网关id查询关联所有平台信息
     *
     * @param gatWayId
     * @param gatWayCode
     * @return
     */
    @Override
    public ResponseResult<List<GatWayPlatformDto>> findGatWayPlatformInfo(String gatWayId, String gatWayCode) {
        List<GatWayPlatformDto> resultList = Lists.newArrayList();

        //查询网关关联平台id
        List<GatWayPlatformEntity> gatWayPlatformEntityList = gatWayPlatformDao.findAllByGatewayId(gatWayId);
        if (CollectionUtils.isNotEmpty(gatWayPlatformEntityList)) {
            //根据多个平台id查询数据
            ResponseResult<Map<String, ChargePlatformInfoDto>> chargePlatformInfoByIds =
                    systemService.findChargePlatformInfoByIds(gatWayPlatformEntityList.stream().map(GatWayPlatformEntity::getPlatformId).collect(Collectors.toList()));
            if (chargePlatformInfoByIds.isSuccess() && !chargePlatformInfoByIds.getData().isEmpty()) {
                Map<String, ChargePlatformInfoDto> platformInfoDtoMap = chargePlatformInfoByIds.getData();
                //查询网关下所有电桩子设备
                ResponseResult<List<DeviceBasicInfoDto>> gatewayChildDeviceById = deviceService.findGatewayChildDeviceById(gatWayId);
                List<DeviceBasicInfoDto> pileDeviceInfoList = Lists.newArrayList();
                if (gatewayChildDeviceById.isSuccess() && CollectionUtils.isNotEmpty(gatewayChildDeviceById.getData())) {
                    //获取充电站设备下资产分类id
                    List<Integer> deviceAssetType = getDeviceAssetType(1);
                    pileDeviceInfoList = gatewayChildDeviceById.getData().stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && deviceAssetType.contains(Integer.parseInt(d.getTypeId()))).collect(Collectors.toList());
                }
                //查询平台下电桩在线状态参数
                List<PlatformStatusVo> platformStatusVos = Lists.newArrayList(platformInfoDtoMap.values()).stream().map(chargePlatformInfoDto -> {
                    PlatformStatusVo platformStatusVo = new PlatformStatusVo();
                    platformStatusVo.setPlatformLogo(chargePlatformInfoDto.getPlatformLogo());
                    platformStatusVo.setProtocolDriver(chargePlatformInfoDto.getProtocolType());
                    return platformStatusVo;
                }).collect(Collectors.toList());
                //查询设备在线状态
                ResponseResult<PlatformStatusDto> platformStatusResult = protocolService.platformStatus(gatWayCode, platformStatusVos);
                Map<String, List<PlatformStatusDto.Pile>> platformPileMap = Maps.newHashMap();
                if (platformStatusResult.isSuccess() && StringUtil.isNotEmpty(platformStatusResult.getData())) {
                    if (platformStatusResult.getData().getIssuedStatus() == 0) {
                        platformPileMap = platformStatusResult.getData().getPlatformPileMap();
                    }
                }
                List<DeviceBasicInfoDto> finalPileDeviceInfoList = pileDeviceInfoList;
                Map<String, List<PlatformStatusDto.Pile>> finalPlatformPileMap = platformPileMap;
                // 遍历平台信息，组装每个平台的详细状态
                platformInfoDtoMap.forEach((k, v) -> {
                    GatWayPlatformDto gatWayPlatformDto = new GatWayPlatformDto();
                    // 复制基本信息
                    BeanUtils.copyProperties(v, gatWayPlatformDto);
                    gatWayPlatformDto.setPlatformId(v.getId());
                    // 组装充电桩在线状态信息
                    if (CollectionUtils.isNotEmpty(finalPileDeviceInfoList)) {
                        List<PlatformStatusDto.Pile> pileList = finalPlatformPileMap.get(v.getPlatformLogo());
                        Map<String, PlatformStatusDto.Pile> pileMap = Maps.newHashMap();
                        // 对充电桩信息进行映射
                        if (CollectionUtils.isNotEmpty(pileList)) {
                            pileMap = pileList.stream().collect(Collectors.toMap(PlatformStatusDto.Pile::getDevId, pile -> pile, (k1, k2) -> k1));
                        }
                        Map<String, PlatformStatusDto.Pile> finalPileMap = pileMap;
                        // 设置每个充电桩的状态信息
                        gatWayPlatformDto.setPileStateInfoList(finalPileDeviceInfoList.stream().map(deviceBasicInfoDto -> {
                            GatWayPlatformDto.PileStateInfo pileStateInfo = new GatWayPlatformDto.PileStateInfo();
                            pileStateInfo.setPileCode(deviceBasicInfoDto.getDeviceNumber());
                            pileStateInfo.setFlag(false);
                            // 检查是否存在该充电桩，并设置其在线状态
                            PlatformStatusDto.Pile pile = finalPileMap.get(deviceBasicInfoDto.getDeviceNumber());
                            if (StringUtil.isNotEmpty(pile)) {
                                pileStateInfo.setFlag(pile.getFlag());
                            }
                            return pileStateInfo;
                        }).collect(Collectors.toList()));
                    }
                    resultList.add(gatWayPlatformDto);
                });
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveChargerPriceInfo(ChargerPriceInfoChangeVo chargerPriceInfoChangeVo) {
        if (StringUtil.isNotEmpty(chargerPriceInfoChangeVo)) {
            ChargerPriceInfoEntity chargerPriceInfoEntity = new ChargerPriceInfoEntity();
            chargerPriceInfoEntity.setId(UUID.randomUUID().toString().replace(FileUtil.BAR, FileUtil.separator).substring(0, 16));
            BeanUtils.copyProperties(chargerPriceInfoChangeVo, chargerPriceInfoEntity);
            chargerPriceInfoEntity.setCreateTime(LocalDateTime.now());
            List<ChargerPriceEntity> chargerPriceEntities = JSON.parseArray(chargerPriceInfoChangeVo.getChargerPriceDtos(), ChargerPriceEntity.class);

            //如果是立即生效，则把之前生效中的全部查出来并修改状态为已失效
            if (chargerPriceInfoChangeVo.getTakeType() == 1) {
                chargerPriceInfoEntity.setTakeTime(localDateTimeToStr(LocalDateTime.now()));
                //查询指定站点当前价格类型生效中的价格信息
                List<ChargerPriceInfoEntity> chargerPriceInfoEntityList = chargerPriceInfoDao.findByInEffectInfoSiteId(chargerPriceInfoChangeVo.getSiteId(), chargerPriceInfoChangeVo.getPriceType());
                //如果本次设备类型是全部，则分别插入直流和交流各一次
                if (chargerPriceInfoChangeVo.getDeviceType() == 3) {
                    //保存插入充放电价格信息列表
                    List<ChargerPriceInfoEntity> savePriceInfoEntityList = Lists.newArrayList();
                    //保存插入充放电费率列表
                    List<ChargerPriceEntity> saveChargerPriceEntityList = Lists.newArrayList();
                    if (CollectionUtils.isNotEmpty(chargerPriceInfoEntityList)) {
                        //把之前已生效状态改为已失效
                        chargerPriceInfoDao.saveAll(chargerPriceInfoEntityList.stream().peek(c -> c.setPriceState(3)).collect(Collectors.toList()));
                    }
                    //插入直流
                    chargerPriceInfoEntity.setDeviceType(1);
                    chargerPriceInfoEntity.setPriceState(1);
                    savePriceInfoEntityList.add(chargerPriceInfoEntity);
                    //插入交流
                    ChargerPriceInfoEntity acPriceInfoEntity = new ChargerPriceInfoEntity();
                    BeanUtils.copyProperties(chargerPriceInfoEntity, acPriceInfoEntity);
                    acPriceInfoEntity.setDeviceType(2);
                    acPriceInfoEntity.setPriceState(1);
                    acPriceInfoEntity.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
                    savePriceInfoEntityList.add(acPriceInfoEntity);
                    List<ChargerPriceInfoEntity> priceInfoEntities = chargerPriceInfoDao.saveAll(savePriceInfoEntityList);
                    priceInfoEntities.forEach(chargerPriceInfo -> chargerPriceEntities.forEach(chargerPriceEntity -> {
                        ChargerPriceEntity chargerPrices = new ChargerPriceEntity();
                        BeanUtils.copyProperties(chargerPriceEntity, chargerPrices);
                        chargerPrices.setPriceId(chargerPriceInfo.getId());
                        saveChargerPriceEntityList.add(chargerPrices);
                    }));
                    chargerPriceDao.saveAll(saveChargerPriceEntityList);
                    //根据站点id查询所有充电桩设备信息
                    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(chargerPriceInfoChangeVo.getSiteId()), DeviceTypeParamVo.CDZ);
                    if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                        List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(chargerPriceInfoChangeVo.getSiteId());

                        //根据设备类型转成map，只会有直流和交流两条数据
                        Map<Integer, ChargerPriceInfoEntity> priceInfoEntityMap = priceInfoEntities.stream().collect(Collectors.toMap(ChargerPriceInfoEntity::getDeviceType, chargerPriceEntitie -> chargerPriceEntitie, (k1, k2) -> k1));
                        //组装48个时段费率配置数据
                        List<ChargerPriceEntity> tariffRecordHalfHour = getTariffRecordHalfHour(chargerPriceEntities, localDateToStr(LocalDate.now()));
                        List<CostFormat> costFormats = getCostFormat(tariffRecordHalfHour);

                        List<PileRateSetVo> pileRateSetVos = Lists.newArrayList();
                        deviceBasicInfoDtoList.forEach(deviceBasicInfoDto -> {
                            //获取电桩类型
                            String typeId = deviceBasicInfoDto.getTypeId();
                            if (StringUtil.isNotEmpty(typeId)) {
                                PileRateSetVo pileRateSetVo = new PileRateSetVo();
                                pileRateSetVo.setType(chargerPriceInfoChangeVo.getPriceType() == 1 ? 0 : 1);
                                pileRateSetVo.setTimeFrameNum(48);
                                pileRateSetVo.setPileCode(deviceBasicInfoDto.getDeviceNumber());
                                int pileType = Integer.parseInt(typeId);
                                if (pileType == 29 || pileType == 30) {
                                    pileRateSetVo.setTemplateId(priceInfoEntityMap.get(1).getId());
                                } else {
                                    pileRateSetVo.setTemplateId(priceInfoEntityMap.get(2).getId());
                                }
                                pileRateSetVo.setTimeFrameRates(costFormats);
                                pileRateSetVos.add(pileRateSetVo);
                            }
                        });
                        if (CollectionUtils.isNotEmpty(pileRateSetVos)) {
                            //根据价格id分组，循环多次批量下发
                            Map<String, List<PileRateSetVo>> groupByPriceIdMap = pileRateSetVos.stream().collect(Collectors.groupingBy(PileRateSetVo::getTemplateId));
                            groupByPriceIdMap.forEach((k, v) -> {
                                List<PileResultDto> pileResultDtoList = protocolService.batchPileRateSet(v).getData();
                                //生成应用范围
                                pirceAppliedRangeDao.saveAll(pileResultDtoList.stream().map(pileResultDto -> {
                                    PirceAppliedRangeEntity pirceAppliedRangeEntity = new PirceAppliedRangeEntity();
                                    pirceAppliedRangeEntity.setPileCode(pileResultDto.getPileCode());
                                    pirceAppliedRangeEntity.setTakeResult(pileResultDto.getResult());
                                    pirceAppliedRangeEntity.setPriceId(k);
                                    return pirceAppliedRangeEntity;
                                }).collect(Collectors.toList()));
                            });
                        }
                    }
                } else {
                    List<ChargerPriceInfoEntity> chargerPriceInfoEntities = chargerPriceInfoEntityList.stream().filter(c -> c.getDeviceType().equals(chargerPriceInfoChangeVo.getDeviceType())).peek(c -> c.setPriceState(3)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(chargerPriceInfoEntities)) {
                        //把之前已生效状态改为已失效
                        chargerPriceInfoDao.saveAll(chargerPriceInfoEntities);
                    }
                    chargerPriceInfoEntity.setPriceState(1);
                    ChargerPriceInfoEntity priceInfoEntity = chargerPriceInfoDao.save(chargerPriceInfoEntity);
                    chargerPriceDao.saveAll(chargerPriceEntities.stream().peek(c -> c.setPriceId(priceInfoEntity.getId())).collect(Collectors.toList()));

                    //根据站点id查询所有充电桩设备信息
                    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(chargerPriceInfoChangeVo.getSiteId()), DeviceTypeParamVo.CDZ);
                    if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                        List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(chargerPriceInfoChangeVo.getSiteId());
                        //过滤出需要下发类型的设备
                        deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream()
                                .filter(d -> chargerPriceInfoChangeVo.getDeviceType() == 1 ?
                                        d.getTypeId().equals("30") || d.getTypeId().equals("29") :
                                        d.getTypeId().equals("28"))
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(deviceBasicInfoDtoList)) {
                            //组装48个时段费率配置数据
                            List<ChargerPriceEntity> tariffRecordHalfHour = getTariffRecordHalfHour(chargerPriceEntities, localDateToStr(LocalDate.now()));
                            List<CostFormat> costFormats = getCostFormat(tariffRecordHalfHour);

                            List<PileRateSetVo> pileRateSetVos = Lists.newArrayList();
                            deviceBasicInfoDtoList.forEach(deviceBasicInfoDto -> {
                                PileRateSetVo pileRateSetVo = new PileRateSetVo();
                                pileRateSetVo.setType(chargerPriceInfoChangeVo.getPriceType() == 1 ? 0 : 1);
                                pileRateSetVo.setTimeFrameNum(48);
                                pileRateSetVo.setPileCode(deviceBasicInfoDto.getDeviceNumber());
                                pileRateSetVo.setTemplateId(priceInfoEntity.getId());
                                pileRateSetVo.setTimeFrameRates(costFormats);
                                pileRateSetVos.add(pileRateSetVo);
                            });
                            List<PileResultDto> pileResultDtoList = protocolService.batchPileRateSet(pileRateSetVos).getData();
                            //生成应用范围
                            pirceAppliedRangeDao.saveAll(pileResultDtoList.stream().map(pileResultDto -> {
                                PirceAppliedRangeEntity pirceAppliedRangeEntity = new PirceAppliedRangeEntity();
                                pirceAppliedRangeEntity.setPileCode(pileResultDto.getPileCode());
                                pirceAppliedRangeEntity.setTakeResult(pileResultDto.getResult());
                                pirceAppliedRangeEntity.setPriceId(priceInfoEntity.getId());
                                return pirceAppliedRangeEntity;
                            }).collect(Collectors.toList()));
                        }
                    }
                }
            } else {
                //如果本次设备类型是全部，则分别插入直流和交流各一次
                if (chargerPriceInfoChangeVo.getDeviceType() == 3) {
                    //保存插入充放电价格信息列表
                    List<ChargerPriceInfoEntity> savePriceInfoEntityList = Lists.newArrayList();
                    //保存插入充放电费率列表
                    List<ChargerPriceEntity> saveChargerPriceEntityList = Lists.newArrayList();
                    //插入直流
                    chargerPriceInfoEntity.setDeviceType(1);
                    chargerPriceInfoEntity.setPriceState(2);
                    savePriceInfoEntityList.add(chargerPriceInfoEntity);
                    //插入交流
                    ChargerPriceInfoEntity acPriceInfoEntity = new ChargerPriceInfoEntity();
                    BeanUtils.copyProperties(chargerPriceInfoEntity, acPriceInfoEntity);
                    acPriceInfoEntity.setDeviceType(2);
                    acPriceInfoEntity.setPriceState(2);
                    acPriceInfoEntity.setId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
                    savePriceInfoEntityList.add(acPriceInfoEntity);

                    //保存两条数据
                    List<ChargerPriceInfoEntity> priceInfoEntities = chargerPriceInfoDao.saveAll(savePriceInfoEntityList);
                    priceInfoEntities.forEach(chargerPriceInfo -> chargerPriceEntities.forEach(chargerPriceEntity -> {
                        ChargerPriceEntity chargerPrices = new ChargerPriceEntity();
                        BeanUtils.copyProperties(chargerPriceEntity, chargerPrices);
                        chargerPrices.setPriceId(chargerPriceInfo.getId());
                        saveChargerPriceEntityList.add(chargerPrices);
                    }));
                    chargerPriceDao.saveAll(saveChargerPriceEntityList);
                } else {
                    chargerPriceInfoEntity.setPriceState(2);
                    ChargerPriceInfoEntity priceInfoEntity = chargerPriceInfoDao.save(chargerPriceInfoEntity);
                    chargerPriceDao.saveAll(chargerPriceEntities.stream().peek(c -> c.setPriceId(priceInfoEntity.getId())).collect(Collectors.toList()));
                }
            }
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 获取费率配置每半小时的时间
     *
     * @param chargerPriceEntityList
     * @param date
     * @return
     */
    public List<ChargerPriceEntity> getTariffRecordHalfHour(List<ChargerPriceEntity> chargerPriceEntityList, String date) {
        List<ChargerPriceEntity> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(chargerPriceEntityList)) {
            chargerPriceEntityList.forEach(chargerPriceEntity -> {
                String startTime = chargerPriceEntity.getStartTime();
                String endTime = chargerPriceEntity.getEndTime();
                List<String> minuteBetweenDate = getLocalDateTimeBetween(strToLocalDateTime(date + " " + startTime + ":00"), strToLocalDateTime(date + " " + endTime + ":00"), "30m")
                        .stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());
                minuteBetweenDate.forEach(halfHour -> {
                    ChargerPriceEntity result = new ChargerPriceEntity();
                    BeanUtils.copyProperties(chargerPriceEntity, result);
                    result.setStartTime(halfHour);
                    resultList.add(result);
                });
            });
        }
        return resultList;
    }

    /**
     * 组装转换费率信息
     *
     * @param chargerPriceEntityList
     * @return
     */
    public List<CostFormat> getCostFormat(List<ChargerPriceEntity> chargerPriceEntityList) {
        List<CostFormat> costFormats = Lists.newArrayList();

        chargerPriceEntityList.forEach(chargerPriceEntity -> {
            CostFormat costFormat = new CostFormat();
            //时段开始时间
            String startTime = chargerPriceEntity.getStartTime();
            long startTamp = Timestamp.valueOf(startTime).getTime() / 1000 + 8 * 3600;
            costFormat.setStartTime((int) startTamp);
            costFormat.setEndTime((int) (startTamp + 1800));

            costFormat.setPrice(chargerPriceEntity.getElectMoney().doubleValue() * 1000);
            BigDecimal serviceMoney = chargerPriceEntity.getServiceMoney();
            if (StringUtil.isNotEmpty(serviceMoney)) {
                costFormat.setServiceCharger(serviceMoney.doubleValue() * 1000);
            }
            Integer periodType = chargerPriceEntity.getPeriodType();
            costFormat.setType(periodType == 5 ? 4 : (periodType == 6 ? 3 : periodType));
            costFormats.add(costFormat);
        });
        return costFormats;
    }

    /**
     * 根据站点id查询定价记录和生效中的价格配置
     *
     * @param siteId    站点id
     * @param priceType 价格类型 1-充电 2-放电
     * @return
     */
    @Override
    public ResponseResult<ChargerPriceInfoListDto> findFixPriceRecordList(String siteId, Integer priceType) {
        ChargerPriceInfoListDto result = new ChargerPriceInfoListDto();

        //查询指定站点指定价格类型所有定价记录
        List<ChargerPriceInfoEntity> chargerPriceInfoEntityList = chargerPriceInfoDao.findAllBySiteIdAndPriceType(siteId, priceType);
        if (CollectionUtils.isNotEmpty(chargerPriceInfoEntityList)) {
            result.setFixPriceRecordList(chargerPriceInfoEntityList.stream().map(chargerPriceInfoEntity -> {
                ChargerPriceInfoListDto.FixPriceRecord fixPriceRecord = new ChargerPriceInfoListDto.FixPriceRecord();
                BeanUtils.copyProperties(chargerPriceInfoEntity, fixPriceRecord);
                return fixPriceRecord;
            }).collect(Collectors.toList()).stream().sorted(Comparator.comparing(ChargerPriceInfoListDto.FixPriceRecord::getCreateTime).reversed()).collect(Collectors.toList()));
            //过滤出当前生效中的价格信息
            List<ChargerPriceInfoEntity> chargerPriceInfoEntities = chargerPriceInfoEntityList.stream().filter(c -> StringUtil.isNotEmpty(c.getPriceState()) && c.getPriceState() == 1).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(chargerPriceInfoEntities)) {
                //直流价格信息
                List<ChargerPriceInfoEntity> dcPriceInfoEntities = chargerPriceInfoEntities.stream().filter(c -> c.getDeviceType() == 1).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(dcPriceInfoEntities)) {
                    ChargerPriceInfoEntity chargerPriceInfoEntity = dcPriceInfoEntities.get(0);
                    //根据价格id查询价格配置信息
                    List<ChargerPriceEntity> chargerPriceEntities = chargerPriceDao.findAllByPriceIdIn(Collections.singletonList(chargerPriceInfoEntity.getId()));
                    result.setDcPriceConfigList(chargerPriceEntities.stream().map(chargerPriceEntity -> {
                        ChargerPriceInfoListDto.PriceConfig priceConfig = new ChargerPriceInfoListDto.PriceConfig();
                        BeanUtils.copyProperties(chargerPriceEntity, priceConfig);
                        return priceConfig;
                    }).collect(Collectors.toList()));
                }
                //交流价格信息
                List<ChargerPriceInfoEntity> acPriceInfoEntities = chargerPriceInfoEntities.stream().filter(c -> c.getDeviceType() == 2).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(acPriceInfoEntities)) {
                    ChargerPriceInfoEntity chargerPriceInfoEntity = acPriceInfoEntities.get(0);
                    //根据价格id查询价格配置信息
                    List<ChargerPriceEntity> chargerPriceEntities = chargerPriceDao.findAllByPriceIdIn(Collections.singletonList(chargerPriceInfoEntity.getId()));
                    result.setAcPriceConfigList(chargerPriceEntities.stream().map(chargerPriceEntity -> {
                        ChargerPriceInfoListDto.PriceConfig priceConfig = new ChargerPriceInfoListDto.PriceConfig();
                        BeanUtils.copyProperties(chargerPriceEntity, priceConfig);
                        return priceConfig;
                    }).collect(Collectors.toList()));
                }
            }
        }
        return ResponseResult.ok(result);
    }

    /**
     * 根据价格id查询价格详情
     *
     * @param pirceId
     * @return
     */
    @Override
    public ResponseResult<ChargerPriceDetailsDto> findPriceDetailsById(String pirceId) {
        ChargerPriceDetailsDto result = new ChargerPriceDetailsDto();

        //根据id查询价格基本信息
        Optional<ChargerPriceInfoEntity> priceInfoDaoById = chargerPriceInfoDao.findById(pirceId);
        if (priceInfoDaoById.isPresent()) {
            ChargerPriceInfoEntity priceInfoEntity = priceInfoDaoById.get();
            BeanUtils.copyProperties(priceInfoEntity, result);
            //根据价格id查询价格配置信息
            result.setPriceConfigList(chargerPriceDao.findAllByPriceIdIn(Collections.singletonList(pirceId)).stream().map(chargerPriceEntity -> {
                ChargerPriceDetailsDto.PriceConfig priceConfig = new ChargerPriceDetailsDto.PriceConfig();
                BeanUtils.copyProperties(chargerPriceEntity, priceConfig);
                return priceConfig;
            }).collect(Collectors.toList()));
            //根据价格id查询应用范围数据
            List<PirceAppliedRangeEntity> pirceAppliedRangeEntityList = pirceAppliedRangeDao.findAllByPriceId(pirceId);
            if (CollectionUtils.isNotEmpty(pirceAppliedRangeEntityList)) {
                //根据多个电桩编码查询设备详情数据
                Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceService.findDeviceBasicInfoByCodes(pirceAppliedRangeEntityList.stream().map(PirceAppliedRangeEntity::getPileCode).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList())).getData();
                result.setPirceAppliedRangeList(pirceAppliedRangeEntityList.stream().map(pirceAppliedRangeEntity -> {
                    ChargerPriceDetailsDto.PirceAppliedRange pirceAppliedRange = new ChargerPriceDetailsDto.PirceAppliedRange();
                    BeanUtils.copyProperties(pirceAppliedRangeEntity, pirceAppliedRange);
                    DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoDtoMap.get(pirceAppliedRangeEntity.getPileCode());
                    if (StringUtil.isNotEmpty(deviceBasicInfoDto)) {
                        pirceAppliedRange.setPileName(deviceBasicInfoDto.getDeviceName());
                    }
                    return pirceAppliedRange;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    /**
     * 根据价格id取消待生效价格信息
     *
     * @param pirceId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deletePriceInfoById(String pirceId) {
        chargerPriceInfoDao.deleteById(pirceId);
        chargerPriceDao.deleteAllByPriceId(pirceId);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 添加占桩价格信息
     *
     * @param occupyPilePriceChangeVo
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOccupyPilePriceInfo(OccupyPilePriceChangeVo occupyPilePriceChangeVo) {
        OccupyPilePriceEntity occupyPilePriceEntity = new OccupyPilePriceEntity();
        BeanUtils.copyProperties(occupyPilePriceChangeVo, occupyPilePriceEntity);
        occupyPilePriceEntity.setConfigPriceInfo(occupyPilePriceChangeVo.getConfigPriceInfoStr());
        occupyPilePriceEntity.setIsDelete(1);
        //新增
        if (StringUtil.isEmpty(occupyPilePriceChangeVo.getId())) {
            //如果设备类型是全部，则直流和交流各插入一次
            if (occupyPilePriceChangeVo.getDeviceType() == 3) {
                List<OccupyPilePriceEntity> saveOccupyPilePriceList = Lists.newArrayList();
                //插入直流
                occupyPilePriceEntity.setDeviceType(1);
                occupyPilePriceEntity.setIsDelete(1);
                saveOccupyPilePriceList.add(occupyPilePriceEntity);
                //插入交流
                OccupyPilePriceEntity acOccupyPilePriceEntity = new OccupyPilePriceEntity();
                acOccupyPilePriceEntity.setDeviceType(2);
                acOccupyPilePriceEntity.setIsDelete(1);
                saveOccupyPilePriceList.add(acOccupyPilePriceEntity);
                occupyPilePriceDao.saveAll(saveOccupyPilePriceList);
            }
        }
        occupyPilePriceDao.save(occupyPilePriceEntity);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据站点id查询占桩价格信息
     *
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<OccupyPilePriceDto> findOccupyPilePriceInfoById(String siteId) {
        OccupyPilePriceDto occupyPilePriceDto = new OccupyPilePriceDto();
        //根据站点id查询相关占桩价格信息
        List<OccupyPilePriceEntity> occupyPilePriceEntityList = occupyPilePriceDao.findBySiteIdAndIsDelete(siteId, 1);
        if (CollectionUtils.isNotEmpty(occupyPilePriceEntityList)) {
            //过滤出直流价格
            List<OccupyPilePriceEntity> occupyPilePriceEntities = occupyPilePriceEntityList.stream().filter(o -> o.getDeviceType() == 1).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(occupyPilePriceEntities)) {
                OccupyPilePriceDto.PriceInfoData priceInfoData = new OccupyPilePriceDto.PriceInfoData();

                OccupyPilePriceEntity occupyPilePriceEntity = occupyPilePriceEntities.get(0);

                BeanUtils.copyProperties(occupyPilePriceEntity, priceInfoData);
                String configPriceInfo = occupyPilePriceEntity.getConfigPriceInfo();
                priceInfoData.setConfigPriceInfoStr(configPriceInfo);
                List<OccupyPilePriceDto.ConfigPriceInfo> configPriceInfoList = JSON.parseArray(configPriceInfo, OccupyPilePriceDto.ConfigPriceInfo.class);
                priceInfoData.setConfigPriceInfoList(configPriceInfoList);
                occupyPilePriceDto.setDcPriceInfoData(priceInfoData);
            }
            //过滤出交流价格
            List<OccupyPilePriceEntity> occupyPilePriceEntities1 = occupyPilePriceEntityList.stream().filter(o -> o.getDeviceType() == 2).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(occupyPilePriceEntities1)) {
                OccupyPilePriceDto.PriceInfoData priceInfoData = new OccupyPilePriceDto.PriceInfoData();

                OccupyPilePriceEntity occupyPilePriceEntity = occupyPilePriceEntities1.get(0);

                BeanUtils.copyProperties(occupyPilePriceEntity, priceInfoData);
                String configPriceInfo = occupyPilePriceEntity.getConfigPriceInfo();
                priceInfoData.setConfigPriceInfoStr(configPriceInfo);
                List<OccupyPilePriceDto.ConfigPriceInfo> configPriceInfoList = JSON.parseArray(configPriceInfo, OccupyPilePriceDto.ConfigPriceInfo.class);
                priceInfoData.setConfigPriceInfoList(configPriceInfoList);
                occupyPilePriceDto.setAcPriceInfoData(priceInfoData);
            }
        }
        return ResponseResult.ok(occupyPilePriceDto);
    }

    /**
     * 根据占桩id删除指定占桩费率信息
     *
     * @param pirceId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteOccupyPilePriceById(String pirceId) {
        Optional<OccupyPilePriceEntity> pilePriceDaoById = occupyPilePriceDao.findById(pirceId);
        if (pilePriceDaoById.isPresent()) {
            OccupyPilePriceEntity occupyPilePriceEntity = pilePriceDaoById.get();
            occupyPilePriceEntity.setIsDelete(2);
            occupyPilePriceDao.save(occupyPilePriceEntity);
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 新增或编辑白名单信息
     *
     * @param siteWhiteRosterChangeVo
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveWhiteRosterInfo(SiteWhiteRosterChangeVo siteWhiteRosterChangeVo) {
        SiteWhiteRosterEntity siteWhiteRosterEntity = new SiteWhiteRosterEntity();
        BeanUtils.copyProperties(siteWhiteRosterChangeVo, siteWhiteRosterEntity);
        siteWhiteRosterDao.save(siteWhiteRosterEntity);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 根据站点id查询白名单信息列表
     *
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<List<SiteWhiteRosterDto>> findSiteWhiteRosterList(String siteId) {
        List<SiteWhiteRosterDto> resultList = Lists.newArrayList();

        //根据站点id查询白名单信息
        List<SiteWhiteRosterEntity> siteWhiteRosterEntityList = siteWhiteRosterDao.findAllBySiteId(siteId);
        if (CollectionUtils.isNotEmpty(siteWhiteRosterEntityList)) {
            resultList = siteWhiteRosterEntityList.stream().map(siteWhiteRosterEntity -> {
                SiteWhiteRosterDto siteWhiteRosterDto = new SiteWhiteRosterDto();
                BeanUtils.copyProperties(siteWhiteRosterEntity, siteWhiteRosterDto);
                return siteWhiteRosterDto;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 根据白名单id删除相关信息
     *
     * @param whiteId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteSiteWhiteRosterById(String whiteId) {
        siteWhiteRosterDao.deleteById(whiteId);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 组装转换费率信息
     *
     * @param chargerPriceEntityList
     * @return
     */
    public List<EventRateReqPublicVo.TimeFrameRate> getEventRateReqPublic(List<ChargerPriceEntity> chargerPriceEntityList) {
        List<EventRateReqPublicVo.TimeFrameRate> cTimeFrameRate = Lists.newArrayList();

        chargerPriceEntityList.forEach(chargerPriceEntity -> {
            EventRateReqPublicVo.TimeFrameRate timeFrameRate = new EventRateReqPublicVo.TimeFrameRate();
            //时段开始时间
            String startTime = chargerPriceEntity.getStartTime();
            long startTamp = Timestamp.valueOf(startTime).getTime() / 1000 + 8 * 3600;
            timeFrameRate.setStartTime(startTamp);
            timeFrameRate.setEndTime(startTamp + 1800);
            timeFrameRate.setPrice((long) (chargerPriceEntity.getElectMoney().doubleValue() * 1000));
            BigDecimal serviceMoney = chargerPriceEntity.getServiceMoney();
            if (StringUtil.isNotEmpty(serviceMoney)) {
                timeFrameRate.setServiceCharger((long) (serviceMoney.doubleValue() * 1000));
            }
            Integer periodType = chargerPriceEntity.getPeriodType();
            if (periodType == 5) {
                timeFrameRate.setType(4);
            } else if (periodType == 6) {
                timeFrameRate.setType(3);
            } else {
                timeFrameRate.setType(periodType);
            }
            cTimeFrameRate.add(timeFrameRate);
        });
        return cTimeFrameRate;
    }

    /**
     * 校验vin是否在白名单中
     *
     * @param pileCode
     * @param accountCode
     * @return
     */
    @Override
    public ResponseResult<Boolean> checkAccountCode(String pileCode, String accountCode) {
        ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByCodes = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(pileCode));
        if (deviceBasicInfoByCodes.isSuccess() && !deviceBasicInfoByCodes.getData().isEmpty()) {
            DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByCodes.getData().get(pileCode);
            //查询当前电桩所属站点下白名单信息
            List<SiteWhiteRosterEntity> siteWhiteRosterEntities = siteWhiteRosterDao.findAllBySiteId(deviceBasicInfoDto.getSiteId());
            if (CollectionUtils.isNotEmpty(siteWhiteRosterEntities)) {
                List<SiteWhiteRosterEntity> whiteRosterEntities = siteWhiteRosterEntities.stream().filter(s -> /*s.getAuthorityType() == 2 && */s.getAuthorityAccount().equals(accountCode)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(whiteRosterEntities)) {
                    return ResponseResult.ok(true);
                }
            }
        }
        return ResponseResult.ok(false);
    }

    @Override
    public ResponseResult<PileResultDto> pileStart(PileStartVo pileStartVo) {
        if (StringUtil.isEmpty(pileStartVo.getPileCode())) {
            return ResponseResult.paramError("电桩编号为空");
        }
        //校验设备是否在线
        DeviceModel deviceModel = RedisDeviceUtil.getDevice(pileStartVo.getPileCode());
        if (deviceModel == null || StringUtil.isEmpty(deviceModel.getTxStatus()) || deviceModel.getTxStatus() == 0) {
            return ResponseResult.paramError("设备未注册, 不允许启动");
        }
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileStartVo.getPileCode());
        if (deviceModel.getTxStatus() == 88 || pileRealModel == null || StringUtil.isEmpty(pileRealModel.getWorkStatus()) || pileRealModel.getWorkStatus() == 88) {
            return ResponseResult.paramError("设备已离线, 不允许启动");
        }
        //根据电桩编码查询计费信息，如果没查到计费信息，则直接返回启动失败
        ResponseResult<Map<String, EventRateReqPublicVo>> siteRateInfoByPileCodes = findSiteRateInfoByPileCodes(Collections.singletonList(pileStartVo.getPileCode()));
        if (siteRateInfoByPileCodes.isSuccess() && !siteRateInfoByPileCodes.getData().isEmpty() && siteRateInfoByPileCodes.getData().containsKey(pileStartVo.getPileCode())) {
            EventRateReqPublicVo eventRateReqPublicVo = siteRateInfoByPileCodes.getData().get(pileStartVo.getPileCode());
            //费率id
            String rateTemplateId;
            if (pileStartVo.getRunMode() == 0) {
                rateTemplateId = eventRateReqPublicVo.getCRateId();
            } else {
                rateTemplateId = eventRateReqPublicVo.getDRateId();
            }
            if (StringUtil.isNotEmpty(rateTemplateId)) {
                ResponseResult<PileResultDto> pileResultDto = null;
                //生成订单号
                String orderNum = webAppFeignService.generateOrderNum(pileStartVo.getPileCode(), OrderSerialParamVo.PAY_NUMBER).getData();
                try {
                    //根据电桩编码，获取当前电桩所属站点id
                    DeviceBasicInfoDto deviceBasicInfoDto = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(pileStartVo.getPileCode())).getData().get(pileStartVo.getPileCode());
                    if (deviceBasicInfoDto == null) {
                        return ResponseResult.error("未找到该电桩信息");
                    }
                    if (StringUtil.isEmpty(deviceBasicInfoDto.getSiteId())) {
                        return ResponseResult.paramError("站点id不能为空");
                    }
                    //判断该站点是否支持预约
                    if (StringUtil.isNotEmpty(pileStartVo.getClockingTime())) {
                        //根据站点id查询站点数据
                        SiteInfoDto siteInfo = deviceService.findSiteBasicInfoByIds(Collections.singletonList(deviceBasicInfoDto.getSiteId()))
                                .getData().get(deviceBasicInfoDto.getSiteId());
                        if (siteInfo != null && StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                            JSONObject jsonObject = JSON.parseObject(siteInfo.getSiteReadwriteObject());
                            if (jsonObject.containsKey(SiteFieldParamVo.SUPPORT_ORDER) && StringUtil.isNotEmpty(jsonObject.get(SiteFieldParamVo.SUPPORT_ORDER))) {
                                Integer supportOrder = jsonObject.getInteger(SiteFieldParamVo.SUPPORT_ORDER);
                                if (supportOrder == 0) {
                                    return ResponseResult.paramError("该站点不支持预约");
                                }
                            }
                        }
                    }
                    //创建订单记录
                    OrderChangeVo orderChangeVo = new OrderChangeVo();
                    BeanUtils.copyProperties(pileStartVo, orderChangeVo);
                    orderChangeVo.setSiteId(deviceBasicInfoDto.getSiteId());
                    orderChangeVo.setGunCode(Integer.valueOf(pileStartVo.getGunCode()));
                    orderChangeVo.setOrderNum(orderNum);
                    orderChangeVo.setPrepayMoney(new BigDecimal("0.0"));
                    orderChangeVo.setOrderStatus(0);
                    orderChangeVo.setIsOrderly(2);
                    orderChangeVo.setPlatformLogo(PlatformLogoVo.SUNMAX_LOGO);
                    orderChangeVo.setSettlementState(0);
                    orderChangeVo.setPayWay(1);
                    orderChangeVo.setRateTemplateId(rateTemplateId);
                    webAppFeignService.createPileOrder(orderChangeVo);
                    pileStartVo.setSerialNum(orderNum);
                    pileStartVo.setSiteId(deviceBasicInfoDto.getSiteId());
                    pileResultDto = protocolService.pileStart(pileStartVo);
                    //如果电桩启动失败或请求超时，则修改订单状态为启动失败
                    if (pileResultDto.isSuccess() && pileResultDto.getData().getResult() != 0) {
                        //如果启动失败，先判断电桩实时缓存数据中该枪状态是否为空闲，如果是则修改订单状态为启动失败
                        PileRealModel pileReal = getPileRealModel(pileStartVo.getPileCode());
                        if (StringUtil.isNotEmpty(pileReal) && StringUtil.isNotEmpty(pileReal.getPileCode())) {
                            Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileReal.getGunRealModelMap();
                            if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(pileStartVo.getGunCode())) {
                                PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(pileStartVo.getGunCode());
                                if (gunRealModel.getGunStatus() == 0) {
                                    updateOrderStatus(orderNum);
                                }
                            }
                        }
                    }
                    return pileResultDto;
                } catch (Exception e) {
                    log.error("电桩启动失败", e);
                    //如果启动失败，先判断电桩实时缓存数据中该枪状态是否为空闲，如果是则修改订单状态为启动失败
                    PileRealModel pileReal = getPileRealModel(pileStartVo.getPileCode());
                    if (StringUtil.isNotEmpty(pileReal) && StringUtil.isNotEmpty(pileReal.getPileCode())) {
                        Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileReal.getGunRealModelMap();
                        if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(pileStartVo.getGunCode())) {
                            PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(pileStartVo.getGunCode());
                            if (gunRealModel.getGunStatus() == 0) {
                                updateOrderStatus(orderNum);
                            }
                        }
                    }
                    return pileResultDto;
                }
            }
        }
        return ResponseResult.error("启动失败，没查询到费率信息！");
    }

    /**
     * 根据订单编码修改订单状态
     *
     * @param orderNum 订单编码
     */
    private void updateOrderStatus(String orderNum) {
        //根据订单编码查询订单信息
        OrderRecordEntity orderRecord = orderRecordDao.findByOrderNum(orderNum);
        if (StringUtil.isNotEmpty(orderRecord)) {
            orderRecord.setOrderStatus(3);
            orderRecord.setAbnormalCode(JSON.toJSONString(Arrays.asList(4, 5)));
            orderRecordDao.save(orderRecord);
        }
    }

    @Override
    public ResponseResult<PileResultDto> pileStop(PileStopVo pileStopVo) {
        return protocolService.pileStop(pileStopVo);
    }

    @Override
    public ResponseResult<PileResultDto> pilePowerCtrl(PilePowerCtrlVo pilePowerCtrlVo) {
        return protocolService.powerCtrl(pilePowerCtrlVo);
    }

    /**
     * 网关重启
     *
     * @param deviceCode
     * @return
     */
    @Override
    public ResponseResult<String> rebootGateWey(String deviceCode) {
        RebootPublicVo rebootPublicVo = new RebootPublicVo();
        rebootPublicVo.setType(1);
        RebootSubscribeVo rebootSubscribeVo = protocolService.reboot(deviceCode, rebootPublicVo).getData();
        if (rebootSubscribeVo.getResult() == 0) {
            return ResponseResult.error("重启失败");
        }
        return ResponseResult.ok("重启成功");
    }

    /**
     * 根据站点id修改站点状态
     *
     * @param siteId     所属站点id
     * @param siteStatus 站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中
     * @return
     */
    @Override
    public ResponseResult<String> updateSiteStateById(String siteId, Integer siteStatus) {
        return deviceService.updateSiteStateById(siteId, siteStatus);
    }

    /**
     * 根据多个站点id查询充放电费率列表
     *
     * @param siteIds   多个站点id
     * @param priceType 价格类型 1-充电 2-放电
     * @return
     */
    @Override
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListBySiteIds(List<String> siteIds, Integer priceType) {
        Map<String, List<ChargerPriceRateDto>> resultMap = Maps.newHashMap();

        //根据设备所属站点id查询站点下充放电价格信息数据
        List<ChargerPriceInfoEntity> priceInfoEntities = chargerPriceInfoDao.findAllBySiteIdInAndPriceStateAndPriceType(siteIds, 1, priceType);
        if (CollectionUtils.isNotEmpty(priceInfoEntities)) {
            //过滤出直流
            List<ChargerPriceInfoEntity> dcPriceInfoEntities = priceInfoEntities.stream().filter(c -> c.getDeviceType() == 1).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(dcPriceInfoEntities)) {
                Map<String, ChargerPriceInfoEntity> siteIdToMap = dcPriceInfoEntities.stream().collect(Collectors.toMap(ChargerPriceInfoEntity::getSiteId, chargerPriceInfoEntity -> chargerPriceInfoEntity, (k1, k2) -> k1));
                //根据多个价格id查询费率配置信息
                Map<String, List<ChargerPriceEntity>> groupByPirceIdMap = chargerPriceDao.findAllByPriceIdIn(priceInfoEntities.stream().map(ChargerPriceInfoEntity::getId).collect(Collectors.toList()))
                        .stream().collect(Collectors.groupingBy(ChargerPriceEntity::getPriceId));
                //循环站点id，获取充电价格信息
                siteIds.forEach(siteId -> {
                    List<ChargerPriceRateDto> chargerPriceRateDtoList = Lists.newArrayList();
                    //获取站点的充电价格信息
                    if (siteIdToMap.containsKey(siteId)) {
                        ChargerPriceInfoEntity chargerPriceInfoEntity = siteIdToMap.get(siteId);
                        //根据价格id，获取充电价格列表信息
                        if (!groupByPirceIdMap.isEmpty() && groupByPirceIdMap.containsKey(chargerPriceInfoEntity.getId())) {
                            chargerPriceRateDtoList = groupByPirceIdMap.get(chargerPriceInfoEntity.getId()).stream().map(chargerPriceEntity -> {
                                ChargerPriceRateDto chargerPriceRateDto = new ChargerPriceRateDto();
                                BeanUtils.copyProperties(chargerPriceEntity, chargerPriceRateDto);
                                return chargerPriceRateDto;
                            }).collect(Collectors.toList());
                        }
                    }
                    resultMap.put(siteId, chargerPriceRateDtoList);
                });
            }
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据电桩编码查询站点充放电费率
     *
     * @param pileCodes
     * @return
     */
    @Override
    public ResponseResult<Map<String, EventRateReqPublicVo>> findSiteRateInfoByPileCodes(List<String> pileCodes) {
        Map<String, EventRateReqPublicVo> resultMap = Maps.newHashMap();

        ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByCodes = deviceService.findDeviceBasicInfoByCodes(pileCodes);
        if (deviceBasicInfoByCodes.isSuccess() && !deviceBasicInfoByCodes.getData().isEmpty()) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            deviceBasicInfoByCodes.getData().forEach((k, v) -> deviceBasicInfoDtoList.add(v));
            List<String> siteIdList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            //根据设备所属站点id查询站点下充放电价格信息数据
            List<ChargerPriceInfoEntity> priceInfoEntities = chargerPriceInfoDao.findAllBySiteIdInAndPriceState(siteIdList, 1);
            if (CollectionUtils.isNotEmpty(priceInfoEntities)) {
                Map<String, List<ChargerPriceInfoEntity>> groupBySiteIdMap = priceInfoEntities.stream().collect(Collectors.groupingBy(ChargerPriceInfoEntity::getSiteId));
                //根据多个价格id查询费率配置信息
                Map<String, List<ChargerPriceEntity>> groupByPirceIdMap = chargerPriceDao.findAllByPriceIdIn(priceInfoEntities.stream().map(ChargerPriceInfoEntity::getId).collect(Collectors.toList()))
                        .stream().collect(Collectors.groupingBy(ChargerPriceEntity::getPriceId));
                //循环电桩编码，获取费率信息
                pileCodes.forEach(pileCode -> {
                    EventRateReqPublicVo result = new EventRateReqPublicVo();
                    DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByCodes.getData().get(pileCode);
                    //获取电桩类型
                    String pileType = deviceBasicInfoDto.getTypeId();
                    if (StringUtil.isNotEmpty(pileType)) {
                        List<ChargerPriceInfoEntity> chargerPriceInfoEntityList = groupBySiteIdMap.get(deviceBasicInfoDto.getSiteId());
                        //如果电桩类型是v2g，则查询直流类型价格
                        if (Integer.parseInt(pileType) == 28) {
                            chargerPriceInfoEntityList = chargerPriceInfoEntityList.stream().filter(c -> c.getDeviceType() == 2).collect(Collectors.toList());
                        } else if (Integer.parseInt(pileType) == 29 || Integer.parseInt(pileType) == 30) {
                            chargerPriceInfoEntityList = chargerPriceInfoEntityList.stream().filter(c -> c.getDeviceType() == 1).collect(Collectors.toList());
                        }
                        if (CollectionUtils.isNotEmpty(chargerPriceInfoEntityList)) {
                            //充电价格信息
                            List<ChargerPriceInfoEntity> chargerPriceInfoEntities = chargerPriceInfoEntityList.stream().filter(c -> c.getPriceType() == 1).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(chargerPriceInfoEntities)) {
                                ChargerPriceInfoEntity chargerPriceInfoEntity = chargerPriceInfoEntities.get(0);
                                result.setCRateId(chargerPriceInfoEntity.getId());
                                result.setCTimeFrameNum(48);
                                //查询费率时段信息
                                List<ChargerPriceEntity> chargerPriceEntityList = getTariffRecordHalfHour(groupByPirceIdMap.get(chargerPriceInfoEntity.getId()), localDateToStr(LocalDate.now()));
                                result.setCTimeFrameRate(getEventRateReqPublic(chargerPriceEntityList));

                            }
                            //放电价格信息
                            List<ChargerPriceInfoEntity> dischargerPriceInfoEntities = chargerPriceInfoEntityList.stream().filter(c -> c.getPriceType() == 2).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(dischargerPriceInfoEntities)) {
                                ChargerPriceInfoEntity chargerPriceInfoEntity = dischargerPriceInfoEntities.get(0);
                                result.setDRateId(chargerPriceInfoEntity.getId());
                                result.setDTimeFrameNum(48);
                                //查询费率时段信息
                                List<ChargerPriceEntity> chargerPriceEntityList = getTariffRecordHalfHour(groupByPirceIdMap.get(chargerPriceInfoEntity.getId()), localDateToStr(LocalDate.now()));
                                result.setDTimeFrameRate(getEventRateReqPublic(chargerPriceEntityList));

                            }
                        }
                    }
                    resultMap.put(pileCode, result);
                });
            }
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据多个电桩编码查询占桩计费数据
     *
     * @param pileCodes
     * @return
     */
    @Override
    public ResponseResult<Map<String, OccupyPileRateDto>> findOccupyPileRateByPileCodes(List<String> pileCodes) {
        Map<String, OccupyPileRateDto> resultMap = Maps.newHashMap();
        //根据多个电桩编码查询设备基本数据
        ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByCodes = deviceService.findDeviceBasicInfoByCodes(pileCodes);
        if (deviceBasicInfoByCodes.isSuccess() && !deviceBasicInfoByCodes.getData().isEmpty()) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            deviceBasicInfoByCodes.getData().forEach((k, v) -> deviceBasicInfoDtoList.add(v));
            List<String> siteIdList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            //根据设备所属站点id查询站点下占桩价格信息数据
            List<OccupyPilePriceEntity> occupyPilePriceEntityList = occupyPilePriceDao.findAllBySiteIdInAndIsDelete(siteIdList, 1);
            if (CollectionUtils.isNotEmpty(occupyPilePriceEntityList)) {
                Map<String, List<OccupyPilePriceEntity>> groupBySiteIdMap = occupyPilePriceEntityList.stream().collect(Collectors.groupingBy(OccupyPilePriceEntity::getSiteId));
                //循环电桩编码，获取费率信息
                pileCodes.forEach(pileCode -> {
                    DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByCodes.getData().get(pileCode);
                    //获取电桩类型
                    String pileType = deviceBasicInfoDto.getTypeId();
                    if (StringUtil.isNotEmpty(pileType)) {
                        List<OccupyPilePriceEntity> occupyPilePriceEntities = groupBySiteIdMap.get(deviceBasicInfoDto.getSiteId());
                        //如果电桩类型是v2g，则查询直流类型价格
                        if (Integer.parseInt(pileType) == 7 || Integer.parseInt(pileType) == 6) {
                            occupyPilePriceEntities = occupyPilePriceEntities.stream().filter(c -> c.getDeviceType() == 1).collect(Collectors.toList());
                        } else {
                            occupyPilePriceEntities = occupyPilePriceEntities.stream().filter(c -> c.getDeviceType() == 2).collect(Collectors.toList());
                        }
                        if (CollectionUtils.isNotEmpty(occupyPilePriceEntities)) {
                            OccupyPileRateDto occupyPileRateDto = new OccupyPileRateDto();
                            OccupyPilePriceEntity occupyPilePriceEntity = occupyPilePriceEntities.get(0);
                            BeanUtils.copyProperties(occupyPilePriceEntity, occupyPileRateDto);
                            occupyPileRateDto.setConfigPriceInfoList(JSON.parseArray(occupyPilePriceEntity.getConfigPriceInfo(), OccupyPileRateDto.ConfigPriceInfo.class));
                            resultMap.put(pileCode, occupyPileRateDto);
                        }
                    }
                });
            }
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据多个电桩编码查询充放电费率列表
     *
     * @param pileCodes
     * @param priceType
     * @return
     */
    @Override
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPileCodes(List<String> pileCodes, Integer priceType) {
        Map<String, List<ChargerPriceRateDto>> resultMap = Maps.newHashMap();

        ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByCodes = deviceService.findDeviceBasicInfoByCodes(pileCodes);
        if (deviceBasicInfoByCodes.isSuccess() && !deviceBasicInfoByCodes.getData().isEmpty()) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            deviceBasicInfoByCodes.getData().forEach((k, v) -> deviceBasicInfoDtoList.add(v));
            List<String> siteIdList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
            //根据设备所属站点id查询站点下充放电价格信息数据
            List<ChargerPriceInfoEntity> priceInfoEntities = chargerPriceInfoDao.findAllBySiteIdInAndPriceStateAndPriceType(siteIdList, 1, priceType);
            if (CollectionUtils.isNotEmpty(priceInfoEntities)) {
                Map<String, List<ChargerPriceInfoEntity>> groupBySiteIdMap = priceInfoEntities.stream().collect(Collectors.groupingBy(ChargerPriceInfoEntity::getSiteId));
                //根据多个价格id查询费率配置信息
                Map<String, List<ChargerPriceEntity>> groupByPirceIdMap = chargerPriceDao.findAllByPriceIdIn(priceInfoEntities.stream().map(ChargerPriceInfoEntity::getId).collect(Collectors.toList()))
                        .stream().collect(Collectors.groupingBy(ChargerPriceEntity::getPriceId));
                //循环电桩编码，获取费率信息
                pileCodes.forEach(pileCode -> {
                    DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoByCodes.getData().get(pileCode);
                    //获取电桩类型
                    String pileType = deviceBasicInfoDto.getTypeId();
                    if (StringUtil.isNotEmpty(pileType)) {
                        List<ChargerPriceInfoEntity> chargerPriceInfoEntityList = groupBySiteIdMap.get(deviceBasicInfoDto.getSiteId());
                        //如果电桩类型是v2g，则查询直流类型价格
                        if (Integer.parseInt(pileType) == 29) {
                            chargerPriceInfoEntityList = chargerPriceInfoEntityList.stream().filter(c -> priceType.equals(c.getPriceType()) && c.getDeviceType() == 1).collect(Collectors.toList());
                        } else if (Integer.parseInt(pileType) == 28) {
                            chargerPriceInfoEntityList = chargerPriceInfoEntityList.stream().filter(c -> c.getDeviceType() == 2).collect(Collectors.toList());
                        } else if (Integer.parseInt(pileType) == 30) {
                            chargerPriceInfoEntityList = chargerPriceInfoEntityList.stream().filter(c -> c.getDeviceType() == 1).collect(Collectors.toList());
                        }
                        if (CollectionUtils.isNotEmpty(chargerPriceInfoEntityList)) {
                            ChargerPriceInfoEntity priceInfoEntity = chargerPriceInfoEntityList.get(0);
                            List<ChargerPriceEntity> chargerPriceEntities = groupByPirceIdMap.get(priceInfoEntity.getId());
                            if (CollectionUtils.isNotEmpty(chargerPriceEntities)) {
                                resultMap.put(pileCode, chargerPriceEntities.stream().map(chargerPriceEntity -> {
                                    ChargerPriceRateDto chargerPriceRateDto = new ChargerPriceRateDto();
                                    BeanUtils.copyProperties(chargerPriceEntity, chargerPriceRateDto);
                                    return chargerPriceRateDto;
                                }).collect(Collectors.toList()));
                            }
                        }
                    }
                });
            }
        }
        return ResponseResult.ok(resultMap);
    }


    /**
     * 保存或编辑白名单模式
     *
     * @param id         白名单模式唯一id
     * @param siteId     站点id
     * @param rosterMode 名单模式 1-仅白名单用户可用 2-白名单用户免费充电
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveOrUpdateRosterMode(String id, String siteId, Integer rosterMode) {

        SiteRosterModeEntity siteRosterModeEntity = new SiteRosterModeEntity();
        if (StringUtil.isEmpty(id)) {//新增
            //根据站点id查询一次，如果已有该站点白名单模式，则修改
            SiteRosterModeEntity siteRosterMode = siteRosterModeDao.findBySiteId(siteId);
            if (StringUtil.isNotEmpty(siteRosterMode)) {
                siteRosterModeEntity = siteRosterMode;
            }
        } else {
            siteRosterModeEntity.setId(id);
        }
        siteRosterModeEntity.setSiteId(siteId);
        siteRosterModeEntity.setRosterMode(rosterMode);
        siteRosterModeDao.save(siteRosterModeEntity);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    @Override
    public ResponseResult<SiteRosterModeDto> findRosterModeBySiteId(String siteId) {
        SiteRosterModeDto siteRosterModeDto = new SiteRosterModeDto();
        SiteRosterModeEntity siteRosterModeEntity = siteRosterModeDao.findBySiteId(siteId);
        if (StringUtil.isNotEmpty(siteRosterModeEntity)) {
            BeanUtils.copyProperties(siteRosterModeEntity, siteRosterModeDto);
        }
        return ResponseResult.ok(siteRosterModeDto);
    }

    /**
     * 根据多个费率id查询充放电费率列表
     *
     * @param priceInfoIds 多个费率id
     * @return
     */
    @Override
    public ResponseResult<Map<String, List<ChargerPriceRateDto>>> findChargerRateListByPriceInfoIds(List<String> priceInfoIds) {
        Map<String, List<ChargerPriceRateDto>> resultMap = Maps.newHashMap();

        List<ChargerPriceEntity> chargerPriceEntityList = chargerPriceDao.findAllByPriceIdIn(priceInfoIds);
        if (CollectionUtils.isNotEmpty(chargerPriceEntityList)) {
            resultMap = chargerPriceEntityList.stream().collect(Collectors.groupingBy(ChargerPriceEntity::getPriceId)).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().stream().map(chargerPriceEntity -> {
                ChargerPriceRateDto chargerPriceRateDto = new ChargerPriceRateDto();
                BeanUtils.copyProperties(chargerPriceEntity, chargerPriceRateDto);
                return chargerPriceRateDto;
            }).collect(Collectors.toList())));
        }
        return ResponseResult.ok(resultMap);
    }

    /**
     * 根据站点id查询白名单信息
     *
     * @param siteId    站点id
     * @param queryType 查询类型 1-用户手机号 2-车辆vin码(可为空，和查询数据参数联动)
     * @param queryData 查询数据(可为空，和查询类型参数联动)
     * @return
     */
    @Override
    public ResponseResult<SiteRosterInfoDto> findSiteWhiteRosterById(String siteId, Integer queryType, String queryData) {
        SiteRosterInfoDto siteRosterInfoDto = new SiteRosterInfoDto();

        //根据站点id查询白名单模式
        SiteRosterModeEntity siteRosterModeEntity = siteRosterModeDao.findBySiteId(siteId);
        if (StringUtil.isNotEmpty(siteRosterModeEntity)) {
            siteRosterInfoDto.setRosterMode(siteRosterModeEntity.getRosterMode());
        }
        List<SiteRosterInfoDto.WhiteRosterInfo> whiteRosterInfoList;
        //查询白名单信息
        List<SiteWhiteRosterEntity> siteWhiteRosterEntityList = siteWhiteRosterDao.findAll((Specification<SiteWhiteRosterEntity>) (root, cq, cb) -> {
            List<Predicate> list = new ArrayList<>();
            list.add(cb.equal(root.get("siteId"), siteId));
            if (StringUtil.isNotEmpty(queryType) && StringUtil.isNotEmpty(queryData)) {
                //鉴权类型查询
                list.add(cb.equal(root.get("authorityType"), queryType));
                //鉴权账户查询
                list.add(cb.equal(root.get("authorityAccount"), queryData));
            }
            return cb.and(list.toArray(new Predicate[0]));
        });
        if (CollectionUtils.isNotEmpty(siteWhiteRosterEntityList)) {
            whiteRosterInfoList = siteWhiteRosterEntityList.stream().map(siteWhiteRosterEntity -> {
                SiteRosterInfoDto.WhiteRosterInfo whiteRosterInfo = new SiteRosterInfoDto.WhiteRosterInfo();
                BeanUtils.copyProperties(siteWhiteRosterEntity, whiteRosterInfo);
                return whiteRosterInfo;
            }).collect(Collectors.toList());
            siteRosterInfoDto.setWhiteRosterInfoList(whiteRosterInfoList);
        }
        return ResponseResult.ok(siteRosterInfoDto);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> saveRosterMode(String siteId, Integer rosterMode) {
        SiteRosterModeEntity siteRosterModeEntity = new SiteRosterModeEntity();
        siteRosterModeEntity.setSiteId(siteId);
        siteRosterModeEntity.setRosterMode(rosterMode);
        siteRosterModeDao.save(siteRosterModeEntity);
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }

    /**
     * 查询系统设备列表
     *
     * @param siteId 站点唯一id
     * @return
     */
    @Override
    public ResponseResult<SystemDeviceListDto> querySystemDeviceList(String siteId) {
        SystemDeviceListDto result = new SystemDeviceListDto();
        //根据站点id查询所有设备信息
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteId)) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(siteId);
            //获取储能设备数据
            List<Integer> storageAssetType = getDeviceAssetType(4);
            List<DeviceBasicInfoDto> storageDeviceList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && storageAssetType.contains(Integer.parseInt(d.getTypeId()))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(storageDeviceList)) {
                result.setStorageDeviceList(storageDeviceList.stream().map(deviceBasicInfoDto -> {
                    SystemDeviceListDto.DeviceData deviceData = new SystemDeviceListDto.DeviceData();
                    BeanUtils.copyProperties(deviceBasicInfoDto, deviceData);
                    return deviceData;
                }).collect(Collectors.toList()));
            }
            //获取光伏设备数据
            List<Integer> pvAssetType = getDeviceAssetType(3);
            List<DeviceBasicInfoDto> pvDeviceList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && pvAssetType.contains(Integer.parseInt(d.getTypeId()))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pvDeviceList)) {
                result.setPvDeviceList(pvDeviceList.stream().map(deviceBasicInfoDto -> {
                    SystemDeviceListDto.DeviceData deviceData = new SystemDeviceListDto.DeviceData();
                    BeanUtils.copyProperties(deviceBasicInfoDto, deviceData);
                    return deviceData;
                }).collect(Collectors.toList()));
            }
            //获取充电桩设备数据
            List<Integer> pileAssetType = getDeviceAssetType(1);
            List<DeviceBasicInfoDto> pileDeviceList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && pileAssetType.contains(Integer.parseInt(d.getTypeId()))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(pileDeviceList)) {
                result.setPileDeviceList(pileDeviceList.stream().map(deviceBasicInfoDto -> {
                    SystemDeviceListDto.DeviceData deviceData = new SystemDeviceListDto.DeviceData();
                    BeanUtils.copyProperties(deviceBasicInfoDto, deviceData);
                    return deviceData;
                }).collect(Collectors.toList()));
            }
            //获取配电设备数据
            List<Integer> powerAssetType = getDeviceAssetType(6);
            List<DeviceBasicInfoDto> powerDeviceList = deviceBasicInfoDtoList.stream().filter(d -> StringUtil.isNotEmpty(d.getTypeId()) && powerAssetType.contains(Integer.parseInt(d.getTypeId()))).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(powerDeviceList)) {
                result.setPowerDeviceList(powerDeviceList.stream().map(deviceBasicInfoDto -> {
                    SystemDeviceListDto.DeviceData deviceData = new SystemDeviceListDto.DeviceData();
                    BeanUtils.copyProperties(deviceBasicInfoDto, deviceData);
                    return deviceData;
                }).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    @Override
    public ResponseResult<List<SiteInfoDto>> findSiteInfoByUserId(String userId, Integer scenarioTypes) {
        List<SiteInfoDto> resultList = Lists.newArrayList();
        //根据当前登录所属租户id，查询相关配置资产授权站点数据
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
        if (!allOrganEmpowerByTenantId.isSuccess() || CollectionUtils.isEmpty(allOrganEmpowerByTenantId.getData())) {
            return ResponseResult.ok(resultList);
        }

        List<OrganEmpowerListDto> organEmpowerListDtos = allOrganEmpowerByTenantId.getData();
        Map<String, OrganEmpowerListDto> organEmpowerListDtoMap = organEmpowerListDtos.stream()
                .collect(Collectors.toMap(OrganEmpowerListDto::getSiteId, Function.identity(), (k1, k2) -> k1));

        //根据站点id查询站点本体表结构信息
        ResponseResult<List<SiteInfoDto>> siteInfoListByIds = deviceService.findSiteInfoListByIds(organEmpowerListDtos.stream()
                .map(OrganEmpowerListDto::getSiteId)
                .filter(StringUtil::isNotEmpty)
                .distinct()
                .collect(Collectors.toList()));

        if (!siteInfoListByIds.isSuccess() || CollectionUtils.isEmpty(siteInfoListByIds.getData())) {
            return ResponseResult.ok(resultList);
        }

        //根据能源场景类型过滤
        List<SiteInfoDto> siteInfoDtoList = siteInfoListByIds.getData().stream()
                .filter(s -> isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains(String.valueOf(scenarioTypes))).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(siteInfoDtoList)) {
            return ResponseResult.ok(resultList);
        }
        Map<String, SiteInfoDto> siteInfoDtoMap = siteInfoDtoList.stream().collect(Collectors.toMap(SiteInfoDto::getId, Function.identity(), (k1, k2) -> k1));

        siteInfoDtoMap.forEach((k, v) -> {
            OrganEmpowerListDto organEmpowerListDto = organEmpowerListDtoMap.get(k);
            v.setAuthority(organEmpowerListDto.getAuthority());
            resultList.add(v);
        });
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<PageDto<PvSiteListDto>> findPvSiteListByPage(String userId, PvSiteListQueryVo pvSiteListQueryVo) {
        List<PvSiteListDto> resultList = Lists.newArrayList();
        //根据当前登录用户id，查询相关配置资产授权站点数据
        ResponseResult<List<OrganEmpowerListDto>> allOrganEmpowerByTenantId = systemService.findAllOrganEmpowerByUserId(userId);
        if (allOrganEmpowerByTenantId.isSuccess() && CollectionUtils.isNotEmpty(allOrganEmpowerByTenantId.getData())) {
            List<String> siteIds = allOrganEmpowerByTenantId.getData().stream().map(OrganEmpowerListDto::getSiteId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());

            //根据资产权限站点id查询站点信息数据
            List<SiteInfoDto> siteInfoDtoList = new ArrayList<>(deviceService.findSiteBasicInfoByIds(siteIds).getData().values())
                    .stream().filter(s -> StringUtil.isNotEmpty(s.getScenarioTypes()) && s.getScenarioTypes().contains("1")).collect(Collectors.toList());

            if (CollectionUtils.isEmpty(siteInfoDtoList)) {
                return ResponseResult.ok(new PageDto<>(resultList, pvSiteListQueryVo.getPage(), pvSiteListQueryVo.getSize()));
            }

            //根据站点状态查询
            if (StringUtil.isNotEmpty(pvSiteListQueryVo.getSiteStatus())) {
                siteInfoDtoList = siteInfoDtoList.stream().filter(o -> StringUtil.isNotEmpty(o.getSiteStatus()) && o.getSiteStatus().equals(pvSiteListQueryVo.getSiteStatus())).collect(Collectors.toList());
            }

            //根据区域查询
            if (StringUtil.isNotEmpty(pvSiteListQueryVo.getAreaType()) && StringUtil.isNotEmpty(pvSiteListQueryVo.getArea())) {
                siteInfoDtoList = siteInfoDtoList.stream()
                        .filter(siteInfoDto -> matchesPvAreaType(parseLocationMap(siteInfoDto.getSiteReadwriteObject()), pvSiteListQueryVo))
                        .collect(Collectors.toList());
            }

            //根据光伏类型查询
            if (StringUtil.isNotEmpty(pvSiteListQueryVo.getPvType())) {
                siteInfoDtoList = siteInfoDtoList.stream().filter(siteInfoDto -> {
                    List<SiteScenarioTypeDto> scenarioTypeList = siteInfoDto.getSiteScenarioTypeDtos().stream()
                            .filter(d -> StringUtil.isNotEmpty(d.getScenarioType()) && d.getScenarioType() == 1).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                        return CollectionUtils.isNotEmpty(scenarioTypeList.stream().filter(d -> isNotEmpty(d.getReadwriteObject())
                                        && matchesFieldParam(parseObjectMap(d.getReadwriteObject()), pvSiteListQueryVo.getPvType(), SiteFieldParamVo.PV_SYS_TYPE))
                                .collect(Collectors.toList()));
                    }
                    return false;
                }).collect(Collectors.toList());
            }

            //根据并网等级查询
            if (StringUtil.isNotEmpty(pvSiteListQueryVo.getTiedGrade())) {
                siteInfoDtoList = siteInfoDtoList.stream().filter(siteInfoDto -> {
                    List<SiteScenarioTypeDto> scenarioTypeList = siteInfoDto.getSiteScenarioTypeDtos().stream()
                            .filter(d -> StringUtil.isNotEmpty(d.getScenarioType()) && d.getScenarioType() == 1).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                        return CollectionUtils.isNotEmpty(scenarioTypeList.stream().filter(d -> isNotEmpty(d.getReadwriteObject())
                                        && matchesFieldParam(parseObjectMap(d.getReadwriteObject()), pvSiteListQueryVo.getTiedGrade(), SiteFieldParamVo.TIED_GRADE))
                                .collect(Collectors.toList()));
                    }
                    return false;
                }).collect(Collectors.toList());
            }

            //根据消纳方式查询
            if (StringUtil.isNotEmpty(pvSiteListQueryVo.getConsumMode())) {
                siteInfoDtoList = siteInfoDtoList.stream().filter(siteInfoDto -> {
                    List<SiteScenarioTypeDto> scenarioTypeList = siteInfoDto.getSiteScenarioTypeDtos().stream()
                            .filter(d -> StringUtil.isNotEmpty(d.getScenarioType()) && d.getScenarioType() == 1).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(scenarioTypeList)) {
                        return CollectionUtils.isNotEmpty(scenarioTypeList.stream().filter(d -> isNotEmpty(d.getReadwriteObject())
                                        && matchesFieldParam(parseObjectMap(d.getReadwriteObject()), pvSiteListQueryVo.getConsumMode(), SiteFieldParamVo.CONSUM_MODE))
                                .collect(Collectors.toList()));
                    }
                    return false;
                }).collect(Collectors.toList());
            }

            //根据站点名称查询
            if (StringUtil.isNotEmpty(pvSiteListQueryVo.getSiteName())) {
                siteInfoDtoList = siteInfoDtoList.stream().filter(o -> o.getSiteName().contains(pvSiteListQueryVo.getSiteName())).collect(Collectors.toList());
            }

            if (CollectionUtils.isEmpty(siteInfoDtoList)) {
                return ResponseResult.ok(new PageDto<>(resultList, pvSiteListQueryVo.getPage(), pvSiteListQueryVo.getSize()));
            }

            //根据多个站点id查询站点实时功率曲线数据
            List<String> siteIdList = siteInfoDtoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
            Map<String, Map<String, Object>> pvSitePowerCurve = findPvSitePowerData(siteIdList, getDayStart(localDateToStr(LocalDate.now())), getDayEnd(localDateToStr(LocalDate.now())), "1m");

            //循环站点信息获取数据
            siteInfoDtoList.forEach(siteBasicInfoDto -> {
                PvSiteListDto pvSiteListDto = new PvSiteListDto();
                BeanUtils.copyProperties(siteBasicInfoDto, pvSiteListDto);
                //获取站点位置信息
                String siteReadwriteObject = siteBasicInfoDto.getSiteReadwriteObject();
                if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                    Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
                    });
                    //站点位置信息对象
                    if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                        Map<String, Object> readwriteMap = JSON.parseObject(JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {
                        });
                        //省份
                        if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.PROVINCE) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.PROVINCE))) {
                            pvSiteListDto.setProvince(readwriteMap.get(SiteFieldParamVo.PROVINCE).toString());
                        }
                        //市级
                        if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.CITY) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.CITY))) {
                            pvSiteListDto.setCity(readwriteMap.get(SiteFieldParamVo.CITY).toString());
                        }
                        //所在区县
                        if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.COUNTY) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.COUNTY))) {
                            pvSiteListDto.setCounty(readwriteMap.get(SiteFieldParamVo.COUNTY).toString());
                        }
                        //详细地址
                        if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.ADDRESS) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.ADDRESS))) {
                            pvSiteListDto.setAddress(readwriteMap.get(SiteFieldParamVo.ADDRESS).toString());
                        }
                    }
                    //光伏类型
                    if (parseObjectMap.containsKey(SiteFieldParamVo.PV_TYPE) && StringUtil.isNotEmpty(parseObjectMap.get(SiteFieldParamVo.PV_TYPE))) {
                        pvSiteListDto.setPvType(Integer.parseInt(parseObjectMap.get(SiteFieldParamVo.PV_TYPE).toString()));
                    }
                    //消纳方式
                    if (parseObjectMap.containsKey(SiteFieldParamVo.CONSUM_MODE) && StringUtil.isNotEmpty(parseObjectMap.get(SiteFieldParamVo.CONSUM_MODE))) {
                        pvSiteListDto.setConsumMode(String.valueOf(parseObjectMap.get(SiteFieldParamVo.CONSUM_MODE).toString()));
                    }
                    //光伏站装机量
                    if (parseObjectMap.containsKey(SiteFieldParamVo.PV_CAPACITY) && StringUtil.isNotEmpty(parseObjectMap.get(SiteFieldParamVo.PV_CAPACITY))) {
                        pvSiteListDto.setPvCapacity(Double.parseDouble(String.valueOf(parseObjectMap.get(SiteFieldParamVo.PV_CAPACITY))));
                    }
                    //电站投运时间
                    if (parseObjectMap.containsKey(SiteFieldParamVo.OFFICIAL_RUN_TIME) && StringUtil.isNotEmpty(parseObjectMap.get(SiteFieldParamVo.OFFICIAL_RUN_TIME))) {
                        pvSiteListDto.setOfficialRunTime(String.valueOf(parseObjectMap.get(SiteFieldParamVo.OFFICIAL_RUN_TIME)));
                    }
                    //并网等级
                    if (parseObjectMap.containsKey(SiteFieldParamVo.TIED_GRADE) && StringUtil.isNotEmpty(parseObjectMap.get(SiteFieldParamVo.TIED_GRADE))) {
                        pvSiteListDto.setTiedGrade(Integer.parseInt(String.valueOf(parseObjectMap.get(SiteFieldParamVo.TIED_GRADE))));
                    }
                }
                //获取当前站点历史曲线和实时数据
                if (MapUtils.isNotEmpty(pvSitePowerCurve) && pvSitePowerCurve.containsKey(siteBasicInfoDto.getId()) && MapUtils.isNotEmpty(pvSitePowerCurve.get(siteBasicInfoDto.getId()))) {
                    Map<String, Object> dataMap = pvSitePowerCurve.get(siteBasicInfoDto.getId());

                    //获取今日功率曲线数据
                    if (dataMap.containsKey("dataList") && StringUtil.isNotEmpty(dataMap.get("dataList")) && dataMap.get("dataList") instanceof List<?>) {
                        pvSiteListDto.setRealPowerList(JSON.parseArray(JSON.toJSONString(dataMap.get("dataList")), Double.class));
                    }
                    //获取今日功率曲线时间轴数据
                    if (dataMap.containsKey("dateList") && StringUtil.isNotEmpty(dataMap.get("dateList")) && dataMap.get("dateList") instanceof List<?>) {
                        pvSiteListDto.setXAxisList((JSON.parseArray(JSON.toJSONString(dataMap.get("dateList")), String.class)));
                    }

                    //获取站点实时功率
                    if (dataMap.containsKey(FunctionLogoParamVo.ACTIVE_POWER) && StringUtil.isNotEmpty(dataMap.get(FunctionLogoParamVo.ACTIVE_POWER))) {
                        pvSiteListDto.setRealPower(Double.parseDouble(String.valueOf(dataMap.get(FunctionLogoParamVo.ACTIVE_POWER))));
                    }

                    //获取站点今日发电量
                    if (dataMap.containsKey(FunctionLogoParamVo.TOTAL_POWER_GENERATION) && StringUtil.isNotEmpty(dataMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION))) {
                        pvSiteListDto.setDayQt(Double.parseDouble(String.valueOf(dataMap.get(FunctionLogoParamVo.TOTAL_POWER_GENERATION))));
                    }
                }

                //计算实时功率归一化(实时功率 / 光伏站点装机量 * 100)
                if (StringUtil.isNotEmpty(pvSiteListDto.getRealPower()) && StringUtil.isNotEmpty(pvSiteListDto.getPvCapacity()) && pvSiteListDto.getPvCapacity() > 0.0) {
                    pvSiteListDto.setPowerAtOne(getToDouble(pvSiteListDto.getRealPower() / pvSiteListDto.getPvCapacity() * 100));
                }

                //计算今日发电小时数据(今日发电量 / 光伏站点装机量)
                if (StringUtil.isNotEmpty(pvSiteListDto.getDayQt()) && StringUtil.isNotEmpty(pvSiteListDto.getPvCapacity()) && pvSiteListDto.getPvCapacity() > 0.0) {
                    pvSiteListDto.setDayQtHours(getToDouble(pvSiteListDto.getDayQt() / pvSiteListDto.getPvCapacity()));
                }
                resultList.add(pvSiteListDto);
            });

        }
        return ResponseResult.ok(new PageDto<>(resultList, pvSiteListQueryVo.getPage(), pvSiteListQueryVo.getSize()));
    }

    /**
     * 根据价格id修改价格状态
     *
     * @param pirceId    价格id
     * @param priceState 价格状态 1-生效中 2-待生效 3-已失效
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> updatePriceStateById(String pirceId, Integer priceState) {
        Optional<ChargerPriceInfoEntity> byId = chargerPriceInfoDao.findById(pirceId);
        if (byId.isPresent()) {
            ChargerPriceInfoEntity chargerPriceInfoEntity = byId.get();
            chargerPriceInfoEntity.setPriceState(priceState);
            chargerPriceInfoDao.save(chargerPriceInfoEntity);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.ok(ResponseResult.FAIL);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> applyPriceInfoById(String pirceId, String siteIds) {
        List<String> siteIdList = JSONArray.parseArray(siteIds, String.class);
        Optional<ChargerPriceInfoEntity> byId = chargerPriceInfoDao.findById(pirceId);
        if (byId.isPresent()) {
            ChargerPriceInfoEntity chargerPriceInfoEntity = byId.get();

            //根据多个站点id，查询站点下已经有过的当前类型生效中的价格信息
            List<ChargerPriceInfoEntity> chargerPriceInfoEntities = chargerPriceInfoDao.findAllBySiteIdInAndPriceStateAndPriceTypeAndDeviceType(siteIdList, 1,
                    chargerPriceInfoEntity.getPriceType(), chargerPriceInfoEntity.getDeviceType());
            if (CollectionUtils.isNotEmpty(chargerPriceInfoEntities)) {
                //将查询出的价格信息修改为已失效
                chargerPriceInfoDao.saveAll(chargerPriceInfoEntities.stream().peek(c -> c.setPriceState(3)).collect(Collectors.toList()));
            }
            //根据价格id，查询时段信息数据
            List<ChargerPriceEntity> chargerPriceEntityList = chargerPriceDao.findAllByPriceIdIn(Collections.singletonList(pirceId));
            //批量保存多个站点的价格数据
            Map<String, ChargerPriceInfoEntity> chargerPriceInfoEntityMap = chargerPriceInfoDao.saveAll(siteIdList.stream().map(siteId -> {
                ChargerPriceInfoEntity result = new ChargerPriceInfoEntity();
                BeanUtils.copyProperties(chargerPriceInfoEntity, result);
                result.setSiteId(siteId);
                result.setId(UUID.randomUUID().toString().replace(FileUtil.BAR, FileUtil.separator).substring(0, 16));
                return result;
            }).collect(Collectors.toList())).stream().collect(Collectors.toMap(ChargerPriceInfoEntity::getSiteId, Function.identity(), (v1, v2) -> v1));
            //组装多个站点下的时段数据
            List<ChargerPriceEntity> chargerPriceList = Lists.newArrayList();
            siteIdList.forEach(siteId -> {
                ChargerPriceInfoEntity chargerPriceInfo = chargerPriceInfoEntityMap.get(siteId);
                //循环组装站点下时段数据
                chargerPriceList.addAll(chargerPriceEntityList.stream().map(c -> {
                    ChargerPriceEntity result = new ChargerPriceEntity();
                    BeanUtils.copyProperties(c, result);
                    result.setId(null);
                    result.setPriceId(chargerPriceInfo.getId());
                    return result;
                }).collect(Collectors.toList()));
            });
            chargerPriceDao.saveAll(chargerPriceList);
            return ResponseResult.ok(ResponseResult.SUCCESS);
        }
        return ResponseResult.ok(ResponseResult.FAIL);
    }

    @Override
    public ResponseResult<Void> pileReset(PileResetVo pileResetVo) {
        return protocolService.pileReset(pileResetVo);
    }

    private Map<String, Object> parseLocationMap(String siteReadwriteObject) {
        Map<String, Object> readwriteMap = Maps.newHashMap();
        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
            Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
            });
            if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                readwriteMap = JSON.parseObject(JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {
                });
            }
        }
        return readwriteMap;
    }

    public static boolean matchesPvAreaType(Map<String, Object> readwriteMap, PvSiteListQueryVo pvSiteListQueryVo) {
        String areaKey = pvSiteListQueryVo.getAreaType() == 1 ? SiteFieldParamVo.PROVINCE : SiteFieldParamVo.CITY;
        Object areaValue = readwriteMap.get(areaKey);

        return areaValue != null && areaValue.equals(pvSiteListQueryVo.getArea());
    }

    private Map<String, Object> parseObjectMap(String siteReadwriteObject) {
        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
            return JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {
            });
        }
        return Maps.newHashMap();
    }


    private boolean matchesFieldParam(Map<String, Object> parseObjectMap, Integer data, String fieldParam) {
        return parseObjectMap.containsKey(fieldParam) &&
                StringUtil.isNotEmpty(parseObjectMap.get(fieldParam)) &&
                parseObjectMap.get(fieldParam).equals(String.valueOf(data));
    }

    /**
     * 根据多个站点id查询站点光伏历史功率和实时功能点数据(根据单个站点返回)
     *
     * @param siteIdList   多个站点id
     * @param startTime    开始时间(yyyy-MM-dd HH:mm:ss)
     * @param endTime      结束时间(yyyy-MM-dd HH:mm:ss)
     * @param timeInterval 时间间隔
     * @return 站点id -> dataList -> 数据列表
     * dateList -> 时间列表
     * 功能点标识 -> 数据
     */
    public Map<String, Map<String, Object>> findPvSitePowerData(List<String> siteIdList, String startTime, String endTime, String timeInterval) {
        Map<String, Map<String, Object>> resultMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(siteIdList) || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
            return resultMap;
        }
        //获取今日每分钟时间轴
        List<String> dateList = getLocalDateTimeBetween(strToLocalDateTime(startTime), strToLocalDateTime(endTime), timeInterval).stream().map(DateUtil::localDateTimeToStr).collect(Collectors.toList());

        //根据站点id，查询站点下所有设备列表,并过滤出逆变器设备
        List<DeviceBasicInfoDto> deviceInverterList = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, null).getData().values().stream()
                .flatMap(Collection::stream).filter(d -> Objects.equals(d.getTypeId(), "20") || Objects.equals(d.getTypeId(), "77"))
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(deviceInverterList)) {
            return resultMap;
        }
        Set<String> deviceIds = deviceInverterList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());

        //查询逆变器设备所有实时功率功能点历史数据
        DeviceHistoryQueryVo devicePowerVo = new DeviceHistoryQueryVo();
        devicePowerVo.setDeviceIds(deviceIds);
        devicePowerVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.ACTIVE_POWER));
        devicePowerVo.setStartTime(startTime);
        devicePowerVo.setEndTime(endTime);
        devicePowerVo.setTimeInterval(timeInterval);
        //查询逆变器功能点历史数据
        Map<String, Map<String, List<DeviceHistoryDto>>> devicePowerMap = dataService.findDeviceHistoryValueList(devicePowerVo).getData();

        //查询逆变器今日充电量数据
        DeviceHistoryQueryVo dayQtQueryVo = new DeviceHistoryQueryVo();
        dayQtQueryVo.setDeviceIds(deviceIds);
        dayQtQueryVo.setFunctionLogos(Collections.singleton(FunctionLogoParamVo.TOTAL_POWER_GENERATION));
        dayQtQueryVo.setStartTime(DateUtil.getDayStart(DateUtil.localDateToStr(LocalDate.now())));
        dayQtQueryVo.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
        dayQtQueryVo.setTimeInterval("1d");
        Map<String, Map<String, List<NodeDifHistoryDto>>> deviceDayQtMap = dataService.findNodeDifHistoryListFeign(dayQtQueryVo).getData();

        //根据多个设备id，和多个功能点标识查询设备功能点实时数据
        Map<String, Map<String, RealDataModel>> deviceRealDataMap = deviceService.getDeviceFunctionsRealDataByIds(deviceIds,
                FunctionLogoParamVo.ACTIVE_POWER, 2).getData();

        //根据站点id分组并循环
        deviceInverterList.stream().collect(Collectors.groupingBy(DeviceBasicInfoDto::getSiteId))
                .forEach((siteId, deviceList) -> {
                    Map<String, Object> dataMap = Maps.newHashMap();

                    //过滤出当前站点下所有逆变器设备的历史数据
                    List<Map<String, List<DeviceHistoryDto>>> deviceHistoryList = deviceList.stream()
                            .map(deviceBasicInfoDto -> devicePowerMap.getOrDefault(deviceBasicInfoDto.getId(), Maps.newHashMap()))
                            .collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(deviceHistoryList)) {
                        // 存储所有历史数据
                        List<DeviceHistoryDto> deviceHistoryDtoList = deviceHistoryList.stream()
                                .flatMap(deviceHistoryMap -> deviceHistoryMap.getOrDefault(FunctionLogoParamVo.ACTIVE_POWER, Lists.newArrayList()).stream())
                                .collect(Collectors.toList());

                        List<Object> dataList = Lists.newArrayList();
                        //根据时间分组
                        Map<String, List<DeviceHistoryDto>> groupByDateMap = deviceHistoryDtoList.stream().collect(Collectors.groupingBy(DeviceHistoryDto::getDateTime));
                        dateList.forEach(dateTime -> {
                            if (groupByDateMap.containsKey(dateTime)) {
                                dataList.add(getToDouble(groupByDateMap.get(dateTime).stream().filter(d -> StringUtil.isNotEmpty(d.getDataValue())).mapToDouble(d -> Double.parseDouble(String.valueOf(d.getDataValue()))).sum()));
                            } else {
                                dataList.add(null);
                            }
                        });
                        dataMap.put("dataList", dataList);
                        dataMap.put("dateList", dateList);
                    }

                    //统计有功功率
                    dataMap.put(FunctionLogoParamVo.ACTIVE_POWER, getToDouble(deviceList.stream()
                            .filter(d -> deviceRealDataMap.containsKey(d.getId()))
                            .map(d -> deviceRealDataMap.get(d.getId()).get(FunctionLogoParamVo.ACTIVE_POWER))
                            .filter(Objects::nonNull)
                            .mapToDouble(d -> DoubleUtil.objToDouble(d.getDataValue())).sum()));

                    //统计今日充电量
                    dataMap.put(FunctionLogoParamVo.TOTAL_POWER_GENERATION, getToDouble(deviceList.stream()
                            .filter(d -> deviceDayQtMap.containsKey(d.getId()))
                            .flatMap(d -> deviceDayQtMap.get(d.getId()).get(FunctionLogoParamVo.TOTAL_POWER_GENERATION).stream())
                            .filter(Objects::nonNull)
                            .mapToDouble(d -> DoubleUtil.getObjSub(d.getFirstDataValue(), d.getLastDataValue())).sum()));

                    resultMap.put(siteId, dataMap);
                });
        return resultMap;
    }


}
