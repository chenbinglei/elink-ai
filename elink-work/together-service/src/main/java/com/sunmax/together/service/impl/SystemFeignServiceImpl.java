package com.sunmax.together.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.system.ChargePlatformInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PlatformSetVo;
import com.sunmax.together.dao.asset.GatWayPlatformDao;
import com.sunmax.together.entity.assets.GatWayPlatformEntity;
import com.sunmax.together.service.SystemFeignService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.ProtocolService;
import com.sunmax.together.service.feign.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SystemFeignServiceImpl implements SystemFeignService {

    @Autowired
    private GatWayPlatformDao gatWayPlatformDao;

    @Autowired
    private ProtocolService protocolService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private DeviceService deviceService;

    /**
     * 根据删除平台id删除网关关联关系并重新下发
     * @param platformId
     * @return
     */
    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<String> deleteAndGatewayPlatformSet(String platformId) {
        //根据平台id查询和网关关联关系
        List<GatWayPlatformEntity> gatWayPlatformEntityList = gatWayPlatformDao.findAllByPlatformId(platformId);
        if (CollectionUtils.isNotEmpty(gatWayPlatformEntityList)) {
            //根据多个网关id查询关联平台信息
            List<String> gatewayIdList = gatWayPlatformEntityList.stream().map(GatWayPlatformEntity::getGatewayId).collect(Collectors.toList());
            List<GatWayPlatformEntity> gatWayPlatformEntities = gatWayPlatformDao.findAllByGatewayIdIn(gatewayIdList);
            //过滤掉当前删除平台信息
            List<GatWayPlatformEntity> platformEntities = gatWayPlatformEntities.stream().filter(g -> !g.getPlatformId().equals(platformId)).collect(Collectors.toList());
            Map<String, List<GatWayPlatformEntity>> groupByGatewayIdMap = Maps.newHashMap();
            Map<String, ChargePlatformInfoDto> platformInfoDtoMap = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(platformEntities)) {
                //根据网关id分组
                groupByGatewayIdMap = platformEntities.stream().collect(Collectors.groupingBy(GatWayPlatformEntity::getGatewayId));
                //根据多个平台id查询平台信息
                List<String> platformIdList = platformEntities.stream().map(GatWayPlatformEntity::getPlatformId).distinct().collect(Collectors.toList());
                platformInfoDtoMap = systemService.findChargePlatformInfoByIds(platformIdList).getData();
            }
            //多平台多网关下发参数
            Map<String, List<PlatformSetVo>> platformSetVoMap = Maps.newHashMap();
            //根据多个网关id查询设备基本信息
            ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByIds = deviceService.findDeviceBasicInfoByIds(gatewayIdList);
            if (deviceBasicInfoByIds.isSuccess() && !deviceBasicInfoByIds.getData().isEmpty()) {
                Map<String, List<GatWayPlatformEntity>> finalGroupByGatewayIdMap = groupByGatewayIdMap;
                Map<String, ChargePlatformInfoDto> finalPlatformInfoDtoMap = platformInfoDtoMap;
                deviceBasicInfoByIds.getData().forEach((k, v) -> {
                    List<PlatformSetVo> platformSetVoList = Lists.newArrayList();
                    List<GatWayPlatformEntity> platformEntityList = finalGroupByGatewayIdMap.get(k);
                    if (CollectionUtils.isNotEmpty(platformEntityList)) {
                        platformSetVoList = platformEntityList.stream().map(gatWayPlatformEntity -> {
                            ChargePlatformInfoDto chargePlatformInfoDto = finalPlatformInfoDtoMap.get(gatWayPlatformEntity.getPlatformId());
                            PlatformSetVo platformSetVo = new PlatformSetVo();
                            platformSetVo.setPlatformLogo(chargePlatformInfoDto.getPlatformLogo());
                            platformSetVo.setProtocolDriver(chargePlatformInfoDto.getProtocolType());
                            platformSetVo.setIp(chargePlatformInfoDto.getIpAddress());
                            platformSetVo.setPort(Integer.parseInt(chargePlatformInfoDto.getPortNumber()));
                            return platformSetVo;
                        }).collect(Collectors.toList());
                    }
                    platformSetVoMap.put(v.getDeviceNumber(), platformSetVoList);
                });
            }
            //多网关多平台下发
            protocolService.batchPlatformSet(platformSetVoMap);
            //根据平台id删除和所有网关关联关系
            gatWayPlatformDao.deleteAllByPlatformId(platformId);
        }
        return ResponseResult.ok(ResponseResult.SUCCESS);
    }
}
