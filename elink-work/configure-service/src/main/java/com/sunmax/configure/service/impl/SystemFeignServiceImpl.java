package com.sunmax.configure.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.dto.system.SiteOperateDto;
import com.sunmax.common.enums.ProtocolEnum;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.configure.dto.province.SubstationFieldDto;
import com.sunmax.configure.runner.SubstationRunner;
import com.sunmax.configure.service.SystemFeignService;
import com.sunmax.configure.service.feign.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SystemFeignServiceImpl implements SystemFeignService {

    @Autowired
    private DeviceService deviceService;

    @Override
    public ResponseResult<Boolean> updateHttpSiteForward(PlatformDataForwardDto dataForward) {
        Boolean result = false;
        //省平台缓存
        if (Objects.equals(dataForward.getProtocolCode(), ProtocolEnum.PROVINCE.getCode())) {
            if (dataForward.getStatus() == 1) {
                result = this.dataForwardStart(dataForward, SubstationRunner.privinceSitePileMap);
            } else {
                result = this.dataForwardStop(dataForward.getId(), SubstationRunner.privinceSitePileMap);
            }
        }
        //市平台缓存
        if (Objects.equals(dataForward.getProtocolCode(), ProtocolEnum.CITY.getCode())) {
            if (dataForward.getStatus() == 1) {
                result = this.dataForwardStart(dataForward, SubstationRunner.citySitePileMap);
            } else {
                result = this.dataForwardStop(dataForward.getId(), SubstationRunner.citySitePileMap);
            }
        }
        return ResponseResult.ok(result);
    }

    public Boolean dataForwardStart(PlatformDataForwardDto dataForward, Map<String, SubstationFieldDto> sitePileMap) {
        //先删除该数据转发之前的站点缓存数据
        Set<String> delSiteIds = sitePileMap.values().stream().filter(s -> Objects.equals(s.getDataForwardId(), dataForward.getId()))
                .map(SubstationFieldDto::getSiteId).collect(Collectors.toSet());
        sitePileMap.entrySet().removeIf(next -> delSiteIds.contains(next.getValue().getSiteId()));

        //根据多个站点id查询站点及电桩设备数据
        List<String> siteIds = dataForward.getSiteOperateList().stream().map(SiteOperateDto::getOperateId).collect(Collectors.toList());
        Map<String, SiteInfoDto> siteInfoMap = deviceService.findSiteBasicInfoByIds(siteIds).getData();
        Map<String, List<DeviceBasicInfoDto>> sitePileInfoMap = deviceService.findDeviceBasicInfoBySiteIds(siteIds, 1).getData();
        dataForward.getSiteOperateList().forEach(siteOperate -> {
            SubstationFieldDto result = new SubstationFieldDto();
            result.setSiteId(siteOperate.getSiteId());
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
            sitePileMap.put(siteOperate.getSiteId(), result);
        });
        return true;
    }

    public Boolean dataForwardStop(String dataForwardId, Map<String, SubstationFieldDto> sitePileMap) {
        //删除该数据转发的站点缓存数据
        Set<String> delSiteIds = sitePileMap.values().stream().filter(s -> Objects.equals(s.getDataForwardId(), dataForwardId))
                .map(SubstationFieldDto::getSiteId).collect(Collectors.toSet());
        sitePileMap.entrySet().removeIf(next -> delSiteIds.contains(next.getValue().getSiteId()));
        return true;
    }

}
