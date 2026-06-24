package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.*;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.entity.model.ModelEntity;
import com.sunmax.device.entity.model.ModelReaEntity;
import com.sunmax.device.entity.model.ReaEntity;
import com.sunmax.device.service.feign.ConfigureService;
import com.sunmax.device.service.feign.DataService;
import com.sunmax.device.service.feign.ProtocolService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.DeviceServiceImpl;
import com.sunmax.device.vo.device.DeviceGunChangeVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Example;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DeviceService 扩展单元测试2")
class DeviceServiceExt2Test {

    @Mock private DeviceDao deviceDao;
    @Mock private ModelDao modelDao;
    @Mock private ModelReaDao modelReaDao;
    @Mock private ReaDao reaDao;
    @Mock private ModelFunctionDao modelFunctionDao;
    @Mock private FunctionDao functionDao;
    @Mock private ModelTopologyDao modelTopologyDao;
    @Mock private DeviceTopologyDao deviceTopologyDao;
    @Mock private SystemService systemService;
    @Mock private DeviceEventDao deviceEventDao;
    @Mock private ModelEventDao modelEventDao;
    @Mock private SiteInfoDao siteInfoDao;
    @Mock private GatewaySubDeviceDao gatewaySubDeviceDao;
    @Mock private DataService dataService;
    @Mock private DeviceGunDao deviceGunDao;
    @Mock private AssetTypeDao assetTypeDao;
    @Mock private ProtocolService protocolService;
    @Mock private ScenarioTypeDao scenarioTypeDao;
    @Mock private ConfigureService configureService;
    @Mock private DeviceFunctionFieldDao deviceFunctionFieldDao;

    @InjectMocks private DeviceServiceImpl deviceService;

