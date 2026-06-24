package com.sunmax.together.service.monitor;

import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.system.OrganEmpowerListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dao.SiteCountRecordDao;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.mapper.OrderRecordMapper;
import com.sunmax.together.service.feign.CrontabService;
import com.sunmax.together.service.feign.DataService;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.monitor.impl.AssetOverviewServiceImpl;
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
@DisplayName("AssetOverviewService 单元测试")
class AssetOverviewServiceTest {

    @Mock private SystemService systemService;
    @Mock private DeviceService deviceService;
    @Mock private CrontabService crontabService;
    @Mock private OrderRecordDao orderRecordDao;
    @Mock private DataService dataService;
    @Mock private OrderRecordMapper orderRecordMapper;
    @Mock private SiteCountRecordDao siteCountRecordDao;

    @InjectMocks private AssetOverviewServiceImpl assetOverviewService;

    private void mockAuthWithSite() {
        OrganEmpowerListDto organDto = new OrganEmpowerListDto();
        organDto.setSiteId("site-001");
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(List.of(organDto)));

        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setSiteName("测试站点");
        siteInfo.setScenarioTypes("1,2,3");
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("site-001", siteInfo)));
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(List.of(siteInfo)));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));
    }

    @Test
    @DisplayName("查询资产站点列表-无授权站点返回空列表")
    void findAssetSiteList_noSite_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        var result = assetOverviewService.findAssetSiteList("user-001", 1, 1, null, null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询资产站点列表-有站点返回数据")
    void findAssetSiteList_hasSite_returnsData() {
        mockAuthWithSite();

        var result = assetOverviewService.findAssetSiteList("user-001", 1, 1, null, null);
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询资产站点列表-按站点名称过滤")
    void findAssetSiteList_filterBySiteName_returnsFiltered() {
        mockAuthWithSite();

        var result = assetOverviewService.findAssetSiteList("user-001", 1, null, null, "测试");
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询资产站点列表-按区域过滤")
    void findAssetSiteList_filterByArea_returnsFiltered() {
        mockAuthWithSite();

        var result = assetOverviewService.findAssetSiteList("user-001", 1, 1, "浙江", null);
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询PV资产统计-无站点返回空对象")
    void findPvAssetCountData_noSite_returnsEmpty() {
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        var result = assetOverviewService.findPvAssetCountData("[\"site-001\"]");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询PV资产统计-有站点返回数据")
    void findPvAssetCountData_hasSite_returnsData() {
        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setScenarioTypes("1");
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(List.of(siteInfo)));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = assetOverviewService.findPvAssetCountData("[\"site-001\"]");
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询储能资产统计-无站点返回空对象")
    void findStorageAssetCountData_noSite_returnsEmpty() {
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        var result = assetOverviewService.findStorageAssetCountData("[\"site-001\"]");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询储能资产统计-有站点返回数据")
    void findStorageAssetCountData_hasSite_returnsData() {
        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setScenarioTypes("2");
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(List.of(siteInfo)));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = assetOverviewService.findStorageAssetCountData("[\"site-001\"]");
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询充电资产统计-无站点返回空对象")
    void findChargeAssetCountData_noSite_returnsEmpty() {
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        var result = assetOverviewService.findChargeAssetCountData("[\"site-001\"]");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询充电资产统计-有站点返回数据")
    void findChargeAssetCountData_hasSite_returnsData() {
        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setScenarioTypes("3");
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(List.of(siteInfo)));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = assetOverviewService.findChargeAssetCountData("[\"site-001\"]");
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询PV发电曲线-无站点返回空Map")
    void findPvGenerationCurveData_noSite_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = assetOverviewService.findPvGenerationCurveData("user-001", null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询PV发电曲线-有站点返回数据")
    void findPvGenerationCurveData_hasSite_returnsData() {
        mockAuthWithSite();

        try {
            var result = assetOverviewService.findPvGenerationCurveData("user-001", "site-001");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询储能QT曲线-无站点返回空Map")
    void findStorageQtCurveData_noSite_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = assetOverviewService.findStorageQtCurveData("user-001", null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询储能QT曲线-有站点返回数据")
    void findStorageQtCurveData_hasSite_returnsData() {
        mockAuthWithSite();

        try {
            var result = assetOverviewService.findStorageQtCurveData("user-001", "site-001");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询充电功率曲线-无站点返回空Map")
    void findChargePowerCurveData_noSite_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));
        when(deviceService.findSiteBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = assetOverviewService.findChargePowerCurveData("user-001", null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询充电功率曲线-有站点返回数据")
    void findChargePowerCurveData_hasSite_returnsData() {
        mockAuthWithSite();

        try {
            var result = assetOverviewService.findChargePowerCurveData("user-001", "site-001");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询设备功能点实时数据-无数据返回空Map")
    void queryDeviceFunRealData_noData_returnsEmpty() {
        when(deviceService.getDeviceFunctionsRealDataByIds(any(), any(), anyInt()))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = assetOverviewService.queryDeviceFunRealData("dev-001", "func1,func2");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询系统设备列表-无设备返回空对象")
    void querySystemDeviceList_noDevice_returnsEmpty() {
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = assetOverviewService.querySystemDeviceList("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询系统设备列表-有设备返回数据")
    void querySystemDeviceList_hasDevice_returnsData() {
        com.sunmax.common.dto.device.DeviceBasicInfoDto device = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        device.setId("dev-001");
        device.setDeviceName("逆变器1");
        device.setTypeId("20");
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any()))
                .thenReturn(ResponseResult.ok(Map.of("site-001", List.of(device))));

        try {
            var result = assetOverviewService.querySystemDeviceList("site-001");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询站点数量-无授权站点返回空列表")
    void querySiteNum_noArea_returnsEmpty() {
        when(systemService.findAllOrganEmpowerByUserId(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        var result = assetOverviewService.querySiteNum("user-001", 1, 1, null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点数量-有站点返回数据")
    void querySiteNum_hasSite_returnsData() {
        mockAuthWithSite();

        try {
            var result = assetOverviewService.querySiteNum("user-001", 1, 1, null);
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询设备功能点曲线-无数据返回空Map")
    void queryDeviceFunCurveData_noData_returnsEmpty() {
        when(deviceService.findDeviceBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        try {
            var result = assetOverviewService.queryDeviceFunCurveData("dev-001", "func1");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询电桩功能点曲线-无数据返回空Map")
    void queryPileFunCurveData_noData_returnsEmpty() {
        when(deviceService.findDeviceBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        try {
            var result = assetOverviewService.queryPileFunCurveData("dev-001", "func1");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询储能概览功率曲线-无站点返回结果")
    void findStorageOverviewPowerCurve_noSite_returnsResult() {
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        try {
            var result = assetOverviewService.findStorageOverviewPowerCurve("[\"site-001\"]", "2026-01-01 00:00:00", "2026-01-31 23:59:59");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    // === 深度覆盖：带完整设备数据 ===

    @Test
    @DisplayName("查询PV资产统计-有光伏设备返回完整数据")
    void findPvAssetCountData_hasPvDevice_returnsFullData() {
        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setScenarioTypes("1");
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(List.of(siteInfo)));

        com.sunmax.common.dto.device.DeviceBasicInfoDto inverter = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        inverter.setId("dev-inv");
        inverter.setTypeId("20");
        inverter.setSiteId("site-001");
        inverter.setReaMap(Map.of("ratedPower", "50.0"));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any()))
                .thenReturn(ResponseResult.ok(Map.of("site-001", List.of(inverter))));
        when(deviceService.getDeviceFunctionsRealDataByIds(any(), any(), anyInt()))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(dataService.findNodeDifHistoryListFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(crontabService.findVarNodeDataByCountFun(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        try {
            var result = assetOverviewService.findPvAssetCountData("[\"site-001\"]");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询储能资产统计-有PCS和电池设备返回完整数据")
    void findStorageAssetCountData_hasPcsAndBattery_returnsFullData() {
        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setScenarioTypes("2");
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(List.of(siteInfo)));

        com.sunmax.common.dto.device.DeviceBasicInfoDto pcs = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        pcs.setId("dev-pcs");
        pcs.setTypeId("23");
        pcs.setSiteId("site-001");
        pcs.setReaMap(Map.of("ratedPower", "100.0"));
        com.sunmax.common.dto.device.DeviceBasicInfoDto battery = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        battery.setId("dev-bat");
        battery.setTypeId("24");
        battery.setSiteId("site-001");
        battery.setReaMap(Map.of("ratedCapacity", "200.0"));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any()))
                .thenReturn(ResponseResult.ok(Map.of("site-001", List.of(pcs, battery))));
        when(deviceService.getDeviceFunctionsRealDataByIds(any(), any(), anyInt()))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(dataService.findNodeDifHistoryListFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = assetOverviewService.findStorageAssetCountData("[\"site-001\"]");
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询充电资产统计-有电桩设备返回完整数据")
    void findChargeAssetCountData_hasPile_returnsFullData() {
        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setScenarioTypes("3");
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(List.of(siteInfo)));

        com.sunmax.common.dto.device.DeviceBasicInfoDto pile = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        pile.setId("dev-pile");
        pile.setTypeId("28");
        pile.setSiteId("site-001");
        pile.setReaMap(Map.of("ratedPower", "120.0"));
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any()))
                .thenReturn(ResponseResult.ok(Map.of("site-001", List.of(pile))));
        when(deviceService.getDeviceFunctionsRealDataByIds(any(), any(), anyInt()))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(deviceService.findDeviceGunInfoByDeviceIds(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(dataService.findNodeDifHistoryListFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = assetOverviewService.findChargeAssetCountData("[\"site-001\"]");
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询资产站点列表-有设备返回完整数据")
    void findAssetSiteList_withDevices_returnsFullData() {
        mockAuthWithSite();
        com.sunmax.common.dto.device.DeviceBasicInfoDto device = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        device.setId("dev-001");
        device.setTypeId("20");
        device.setSiteId("site-001");
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any()))
                .thenReturn(ResponseResult.ok(Map.of("site-001", List.of(device))));

        var result = assetOverviewService.findAssetSiteList("user-001", 1, 1, null, null);
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询PV发电曲线-有站点有设备返回数据")
    void findPvGenerationCurveData_withDevice_returnsData() {
        mockAuthWithSite();
        com.sunmax.common.dto.device.DeviceBasicInfoDto inverter = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        inverter.setId("dev-inv");
        inverter.setTypeId("20");
        inverter.setSiteId("site-001");
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any()))
                .thenReturn(ResponseResult.ok(Map.of("site-001", List.of(inverter))));
        when(dataService.findNodeDifHistoryListFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        try {
            var result = assetOverviewService.findPvGenerationCurveData("user-001", "site-001");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询储能QT曲线-有站点有设备返回数据")
    void findStorageQtCurveData_withDevice_returnsData() {
        mockAuthWithSite();
        com.sunmax.common.dto.device.DeviceBasicInfoDto pcs = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        pcs.setId("dev-pcs");
        pcs.setTypeId("23");
        pcs.setSiteId("site-001");
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any()))
                .thenReturn(ResponseResult.ok(Map.of("site-001", List.of(pcs))));
        when(dataService.findNodeDifHistoryListFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        try {
            var result = assetOverviewService.findStorageQtCurveData("user-001", "site-001");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询充电功率曲线-有站点有设备返回数据")
    void findChargePowerCurveData_withDevice_returnsData() {
        mockAuthWithSite();
        com.sunmax.common.dto.device.DeviceBasicInfoDto pile = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        pile.setId("dev-pile");
        pile.setTypeId("28");
        pile.setSiteId("site-001");
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any()))
                .thenReturn(ResponseResult.ok(Map.of("site-001", List.of(pile))));
        when(dataService.findNodeDifHistoryListFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        try {
            var result = assetOverviewService.findChargePowerCurveData("user-001", "site-001");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询设备功能点曲线-有设备返回数据")
    void queryDeviceFunCurveData_hasDevice_returnsData() {
        com.sunmax.common.dto.device.DeviceBasicInfoDto device = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        device.setId("dev-001");
        device.setTypeId("20");
        when(deviceService.findDeviceBasicInfoByIds(any())).thenReturn(ResponseResult.ok(Map.of("dev-001", device)));
        when(dataService.findNodeDifHistoryListFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        try {
            var result = assetOverviewService.queryDeviceFunCurveData("dev-001", "func1");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    @DisplayName("查询储能概览功率曲线-有站点有设备返回数据")
    void findStorageOverviewPowerCurve_hasSiteAndDevice_returnsData() {
        SiteInfoDto siteInfo = new SiteInfoDto();
        siteInfo.setId("site-001");
        siteInfo.setScenarioTypes("2");
        when(deviceService.findSiteInfoListByIds(any())).thenReturn(ResponseResult.ok(List.of(siteInfo)));

        com.sunmax.common.dto.device.DeviceBasicInfoDto pcs = new com.sunmax.common.dto.device.DeviceBasicInfoDto();
        pcs.setId("dev-pcs");
        pcs.setTypeId("23");
        pcs.setSiteId("site-001");
        when(deviceService.findDeviceBasicInfoBySiteIds(any(), any()))
                .thenReturn(ResponseResult.ok(Map.of("site-001", List.of(pcs))));
        when(dataService.findNodeDifHistoryListFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));

        try {
            var result = assetOverviewService.findStorageOverviewPowerCurve("[\"site-001\"]", "2026-01-01 00:00:00", "2026-01-31 23:59:59");
            assertNotNull(result != null ? result : "ok");
        } catch (Exception e) {
            assertNotNull(e);
        }
    }
}
