package com.sunmax.device.service;

import com.sunmax.common.dto.device.AffiliatesInfoDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.PileGunChangeVo;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.ModelEventDao;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.service.feign.ProtocolService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.TogetherFeignServiceImpl;
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
@DisplayName("TogetherFeignService 单元测试")
class TogetherFeignServiceTest {

    @Mock private DeviceDao deviceDao;
    @Mock private DeviceGunDao deviceGunDao;
    @Mock private SiteInfoDao siteInfoDao;
    @Mock private AffiliatesInfoDao affiliatesInfoDao;
    @Mock private SystemService systemService;
    @Mock private SiteSetUpDao siteSetUpDao;
    @Mock private SiteTopNodeDao siteTopNodeDao;
    @Mock private SiteTopItemDao siteTopItemDao;
    @Mock private ScenarioTypeDao scenarioTypeDao;
    @Mock private DeviceEventDao deviceEventDao;
    @Mock private ModelEventDao modelEventDao;
    @Mock private ProtocolService protocolService;

    @InjectMocks private TogetherFeignServiceImpl togetherFeignService;

    @Test
    @DisplayName("更新充电桩扩展属性-设备不存在返回参数错误")
    void updatePileRea_notFound_returnsParamError() {
        when(deviceDao.findById("dev-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = togetherFeignService.updatePileRea("dev-001", "user-001", "{}");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新充电桩扩展属性-设备存在时更新成功")
    void updatePileRea_found_updatesSuccess() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));
        when(deviceDao.save(any(DeviceEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = togetherFeignService.updatePileRea("dev-001", "user-001", "{\"key\":\"value\"}");
        assertTrue(result.isSuccess());
        verify(deviceDao).save(any(DeviceEntity.class));
    }

    @Test
    @DisplayName("更新充电桩枪-枪不存在返回参数错误")
    void updatePileGun_notFound_returnsParamError() {
        PileGunChangeVo vo = new PileGunChangeVo();
        vo.setId("gun-001");
        when(deviceGunDao.findById("gun-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = togetherFeignService.updatePileGun(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新充电桩枪-枪存在时更新成功")
    void updatePileGun_found_updatesSuccess() {
        PileGunChangeVo vo = new PileGunChangeVo();
        vo.setId("gun-001");
        vo.setGunCode("1");

        DeviceGunEntity existing = new DeviceGunEntity();
        existing.setId("gun-001");
        existing.setDeviceId("dev-001");
        when(deviceGunDao.findById("gun-001")).thenReturn(Optional.of(existing));
        when(deviceGunDao.save(any(DeviceGunEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = togetherFeignService.updatePileGun(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新站点状态-站点不存在返回错误")
    void updateSiteStateById_notFound_returnsError() {
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = togetherFeignService.updateSiteStateById("site-001", 1);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新站点状态-站点存在时更新成功")
    void updateSiteStateById_found_updatesSuccess() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(siteInfoDao.save(any(SiteInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<?> result = togetherFeignService.updateSiteStateById("site-001", 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("校验站点密码-密码正确返回1")
    void checkSitePassword_correctPassword_returns1() {
        SiteSetUpEntity setUp = new SiteSetUpEntity();
        setUp.setId("setup-001");
        setUp.setSiteId("site-001");
        setUp.setOperatePassword("123456");
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(setUp);

        ResponseResult<Integer> result = togetherFeignService.checkSitePassword("site-001", "123456");
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData());
    }

    @Test
    @DisplayName("校验站点密码-密码错误返回0")
    void checkSitePassword_wrongPassword_returns0() {
        SiteSetUpEntity setUp = new SiteSetUpEntity();
        setUp.setId("setup-001");
        setUp.setSiteId("site-001");
        setUp.setOperatePassword("123456");
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(setUp);

        ResponseResult<Integer> result = togetherFeignService.checkSitePassword("site-001", "wrong");
        assertTrue(result.isSuccess());
        assertEquals(0, result.getData());
    }

    @Test
    @DisplayName("校验站点密码-无设置返回0")
    void checkSitePassword_noSetUp_returns0() {
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(null);

        ResponseResult<Integer> result = togetherFeignService.checkSitePassword("site-001", "123456");
        assertTrue(result.isSuccess());
        assertEquals(0, result.getData());
    }

    @Test
    @DisplayName("更新设备运营状态-设备不存在返回错误")
    void updateDeviceOperateStatus_notFound_returnsError() {
        when(deviceDao.findById("dev-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = togetherFeignService.updateDeviceOperateStatus("dev-001", 1);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新设备运营状态-设备存在时更新成功")
    void updateDeviceOperateStatus_found_updatesSuccess() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));
        when(deviceDao.save(any(DeviceEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<?> result = togetherFeignService.updateDeviceOperateStatus("dev-001", 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点关联方信息-无数据返回空Map")
    void findSiteAffiliatesInfoByIds_empty_returnsEmpty() {
        when(affiliatesInfoDao.findAllBySiteIdIn(List.of("site-001"))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = togetherFeignService.findSiteAffiliatesInfoByIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点关联方信息-有数据返回Map")
    void findSiteAffiliatesInfoByIds_withData_returnsMap() {
        AffiliatesInfoEntity entity = new AffiliatesInfoEntity();
        entity.setId("aff-001");
        entity.setSiteId("site-001");
        entity.setTenantId("tenant-001");
        when(affiliatesInfoDao.findAllBySiteIdIn(List.of("site-001"))).thenReturn(List.of(entity));

        TenantDetailsDto tenant = new TenantDetailsDto();
        tenant.setId("tenant-001");
        tenant.setTenantName("测试租户");
        when(systemService.findTenantDetailsByIds(any())).thenReturn(ResponseResult.ok(List.of(tenant)));

        ResponseResult<Map<String, List<AffiliatesInfoDto>>> result = togetherFeignService.findSiteAffiliatesInfoByIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据子设备ID查询站点ID-站点存在直接返回")
    void findSiteIdBySubId_siteFound_returnsSiteId() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("sub-001");
        when(siteInfoDao.findById("sub-001")).thenReturn(Optional.of(site));

        ResponseResult<?> result = togetherFeignService.findSiteIdBySubId("sub-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据子设备ID查询站点ID-场景类型存在返回站点ID")
    void findSiteIdBySubId_scenarioFound_returnsSiteId() {
        when(siteInfoDao.findById("sub-001")).thenReturn(Optional.empty());

        ScenarioTypeEntity scenario = new ScenarioTypeEntity();
        scenario.setId("sub-001");
        scenario.setSiteId("site-001");
        when(scenarioTypeDao.findById("sub-001")).thenReturn(Optional.of(scenario));

        ResponseResult<?> result = togetherFeignService.findSiteIdBySubId("sub-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据子设备ID查询站点ID-都不存在返回null")
    void findSiteIdBySubId_notFound_returnsNull() {
        when(siteInfoDao.findById("sub-001")).thenReturn(Optional.empty());
        when(scenarioTypeDao.findById("sub-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = togetherFeignService.findSiteIdBySubId("sub-001");
        assertNull(result);
    }

    @Test
    @DisplayName("更新事件忽略状态-事件不存在时调用protocolService")
    void updateEventIgnoreStatus_notFound_delegatesToProtocol() {
        when(deviceEventDao.findById("evt-001")).thenReturn(Optional.empty());
        when(protocolService.updateEventIgnoreStatus("evt-001", 1)).thenReturn(ResponseResult.ok());

        ResponseResult<Void> result = togetherFeignService.updateEventIgnoreStatus("evt-001", 1);
        assertTrue(result.isSuccess());
        verify(protocolService).updateEventIgnoreStatus("evt-001", 1);
    }

    @Test
    @DisplayName("更新事件忽略状态-事件存在时更新成功")
    void updateEventIgnoreStatus_found_updatesSuccess() {
        DeviceEventEntity event = new DeviceEventEntity();
        event.setId("evt-001");
        when(deviceEventDao.findById("evt-001")).thenReturn(Optional.of(event));
        when(deviceEventDao.save(any(DeviceEventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = togetherFeignService.updateEventIgnoreStatus("evt-001", 1);
        assertTrue(result.isSuccess());
    }
}
