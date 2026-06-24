package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.*;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.entity.model.ModelEntity;
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
@DisplayName("DeviceService 扩展单元测试")
class DeviceServiceExtTest {

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
    @DisplayName("删除设备-硬删除成功(无设备序列号)")
    void deleteDeviceById_hardDelete_noDeviceNumber_success() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceNumber(null);
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));

        ResponseResult<Void> result = deviceService.deleteDeviceById("dev-001", true);
        assertTrue(result.isSuccess());
        verify(deviceDao).delete(device);
        verify(deviceTopologyDao).deleteAllByLeftDeviceIdOrRightDeviceId("dev-001", "dev-001");
    }

    @Test
    @DisplayName("删除设备-软删除成功")
    void deleteDeviceById_softDelete_success() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));
        when(deviceDao.save(any(DeviceEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = deviceService.deleteDeviceById("dev-001", false);
        assertTrue(result.isSuccess());
        verify(deviceDao).save(any(DeviceEntity.class));
    }

    @Test
    @DisplayName("删除设备-不存在时返回参数错误")
    void deleteDeviceById_notFound_returnsParamError() {
        when(deviceDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<Void> result = deviceService.deleteDeviceById("nonexistent", true);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存设备枪-新增成功")
    void saveDeviceGun_new_success() {
        DeviceGunChangeVo vo = new DeviceGunChangeVo();
        vo.setDeviceId("dev-001");
        vo.setGunCode("1");

        when(deviceGunDao.findAllByDeviceIdAndGunCode("dev-001", "1"))
                .thenReturn(Collections.emptyList());
        when(deviceGunDao.save(any(DeviceGunEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(deviceDao.findById("dev-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = deviceService.saveDeviceGun(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存设备枪-枪编号已存在返回错误")
    void saveDeviceGun_gunCodeExists_returnsError() {
        DeviceGunChangeVo vo = new DeviceGunChangeVo();
        vo.setDeviceId("dev-001");
        vo.setGunCode("1");

        DeviceGunEntity existing = new DeviceGunEntity();
        existing.setId("gun-001");
        when(deviceGunDao.findAllByDeviceIdAndGunCode("dev-001", "1"))
                .thenReturn(List.of(existing));

        ResponseResult<Void> result = deviceService.saveDeviceGun(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备枪列表-有数据返回列表")
    void findDeviceGunListByDeviceId_withData_returnsList() {
        DeviceGunEntity gun = new DeviceGunEntity();
        gun.setId("gun-001");
        gun.setDeviceId("dev-001");
        gun.setGunCode("1");
        when(deviceGunDao.findAllByDeviceId("dev-001")).thenReturn(List.of(gun));

        ResponseResult<?> result = deviceService.findDeviceGunListByDeviceId("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备枪列表-无数据返回空列表")
    void findDeviceGunListByDeviceId_empty_returnsEmpty() {
        when(deviceGunDao.findAllByDeviceId("dev-001")).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceService.findDeviceGunListByDeviceId("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除所有设备枪-成功")
    void deleteAllDeviceGun_success() {
        DeviceGunEntity gun = new DeviceGunEntity();
        gun.setId("gun-001");
        when(deviceGunDao.findAllById(List.of("gun-001"))).thenReturn(List.of(gun));

        ResponseResult<Void> result = deviceService.deleteAllDeviceGun(List.of("gun-001"));
        assertTrue(result.isSuccess());
        verify(deviceGunDao).deleteAll(List.of(gun));
    }

    @Test
    @DisplayName("保存设备功能点字段-成功")
    void saveDeviceFunctionField_success() {
        when(deviceFunctionFieldDao.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(deviceFunctionFieldDao.save(any(DeviceFunctionFieldEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = deviceService.saveDeviceFunctionField("dev-001", "[\"func1\",\"func2\"]");
        assertTrue(result.isSuccess());
        verify(deviceFunctionFieldDao).save(any(DeviceFunctionFieldEntity.class));
    }

    @Test
    @DisplayName("保存设备功能点字段-更新已有记录")
    void saveDeviceFunctionField_updateExisting_success() {
        DeviceFunctionFieldEntity existing = new DeviceFunctionFieldEntity();
        existing.setId("field-001");
        existing.setDeviceId("dev-001");
        when(deviceFunctionFieldDao.findOne(any(Example.class))).thenReturn(Optional.of(existing));
        when(deviceFunctionFieldDao.save(any(DeviceFunctionFieldEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = deviceService.saveDeviceFunctionField("dev-001", "[\"func1\",\"func3\"]");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询模型名称列表-有数据返回列表")
    void getModelNameListByTypeId_withData_returnsList() {
        ModelEntity model = new ModelEntity();
        model.setId("model-001");
        model.setModelName("测试模型");
        model.setTypeId("type-001");
        when(modelDao.findAllByTypeIdIn(List.of("type-001"))).thenReturn(List.of(model));

        ResponseResult<?> result = deviceService.getModelNameListByTypeId("type-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除设备拓扑-成功")
    void deleteDeviceTopologyById_success() {
        ResponseResult<Void> result = deviceService.deleteDeviceTopologyById("topo-001");
        assertTrue(result.isSuccess());
        verify(deviceTopologyDao).deleteById("topo-001");
    }

    @Test
    @DisplayName("更新设备事件状态-不存在时也返回成功")
    void updateDeviceEventStatusById_notFound_returnsSuccess() {
        when(deviceEventDao.findById("evt-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = deviceService.updateDeviceEventStatusById("evt-001");
        assertTrue(result.isSuccess());
        verify(deviceEventDao, never()).save(any());
    }

    @Test
    @DisplayName("更新设备事件状态-存在时更新成功")
    void updateDeviceEventStatusById_found_updatesSuccess() {
        DeviceEventEntity event = new DeviceEventEntity();
        event.setId("evt-001");
        event.setEventStatus(0);
        when(deviceEventDao.findById("evt-001")).thenReturn(Optional.of(event));
        when(deviceEventDao.save(any(DeviceEventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = deviceService.updateDeviceEventStatusById("evt-001");
        assertTrue(result.isSuccess());
        verify(deviceEventDao).save(any(DeviceEventEntity.class));
    }

    @Test
    @DisplayName("查询网关子设备列表-有数据返回列表")
    void findGatewaySubDeviceList_withData_returnsList() {
        DeviceEntity gateway = new DeviceEntity();
        gateway.setId("gw-001");
        gateway.setDeviceNumber("GW001");
        when(deviceDao.findById("gw-001")).thenReturn(Optional.of(gateway));

        GatewaySubDeviceEntity sub = new GatewaySubDeviceEntity();
        sub.setId("sub-001");
        sub.setGatewayId("gw-001");
        sub.setSubDeviceId("dev-001");
        when(gatewaySubDeviceDao.findAllByGatewayId("gw-001")).thenReturn(List.of(sub));

        DeviceEntity subDevice = new DeviceEntity();
        subDevice.setId("dev-001");
        subDevice.setDeviceName("子设备1");
        when(deviceDao.findAllById(Set.of("dev-001"))).thenReturn(List.of(subDevice));

        ResponseResult<?> result = deviceService.findGatewaySubDeviceList("gw-001");
        assertTrue(result.isSuccess());
    }
}
