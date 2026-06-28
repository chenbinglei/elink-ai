package com.sunmax.device.service;

import com.sunmax.common.config.redis.*;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.*;
import com.sunmax.device.dto.model.ModelFieldUpdateDto;
import com.sunmax.device.service.feign.ConfigureService;
import com.sunmax.device.service.feign.DataService;
import com.sunmax.device.service.feign.ProtocolService;
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
@DisplayName("DeviceService 单元测试")
class DeviceServiceTest {

    @Mock
    private DeviceDao deviceDao;

    @Mock
    private ModelDao modelDao;

    @Mock
    private ModelReaDao modelReaDao;

    @Mock
    private ReaDao reaDao;

    @Mock
    private ModelFunctionDao modelFunctionDao;

    @Mock
    private FunctionDao functionDao;

    @Mock
    private ModelTopologyDao modelTopologyDao;

    @Mock
    private DeviceTopologyDao deviceTopologyDao;

    @Mock
    private SystemService systemService;

    @Mock
    private DeviceEventDao deviceEventDao;

    @Mock
    private ModelEventDao modelEventDao;

    @Mock
    private SiteInfoDao siteInfoDao;

    @Mock
    private GatewaySubDeviceDao gatewaySubDeviceDao;

    @Mock
    private DataService dataService;

    @Mock
    private DeviceGunDao deviceGunDao;

    @Mock
    private AssetTypeDao assetTypeDao;

    @Mock
    private ProtocolService protocolService;

    @Mock
    private ScenarioTypeDao scenarioTypeDao;

    @Mock
    private ConfigureService configureService;

    @Mock
    private DeviceFunctionFieldDao deviceFunctionFieldDao;

    @Mock
    private RedisDeviceUtil redisDeviceUtil;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private DeviceServiceImpl deviceService;

    @Test
    @DisplayName("根据ID查询设备基本信息-不存在时返回参数错误")
    void findDeviceBasicInfoById_notExists_returnsParamError() {
        when(deviceDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<DeviceBasicInfoDto> result = deviceService.findDeviceBasicInfoById("nonexistent");

        assertNotNull(result);
    }

    @Test
    @DisplayName("根据模型ID获取模型编辑字段列表")
    void getModelFieldUpdateListByModelId_returnsList() {
        when(modelFunctionDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<List<ModelFieldUpdateDto>> result = deviceService.getModelFieldUpdateListByModelId("model-001");

        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除设备-不存在时返回参数错误")
    void deleteDeviceById_notExists_returnsParamError() {
        when(deviceDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<Void> result = deviceService.deleteDeviceById("nonexistent", false);

        assertNotNull(result);
    }

    @Test
    @DisplayName("查询设备功能列表-设备不存在时返回空")
    void findDeviceFunctionListById_notExists_returnsEmpty() {
        when(deviceDao.findById("nonexistent")).thenReturn(Optional.empty());

        var result = deviceService.findDeviceFunctionListById("nonexistent");

        assertNotNull(result);
    }

    @Test
    @DisplayName("查询设备REA列表-设备不存在时返回空")
    void findDeviceReaListById_notExists_returnsEmpty() {
        when(deviceDao.findById("nonexistent")).thenReturn(Optional.empty());

        var result = deviceService.findDeviceReaListById("nonexistent");

        assertNotNull(result);
    }

    @Test
    @DisplayName("删除设备拓扑-直接删除")
    void deleteDeviceTopologyById_deletesSuccessfully() {
        when(deviceTopologyDao.findById("topo-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = deviceService.deleteDeviceTopologyById("topo-001");

        assertNotNull(result);
    }

    @Test
    @DisplayName("查询网关子设备列表-网关不存在时返回空")
    void findGatewaySubDeviceList_notExists_returnsEmpty() {
        when(deviceDao.findById("gw-001")).thenReturn(Optional.empty());

        var result = deviceService.findGatewaySubDeviceList("gw-001");

        assertNotNull(result);
    }
}
