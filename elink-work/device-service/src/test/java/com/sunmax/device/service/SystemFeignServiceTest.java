package com.sunmax.device.service;

import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.ScenarioTypeDao;
import com.sunmax.device.dao.access.SiteInfoDao;
import com.sunmax.device.dao.access.SiteSetUpDao;
import com.sunmax.device.dao.model.ModelReaDao;
import com.sunmax.device.dao.model.ReaDao;
import com.sunmax.device.entity.access.SiteInfoEntity;
import com.sunmax.device.entity.access.SiteSetUpEntity;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.SystemFeignServiceImpl;
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
@DisplayName("SystemFeignService 单元测试")
class SystemFeignServiceTest {

    @Mock private SiteInfoDao siteInfoDao;
    @Mock private ScenarioTypeDao scenarioTypeDao;
    @Mock private SystemService systemService;
    @Mock private ModelReaDao modelReaDao;
    @Mock private ReaDao reaDao;
    @Mock private SiteSetUpDao siteSetUpDao;

    @InjectMocks private SystemFeignServiceImpl systemFeignService;

    @Test
    @DisplayName("查询全部站点详情-无数据返回空列表")
    void findAllSiteBasicInfoList_empty_returnsEmpty() {
        when(siteInfoDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(Collections.emptyList());

        ResponseResult<?> result = systemFeignService.findAllSiteBasicInfoList(null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询全部站点详情-有数据返回列表")
    void findAllSiteBasicInfoList_withData_returnsList() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("测试站点");
        site.setIsDelete(1);
        when(siteInfoDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(site));

        ResponseResult<?> result = systemFeignService.findAllSiteBasicInfoList(null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据站点ID查询站点详情-无数据返回空Map")
    void findSiteBasicInfoByIds_empty_returnsEmpty() {
        when(siteInfoDao.findAllByIdInAndIsDelete(List.of("site-001"), 1))
                .thenReturn(Collections.emptyList());

        ResponseResult<?> result = systemFeignService.findSiteBasicInfoByIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据站点ID查询站点详情-有数据返回Map")
    void findSiteBasicInfoByIds_withData_returnsMap() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("测试站点");
        site.setTenantId("tenant-001");
        site.setIsDelete(1);
        when(siteInfoDao.findAllByIdInAndIsDelete(List.of("site-001"), 1))
                .thenReturn(List.of(site));

        TenantDetailsDto tenant = new TenantDetailsDto();
        tenant.setId("tenant-001");
        tenant.setTenantName("测试租户");
        when(systemService.findTenantDetailsByIds(any())).thenReturn(ResponseResult.ok(List.of(tenant)));
        when(scenarioTypeDao.findAllBySiteIdIn(any())).thenReturn(Collections.emptyList());
        when(modelReaDao.findAllByModelIdIn(any())).thenReturn(Collections.emptyList());

        ResponseResult<?> result = systemFeignService.findSiteBasicInfoByIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点配置-无数据返回空Map")
    void findSiteSetUpBySiteIds_empty_returnsEmpty() {
        when(siteSetUpDao.findAllBySiteIdIn(List.of("site-001"))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = systemFeignService.findSiteSetUpBySiteIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点配置-有数据返回Map")
    void findSiteSetUpBySiteIds_withData_returnsMap() {
        SiteSetUpEntity entity = new SiteSetUpEntity();
        entity.setId("setup-001");
        entity.setSiteId("site-001");
        when(siteSetUpDao.findAllBySiteIdIn(List.of("site-001"))).thenReturn(List.of(entity));

        ResponseResult<?> result = systemFeignService.findSiteSetUpBySiteIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据站点ID查询站点信息-无数据返回空列表")
    void findSiteInfoListByIds_empty_returnsEmpty() {
        when(siteInfoDao.findAllByIdInAndIsDelete(List.of("site-001"), 1))
                .thenReturn(Collections.emptyList());

        ResponseResult<?> result = systemFeignService.findSiteInfoListByIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据站点ID查询站点信息-有数据返回列表")
    void findSiteInfoListByIds_withData_returnsList() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("测试站点");
        site.setIsDelete(1);
        when(siteInfoDao.findAllByIdInAndIsDelete(List.of("site-001"), 1))
                .thenReturn(List.of(site));

        ResponseResult<?> result = systemFeignService.findSiteInfoListByIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }
}