    @Test
    @DisplayName("批量更新网关子设备-绑定成功")
    void batchUpdateGatewaySubDevice_bind_success() {
        when(gatewaySubDeviceDao.findAllBySubDeviceIdIn(List.of("sub-001"))).thenReturn(Collections.emptyList());
        when(gatewaySubDeviceDao.saveAll(any())).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = deviceService.batchUpdateGatewaySubDevice("gw-001", List.of("sub-001"), 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("批量更新网关子设备-解绑成功")
    void batchUpdateGatewaySubDevice_unbind_success() {
        when(gatewaySubDeviceDao.findAllByGatewayIdAndSubDeviceIdIn("gw-001", List.of("sub-001")))
                .thenReturn(Collections.emptyList());

        ResponseResult<Void> result = deviceService.batchUpdateGatewaySubDevice("gw-001", List.of("sub-001"), 2);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("批量更新网关子设备-参数为空返回错误")
    void batchUpdateGatewaySubDevice_emptyParams_returnsError() {
        ResponseResult<Void> result = deviceService.batchUpdateGatewaySubDevice("gw-001", Collections.emptyList(), 1);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备资产列表-站点存在返回数据")
    void getDeviceAssetList_siteFound_returnsData() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(scenarioTypeDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());
        when(deviceDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceService.getDeviceAssetList("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备资产列表-站点不存在返回空")
    void getDeviceAssetList_siteNotFound_returnsEmpty() {
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = deviceService.getDeviceAssetList("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备资产列表-站点ID为空返回空")
    void getDeviceAssetList_emptySiteId_returnsEmpty() {
        ResponseResult<?> result = deviceService.getDeviceAssetList("");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备资产列表-有场景类型和设备")
    void getDeviceAssetList_withScenarioAndDevice_returnsData() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));

        ScenarioTypeEntity scenario = new ScenarioTypeEntity();
        scenario.setId("scenario-001");
        scenario.setSystemName("测试场景");
        scenario.setSiteId("site-001");
        when(scenarioTypeDao.findAll(any(Example.class))).thenReturn(List.of(scenario));

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceName("测试设备");
        device.setSiteId("site-001");
        device.setIsDelete(1);
        when(deviceDao.findAll(any(Example.class))).thenReturn(List.of(device));

        ResponseResult<?> result = deviceService.getDeviceAssetList("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备扩展属性-设备不存在返回空列表")
    void findDeviceReaListById_notFound_returnsEmpty() {
        when(deviceDao.findById("dev-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = deviceService.findDeviceReaListById("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备扩展属性-设备存在返回列表")
    void findDeviceReaListById_found_returnsList() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setModelId("model-001");
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));

        ModelReaEntity modelRea = new ModelReaEntity();
        modelRea.setId("mr-001");
        modelRea.setReaId("rea-001");
        modelRea.setModelId("model-001");
        when(modelReaDao.findAllByModelId("model-001")).thenReturn(List.of(modelRea));

        ReaEntity rea = new ReaEntity();
        rea.setId("rea-001");
        rea.setFieldName("testField");
        when(reaDao.findAllById(Set.of("rea-001"))).thenReturn(List.of(rea));

        ResponseResult<?> result = deviceService.findDeviceReaListById("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询模型字段更新列表-有数据返回列表")
    void getModelFieldUpdateListByModelId_withData_returnsList() {
        ModelEntity model = new ModelEntity();
        model.setId("model-001");
        when(modelDao.findById("model-001")).thenReturn(Optional.of(model));
        when(modelReaDao.findAllByModelId("model-001")).thenReturn(Collections.emptyList());
        when(modelFunctionDao.findAllByModelIdAndIsDelete("model-001", 1)).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceService.getModelFieldUpdateListByModelId("model-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备枪信息-有数据返回")
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
    @DisplayName("查询网关子设备-有数据返回")
    void findSiteSubDeviceList_withData_returnsList() {
        GatewaySubDeviceEntity sub = new GatewaySubDeviceEntity();
        sub.setId("sub-001");
        sub.setGatewayId("gw-001");
        sub.setSubDeviceId("dev-001");
        when(gatewaySubDeviceDao.findAllByGatewayId("gw-001")).thenReturn(List.of(sub));

        DeviceEntity subDevice = new DeviceEntity();
        subDevice.setId("dev-001");
        subDevice.setDeviceName("子设备1");
        when(deviceDao.findAllById(Set.of("dev-001"))).thenReturn(List.of(subDevice));

        ResponseResult<?> result = deviceService.findSiteSubDeviceList("gw-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存设备枪-编辑模式枪编号已存在返回错误")
    void saveDeviceGun_edit_gunCodeExists_returnsError() {
        DeviceGunChangeVo vo = new DeviceGunChangeVo();
        vo.setId("gun-001");
        vo.setDeviceId("dev-001");
        vo.setGunCode("1");

        DeviceGunEntity existing = new DeviceGunEntity();
        existing.setId("gun-002");
        when(deviceGunDao.findAllByDeviceIdAndGunCode("dev-001", "1"))
                .thenReturn(List.of(existing));

        ResponseResult<Void> result = deviceService.saveDeviceGun(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存所有设备枪-无枪数据返回参数错误")
    void saveAllDeviceGun_noGunData_returnsParamError() {
        when(deviceGunDao.findAllById(List.of("gun-001"))).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = deviceService.saveAllDeviceGun(List.of("gun-001"), List.of("dev-001"));
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存所有设备枪-有枪数据且无冲突时成功")
    void saveAllDeviceGun_withGunData_success() {
        DeviceGunEntity gun = new DeviceGunEntity();
        gun.setId("gun-001");
        gun.setDeviceId("dev-001");
        gun.setGunCode("1");
        when(deviceGunDao.findAllById(List.of("gun-001"))).thenReturn(List.of(gun));

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-002");
        device.setDeviceName("设备2");
        device.setSiteId("site-001");
        when(deviceDao.findAllById(List.of("dev-002"))).thenReturn(List.of(device));
        when(deviceGunDao.findAllByDeviceIdIn(List.of("dev-002"))).thenReturn(Collections.emptyList());
        when(deviceGunDao.saveAll(any())).thenReturn(Collections.emptyList());
        when(configureService.notificationStationInfo(any())).thenReturn(ResponseResult.ok());

        ResponseResult<Void> result = deviceService.saveAllDeviceGun(List.of("gun-001"), List.of("dev-002"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备基本信息-设备不存在返回空")
    void findDeviceBasicInfoById_notFound_returnsEmpty() {
        when(deviceDao.findById("dev-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = deviceService.findDeviceBasicInfoById("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据编码查询设备基本信息-无设备返回空")
    void findDeviceBasicInfoByCodes_empty_returnsEmpty() {
        when(deviceDao.findAllByDeviceNumberInAndIsDelete(List.of("DEV001"), 1)).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceService.findDeviceBasicInfoByCodes(List.of("DEV001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询网关子设备列表-网关不存在返回空")
    void findGatewaySubDeviceList_gatewayNotFound_returnsEmpty() {
        when(deviceDao.findById("gw-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = deviceService.findGatewaySubDeviceList("gw-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询网关子设备-有数据返回")
    void findGatewayChildDeviceById_withData_returnsList() {
        GatewaySubDeviceEntity sub = new GatewaySubDeviceEntity();
        sub.setId("sub-001");
        sub.setGatewayId("gw-001");
        sub.setSubDeviceId("dev-001");
        when(gatewaySubDeviceDao.findAllByGatewayId("gw-001")).thenReturn(List.of(sub));

        DeviceEntity subDevice = new DeviceEntity();
        subDevice.setId("dev-001");
        when(deviceDao.findAllById(Set.of("dev-001"))).thenReturn(List.of(subDevice));

        ResponseResult<?> result = deviceService.findGatewayChildDeviceById("gw-001");
        assertTrue(result.isSuccess());
    }
}
