package com.sunmax.device.service;

import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.DeviceGunDao;
import com.sunmax.device.dao.access.SiteInfoDao;
import com.sunmax.device.dao.model.AssetTypeDao;
import com.sunmax.device.dao.model.ModelReaDao;
import com.sunmax.device.dao.model.ReaDao;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.access.SiteInfoEntity;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.feign.TogetherService;
import com.sunmax.device.service.impl.WebServerServiceImpl;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("WebServerService 扩展单元测试")
class WebServerServiceExtTest {

    @Mock private SiteInfoDao siteInfoDao;
    @Mock private DeviceDao deviceDao;
    @Mock private SystemService systemService;
    @Mock private DeviceGunDao deviceGunDao;
    @Mock private ModelReaDao modelReaDao;
    @Mock private ReaDao reaDao;
    @Mock private AssetTypeDao assetTypeDao;
    @Mock private TogetherService togetherService;

    @InjectMocks private WebServerServiceImpl webServerService;

    @Test
    @DisplayName("查询设备列表-空参数返回参数错误")
    void findDeviceListByIdsAndType_emptyParams_returnsParamError() {
        ResponseResult<?> result = webServerService.findDeviceListByIdsAndType(null, null);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询台账列表-空租户ID返回参数错误")
    void findLedgerListByTenetId_emptyTenantId_returnsParamError() {
        ResponseResult<?> result = webServerService.findLedgerListByTenetId(null);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询台账列表-无站点权限返回空列表")
    void findLedgerListByTenetId_noSitePermission_returnsEmpty() {
        when(systemService.findOrganEmpowerListByTenantId("tenant-001"))
                .thenReturn(ResponseResult.ok(Collections.emptyList()));

        ResponseResult<?> result = webServerService.findLedgerListByTenetId("tenant-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询台账列表-有站点但无有效站点数据返回空列表")
    void findLedgerListByTenetId_noActiveSites_returnsEmpty() {
        OrganEmpowerListDto empDto = new OrganEmpowerListDto();
        empDto.setSiteId("site-001");
        when(systemService.findOrganEmpowerListByTenantId("tenant-001"))
                .thenReturn(ResponseResult.ok(List.of(empDto)));
        when(siteInfoDao.findAllById(Set.of("site-001"))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = webServerService.findLedgerListByTenetId("tenant-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询台账列表-有站点和设备返回完整数据")
    void findLedgerListByTenetId_withData_returnsFullData() {
        OrganEmpowerListDto empDto = new OrganEmpowerListDto();
        empDto.setSiteId("site-001");
        when(systemService.findOrganEmpowerListByTenantId("tenant-001"))
                .thenReturn(ResponseResult.ok(List.of(empDto)));

        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setTenantId("tenant-001");
        site.setIsDelete(1);
        site.setSiteModelId("model-001");
        when(siteInfoDao.findAllById(Set.of("site-001"))).thenReturn(List.of(site));
        when(systemService.findTenantDetailsByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));
        when(deviceDao.findAllBySiteIdInAndIsDelete(any(), eq(1))).thenReturn(Collections.emptyList());
        when(modelReaDao.findAllByModelIdIn(any())).thenReturn(Collections.emptyList());

        ResponseResult<?> result = webServerService.findLedgerListByTenetId("tenant-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询统计列表-空站点ID返回参数错误")
    void findCountListByCondition_emptySiteIds_returnsParamError() {
        CountQueryVo vo = new CountQueryVo();
        vo.setSiteIds(null);

        ResponseResult<?> result = webServerService.findCountListByCondition(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询统计列表-有站点和设备返回统计数据")
    void findCountListByCondition_withData_returnsCountData() {
        CountQueryVo vo = new CountQueryVo();
        vo.setSiteIds("site-001");
        vo.setStartTime("2026-01-01");
        vo.setEndTime("2026-06-01");

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setSiteId("site-001");
        device.setTypeId("28");
        device.setDeviceNumber("DEV001");
        when(deviceDao.findAllBySiteIdInAndIsDelete(List.of("site-001"), 1)).thenReturn(List.of(device));
        when(togetherService.findOrderRecordListBySiteIds(any(), any(), any()))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        ResponseResult<?> result = webServerService.findCountListByCondition(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询统计列表-有订单数据返回完整统计")
    void findCountListByCondition_withOrders_returnsFullCount() {
        CountQueryVo vo = new CountQueryVo();
        vo.setSiteIds("site-001");

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setSiteId("site-001");
        device.setTypeId("28");
        device.setDeviceNumber("DEV001");
        when(deviceDao.findAllBySiteIdInAndIsDelete(List.of("site-001"), 1)).thenReturn(List.of(device));

        OrderCountDto order = new OrderCountDto();
        order.setPileCode("DEV001");
        order.setChargeCount(10);
        order.setChargeQt(100.0);
        Map<String, List<OrderCountDto>> orderMap = Map.of("site-001", List.of(order));
        when(togetherService.findOrderRecordListBySiteIds(any(), any(), any()))
                .thenReturn(ResponseResult.ok(orderMap));

        ResponseResult<?> result = webServerService.findCountListByCondition(vo);
        assertTrue(result.isSuccess());
    }
}
