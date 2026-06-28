package com.sunmax.together.service.monitor;

import com.google.common.collect.Maps;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.DeviceAlarmEventListDto;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.vo.monitor.centralMonitor.DeviceAlarmQueryVo;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.monitor.impl.WarningServiceImpl;
import com.sunmax.together.vo.monitor.centralMonitor.AlarmQueryVo;
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
@DisplayName("WarningService 单元测试")
class WarningServiceTest {

    @Mock private SystemService systemService;
    @Mock private DeviceService deviceService;

    @InjectMocks private WarningServiceImpl warningService;

    @Test
    @DisplayName("更新事件忽略状态-委托给deviceService")
    void updateEventIgnoreStatus_delegatesToDeviceService() {
        when(deviceService.updateEventIgnoreStatus("event-001", 1)).thenReturn(ResponseResult.ok());

        ResponseResult<Void> result = warningService.updateEventIgnoreStatus("event-001", 1);
        assertTrue(result.isSuccess());
        verify(deviceService).updateEventIgnoreStatus("event-001", 1);
    }

    @Test
    @DisplayName("告警站点统计-参数为空返回错误")
    void alarmSiteCount_emptyParams_returnsError() {
        ResponseResult<?> result = warningService.alarmSiteCount(null, null, null, null, null);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("告警站点统计-开始日期为空返回错误")
    void alarmSiteCount_emptyStartDate_returnsError() {
        ResponseResult<?> result = warningService.alarmSiteCount("user-001", null, null, null, "2026-01-01");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("告警站点统计-结束日期为空返回错误")
    void alarmSiteCount_emptyEndDate_returnsError() {
        ResponseResult<?> result = warningService.alarmSiteCount("user-001", null, null, "2026-01-01", null);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("获取事件列表-无设备返回空列表")
    void getEventList_noDevices_returnsEmptyList() {
        when(deviceService.findDeviceInfoByParentIds(Collections.singletonList("sys-001")))
                .thenReturn(ResponseResult.ok(Maps.newHashMap()));

        ResponseResult<?> result = warningService.getEventList("sys-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("获取事件列表-有设备有告警返回数据")
    void getEventList_withDeviceAndAlarm_returnsData() {
        DeviceBasicInfoDto device = new DeviceBasicInfoDto();
        device.setId("dev-001");
        device.setDeviceName("电桩1");
        Map<String, List<DeviceBasicInfoDto>> data = new HashMap<>();
        data.put("sys-001", List.of(device));
        when(deviceService.findDeviceInfoByParentIds(any())).thenReturn(ResponseResult.ok(data));

        DeviceAlarmEventListDto alarmEvent = new DeviceAlarmEventListDto();
        alarmEvent.setDeviceId("dev-001");
        alarmEvent.setCreateTime("2026-01-15 10:30:00");
        alarmEvent.setUpdateTime("2026-01-15 10:35:00");
        PageDto<DeviceAlarmEventListDto> page = new PageDto<>();
        page.setItems(List.of(alarmEvent));
        when(deviceService.findAllDeviceEventList(any())).thenReturn(ResponseResult.ok(page));

        ResponseResult<?> result = warningService.getEventList("sys-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("告警站点统计-无站点返回空对象")
    void alarmSiteCount_noSite_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId("user-001")).thenReturn(ResponseResult.ok(Collections.emptyList()));

        ResponseResult<?> result = warningService.alarmSiteCount("user-001", null, null, "2026-01-01", "2026-01-31");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("告警站点统计-有站点有告警返回数据")
    void alarmSiteCount_withSiteAndAlarm_returnsData() {
        OrganEmpowerListDto empower = new OrganEmpowerListDto();
        empower.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId("user-001")).thenReturn(ResponseResult.ok(List.of(empower)));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setSiteName("站点1");
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("site-001", siteInfo)));

        DeviceBasicInfoDto device = new DeviceBasicInfoDto();
        device.setId("dev-001");
        Map<String, List<DeviceBasicInfoDto>> deviceMap = new HashMap<>();
        deviceMap.put("site-001", List.of(device));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any())).thenReturn(ResponseResult.ok(deviceMap));

        DeviceAlarmEventListDto alarmEvent = new DeviceAlarmEventListDto();
        alarmEvent.setDeviceId("dev-001");
        PageDto<DeviceAlarmEventListDto> page = new PageDto<>();
        page.setItems(List.of(alarmEvent));
        when(deviceService.findAllDeviceEventList(any())).thenReturn(ResponseResult.ok(page));

        ResponseResult<?> result = warningService.alarmSiteCount("user-001", null, null, "2026-01-01", "2026-01-31");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("告警设备统计-参数为空返回错误")
    void alarmDeviceCount_emptyParams_returnsError() {
        ResponseResult<?> result = warningService.alarmDeviceCount(null, null, null);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("告警设备统计-有数据返回")
    void alarmDeviceCount_withData_returnsData() {
        DeviceAlarmEventListDto alarmEvent = new DeviceAlarmEventListDto();
        alarmEvent.setDeviceId("dev-001");
        alarmEvent.setCreateTime("2026-01-15 10:30:00");
        alarmEvent.setUpdateTime("2026-01-15 11:30:00");
        alarmEvent.setEventName("过温告警");
        alarmEvent.setEventLevel(1);
        PageDto<DeviceAlarmEventListDto> page = new PageDto<>();
        page.setItems(List.of(alarmEvent));
        when(deviceService.findAllDeviceEventList(any())).thenReturn(ResponseResult.ok(page));

        ResponseResult<?> result = warningService.alarmDeviceCount("dev-001", "2026-01-01", "2026-01-31");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备告警列表-无告警返回空Map")
    void findAlarmEventListByDeviceId_noAlarm_returnsEmpty() {
        DeviceAlarmQueryVo vo = new DeviceAlarmQueryVo();
        vo.setDeviceId("dev-001");
        vo.setStartDate("2026-01-01");
        vo.setEndDate("2026-01-31");

        PageDto<DeviceAlarmEventListDto> page = new PageDto<>();
        page.setItems(Collections.emptyList());
        when(deviceService.findAllDeviceEventList(any())).thenReturn(ResponseResult.ok(page));

        ResponseResult<?> result = warningService.findAlarmEventListByDeviceId(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备告警列表-有告警返回数据")
    void findAlarmEventListByDeviceId_withAlarm_returnsData() {
        DeviceAlarmQueryVo vo = new DeviceAlarmQueryVo();
        vo.setDeviceId("dev-001");
        vo.setStartDate("2026-01-01");
        vo.setEndDate("2026-01-31");

        DeviceAlarmEventListDto alarmEvent = new DeviceAlarmEventListDto();
        alarmEvent.setDeviceId("dev-001");
        alarmEvent.setCreateTime("2026-01-15 10:30:00");
        alarmEvent.setUpdateTime("2026-01-15 11:30:00");
        PageDto<DeviceAlarmEventListDto> page = new PageDto<>();
        page.setItems(List.of(alarmEvent));
        when(deviceService.findAllDeviceEventList(any())).thenReturn(ResponseResult.ok(page));

        ResponseResult<?> result = warningService.findAlarmEventListByDeviceId(vo);
        assertTrue(result.isSuccess());
    }

    // === 深度覆盖测试 ===

    @Test
    @DisplayName("查询告警事件列表-无授权返回空分页")
    void queryAlarmEventList_noAuth_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId("user-001")).thenReturn(ResponseResult.ok(Collections.emptyList()));

        AlarmQueryVo vo = new AlarmQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = warningService.queryAlarmEventList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询告警事件列表-有站点无设备返回空分页")
    void queryAlarmEventList_hasSiteNoDevice_returnsEmpty() {
        OrganEmpowerListDto empower = new OrganEmpowerListDto();
        empower.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId("user-001")).thenReturn(ResponseResult.ok(List.of(empower)));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("site-001", siteInfo)));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any())).thenReturn(ResponseResult.ok(Maps.newHashMap()));

        AlarmQueryVo vo = new AlarmQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = warningService.queryAlarmEventList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询告警事件列表-有站点有设备返回数据")
    void queryAlarmEventList_hasSiteAndDevice_returnsData() {
        OrganEmpowerListDto empower = new OrganEmpowerListDto();
        empower.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId("user-001")).thenReturn(ResponseResult.ok(List.of(empower)));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("site-001", siteInfo)));

        DeviceBasicInfoDto device = new DeviceBasicInfoDto();
        device.setId("dev-001");
        device.setTypeId("35");
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any())).thenReturn(ResponseResult.ok(Map.of("site-001", List.of(device))));

        DeviceAlarmEventListDto alarmEvent = new DeviceAlarmEventListDto();
        alarmEvent.setDeviceId("dev-001");
        alarmEvent.setCreateTime("2026-01-15 10:30:00");
        PageDto<DeviceAlarmEventListDto> page = new PageDto<>();
        page.setItems(List.of(alarmEvent));
        when(deviceService.findAllDeviceEventList(any())).thenReturn(ResponseResult.ok(page));

        AlarmQueryVo vo = new AlarmQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        vo.setStartDate("2026-01-01");
        vo.setEndDate("2026-01-31");
        var result = warningService.queryAlarmEventList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询告警事件列表-指定站点ID过滤")
    void queryAlarmEventList_withSiteIdFilter_returnsData() {
        OrganEmpowerListDto empower = new OrganEmpowerListDto();
        empower.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId("user-001")).thenReturn(ResponseResult.ok(List.of(empower)));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("site-001", siteInfo)));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any())).thenReturn(ResponseResult.ok(Maps.newHashMap()));

        AlarmQueryVo vo = new AlarmQueryVo();
        vo.setUserId("user-001");
        vo.setSiteId("site-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = warningService.queryAlarmEventList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询告警事件列表-指定设备类型过滤")
    void queryAlarmEventList_withTypeIdFilter_returnsData() {
        OrganEmpowerListDto empower = new OrganEmpowerListDto();
        empower.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId("user-001")).thenReturn(ResponseResult.ok(List.of(empower)));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("site-001", siteInfo)));

        DeviceBasicInfoDto device = new DeviceBasicInfoDto();
        device.setId("dev-001");
        device.setTypeId("35");
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any())).thenReturn(ResponseResult.ok(Map.of("site-001", List.of(device))));

        DeviceAlarmEventListDto alarmEvent = new DeviceAlarmEventListDto();
        alarmEvent.setDeviceId("dev-001");
        PageDto<DeviceAlarmEventListDto> page = new PageDto<>();
        page.setItems(List.of(alarmEvent));
        when(deviceService.findAllDeviceEventList(any())).thenReturn(ResponseResult.ok(page));

        AlarmQueryVo vo = new AlarmQueryVo();
        vo.setUserId("user-001");
        vo.setTypeId("35");
        vo.setPage(1);
        vo.setSize(10);
        vo.setStartDate("2026-01-01");
        vo.setEndDate("2026-01-31");
        var result = warningService.queryAlarmEventList(vo);
        assertTrue(result.isSuccess());
    }
}
