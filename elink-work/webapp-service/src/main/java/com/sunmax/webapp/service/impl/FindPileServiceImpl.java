package com.sunmax.webapp.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.operate.ChargerPriceRateDto;
import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.dto.together.AppletUserInfoDto;
import com.sunmax.common.dto.together.SiteRosterInfoDto;
import com.sunmax.common.dto.webapp.ChargePriceInfoDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.SiteFieldParamVo;
import com.sunmax.webapp.dto.ChargeDeviceInfoDto;
import com.sunmax.webapp.dto.SiteDataListDto;
import com.sunmax.webapp.dto.SiteDetailsDataDto;
import com.sunmax.webapp.dto.SitePileDetailsDto;
import com.sunmax.webapp.service.FindPileService;
import com.sunmax.webapp.service.feign.DeviceService;
import com.sunmax.webapp.service.feign.SystemService;
import com.sunmax.webapp.service.feign.TogetherService;
import com.sunmax.webapp.vo.SiteQueryVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModel;
import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModelList;
import static com.sunmax.common.util.CommonUtil.distance;
import static com.sunmax.common.util.DoubleUtil.getToDouble;
import static com.sunmax.common.util.StringUtil.convertGunStatus;

@Slf4j
@Service
public class FindPileServiceImpl implements FindPileService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private TogetherService togetherService;

    /**
     * 查询站点列表
     * @param siteQueryVo
     * @return
     */
    @Override
    public ResponseResult<?> querySiteList(SiteQueryVo siteQueryVo) {
        List<SiteDataListDto> resultList = Lists.newArrayList();

        //根据小程序登录标识查询站点id列表
        ResponseResult<AppletDto> appletByAppletCode = systemService.findAppletByAppletCode(siteQueryVo.getAppletKey());
        if (appletByAppletCode.isSuccess() && StringUtil.isNotEmpty(appletByAppletCode.getData()) && !appletByAppletCode.getData().getTenantSiteIdMap().isEmpty()) {
            //有权限查看的站点id列表
            List<String> siteIdList = appletByAppletCode.getData().getTenantSiteIdMap().values().stream().flatMap(Set::stream).collect(Collectors.toList());

            //根据多个站点id查询站点详情信息
            ResponseResult<Map<String, SiteInfoDto>> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIdList);
            if (siteInfoMap.isSuccess() && !siteInfoMap.getData().isEmpty()) {
                List<SiteInfoDto> siteInfoDtoList = Lists.newArrayList(siteInfoMap.getData().values());

                //根据停车费用类型查询
                if (StringUtil.isNotEmpty(siteQueryVo.getParkCostType())) {
                    siteInfoDtoList = siteInfoDtoList.stream()
                            .filter(siteInfoDto -> {
                                String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                                if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                                    Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                                    return parseObjectMap.containsKey(SiteFieldParamVo.PARK_TYPE) && parseObjectMap.get(SiteFieldParamVo.PARK_TYPE).equals(siteQueryVo.getParkCostType());
                                }
                                return false;
                            })
                            .collect(Collectors.toList());
                }

                if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                    List<String> siteIds = siteInfoDtoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
                    //根据多个站点id，查询站点下所有充电桩设备列表
                    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIds, 1);
                    if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                        List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().values().stream().flatMap(List::stream).collect(Collectors.toList());

                        //根据充电方式查询
                        if (StringUtil.isNotEmpty(siteQueryVo.getChargeMode())) {
                            deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream().filter(d -> d.getTypeId().equals(String.valueOf(siteQueryVo.getChargeMode()))).collect(Collectors.toList());
                        }

                        // 根据是否只看空闲查询
                        if (StringUtil.isNotEmpty(siteQueryVo.getIsIdle())) {
                            // 根据多个电桩编码查询电桩实时数据
                            List<PileRealModel> pileRealModelList = getPileRealModelList(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).collect(Collectors.toList()));
                            if (CollectionUtils.isNotEmpty(pileRealModelList)) {
                                Map<String, PileRealModel> pileRealModelMap = pileRealModelList.stream().collect(Collectors.toMap(PileRealModel::getPileCode, Function.identity(), (k1, k2) -> k1));

                                deviceBasicInfoDtoList = deviceBasicInfoDtoList.stream()
                                        .filter(deviceBasicInfoDto -> {
                                            PileRealModel pileRealModel = pileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber());
                                            return pileRealModel != null
                                                    && StringUtil.isNotEmpty(pileRealModel.getPileCode())
                                                    && !pileRealModel.getGunRealModelMap().isEmpty()
                                                    && pileRealModel.getGunRealModelMap().values().stream()
                                                    .anyMatch(gunRealModel -> gunRealModel.getGunStatus() == 0);
                                        })
                                        .collect(Collectors.toList());
                            }
                        }

                        if (CollectionUtils.isNotEmpty(deviceBasicInfoDtoList)) {
                            // 从siteInfoDtoList中过滤出符合的站点
                            Set<String> deviceSiteIds = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getSiteId).collect(Collectors.toSet());
                            siteInfoDtoList = siteInfoDtoList.stream().filter(siteInfoDto -> deviceSiteIds.contains(siteInfoDto.getId())).collect(Collectors.toList());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(siteInfoDtoList)) {
                    List<String> siteIds = siteInfoDtoList.stream().map(SiteInfoDto::getId).collect(Collectors.toList());
                    //根据多个站点id，查询站点下所有充电桩设备列表
                    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIds, 1);
                    //电枪信息map
                    Map<String, List<DeviceGunInfoDto>> deviceGunInfoMap = Maps.newHashMap();
                    //电桩实时数据
                    Map<String, PileRealModel> pileRealModelMap = Maps.newHashMap();
                    if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                        List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().values().stream().flatMap(List::stream).collect(Collectors.toList());
                        //根据多个设备id查询电枪列表数据
                        deviceGunInfoMap = deviceService.findDeviceGunInfoByDeviceIds(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList())).getData();

                        //根据多个电桩编码查询电桩实时数据
                        List<PileRealModel> pileRealModelList = getPileRealModelList(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).collect(Collectors.toList()));
                        if (CollectionUtils.isNotEmpty(pileRealModelList)) {
                            pileRealModelMap = pileRealModelList.stream().collect(Collectors.toMap(PileRealModel::getPileCode, Function.identity(), (k1, k2) -> k1));
                        }
                    }
                    //根据多个站点id，查询直流充电价格数据
                    Map<String, List<ChargerPriceRateDto>> chargerPriceRateMap = togetherService.findChargerRateListBySiteIds(siteIds, 1, 1).getData();

                    Map<String, List<DeviceGunInfoDto>> finalDeviceGunInfoMap = deviceGunInfoMap;
                    Map<String, PileRealModel> finalPileRealModelMap = pileRealModelMap;
                    List<SiteDataListDto> finalResultList = resultList;
                    siteInfoDtoList.forEach(siteInfoDto -> {
                        SiteDataListDto siteDataListDto = new SiteDataListDto();
                        siteDataListDto.setSiteId(siteInfoDto.getId());
                        siteDataListDto.setSiteName(siteInfoDto.getSiteName());
                        siteDataListDto.setSiteStatus(siteInfoDto.getSiteStatus());
                        siteDataListDto.setScenarioTypes(siteInfoDto.getScenarioTypes());
                        //获取站点位置信息
                        String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                        if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                            Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                            //获取站点位置信息
                            if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                                String location = JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION));
                                Map<String, Object> readwriteMap = JSON.parseObject(location, new TypeReference<Map<String, Object>>() {});
                                siteDataListDto.setLocation(location);

                                //经度
                                if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.LONGITUDE) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.LONGITUDE))) {
                                    siteDataListDto.setStationLng(Double.parseDouble(readwriteMap.get(SiteFieldParamVo.LONGITUDE).toString()));
                                }
                                //纬度
                                if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.LATITUDE) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.LATITUDE))) {
                                    siteDataListDto.setStationLat(Double.parseDouble(readwriteMap.get(SiteFieldParamVo.LATITUDE).toString()));
                                }
                                //详细地址
                                if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.ADDRESS) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.ADDRESS))) {
                                    siteDataListDto.setAddress(readwriteMap.get(SiteFieldParamVo.ADDRESS).toString());
                                }
                                //根据小程序用户当前所在位置经纬度和站点经纬度计算距离
                                if (StringUtil.isNotEmpty(siteDataListDto.getStationLng()) && StringUtil.isNotEmpty(siteDataListDto.getStationLat()) &&
                                    StringUtil.isNotEmpty(siteQueryVo.getCenterLon()) && StringUtil.isNotEmpty(siteQueryVo.getCenterLat())) {
                                    siteDataListDto.setDistance(calculateDistance(siteQueryVo.getCenterLon(), siteQueryVo.getCenterLat(), siteDataListDto.getStationLng(), siteDataListDto.getStationLat()));
                                }
                            }
                            //获取站点停车费类型
                            if (parseObjectMap.containsKey(SiteFieldParamVo.PARK_TYPE)) {
                                siteDataListDto.setParkCostType(Integer.parseInt(String.valueOf(parseObjectMap.get(SiteFieldParamVo.PARK_TYPE))));
                            }
                        }
                        //获取站点下电桩设备
                        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteInfoDto.getId())) {
                            //快充空闲数量
                            AtomicReference<Integer> fastChargeIdleNum = new AtomicReference<>(0);
                            //快充总数量
                            AtomicReference<Integer> fastChargeTotalNum = new AtomicReference<>(0);
                            //慢充空闲数量
                            AtomicReference<Integer> slowChargeIdleNum = new AtomicReference<>(0);
                            //慢充总数量
                            AtomicReference<Integer> slowChargeTotalNum = new AtomicReference<>(0);
                            deviceBasicInfoBySiteIds.getData().get(siteInfoDto.getId()).forEach(deviceBasicInfoDto -> {
                                //获取电桩下电枪信息
                                if (!finalDeviceGunInfoMap.isEmpty() && finalDeviceGunInfoMap.containsKey(deviceBasicInfoDto.getId())) {
                                    //循环电枪信息
                                    finalDeviceGunInfoMap.get(deviceBasicInfoDto.getId()).forEach(deviceGunInfoDto -> {
                                        //直流和v2g为快充,交流为慢充
                                        if (StringUtil.isNotEmpty(deviceBasicInfoDto.getTypeId()) &&
                                                Integer.parseInt(deviceBasicInfoDto.getTypeId()) == 29 || Integer.parseInt(deviceBasicInfoDto.getTypeId()) == 30) {
                                            fastChargeTotalNum.updateAndGet(v -> v + 1);
                                            //判断快充电桩是否为空闲状态
                                            if (!finalPileRealModelMap.isEmpty() && finalPileRealModelMap.containsKey(deviceBasicInfoDto.getDeviceNumber())) {
                                                Map<String, PileRealModel.GunRealModel> gunRealModelMap = finalPileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber()).getGunRealModelMap();
                                                if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(deviceGunInfoDto.getGunCode()) && gunRealModelMap.get(deviceGunInfoDto.getGunCode()).getGunStatus() == 0) {
                                                    fastChargeIdleNum.updateAndGet(v -> v + 1);
                                                }
                                            }
                                        } else if (StringUtil.isNotEmpty(deviceBasicInfoDto.getTypeId()) && Integer.parseInt(deviceBasicInfoDto.getTypeId()) == 28){
                                            slowChargeTotalNum.updateAndGet(v -> v + 1);
                                            //判断慢充电桩是否为空闲状态
                                            if (!finalPileRealModelMap.isEmpty() && finalPileRealModelMap.containsKey(deviceBasicInfoDto.getDeviceNumber())) {
                                                Map<String, PileRealModel.GunRealModel> gunRealModelMap = finalPileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber()).getGunRealModelMap();
                                                if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(deviceGunInfoDto.getGunCode()) && gunRealModelMap.get(deviceGunInfoDto.getGunCode()).getGunStatus() == 0) {
                                                    slowChargeIdleNum.updateAndGet(v -> v + 1);
                                                }
                                            }
                                        }
                                    });
                                }
                            });
                            siteDataListDto.setFastIdleNum(fastChargeIdleNum.get());
                            siteDataListDto.setFastTotalNum(fastChargeTotalNum.get());
                            siteDataListDto.setSlowIdleNum(slowChargeIdleNum.get());
                            siteDataListDto.setSlowTotalNum(slowChargeTotalNum.get());
                        }
                        //获取充电价格
                        if (!chargerPriceRateMap.isEmpty() && chargerPriceRateMap.containsKey(siteInfoDto.getId())) {
                            //当前时段电费
                            BigDecimal chargeCost = new BigDecimal("0.0");
                            String nowTime = DateUtil.localTimeToStr(LocalTime.now());
                            for (ChargerPriceRateDto chargerPriceRateDto : chargerPriceRateMap.get(siteInfoDto.getId())) {
                                String startTime = chargerPriceRateDto.getStartTime() + ":59";
                                String endTime = "00:00".equals(chargerPriceRateDto.getEndTime()) ? "23:59:59" : chargerPriceRateDto.getEndTime() + ":59";
                                if (nowTime.compareTo(startTime) >= 0 && nowTime.compareTo(endTime) < 0) {
                                    chargeCost = chargerPriceRateDto.getElectMoney();
                                    break;
                                }
                            }
                            siteDataListDto.setChargePrice(chargeCost);
                        }
                        finalResultList.add(siteDataListDto);
                    });
                }
                //查询距离我多少公里内的站点
                Double howMuchKilometer = siteQueryVo.getHowMuchKilometer();
                if (StringUtil.isNotEmpty(howMuchKilometer)) {
                    resultList = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getDistance()) && s.getDistance() <= howMuchKilometer).collect(Collectors.toList());
                }
                //按照类型排序
                Integer queryType = siteQueryVo.getQueryType();
                if (StringUtil.isNotEmpty(queryType)) {
                    if (queryType == 1) {
                        List<SiteDataListDto> siteDataListDtos = resultList.stream().sorted(Comparator.comparing(SiteDataListDto::getDistance).reversed()).collect(Collectors.toList());
                        return ResponseResult.ok(new PageDto<>(siteDataListDtos, siteQueryVo.getPage(), siteQueryVo.getSize()));
                    } else if (queryType == 2) {
                        List<SiteDataListDto> siteDataListDtos = resultList.stream().filter(s -> StringUtil.isNotEmpty(s.getChargePrice())).sorted(Comparator.comparing(SiteDataListDto::getChargePrice)).collect(Collectors.toList());
                        return ResponseResult.ok(new PageDto<>(siteDataListDtos, siteQueryVo.getPage(), siteQueryVo.getSize()));
                    }
                }
            }
        }
        if (siteQueryVo.getSize() > 0) {
            return ResponseResult.ok(new PageDto<>(resultList, siteQueryVo.getPage(), siteQueryVo.getSize()));
        } else {
            return ResponseResult.ok(resultList);
        }
    }

    /**
     * 计算距离
     * @param centerLon   小程序用户所在位置经度
     * @param centerLat   小程序用户所在位置纬度
     * @param stationLng  站点所在位置经度
     * @param stationLat  站点所在位置经度
     * @return
     */
    private Double calculateDistance( Double centerLon, Double centerLat, Double stationLng, Double stationLat) {
        //获得两点之间距离多少米
        double distanceM = distance(centerLon, centerLat, stationLng, stationLat);
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(2);
        //换算成公里
        double distanceKm = distanceM / 1000;
        return getToDouble(distanceKm);
    }


    /**
     * 根据站点id查询详情信息
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<SiteDetailsDataDto> querySiteDetailsById(String siteId) {
        SiteDetailsDataDto result = new SiteDetailsDataDto();
        //根据站点id查询站点信息
        ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(Collections.singletonList(siteId));
        if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty() && siteBasicInfoByIds.getData().containsKey(siteId)) {
            SiteInfoDto siteInfoDto = siteBasicInfoByIds.getData().get(siteId);
            result.setSiteId(siteInfoDto.getId());
            result.setSiteName(siteInfoDto.getSiteName());
            result.setOperatorName(siteInfoDto.getOperatorName());
            result.setImage(siteInfoDto.getImagePath());
            //获取站点位置信息
            String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
            if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                //获取站点位置信息
                if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                    String location = JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION));
                    result.setLocation(location);
                }
                //获取站点停车费类型
                if (parseObjectMap.containsKey(SiteFieldParamVo.PARK_TYPE)) {
                    result.setParkCostType(Integer.parseInt(String.valueOf(parseObjectMap.get(SiteFieldParamVo.PARK_TYPE))));
                }
                //获取站点营业时间
                if (parseObjectMap.containsKey(SiteFieldParamVo.BUSINE_HOURS)) {
                    result.setBusineHours(String.valueOf(parseObjectMap.get(SiteFieldParamVo.BUSINE_HOURS)));
                }
                //获取站点服务电话
                if (parseObjectMap.containsKey(SiteFieldParamVo.SERVICE_TEL)) {
                    result.setServiceTel(String.valueOf(parseObjectMap.get(SiteFieldParamVo.SERVICE_TEL)));
                }
            }
            //获取站点直流充电价格信息
            ResponseResult<Map<String, List<ChargerPriceRateDto>>> chargerRateListBySiteIds = togetherService.findChargerRateListBySiteIds(Collections.singletonList(siteId), 1, 1);
            if (chargerRateListBySiteIds.isSuccess() && !chargerRateListBySiteIds.getData().isEmpty() && chargerRateListBySiteIds.getData().containsKey(siteId)) {
                //当前时段充电电价
                SiteDetailsDataDto.ChargerPrice chargerPrice = new SiteDetailsDataDto.ChargerPrice();
                String nowTime = DateUtil.localTimeToStr(LocalTime.now());
                result.setChargerPriceList(chargerRateListBySiteIds.getData().get(siteId).stream().map(chargerPriceRateDto -> {
                    SiteDetailsDataDto.ChargerPrice price = new SiteDetailsDataDto.ChargerPrice();
                    BeanUtils.copyProperties(chargerPriceRateDto, price);
                    String startTime = chargerPriceRateDto.getStartTime() + ":59";
                    String endTime = "00:00".equals(chargerPriceRateDto.getEndTime()) ? "23:59:59" : chargerPriceRateDto.getEndTime() + ":59";
                    //和当前时间比较，获取当前时段的电价
                    if (nowTime.compareTo(startTime) >= 0 && nowTime.compareTo(endTime) < 0) {
                        chargerPrice.setIsChargeCurrent(1);
                        BeanUtils.copyProperties(chargerPriceRateDto, chargerPrice);
                    }
                    return price;
                }).collect(Collectors.toList()));
                result.setChargerPrice(chargerPrice);
            }
        }
        return ResponseResult.ok(result);
    }

    /**
     * 根据站点id查询站点电桩详情信息
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<SitePileDetailsDto> querySitePileDetailsById(String siteId) {
        SitePileDetailsDto result = new SitePileDetailsDto();
        //根据站点id，查询站点下电桩数量
        ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), 1);
        if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty() && deviceBasicInfoBySiteIds.getData().containsKey(siteId)) {
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(siteId);
            //根据多个电桩设备id，查询电枪数据
            Map<String, List<DeviceGunInfoDto>> deviceGunInfoMap = deviceService.findDeviceGunInfoByDeviceIds(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toList())).getData();
            //根据多个电桩编码查询电桩实时数据
            List<PileRealModel> pileRealModelList = getPileRealModelList(deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getDeviceNumber).collect(Collectors.toList()));
            Map<String, PileRealModel> pileRealModelMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(pileRealModelList)) {
                pileRealModelMap = pileRealModelList.stream().collect(Collectors.toMap(PileRealModel::getPileCode, Function.identity(), (k1, k2) -> k1));
            }
            //快充空闲数量
            AtomicReference<Integer> fastChargeIdleNum = new AtomicReference<>(0);
            //快充总数量
            AtomicReference<Integer> fastChargeTotalNum = new AtomicReference<>(0);
            //慢充空闲数量
            AtomicReference<Integer> slowChargeIdleNum = new AtomicReference<>(0);
            //慢充总数量
            AtomicReference<Integer> slowChargeTotalNum = new AtomicReference<>(0);
            //充电枪信息
            List<SitePileDetailsDto.GunData> gunDataList = Lists.newArrayList();
            Map<String, PileRealModel> finalPileRealModelMap = pileRealModelMap;
            deviceBasicInfoDtoList.forEach(deviceBasicInfoDto -> {
                //获取电桩下电枪信息
                if (!deviceGunInfoMap.isEmpty() && deviceGunInfoMap.containsKey(deviceBasicInfoDto.getId())) {
                    //循环电枪信息
                    deviceGunInfoMap.get(deviceBasicInfoDto.getId()).forEach(deviceGunInfoDto -> {
                        SitePileDetailsDto.GunData gunData = new SitePileDetailsDto.GunData();
                        //直流和v2g为快充,交流为慢充
                        if (StringUtil.isNotEmpty(deviceBasicInfoDto.getTypeId()) &&
                                Integer.parseInt(deviceBasicInfoDto.getTypeId()) == 29 || Integer.parseInt(deviceBasicInfoDto.getTypeId()) == 30) {
                            fastChargeTotalNum.updateAndGet(v -> v + 1);
                            //判断快充电桩是否为空闲状态
                            if (!finalPileRealModelMap.isEmpty() && finalPileRealModelMap.containsKey(deviceBasicInfoDto.getDeviceNumber())) {
                                Map<String, PileRealModel.GunRealModel> gunRealModelMap = finalPileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber()).getGunRealModelMap();
                                if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(deviceGunInfoDto.getGunCode())) {
                                    PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(deviceGunInfoDto.getGunCode());
                                    if (gunRealModel.getGunStatus() == 0) {
                                        fastChargeIdleNum.updateAndGet(v -> v + 1);
                                    }
                                    gunData.setGunWorkState(convertGunStatus(finalPileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber()), gunRealModel.getGunCode()));
                                    gunData.setBatterySOC(gunRealModel.getBatterySoc());
                                }
                            }
                        } else if (StringUtil.isNotEmpty(deviceBasicInfoDto.getTypeId()) && Integer.parseInt(deviceBasicInfoDto.getTypeId()) == 28){
                            slowChargeTotalNum.updateAndGet(v -> v + 1);
                            //判断慢充电桩是否为空闲状态
                            if (!finalPileRealModelMap.isEmpty() && finalPileRealModelMap.containsKey(deviceBasicInfoDto.getDeviceNumber())) {
                                Map<String, PileRealModel.GunRealModel> gunRealModelMap = finalPileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber()).getGunRealModelMap();
                                if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(deviceGunInfoDto.getGunCode())) {
                                    PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(deviceGunInfoDto.getGunCode());
                                    if (gunRealModel.getGunStatus() == 0) {
                                        slowChargeIdleNum.updateAndGet(v -> v + 1);
                                    }
                                    gunData.setGunWorkState(convertGunStatus(finalPileRealModelMap.get(deviceBasicInfoDto.getDeviceNumber()), gunRealModel.getGunCode()));
                                    gunData.setBatterySOC(gunRealModel.getBatterySoc());
                                }
                            }
                        }
                        BeanUtils.copyProperties(deviceGunInfoDto, gunData);
                        gunData.setPower(deviceGunInfoDto.getRatedPower());
                        gunData.setPileCode(deviceBasicInfoDto.getDeviceNumber());
                        gunData.setPileDeviceId(deviceBasicInfoDto.getId());
                        gunData.setPileTypeId(Integer.parseInt(deviceBasicInfoDto.getTypeId()));
                        gunDataList.add(gunData);
                    });
                }
            });
            result.setFastIdleNum(fastChargeIdleNum.get());
            result.setFastTotalNum(fastChargeTotalNum.get());
            result.setSlowIdleNum(slowChargeIdleNum.get());
            result.setSlowTotalNum(slowChargeTotalNum.get());
            if (CollectionUtils.isNotEmpty(gunDataList)) {
                result.setDcGunDataList(gunDataList.stream().filter(p -> StringUtil.isNotEmpty(p.getPileTypeId()) && (p.getPileTypeId() == 29 || p.getPileTypeId() == 30)).collect(Collectors.toList()));
                result.setAcGunDataList(gunDataList.stream().filter(p -> StringUtil.isNotEmpty(p.getPileTypeId()) && p.getPileTypeId() == 28).collect(Collectors.toList()));
            }
        }
        return ResponseResult.ok(result);
    }

    /**
     * 根据站点id查询站点充放电计费策略数据
     * @param siteId 站点id
     * @param priceType 价格类型 1-充电 2-放电
     * @return
     */
    @Override
    public ResponseResult<List<ChargerPriceRateDto>> findBillStrategyById(String siteId, Integer priceType) {
        List<ChargerPriceRateDto> resultList = Lists.newArrayList();
        //根据站点id查询充放电价格策略
        ResponseResult<Map<String, List<ChargerPriceRateDto>>> chargerRateListBySiteIds = togetherService.findChargerRateListBySiteIds(Collections.singletonList(siteId), priceType, 1);
        if (chargerRateListBySiteIds.isSuccess() && !chargerRateListBySiteIds.getData().isEmpty() && chargerRateListBySiteIds.getData().containsKey(siteId)) {
            resultList = chargerRateListBySiteIds.getData().get(siteId);
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 输入终端编号获取设备详情
     * @param pileCode 电桩编码
     * @param appletUserId 小程序用户id
     * @param appletKey 小程序登录标识
     * @return
     */
    @Override
    public ResponseResult<ChargeDeviceInfoDto> queryDeviceInfoByPileCode(String pileCode, String appletUserId, String appletKey) {
        ChargeDeviceInfoDto result = new ChargeDeviceInfoDto();
        //根据小程序登录标识查询站点id列表
        ResponseResult<AppletDto> appletByAppletCode = systemService.findAppletByAppletCode(appletKey);
        if (appletByAppletCode.isSuccess() && StringUtil.isNotEmpty(appletByAppletCode.getData()) && !appletByAppletCode.getData().getTenantSiteIdMap().isEmpty()) {
            //有权限查看的站点id列表
            List<String> siteIdList = appletByAppletCode.getData().getTenantSiteIdMap().values().stream().flatMap(Set::stream).collect(Collectors.toList());
            //根据多个站点id，查询站点下所有电桩设备列表
            ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(siteIdList, 1);
            if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceBasicInfoBySiteIds.getData().values().stream().flatMap(List::stream).collect(Collectors.toList()).
                        stream().collect(Collectors.toMap(DeviceBasicInfoDto::getDeviceNumber, deviceBasicInfoDto -> deviceBasicInfoDto, (k1, k2) -> k1));
                if (!deviceBasicInfoDtoMap.containsKey(pileCode)) {
                    return ResponseResult.error("无权限访问，请去我的-关于中联系我们解决！", 200002, result);
                }
                DeviceBasicInfoDto deviceBasicInfoDto = deviceBasicInfoDtoMap.get(pileCode);

                //根据小程序用户id查询小程序用户信息
                AppletUserInfoDto appletUserInfoDto = togetherService.findAppletUserByIds(Collections.singleton(appletUserId)).getData().get(appletUserId);
                //根据站点id，查询站点白名单信息
                ResponseResult<SiteRosterInfoDto> siteWhiteRosterById = togetherService.findSiteWhiteRosterById(deviceBasicInfoDto.getSiteId(), 1, appletUserInfoDto.getPhoneNum());
                //是否要付费
                int isPay = 0;
                if (siteWhiteRosterById.isSuccess() && StringUtil.isNotEmpty(siteWhiteRosterById.getData())) {
                    SiteRosterInfoDto siteRosterInfoDto = siteWhiteRosterById.getData();
                    if (StringUtil.isEmpty(siteRosterInfoDto.getRosterMode())) {
                        return ResponseResult.error("查询失败，当前站点没设置白名单模式！");
                    }

                    // 判断是否是白名单用户
                    boolean isRosterUser = CollectionUtils.isNotEmpty(siteRosterInfoDto.getWhiteRosterInfoList());

                    // 根据rosterMode的不同值进行处理
                    switch (siteRosterInfoDto.getRosterMode()) {
                        case 1:
                            if (!isRosterUser) {
                                return ResponseResult.error("查询失败，当前站点仅白名单用户可用，您还不是白名单用户！");
                            }
                            break;
                        case 2:
                            if (!isRosterUser) {
                                isPay = 1;
                            }
                            break;
                    }
                }
                result.setIsPay(isPay);
                result.setDeviceId(deviceBasicInfoDto.getId());
                result.setPileCode(deviceBasicInfoDto.getDeviceNumber());
                result.setPileName(deviceBasicInfoDto.getDeviceName());
                result.setSiteId(deviceBasicInfoDto.getSiteId());
                result.setSiteName(deviceBasicInfoDto.getSiteName());
                //根据所属站点id，查询站点信息
                ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(Collections.singletonList(deviceBasicInfoDto.getSiteId()));
                if (siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty() && siteBasicInfoByIds.getData().containsKey(deviceBasicInfoDto.getSiteId())) {
                    SiteInfoDto siteInfoDto = siteBasicInfoByIds.getData().get(deviceBasicInfoDto.getSiteId());
                    //获取站点位置信息
                    String siteReadwriteObject = siteInfoDto.getSiteReadwriteObject();
                    if (StringUtil.isNotEmpty(siteReadwriteObject)) {
                        Map<String, Object> parseObjectMap = JSON.parseObject(siteReadwriteObject, new TypeReference<Map<String, Object>>() {});
                        //获取站点位置信息
                        if (parseObjectMap.containsKey(SiteFieldParamVo.LOCATION)) {
                            Map<String, Object> readwriteMap = JSON.parseObject(JSON.toJSONString(parseObjectMap.get(SiteFieldParamVo.LOCATION)), new TypeReference<Map<String, Object>>() {});

                            //详细地址
                            if (!readwriteMap.isEmpty() && readwriteMap.containsKey(SiteFieldParamVo.ADDRESS) && StringUtil.isNotEmpty(readwriteMap.get(SiteFieldParamVo.ADDRESS))) {
                                result.setSiteAddress(readwriteMap.get(SiteFieldParamVo.ADDRESS).toString());
                            }
                        }
                        //获取站点是否全天开放
                        if (parseObjectMap.containsKey(SiteFieldParamVo.OPEN_ALL_DAY) && StringUtil.isNotEmpty(parseObjectMap.get(SiteFieldParamVo.OPEN_ALL_DAY))) {
                            result.setOpenAllDay(Integer.parseInt(parseObjectMap.get(SiteFieldParamVo.OPEN_ALL_DAY).toString()));
                        }
                    }
                }
                //根据多个电桩设备id，查询电枪数据
                ResponseResult<Map<String, List<DeviceGunInfoDto>>> deviceGunInfoByDeviceIds = deviceService.findDeviceGunInfoByDeviceIds(Collections.singletonList(deviceBasicInfoDto.getId()));
                if (deviceGunInfoByDeviceIds.isSuccess() && !deviceGunInfoByDeviceIds.getData().isEmpty() && deviceGunInfoByDeviceIds.getData().containsKey(deviceBasicInfoDto.getId())) {
                    //查询电桩实时数据
                    PileRealModel pileRealModel = getPileRealModel(deviceBasicInfoDto.getDeviceNumber());
                    //充电枪信息
                    List<ChargeDeviceInfoDto.GunData> gunDataList = Lists.newArrayList();
                    deviceGunInfoByDeviceIds.getData().get(deviceBasicInfoDto.getId()).forEach(deviceGunInfoDto -> {
                        ChargeDeviceInfoDto.GunData gunData = new ChargeDeviceInfoDto.GunData();
                        //获取实时数据信息
                        if (StringUtil.isNotEmpty(pileRealModel) && StringUtil.isNotEmpty(pileRealModel.getPileCode())) {
                            Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
                            if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(deviceGunInfoDto.getGunCode())) {
                                PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(deviceGunInfoDto.getGunCode());
                                gunData.setGunWorkState(convertGunStatus(pileRealModel, gunRealModel.getGunCode()));
                                gunData.setBatterySOC(gunRealModel.getBatterySoc());
                            }
                        }
                        BeanUtils.copyProperties(deviceGunInfoDto, gunData);
                        gunData.setPileCode(deviceBasicInfoDto.getDeviceNumber());
                        gunData.setPileDeviceId(deviceBasicInfoDto.getId());
                        gunData.setPileTypeId(Integer.parseInt(deviceBasicInfoDto.getTypeId()));
                        gunDataList.add(gunData);
                    });
                    result.setGunDataList(gunDataList);
                }
            }
        }
        return ResponseResult.ok(result);
    }

    /**
     * 根据设备id查询充放电和占桩价格信息
     * @param deviceId 设备id
     * @param priceType 价格类型 1-充电 2-放电
     * @return
     */
    @Override
    public ResponseResult<ChargePriceInfoDto> findDevicePriceById(String deviceId, Integer priceType) {
        return togetherService.findDevicePriceById(deviceId, priceType);
    }
}
