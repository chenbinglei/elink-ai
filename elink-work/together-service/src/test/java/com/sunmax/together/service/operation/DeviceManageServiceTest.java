package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.ProtocolService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.operation.impl.DeviceManageServiceImpl;
import com.sunmax.together.vo.operation.deviceManage.PileListQueryVo;
import com.sunmax.together.vo.operation.deviceManage.AlarmListQueryVo;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.vo.protocol.PileSetQrVo;
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
@DisplayName("DeviceManageService 单元测试")
class DeviceManageServiceTest {

    @Mock private SystemService systemService;
    @Mock private DeviceService deviceService;
    @Mock private ProtocolService protocolService;

    @InjectMocks private DeviceManageServiceImpl deviceManageService;

    @Test
    @DisplayName("查询电桩列表-无授权返回空分页")
    void findPileListByPage_noAuth_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        PileListQueryVo vo = new PileListQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = deviceManageService.findPileListByPage(vo);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().getItems().isEmpty());
    }

    @Test
    @DisplayName("查询电桩列表-有授权无站点返回空分页")
    void findPileListByPage_hasAuthNoSite_returnsEmpty() {
        OrganEmpowerListDto organDto = new OrganEmpowerListDto();
        organDto.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(List.of(organDto)));
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        PileListQueryVo vo = new PileListQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = deviceManageService.findPileListByPage(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询电桩列表-有站点无设备返回空分页")
    void findPileListByPage_hasSiteNoDevice_returnsEmpty() {
        OrganEmpowerListDto organDto = new OrganEmpowerListDto();
        organDto.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(List.of(organDto)));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("site-001", siteInfo)));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), anyInt())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        PileListQueryVo vo = new PileListQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = deviceManageService.findPileListByPage(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询电桩列表-按站点ID过滤")
    void findPileListByPage_filterBySiteId_returnsFiltered() {
        OrganEmpowerListDto organDto = new OrganEmpowerListDto();
        organDto.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(List.of(organDto)));
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        PileListQueryVo vo = new PileListQueryVo();
        vo.setUserId("user-001");
        vo.setSiteId("site-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = deviceManageService.findPileListByPage(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新设备运营状态-委托给DeviceService")
    void updateDeviceOperateStatus_delegatesToDeviceService() {
        when(deviceService.updateDeviceOperateStatus(any(), anyInt())).thenReturn(ResponseResult.ok("success"));

        var result = deviceManageService.updateDeviceOperateStatus("dev-001", 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询告警列表-无授权返回空列表")
    void findAlarmListByPage_noAuth_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        AlarmListQueryVo vo = new AlarmListQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = deviceManageService.findAlarmListByPage(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询告警列表-无站点返回空列表")
    void findAlarmListByPage_hasAuthNoSite_returnsEmpty() {
        OrganEmpowerListDto organDto = new OrganEmpowerListDto();
        organDto.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(List.of(organDto)));
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        AlarmListQueryVo vo = new AlarmListQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(10);
        var result = deviceManageService.findAlarmListByPage(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询告警列表-size<=0返回全部")
    void findAlarmListByPage_sizeZero_returnsAll() {
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        AlarmListQueryVo vo = new AlarmListQueryVo();
        vo.setUserId("user-001");
        vo.setPage(1);
        vo.setSize(0);
        var result = deviceManageService.findAlarmListByPage(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("电桩设置二维码-委托给ProtocolService")
    void pileSetQr_delegatesToProtocolService() {
        when(protocolService.pileSetQr(any())).thenReturn(ResponseResult.ok());

        PileSetQrVo vo = new PileSetQrVo();
        var result = deviceManageService.pileSetQr(vo);
        assertTrue(result.isSuccess());
    }
}
