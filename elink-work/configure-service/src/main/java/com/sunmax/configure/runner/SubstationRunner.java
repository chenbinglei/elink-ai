package com.sunmax.configure.runner;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.dto.system.SiteOperateDto;
import com.sunmax.common.dto.system.StorageDataForwardDto;
import com.sunmax.common.enums.ProtocolEnum;
import com.sunmax.common.util.StringUtil;
import com.sunmax.configure.dto.province.SubstationFieldDto;
import com.sunmax.configure.service.feign.DeviceService;
import com.sunmax.configure.service.feign.SystemService;
import com.sunmax.configure.vo.RequestCommonVo;
import com.sunmax.configure.vo.storage.StorageCommonVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Sets;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SubstationRunner implements ApplicationRunner {

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    //省接入平台站点数据
    public static Map<String, SubstationFieldDto> privinceSitePileMap = Maps.newConcurrentMap();

    //省接入平台数据
    public static Set<PlatformDataForwardDto> provincePlatformSet = Sets.newHashSet();

    //市接入平台站点数据
    public static Map<String, SubstationFieldDto> citySitePileMap = Maps.newConcurrentMap();

    //互联互通接入平台数据
    public static Map<String, RequestCommonVo> interflowMap = Maps.newConcurrentMap();

    //储能接入平台数据
    public static Map<String, StorageCommonVo> storageMap = Maps.newConcurrentMap();

    /**
     * 获取省市平台接入
     *
     * @param pileCode 电桩编号
     * @param type     类型 1-省 2-市
     * @return 接入省市平台运营商数据
     */
    public static SubstationFieldDto getPileOperateField(String pileCode, Integer type) {
        if (type == 1) {
            return privinceSitePileMap.entrySet().stream()
                    .filter(s -> s.getValue().getPileCodes().contains(pileCode)).findFirst()
                    .map(Map.Entry::getValue).orElse(null);
        }
        if (type == 2) {
            return citySitePileMap.entrySet().stream()
                    .filter(s -> s.getValue().getPileCodes().contains(pileCode)).findFirst()
                    .map(Map.Entry::getValue).orElse(null);
        }
        return null;
    }

    /**
     * 获取互联互通接入平台数据
     *
     * @param platformId 平台id
     * @return 接入平台运营商数据
     */
    public static RequestCommonVo getInterFlowField(String platformId) {
        return interflowMap.get(platformId);
    }

    @Override
    public void run(ApplicationArguments args) {
        //获取省市平台和互联互通平台的数据转发数据
        getPlatformDataList();
    }

    public void getPlatformDataList() {
        //省接入平台站点数据
        Map<String, SubstationFieldDto> privinceSitePileMap = Maps.newConcurrentMap();
        //省接入平台数据
        Set<PlatformDataForwardDto> provincePlatformSet = Sets.newHashSet();
        //市接入平台站点数据
        Map<String, SubstationFieldDto> citySitePileMap = Maps.newConcurrentMap();
        //互联互通接入平台数据
        Map<String, RequestCommonVo> interflowMap = Maps.newConcurrentMap();
        //储能接入平台数据
        Map<String, StorageCommonVo> storageMap = Maps.newConcurrentMap();

        //获取省市平台和互联互通平台数据转发数据
        List<PlatformDataForwardDto> dataForwardList = systemService.getPlatformDataForwardList(Sets.newHashSet(ProtocolEnum.PROVINCE.getCode(),
                ProtocolEnum.CITY.getCode(), ProtocolEnum.INTERFLOW.getCode())).getData();
        if (CollectionUtils.isNotEmpty(dataForwardList)) {
            //根据多个站点id查询站点及电桩设备数据
            List<String> siteIds = dataForwardList.stream().flatMap(dataForward -> dataForward.getSiteOperateList().stream()
                    .map(SiteOperateDto::getSiteId).collect(Collectors.toList()).stream()).collect(Collectors.toList());
            Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
            Map<String, List<DeviceBasicInfoDto>> sitePileInfoMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, 1).getData();
            //对省市运营商数据进行组装
            dataForwardList.stream().filter(d -> Objects.equals(d.getProtocolCode(), ProtocolEnum.PROVINCE.getCode()) || Objects.equals(d.getProtocolCode(), ProtocolEnum.CITY.getCode()))
                    .forEach(dataForward -> {
                        if (Objects.equals(dataForward.getProtocolCode(), ProtocolEnum.PROVINCE.getCode())) {
                            provincePlatformSet.add(dataForward);
                        }
                        dataForward.getSiteOperateList().forEach(siteOperate -> {
                            SubstationFieldDto result = new SubstationFieldDto();
                            result.setSiteId(siteOperate.getSiteId());
                            result.setDataForwardId(dataForward.getId());
                            result.setUrl(dataForward.getAddress());
                            result.setPlatformId(dataForward.getPlatformId());
                            result.setPlatformSecret(dataForward.getPlatformSecret());
                            result.setDataSecret(dataForward.getDataSecret());
                            result.setDataSecretIv(dataForward.getDataSecretIv());
                            result.setSigSecret(dataForward.getSigSecret());
                            result.setOperatorId(siteOperate.getOperateId());
                            if (siteInfoMap.containsKey(siteOperate.getSiteId())) {
                                SiteInfoDto siteInfo = siteInfoMap.get(siteOperate.getSiteId());
                                result.setStationId(siteInfo.getSiteCode());
                                if (StringUtil.isNotEmpty(siteInfo.getSiteReadwriteObject())) {
                                    JSONObject reaMap = JSON.parseObject(siteInfo.getSiteReadwriteObject());
                                    result.setEquipmentOwnerId(reaMap.getString("equipmentOwnerId"));
                                }
                            }
                            if (sitePileInfoMap.containsKey(siteOperate.getSiteId())) {
                                result.setPileCodes(sitePileInfoMap.get(siteOperate.getSiteId()).stream().map(DeviceBasicInfoDto::getDeviceNumber)
                                        .filter(StringUtil::isNotEmpty).collect(Collectors.toSet()));
                            }
                            if (Objects.equals(dataForward.getProtocolCode(), ProtocolEnum.PROVINCE.getCode())) {
                                privinceSitePileMap.put(siteOperate.getSiteId(), result);
                            }
                            if (Objects.equals(dataForward.getProtocolCode(), ProtocolEnum.CITY.getCode())) {
                                citySitePileMap.put(siteOperate.getSiteId(), result);
                            }
                        });
                    });
            //对互联互通数据进行组装
            dataForwardList.stream().filter(d -> Objects.equals(d.getProtocolCode(), ProtocolEnum.INTERFLOW.getCode()))
                    .forEach(dataForward -> {
                        interflowMap.put(dataForward.getPlatformId(), RequestCommonVo.builder()
                                .url(dataForward.getAddress())
                                .platformId(dataForward.getPlatformId())
                                .platformSecret(dataForward.getPlatformSecret())
                                .dataSecret(dataForward.getDataSecret())
                                .dataSecretIv(dataForward.getDataSecretIv())
                                .sigSecret(dataForward.getSigSecret()).build());
                    });
        }
        //获取储能平台数据转发数据
        List<StorageDataForwardDto> storageDataForwardList = systemService.getStorageDataForwardList(Collections.singleton(ProtocolEnum.STORAGE.getCode())).getData();
        if (CollectionUtils.isNotEmpty(storageDataForwardList)) {
            //定义PCS设备和电池簇设备数据
            Map<String, List<DeviceBasicInfoDto>> sitePcsInfoMap = Maps.newHashMap();
            Map<String, List<DeviceBasicInfoDto>> siteBatteryInfoMap = Maps.newHashMap();
            //根据多个站点id查询站点PCS设备数据
            List<String> siteIds = dataForwardList.stream().flatMap(dataForward -> dataForward.getSiteOperateList().stream()
                    .map(SiteOperateDto::getSiteId).collect(Collectors.toList()).stream()).collect(Collectors.toList());
            deviceService.findDeviceBasicInfoBySiteIds(siteIds, 4).getData().forEach((siteId, deviceList) -> {
                sitePcsInfoMap.put(siteId, deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "23")).collect(Collectors.toList()));
                siteBatteryInfoMap.put(siteId, deviceList.stream().filter(d -> Objects.equals(d.getTypeId(), "25")).collect(Collectors.toList()));
            });
            //对储能平台数据进行组装
            storageMap.putAll(storageDataForwardList.stream().map(dataForward -> {
                StorageCommonVo result = new StorageCommonVo();
                BeanUtils.copyProperties(dataForward, result);
                result.setBaseUrl(dataForward.getAddress());
                dataForward.getSiteOperateList().stream().filter(s -> StringUtil.isNotEmpty(s.getSiteId())
                        && StringUtil.isNotEmpty(s.getResourceSn())).forEach(siteOperate -> {
                    StorageCommonVo.SiteResource siteResource = new StorageCommonVo.SiteResource();
                    siteResource.setSiteId(siteOperate.getSiteId());
                    siteResource.setResourceNo(siteOperate.getResourceSn());
                    if (sitePcsInfoMap.containsKey(siteOperate.getSiteId())) {
                        siteResource.setPcsModels(sitePcsInfoMap.get(siteOperate.getSiteId()).stream().map(d -> {
                            StorageCommonVo.DeviceInfo pcsModel = new StorageCommonVo.DeviceInfo();
                            pcsModel.setDeviceId(d.getId());
                            pcsModel.setModelId(d.getModelId());
                            return pcsModel;
                        }).collect(Collectors.toList()));
                    }
                    if (siteBatteryInfoMap.containsKey(siteOperate.getSiteId())) {
                        siteResource.setBatteryModels(siteBatteryInfoMap.get(siteOperate.getSiteId()).stream().map(d -> {
                            StorageCommonVo.DeviceInfo pcsModel = new StorageCommonVo.DeviceInfo();
                            pcsModel.setDeviceId(d.getId());
                            pcsModel.setModelId(d.getModelId());
                            return pcsModel;
                        }).collect(Collectors.toList()));
                    }
                    if (StringUtil.isNotEmpty(siteResource.getPcsModels()) || StringUtil.isNotEmpty(siteResource.getBatteryModels())) {
                        result.getSiteResourceMap().put(siteResource.getSiteId(), siteResource);
                    }
                });
                return result;
            }).collect(Collectors.toMap(StorageCommonVo::getAppId, a -> a, (k1, k2) -> k1)));
        }

        //对数据赋予缓存
        SubstationRunner.privinceSitePileMap = privinceSitePileMap;
        SubstationRunner.provincePlatformSet = provincePlatformSet;
        SubstationRunner.citySitePileMap = citySitePileMap;
        SubstationRunner.interflowMap = interflowMap;
        SubstationRunner.storageMap = storageMap;
    }

}
