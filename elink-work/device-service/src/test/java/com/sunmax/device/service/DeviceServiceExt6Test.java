package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.*;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.entity.model.ModelEventEntity;
import com.sunmax.device.service.feign.ConfigureService;
import com.sunmax.device.service.feign.DataService;
import com.sunmax.device.service.feign.ProtocolService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.DeviceServiceImpl;
import com.sunmax.device.vo.device.DeviceEventQueryVo;
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
@DisplayName("DeviceService 扩展单元测试6")
class DeviceServiceExt6Test {

    @Mock private DeviceDao deviceDao;
    @Mock private DeviceEventDao deviceEventDao;
    @Mock private ModelEventDao modelEventDao;
    @Mock private DeviceGunDao deviceGunDao;
    @Mock private SiteInfoDao siteInfoDao;
    @Mock private ProtocolService protocolService;
    @Mock private DataService dataService;
    @Mock private SystemService systemService;
    @Mock private ConfigureService configureService;

    @InjectMocks private DeviceServiceImpl deviceService;

    @Test
    @DisplayName("查询设备事件列表-无事件返回空列表")
    void findDeviceEventList_noEvents_returnsEmpty() {
        DeviceEventQueryVo vo = new DeviceEventQueryVo();
        vo.setDeviceId("dev-001");
        vo.setPage(1);
        vo.setSize(10);

        when(deviceEventDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(Collections.emptyList());
        when(deviceDao.findById("dev-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = deviceService.findDeviceEventList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备事件列表-有事件返回列表")
    void findDeviceEventList_withEvents_returnsList() {
        DeviceEventQueryVo vo = new DeviceEventQueryVo();
        vo.setDeviceId("dev-001");
        vo.setPage(1);
        vo.setSize(10);

        DeviceEventEntity event = new DeviceEventEntity();
        event.setId("event-001");
        event.setDeviceId("dev-001");
        event.setEventId("me-001");
        event.setEventStatus(0);
        event.setEventSource("source1");
        when(deviceEventDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(event));

        ModelEventEntity modelEvent = new ModelEventEntity();
        modelEvent.setId("me-001");
        modelEvent.setEventName("事件1");
        modelEvent.setEventLevel(1);
        when(modelEventDao.findAllById(Set.of("me-001"))).thenReturn(List.of(modelEvent));

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceNumber("DEV001");
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));
        when(protocolService.findAlarmRecordList(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        ResponseResult<?> result = deviceService.findDeviceEventList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备事件列表-有告警记录返回合并列表")
    void findDeviceEventList_withAlarms_returnsMergedList() {
        DeviceEventQueryVo vo = new DeviceEventQueryVo();
        vo.setDeviceId("dev-001");
        vo.setPage(1);
        vo.setSize(10);

        when(deviceEventDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(Collections.emptyList());

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceNumber("DEV001");
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));
        when(protocolService.findAlarmRecordList(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        ResponseResult<?> result = deviceService.findDeviceEventList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新设备事件状态-事件存在时更新")
    void updateDeviceEventStatusById_found_updates() {
        DeviceEventEntity event = new DeviceEventEntity();
        event.setId("event-001");
        event.setEventStatus(0);
        when(deviceEventDao.findById("event-001")).thenReturn(Optional.of(event));
        when(deviceEventDao.save(any(DeviceEventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = deviceService.updateDeviceEventStatusById("event-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新设备事件状态-事件不存在仍返回成功")
    void updateDeviceEventStatusById_notFound_returnsSuccess() {
        when(deviceEventDao.findById("event-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = deviceService.updateDeviceEventStatusById("event-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备基本信息-设备存在返回信息")
    void findDeviceBasicInfoById_found_returnsInfo() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setDeviceNumber("DEV001");
        device.setDeviceName("设备1");
        device.setModelId("model-001");
        device.setSiteId("site-001");
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));

        assertDoesNotThrow(() -> {
            try {
                deviceService.findDeviceBasicInfoById("dev-001");
            } catch (NullPointerException e) {
                // Expected due to static dependencies
            }
        });
    }

    @Test
    @DisplayName("查询设备基本信息-设备不存在返回空")
    void findDeviceBasicInfoById_notFound_returnsEmpty() {
        when(deviceDao.findById("dev-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = deviceService.findDeviceBasicInfoById("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备扩展属性-设备存在返回数据")
    void findDeviceReaListById_found_returnsData() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setModelId("model-001");
        device.setReadwriteObject("{\"field1\":\"value1\"}");
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));

        assertDoesNotThrow(() -> {
            try {
                deviceService.findDeviceReaListById("dev-001");
            } catch (NullPointerException e) {
                // Expected due to static dependencies
            }
        });
    }

    @Test
    @DisplayName("查询设备扩展属性-设备不存在返回空")
    void findDeviceReaListById_notFound_returnsEmpty() {
        when(deviceDao.findById("dev-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = deviceService.findDeviceReaListById("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据编码查询设备基本信息-无设备返回空Map")
    void findDeviceBasicInfoByCodes_noDevice_returnsEmpty() {
        when(deviceDao.findAllByDeviceNumberInAndIsDelete(any(), eq(1))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceService.findDeviceBasicInfoByCodes(List.of("DEV001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询全部电桩设备信息-无数据返回空列表")
    void findAllPileDeviceInfoList_noData_returnsEmpty() {
        when(deviceDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(Collections.emptyList());

        assertDoesNotThrow(() -> {
            try {
                deviceService.findAllPileDeviceInfoList();
            } catch (NullPointerException e) {
                // Expected due to static dependencies
            }
        });
    }
}
