package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.DeviceEventDao;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.access.DeviceEventEntity;
import com.sunmax.device.service.impl.DataFeignServiceImpl;
import com.sunmax.common.vo.device.DeviceEventChangeVo;
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
@DisplayName("DataFeignService 单元测试")
class DataFeignServiceTest {

    @Mock private DeviceDao deviceDao;
    @Mock private DeviceEventDao deviceEventDao;

    @InjectMocks private DataFeignServiceImpl dataFeignService;

    @Test
    @DisplayName("查询设备事件ID-无设备时返回空Map")
    void findDeviceEventIds_noDevices_returnsEmpty() {
        when(deviceDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = dataFeignService.findDeviceEventIds(0);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备事件ID-有设备有事件时返回Map")
    void findDeviceEventIds_withDevicesAndEvents_returnsMap() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setIsDelete(1);
        when(deviceDao.findAll(any(Example.class))).thenReturn(List.of(device));

        DeviceEventEntity event = new DeviceEventEntity();
        event.setDeviceId("dev-001");
        event.setEventId("evt-001");
        when(deviceEventDao.findAllByDeviceIdInAndEventStatus(Set.of("dev-001"), 0))
                .thenReturn(List.of(event));

        ResponseResult<?> result = dataFeignService.findDeviceEventIds(0);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备事件ID-eventStatus为null时查询全部事件")
    void findDeviceEventIds_nullStatus_queriesAll() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setIsDelete(1);
        when(deviceDao.findAll(any(Example.class))).thenReturn(List.of(device));
        when(deviceEventDao.findAllByDeviceIdIn(Set.of("dev-001")))
                .thenReturn(Collections.emptyList());

        ResponseResult<?> result = dataFeignService.findDeviceEventIds(null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("批量更新设备事件-空列表时直接返回成功")
    void batchUpdateDeviceEvent_emptyList_returnsSuccess() {
        ResponseResult<Void> result = dataFeignService.batchUpdateDeviceEvent(Collections.emptyList());
        assertTrue(result.isSuccess());
        verify(deviceEventDao, never()).saveAll(any());
    }

    @Test
    @DisplayName("批量更新设备事件-未修复事件新增")
    void batchUpdateDeviceEvent_unresolvedEvent_addsNew() {
        DeviceEventChangeVo vo = new DeviceEventChangeVo();
        vo.setDeviceId("dev-001");
        vo.setEventId("evt-001");
        vo.setEventStatus(0);
        vo.setEventSource("source");

        when(deviceEventDao.findAllByDeviceIdInAndEventStatus(Set.of("dev-001"), 0))
                .thenReturn(Collections.emptyList());
        when(deviceEventDao.saveAll(any())).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = dataFeignService.batchUpdateDeviceEvent(List.of(vo));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("批量更新设备事件-已修复事件更新")
    void batchUpdateDeviceEvent_resolvedEvent_updates() {
        DeviceEventChangeVo vo = new DeviceEventChangeVo();
        vo.setDeviceId("dev-001");
        vo.setEventId("evt-001");
        vo.setEventStatus(1);

        DeviceEventEntity existing = new DeviceEventEntity();
        existing.setId("de-001");
        existing.setDeviceId("dev-001");
        existing.setEventId("evt-001");
        existing.setIgnoreStatus(0);
        existing.setEventSource("source");
        when(deviceEventDao.findAllByDeviceIdInAndEventStatus(Set.of("dev-001"), 0))
                .thenReturn(List.of(existing));
        when(deviceEventDao.saveAll(any())).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = dataFeignService.batchUpdateDeviceEvent(List.of(vo));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备模型ID-指定deviceId时返回单个")
    void findDeviceModelIds_withDeviceId_returnsSingle() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setModelId("model-001");
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));

        ResponseResult<?> result = dataFeignService.findDeviceModelIds("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备模型ID-deviceId为空时返回全部")
    void findDeviceModelIds_nullDeviceId_returnsAll() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setModelId("model-001");
        device.setIsDelete(1);
        when(deviceDao.findAll(any(Example.class))).thenReturn(List.of(device));

        ResponseResult<?> result = dataFeignService.findDeviceModelIds(null);
        assertTrue(result.isSuccess());
    }
}
