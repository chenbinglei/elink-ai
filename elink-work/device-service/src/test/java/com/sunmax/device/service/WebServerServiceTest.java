package com.sunmax.device.service;

import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.SiteInfoDao;
import com.sunmax.device.service.impl.WebServerServiceImpl;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.feign.TogetherService;
import com.sunmax.device.vo.webserver.CountQueryVo;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("WebServerService 单元测试")
class WebServerServiceTest {

    @Mock private SiteInfoDao siteInfoDao;
    @Mock private DeviceDao deviceDao;
    @Mock private SystemService systemService;
    @Mock private TogetherService togetherService;

    @InjectMocks private WebServerServiceImpl webServerService;

    @Test
    @DisplayName("查询台账列表-tenantId为空返回参数错误")
    void findLedgerListByTenetId_emptyTenantId_returnsParamError() {
        ResponseResult<?> result = webServerService.findLedgerListByTenetId("");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询台账列表-租户无授权站点返回空列表")
    void findLedgerListByTenetId_noSites_returnsEmpty() {
        when(systemService.findOrganEmpowerListByTenantId("tenant-001"))
                .thenReturn(ResponseResult.ok(Collections.emptyList()));

        ResponseResult<?> result = webServerService.findLedgerListByTenetId("tenant-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询统计列表-siteIds为空返回参数错误")
    void findCountListByCondition_emptySiteIds_returnsParamError() {
        CountQueryVo vo = new CountQueryVo();
        vo.setSiteIds("");

        ResponseResult<?> result = webServerService.findCountListByCondition(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询统计列表-正常查询返回结果")
    void findCountListByCondition_normalQuery_returnsResult() {
        CountQueryVo vo = new CountQueryVo();
        vo.setSiteIds("site-001,site-002");
        vo.setStartTime("2026-01-01");
        vo.setEndTime("2026-01-31");

        when(deviceDao.findAllBySiteIdInAndIsDelete(List.of("site-001", "site-002"), 1))
                .thenReturn(Collections.emptyList());
        when(togetherService.findOrderRecordListBySiteIds(any(), any(), any()))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        ResponseResult<?> result = webServerService.findCountListByCondition(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备列表-ids为空返回参数错误")
    void findDeviceListByIdsAndType_emptyIds_returnsParamError() {
        ResponseResult<?> result = webServerService.findDeviceListByIdsAndType("", 1);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备列表-type为空返回参数错误")
    void findDeviceListByIdsAndType_emptyType_returnsParamError() {
        ResponseResult<?> result = webServerService.findDeviceListByIdsAndType("id1,id2", null);
        assertFalse(result.isSuccess());
    }
}
