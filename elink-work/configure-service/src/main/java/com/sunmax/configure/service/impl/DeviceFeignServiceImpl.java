package com.sunmax.configure.service.impl;

import com.alibaba.fastjson2.JSON;
import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.dto.device.SiteDeviceTreeDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.dto.system.SiteOperateDto;
import com.sunmax.common.dto.together.SystemDeviceListDto;
import com.sunmax.common.enums.ProtocolEnum;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.configure.common.push.ProvinceDataPush;
import com.sunmax.configure.dto.FunctionListDto;
import com.sunmax.configure.dto.SiteListDto;
import com.sunmax.configure.dto.province.EquipmentInfoDto;
import com.sunmax.configure.dto.province.StationInfoDto;
import com.sunmax.configure.dto.province.SubstationFieldDto;
import com.sunmax.configure.service.DeviceFeignService;
import com.sunmax.configure.service.ProvinceDataAccessService;
import com.sunmax.configure.service.feign.CrontabService;
import com.sunmax.configure.service.feign.DeviceService;
import com.sunmax.configure.service.feign.SystemService;
import com.sunmax.configure.service.feign.TogetherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sunmax.configure.common.push.ProvinceDataPush.getCommonVo;
import static com.sunmax.configure.runner.SubstationRunner.privinceSitePileMap;

@Slf4j
@Service
public class DeviceFeignServiceImpl implements DeviceFeignService {

    @Autowired
    private SystemService systemService;

    @Autowired
    private ProvinceDataAccessService provinceDataAccessService;

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private CrontabService crontabService;

    @Autowired
    private DeviceService deviceService;

    @Override
    public ResponseResult<Void> notificationStationInfo(Set<String> siteIds) {
        //推送省平台电站数据
        List<PlatformDataForwardDto> dataForwardList = systemService.getPlatformDataForwardList(Collections.singleton(ProtocolEnum.PROVINCE.getCode())).getData();
        for (PlatformDataForwardDto dataForward : dataForwardList) {
            List<SiteOperateDto> siteOperateList = dataForward.getSiteOperateList();
            if (CollectionUtils.isNotEmpty(siteOperateList)) {
                for (SiteOperateDto siteOperate : siteOperateList) {
                    if (siteIds.contains(siteOperate.getSiteId())) {
                        StationInfoDto stationInfo = provinceDataAccessService.getStationInfo(JSON.toJSONString(Collections.singletonList(siteOperate.getSiteId())),
                                Collections.singletonList(siteOperate));
                        //把站点数据存进缓存
                        if (privinceSitePileMap.containsKey(siteOperate.getSiteId()) && stationInfo != null && StringUtil.isNotEmpty(stationInfo.getStationId())) {
                            SubstationFieldDto substationField = privinceSitePileMap.get(siteOperate.getSiteId());
                            substationField.setOperatorId(stationInfo.getOperatorId());
                            substationField.setEquipmentOwnerId(stationInfo.getEquipmentOwnerId());
                            substationField.setStationId(stationInfo.getStationId());
                            if (CollectionUtils.isEmpty(stationInfo.getEquipmentInfos())) {
                                substationField.setPileCodes(new HashSet<>());
                            } else {
                                substationField.setPileCodes(stationInfo.getEquipmentInfos().stream().map(EquipmentInfoDto::getEquipmentId)
                                        .filter(StringUtil::isNotEmpty).collect(Collectors.toSet()));
                            }
                            privinceSitePileMap.put(siteOperate.getSiteId(), substationField);
                            //推送充电站信息
                            ProvinceDataPush.stationInfo(getCommonVo(substationField), stationInfo);
                        }
                        break;
                    }
                }
            }
        }
        return ResponseResult.ok();
    }

    /**
     * 查询系统设备列表
     * @param siteId
     * @return
     */
    @Override
    public ResponseResult<SystemDeviceListDto> querySystemDeviceList(String siteId) {
        return togetherService.querySystemDeviceList(siteId);
    }

    @Override
    public ResponseResult<List<ComputeNodeListDto>> findComputeNodeListByDeviceId(String deviceId) {
        return crontabService.findComputeNodeListByDeviceId(deviceId);
    }

    @Override
    public ResponseResult<List<SiteListDto>> findSiteListByUserId(String userId) {
        //返回的集合
        List<SiteListDto> resultList = Lists.newArrayList();
        //根据用户id查询站点信息列表
        List<SiteInfoDto> siteInfoList = deviceService.findSiteInfoListByUserId(userId).getData();
        if (CollectionUtils.isNotEmpty(siteInfoList)) {
            resultList = siteInfoList.stream().map(s -> {
                SiteListDto result = new SiteListDto();
                result.setSiteId(s.getId());
                result.setSiteName(s.getSiteName());
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<FunctionListDto>> findFunctionListByDeviceId(String deviceId) {
        //返回的集合
        List<FunctionListDto> resultList = Lists.newArrayList();
        //根据设备id查询设备功能点数据
        List<ModelFunctionListDto> deviceFunctionList = deviceService.findDeviceFunctionListByDeviceId(deviceId).getData();
        if (CollectionUtils.isNotEmpty(deviceFunctionList)) {
            resultList = deviceFunctionList.stream().map(deviceFunction -> {
                FunctionListDto result = new FunctionListDto();
                result.setFunctionId(deviceFunction.getFunctionId());
                result.setFunctionName(deviceFunction.getFunctionName());
                result.setFunctionLogo(deviceFunction.getFunctionLogo());
                result.setDataType(deviceFunction.getDataType());
                return result;
            }).collect(Collectors.toList());
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<SiteDeviceTreeDto>> findSiteDeviceListBySiteId(String siteId) {
        return deviceService.findSiteDeviceListBySiteId(siteId);
    }

}
