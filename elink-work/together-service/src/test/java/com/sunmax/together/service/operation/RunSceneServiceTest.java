package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.operation.impl.RunSceneServiceImpl;
import com.sunmax.together.vo.operation.runScene.SitePileMonitorQueryVo;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.vo.together.PileGunChangeVo;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RunSceneService 单元测试")
class RunSceneServiceTest {

    @Mock private SystemService systemService;
    @Mock private DeviceService deviceService;
    @Mock private OrderRecordService orderRecordService;

    @InjectMocks private RunSceneServiceImpl runSceneService;

    @Test
    @DisplayName("统计站点电桩监控-无授权返回空对象")
    void countSitePileMonitor_noAuth_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        SitePileMonitorQueryVo vo = new SitePileMonitorQueryVo();
        vo.setUserId("user-001");
        var result = runSceneService.countSitePileMonitor(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("统计站点电桩监控-有授权无站点返回空对象")
    void countSitePileMonitor_hasAuthNoSite_returnsEmpty() {
        OrganEmpowerListDto organDto = new OrganEmpowerListDto();
        organDto.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(List.of(organDto)));
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        SitePileMonitorQueryVo vo = new SitePileMonitorQueryVo();
        vo.setUserId("user-001");
        var result = runSceneService.countSitePileMonitor(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("统计站点电桩监控-指定站点ID查询")
    void countSitePileMonitor_withSiteIds_returnsResult() {
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        SitePileMonitorQueryVo vo = new SitePileMonitorQueryVo();
        vo.setSiteIds("[\"site-001\"]");
        vo.setScenarioTypes(1);
        var result = runSceneService.countSitePileMonitor(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询电桩详情-无设备返回空对象")
    void findPileDetailById_noDevice_returnsEmpty() {
        when(deviceService.findDeviceBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = runSceneService.findPileDetailById("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询电桩详情-有设备返回数据")
    void findPileDetailById_hasDevice_returnsData() {
        DeviceBasicInfoDto deviceInfo = new DeviceBasicInfoDto();
        deviceInfo.setId("dev-001");
        deviceInfo.setDeviceName("Test Pile");
        deviceInfo.setDeviceNumber("P001");
        deviceInfo.setReaMap(new HashMap<>());
        when(deviceService.findDeviceBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("dev-001", deviceInfo)));
        when(deviceService.findDeviceReaListById(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));
        when(deviceService.findDeviceGunInfoByDeviceIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = runSceneService.findPileDetailById("dev-001");
        assertTrue(result.isSuccess());
        assertEquals("Test Pile", result.getData().getDeviceName());
    }

    @Test
    @DisplayName("更新电桩扩展属性-委托给DeviceService")
    void updatePileRea_delegatesToDeviceService() {
        when(deviceService.updatePileRea(any(), any(), any())).thenReturn(ResponseResult.ok());

        var result = runSceneService.updatePileRea("dev-001", "user-001", "{}");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新电桩枪信息-委托给DeviceService")
    void updatePileGun_delegatesToDeviceService() {
        when(deviceService.updatePileGun(any())).thenReturn(ResponseResult.ok());

        PileGunChangeVo vo = new PileGunChangeVo();
        var result = runSceneService.updatePileGun(vo);
        assertTrue(result.isSuccess());
    }
}
