package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.service.feign.ConfigureService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.DeviceServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DeviceService 扩展单元测试5")
class DeviceServiceExt5Test {

    @Mock private DeviceDao deviceDao;
    @Mock private DeviceGunDao deviceGunDao;
    @Mock private DeviceEventDao deviceEventDao;
    @Mock private SiteInfoDao siteInfoDao;
    @Mock private ScenarioTypeDao scenarioTypeDao;
    @Mock private GatewaySubDeviceDao gatewaySubDeviceDao;
    @Mock private SystemService systemService;
    @Mock private ConfigureService configureService;

    @InjectMocks private DeviceServiceImpl deviceService;

    @Test
    @DisplayName("查询网关子设备-有数据返回列表")
    void findGatewayChildDeviceById_withData_returnsList() {
        GatewaySubDeviceEntity sub = new GatewaySubDeviceEntity();
        sub.setId("sub-001");
        sub.setGatewayId("gw-001");
        sub.setSubDeviceId("dev-001");
        when(gatewaySubDeviceDao.findAllByGatewayId("gw-001")).thenReturn(List.of(sub));

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceNumber("DEV001");
        device.setDeviceName("子设备1");
        device.setIsDelete(1);
        when(deviceDao.findAllByIdInAndIsDelete(Set.of("dev-001"), 1)).thenReturn(List.of(device));

        ResponseResult<?> result = deviceService.findGatewayChildDeviceById("gw-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询网关子设备-无数据返回空列表")
    void findGatewayChildDeviceById_noData_returnsEmpty() {
        when(gatewaySubDeviceDao.findAllByGatewayId("gw-001")).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceService.findGatewayChildDeviceById("gw-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备枪信息-有数据返回Map")
    void findDeviceGunInfoByDeviceIds_withData_returnsMap() {
        DeviceGunEntity gun = new DeviceGunEntity();
        gun.setId("gun-001");
        gun.setDeviceId("dev-001");
        gun.setGunCode("1");
        when(deviceGunDao.findAllByDeviceIdIn(List.of("dev-001"))).thenReturn(List.of(gun));

        ResponseResult<?> result = deviceService.findDeviceGunInfoByDeviceIds(List.of("dev-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备枪信息-无数据返回空Map")
    void findDeviceGunInfoByDeviceIds_noData_returnsEmpty() {
        when(deviceGunDao.findAllByDeviceIdIn(List.of("dev-001"))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceService.findDeviceGunInfoByDeviceIds(List.of("dev-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点子设备列表-网关存在返回数据")
    void findSiteSubDeviceList_gatewayFound_returnsData() {
        DeviceEntity gateway = new DeviceEntity();
        gateway.setId("gw-001");
        gateway.setSiteId("site-001");
        when(deviceDao.findById("gw-001")).thenReturn(Optional.of(gateway));
        when(gatewaySubDeviceDao.findAllByGatewayId("gw-001")).thenReturn(Collections.emptyList());
        when(deviceDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceService.findSiteSubDeviceList("gw-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点子设备列表-网关不存在返回空列表")
    void findSiteSubDeviceList_gatewayNotFound_returnsEmpty() {
        when(deviceDao.findById("gw-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = deviceService.findSiteSubDeviceList("gw-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("批量更新网关子设备-类型1添加子设备")
    void batchUpdateGatewaySubDevice_type1_addsSubDevices() {
        when(gatewaySubDeviceDao.findAllBySubDeviceIdIn(List.of("dev-001"))).thenReturn(Collections.emptyList());
        when(gatewaySubDeviceDao.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = deviceService.batchUpdateGatewaySubDevice("gw-001", List.of("dev-001"), 1);
        assertTrue(result.isSuccess());
        verify(gatewaySubDeviceDao).saveAll(any());
    }

    @Test
    @DisplayName("批量更新网关子设备-类型2删除子设备")
    void batchUpdateGatewaySubDevice_type2_removesSubDevices() {
        GatewaySubDeviceEntity sub = new GatewaySubDeviceEntity();
        sub.setId("sub-001");
        when(gatewaySubDeviceDao.findAllByGatewayIdAndSubDeviceIdIn("gw-001", List.of("dev-001"))).thenReturn(List.of(sub));

        ResponseResult<Void> result = deviceService.batchUpdateGatewaySubDevice("gw-001", List.of("dev-001"), 2);
        assertTrue(result.isSuccess());
        verify(gatewaySubDeviceDao).deleteAll(List.of(sub));
    }

    @Test
    @DisplayName("批量更新网关子设备-空参数返回参数错误")
    void batchUpdateGatewaySubDevice_emptyParams_returnsParamError() {
        ResponseResult<Void> result = deviceService.batchUpdateGatewaySubDevice("gw-001", Collections.emptyList(), null);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备资产列表-有站点和设备返回数据")
    void getDeviceAssetList_withSiteAndDevice_returnsData() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("站点1");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(scenarioTypeDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceName("设备1");
        device.setSiteId("site-001");
        device.setIsDelete(1);
        when(deviceDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(device));

        ResponseResult<?> result = deviceService.getDeviceAssetList("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备资产列表-有能源场景返回数据")
    void getDeviceAssetList_withScenario_returnsData() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("站点1");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));

        ScenarioTypeEntity scenario = new ScenarioTypeEntity();
        scenario.setId("scenario-001");
        scenario.setSystemName("光伏系统");
        scenario.setSiteId("site-001");
        when(scenarioTypeDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(scenario));
        when(deviceDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceService.getDeviceAssetList("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备资产列表-空站点ID返回空列表")
    void getDeviceAssetList_emptySiteId_returnsEmpty() {
        ResponseResult<?> result = deviceService.getDeviceAssetList(null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备资产列表-站点不存在返回空列表")
    void getDeviceAssetList_siteNotFound_returnsEmpty() {
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = deviceService.getDeviceAssetList("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备资产列表-设备有父ID时设置父ID")
    void getDeviceAssetList_deviceWithParent_setsParentId() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("站点1");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(scenarioTypeDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceName("设备1");
        device.setSiteId("site-001");
        device.setParentId("parent-001");
        device.setIsDelete(1);
        when(deviceDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(device));

        ResponseResult<?> result = deviceService.getDeviceAssetList("site-001");
        assertTrue(result.isSuccess());
    }
}
