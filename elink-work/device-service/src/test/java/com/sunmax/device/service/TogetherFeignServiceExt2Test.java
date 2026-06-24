package com.sunmax.device.service;

import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.together.PileGunChangeVo;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.ModelEventDao;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.service.feign.ProtocolService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.TogetherFeignServiceImpl;
import com.sunmax.device.util.TopNodeDataUtil;
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
@DisplayName("TogetherFeignService 扩展单元测试2")
class TogetherFeignServiceExt2Test {

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
    @Mock private TopNodeDataUtil topNodeDataUtil;

    @InjectMocks private TogetherFeignServiceImpl togetherFeignService;

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
    @DisplayName("更新充电桩扩展属性-设备不存在返回参数错误")
    void updatePileRea_notFound_returnsParamError() {
        when(deviceDao.findById("dev-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = togetherFeignService.updatePileRea("dev-001", "user-001", "{}");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新充电桩枪数据-枪存在时更新成功")
    void updatePileGun_found_updatesSuccess() {
        DeviceGunEntity gun = new DeviceGunEntity();
        gun.setId("gun-001");
        gun.setDeviceId("dev-001");
        when(deviceGunDao.findById("gun-001")).thenReturn(Optional.of(gun));
        when(deviceGunDao.save(any(DeviceGunEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        PileGunChangeVo vo = new PileGunChangeVo();
        vo.setId("gun-001");
        vo.setGunCode("1");

        ResponseResult<Void> result = togetherFeignService.updatePileGun(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新充电桩枪数据-枪不存在返回参数错误")
    void updatePileGun_notFound_returnsParamError() {
        when(deviceGunDao.findById("gun-001")).thenReturn(Optional.empty());

        PileGunChangeVo vo = new PileGunChangeVo();
        vo.setId("gun-001");

        ResponseResult<Void> result = togetherFeignService.updatePileGun(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新站点状态-站点存在时更新成功")
    void updateSiteStateById_found_updatesSuccess() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(siteInfoDao.save(any(SiteInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = togetherFeignService.updateSiteStateById("site-001", 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新站点状态-站点不存在返回参数错误")
    void updateSiteStateById_notFound_returnsParamError() {
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.empty());

        ResponseResult<String> result = togetherFeignService.updateSiteStateById("site-001", 1);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点关联方-无关联方返回空Map")
    void findSiteAffiliatesInfoByIds_noAffiliates_returnsEmpty() {
        when(affiliatesInfoDao.findAllBySiteIdIn(List.of("site-001"))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = togetherFeignService.findSiteAffiliatesInfoByIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点关联方-有关联方返回数据")
    void findSiteAffiliatesInfoByIds_withAffiliates_returnsData() {
        AffiliatesInfoEntity affiliate = new AffiliatesInfoEntity();
        affiliate.setId("aff-001");
        affiliate.setSiteId("site-001");
        affiliate.setTenantId("tenant-001");
        when(affiliatesInfoDao.findAllBySiteIdIn(List.of("site-001"))).thenReturn(List.of(affiliate));

        TenantDetailsDto tenant = new TenantDetailsDto();
        tenant.setId("tenant-001");
        tenant.setTenantName("运营商1");
        when(systemService.findTenantDetailsByIds(List.of("tenant-001")))
                .thenReturn(ResponseResult.ok(List.of(tenant)));

        ResponseResult<?> result = togetherFeignService.findSiteAffiliatesInfoByIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("校验站点密码-密码正确返回1")
    void checkSitePassword_correct_returnsOne() {
        SiteSetUpEntity setup = new SiteSetUpEntity();
        setup.setId("setup-001");
        setup.setSiteId("site-001");
        setup.setOperatePassword("123456");
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(setup);

        ResponseResult<Integer> result = togetherFeignService.checkSitePassword("site-001", "123456");
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData());
    }

    @Test
    @DisplayName("校验站点密码-密码错误返回0")
    void checkSitePassword_wrong_returnsZero() {
        SiteSetUpEntity setup = new SiteSetUpEntity();
        setup.setOperatePassword("123456");
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(setup);

        ResponseResult<Integer> result = togetherFeignService.checkSitePassword("site-001", "wrong");
        assertTrue(result.isSuccess());
        assertEquals(0, result.getData());
    }

    @Test
    @DisplayName("校验站点密码-无设置返回0")
    void checkSitePassword_noSetup_returnsZero() {
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(null);

        ResponseResult<Integer> result = togetherFeignService.checkSitePassword("site-001", "123456");
        assertTrue(result.isSuccess());
        assertEquals(0, result.getData());
    }

    @Test
    @DisplayName("更新设备运维状态-设备存在时更新成功")
    void updateDeviceOperateStatus_found_updatesSuccess() {
        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        when(deviceDao.findById("dev-001")).thenReturn(Optional.of(device));
        when(deviceDao.save(any(DeviceEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = togetherFeignService.updateDeviceOperateStatus("dev-001", 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新设备运维状态-设备不存在返回失败")
    void updateDeviceOperateStatus_notFound_returnsFail() {
        when(deviceDao.findById("dev-001")).thenReturn(Optional.empty());

        ResponseResult<String> result = togetherFeignService.updateDeviceOperateStatus("dev-001", 1);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点测量ID-返回空Map")
    void findSiteMeasureIdBySiteIds_returnsEmpty() {
        ResponseResult<?> result = togetherFeignService.findSiteMeasureIdBySiteIds(List.of("site-001"), 1, 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新事件忽略状态-事件存在时直接更新")
    void updateEventIgnoreStatus_eventFound_updatesDirectly() {
        DeviceEventEntity event = new DeviceEventEntity();
        event.setId("event-001");
        when(deviceEventDao.findById("event-001")).thenReturn(Optional.of(event));
        when(deviceEventDao.save(any(DeviceEventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = togetherFeignService.updateEventIgnoreStatus("event-001", 1);
        assertTrue(result.isSuccess());
        verify(deviceEventDao).save(any(DeviceEventEntity.class));
    }

    @Test
    @DisplayName("更新事件忽略状态-事件不存在时委托protocolService")
    void updateEventIgnoreStatus_eventNotFound_delegatesToProtocol() {
        when(deviceEventDao.findById("event-001")).thenReturn(Optional.empty());
        when(protocolService.updateEventIgnoreStatus("event-001", 1)).thenReturn(ResponseResult.ok());

        ResponseResult<Void> result = togetherFeignService.updateEventIgnoreStatus("event-001", 1);
        assertTrue(result.isSuccess());
        verify(protocolService).updateEventIgnoreStatus("event-001", 1);
    }
}
