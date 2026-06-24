package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.ModelDao;
import com.sunmax.device.dao.model.PileFaultDao;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.entity.model.ModelEntity;
import com.sunmax.device.entity.model.PileFaultEntity;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.ProtocolFeignServiceImpl;
import com.sunmax.common.vo.protocol.PileSetQrVo;
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
@DisplayName("ProtocolFeignService 单元测试")
class ProtocolFeignServiceTest {

    @Mock private DeviceDao deviceDao;
    @Mock private PileFaultDao pileFaultDao;
    @Mock private PointTableDao pointTableDao;
    @Mock private SiteInfoDao siteInfoDao;
    @Mock private ModelDao modelDao;
    @Mock private SystemService systemService;
    @Mock private DeviceGunDao deviceGunDao;

    @InjectMocks private ProtocolFeignServiceImpl protocolFeignService;

    @Test
    @DisplayName("获取设备编号列表-无设备时返回空集合")
    void getDeviceNumberList_noDevices_returnsEmpty() {
        when(deviceDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = protocolFeignService.getDeviceNumberList(null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("获取设备编号列表-按接入类型查询")
    void getDeviceNumberList_byAccessType_returnsList() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceNumber("DEV001");
        when(deviceDao.findAllByAccessTypeAndIsDelete(1, 1)).thenReturn(List.of(device));

        ResponseResult<?> result = protocolFeignService.getDeviceNumberList(1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("获取设备编号列表-全部设备")
    void getDeviceNumberList_allDevices_returnsList() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceNumber("DEV001");
        when(deviceDao.findAll(any(Example.class))).thenReturn(List.of(device));

        ResponseResult<?> result = protocolFeignService.getDeviceNumberList(null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询充电桩故障列表-设备编号为空时返回空Map")
    void findPileFaultList_emptyDeviceCode_returnsEmpty() {
        ResponseResult<?> result = protocolFeignService.findPileFaultList("", Set.of(1, 2));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询充电桩故障列表-故障码为空时返回空Map")
    void findPileFaultList_emptyFaultCodes_returnsEmpty() {
        ResponseResult<?> result = protocolFeignService.findPileFaultList("DEV001", Collections.emptySet());
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询充电桩故障列表-正常查询返回结果")
    void findPileFaultList_normalQuery_returnsResult() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setModelId("model-001");
        when(deviceDao.findAllByDeviceNumberAndIsDelete("DEV001", 1)).thenReturn(List.of(device));

        PileFaultEntity fault = new PileFaultEntity();
        fault.setFaultCode(1);
        fault.setEventName("过温故障");
        when(pileFaultDao.findAllByModelIdAndFaultCodeIn("model-001", Set.of(1)))
                .thenReturn(List.of(fault));

        ResponseResult<?> result = protocolFeignService.findPileFaultList("DEV001", Set.of(1));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新设备二维码-设备不存在时直接返回成功")
    void updateDeviceQrStr_noDevice_returnsSuccess() {
        PileSetQrVo vo = new PileSetQrVo();
        vo.setPileCode("DEV001");
        vo.setQrStr("http://qr.example.com/");
        when(deviceDao.findAllByDeviceNumberAndIsDelete("DEV001", 1))
                .thenReturn(Collections.emptyList());

        ResponseResult<Void> result = protocolFeignService.updateDeviceQrStr(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新设备二维码-有设备时更新枪二维码")
    void updateDeviceQrStr_withDevice_updatesGunQr() {
        PileSetQrVo vo = new PileSetQrVo();
        vo.setPileCode("DEV001");
        vo.setQrStr("http://qr.example.com/");

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        when(deviceDao.findAllByDeviceNumberAndIsDelete("DEV001", 1)).thenReturn(List.of(device));

        DeviceGunEntity gun = new DeviceGunEntity();
        gun.setId("gun-001");
        gun.setGunCode("1");
        when(deviceGunDao.findAllByDeviceIdIn(Set.of("dev-001"))).thenReturn(List.of(gun));
        when(deviceGunDao.saveAll(any())).thenReturn(List.of(gun));

        ResponseResult<Void> result = protocolFeignService.updateDeviceQrStr(vo);
        assertTrue(result.isSuccess());
        verify(deviceGunDao).saveAll(any());
    }

    @Test
    @DisplayName("查询设备枪信息-无设备时返回空Map")
    void findDeviceGunInfoByDeviceCodes_noDevices_returnsEmpty() {
        when(deviceDao.findAllByDeviceNumberInAndIsDelete(List.of("DEV001"), 1))
                .thenReturn(Collections.emptyList());

        ResponseResult<?> result = protocolFeignService.findDeviceGunInfoByDeviceCodes(List.of("DEV001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备枪信息-有设备有枪返回结果")
    void findDeviceGunInfoByDeviceCodes_withDevicesAndGuns_returnsResult() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceNumber("DEV001");
        when(deviceDao.findAllByDeviceNumberInAndIsDelete(List.of("DEV001"), 1))
                .thenReturn(List.of(device));

        DeviceGunEntity gun = new DeviceGunEntity();
        gun.setId("gun-001");
        gun.setDeviceId("dev-001");
        gun.setGunCode("1");
        when(deviceGunDao.findAllByDeviceIdIn(Set.of("dev-001"))).thenReturn(List.of(gun));

        ResponseResult<?> result = protocolFeignService.findDeviceGunInfoByDeviceCodes(List.of("DEV001"));
        assertTrue(result.isSuccess());
    }
}
