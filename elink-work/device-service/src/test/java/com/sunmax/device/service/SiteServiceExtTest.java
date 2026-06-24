package com.sunmax.device.service;

import com.sunmax.common.dto.device.SiteBasicInfoDto;
import com.sunmax.common.dto.device.SiteEnergyInfoDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.SiteServiceImpl;
import com.sunmax.device.vo.SiteEnergyInfoVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Example;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SiteService 扩展单元测试")
class SiteServiceExtTest {

    @Mock private SiteDao siteDao;
    @Mock private SiteEnergyInfoDao siteEnergyInfoDao;
    @Mock private SystemService systemService;
    @Mock private ProvinceDao provinceDao;
    @Mock private CityDao cityDao;
    @Mock private AreaDao areaDao;
    @Mock private SiteInfoDao siteInfoDao;

    @InjectMocks private SiteServiceImpl siteService;

    @Test
    @DisplayName("查询站点基本信息-不存在返回空对象")
    void findSiteBasicInfoById_notFound_returnsEmpty() {
        when(siteDao.findById("site-001")).thenReturn(Optional.empty());

        ResponseResult<SiteBasicInfoDto> result = siteService.findSiteBasicInfoById("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点基本信息-存在返回数据")
    void findSiteBasicInfoById_found_returnsData() {
        SiteEntity site = new SiteEntity();
        site.setId("site-001");
        site.setCreateId("user-001");
        when(siteDao.findById("site-001")).thenReturn(Optional.of(site));
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(systemService.findTenantDetailsByIds(any())).thenReturn(ResponseResult.ok(Collections.emptyList()));

        ResponseResult<SiteBasicInfoDto> result = siteService.findSiteBasicInfoById("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除站点-不存在返回错误")
    void deleteSiteInfoById_notFound_returnsError() {
        when(siteDao.findById("site-001")).thenReturn(Optional.empty());

        ResponseResult<String> result = siteService.deleteSiteInfoById("site-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除站点-存在时软删除成功")
    void deleteSiteInfoById_found_deletesSuccess() {
        SiteEntity site = new SiteEntity();
        site.setId("site-001");
        when(siteDao.findById("site-001")).thenReturn(Optional.of(site));
        when(siteDao.save(any(SiteEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(systemService.deleteAllOrganEmpowerBySiteId("site-001")).thenReturn(ResponseResult.ok());

        ResponseResult<String> result = siteService.deleteSiteInfoById("site-001");
        assertTrue(result.isSuccess());
        verify(siteDao).save(any(SiteEntity.class));
    }

    @Test
    @DisplayName("保存站点能源信息-新增成功")
    void saveOrUpdateSiteEnergyInfo_new_success() {
        SiteEnergyInfoVo vo = new SiteEnergyInfoVo();
        vo.setSiteId("site-001");
        vo.setUserId("user-001");

        when(siteEnergyInfoDao.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(siteEnergyInfoDao.save(any(SiteEnergyInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = siteService.saveOrUpdeteSiteEnergyInfo(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存站点能源信息-站点已存在返回参数错误")
    void saveOrUpdateSiteEnergyInfo_siteExists_returnsError() {
        SiteEnergyInfoVo vo = new SiteEnergyInfoVo();
        vo.setSiteId("site-001");
        vo.setUserId("user-001");

        SiteEnergyInfoEntity existing = new SiteEnergyInfoEntity();
        existing.setId("energy-001");
        when(siteEnergyInfoDao.findOne(any(Example.class))).thenReturn(Optional.of(existing));

        ResponseResult<Void> result = siteService.saveOrUpdeteSiteEnergyInfo(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存站点能源信息-编辑成功")
    void saveOrUpdateSiteEnergyInfo_edit_success() {
        SiteEnergyInfoVo vo = new SiteEnergyInfoVo();
        vo.setId("energy-001");
        vo.setSiteId("site-001");
        vo.setUserId("user-001");

        SiteEnergyInfoEntity existing = new SiteEnergyInfoEntity();
        existing.setId("energy-001");
        when(siteEnergyInfoDao.findById("energy-001")).thenReturn(Optional.of(existing));
        when(siteEnergyInfoDao.save(any(SiteEnergyInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = siteService.saveOrUpdeteSiteEnergyInfo(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点能源信息-有数据返回")
    void findSiteEnergyInfoById_withData_returnsData() {
        SiteEnergyInfoEntity entity = new SiteEnergyInfoEntity();
        entity.setId("energy-001");
        entity.setSiteId("site-001");
        when(siteEnergyInfoDao.findOne(any(Example.class))).thenReturn(Optional.of(entity));

        ResponseResult<SiteEnergyInfoDto> result = siteService.findSiteEnergyInfoById("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点能源信息-无数据返回空对象")
    void findSiteEnergyInfoById_noData_returnsEmpty() {
        when(siteEnergyInfoDao.findOne(any(Example.class))).thenReturn(Optional.empty());

        ResponseResult<SiteEnergyInfoDto> result = siteService.findSiteEnergyInfoById("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询省份数据-返回列表")
    void queryProvinceData_returnsList() {
        when(provinceDao.findAll()).thenReturn(Collections.emptyList());

        ResponseResult<?> result = siteService.queryProvinceData();
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询城市数据-返回列表")
    void queryCityDataByProvinceId_returnsList() {
        when(cityDao.findAllByProvinceIdIn(any())).thenReturn(Collections.emptyList());

        ResponseResult<?> result = siteService.queryCityDataByProvinceId("110000");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询区县数据-返回列表")
    void queryAreaDataByCityId_returnsList() {
        when(areaDao.findAllByCityIdIn(any())).thenReturn(Collections.emptyList());

        ResponseResult<?> result = siteService.queryAreaDataByCityId("110100");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据多个站点ID查询能源信息-返回Map")
    void findSiteEnergyInfoBySiteIds_returnsMap() {
        when(siteEnergyInfoDao.findAllBySiteIdIn(List.of("site-001"))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = siteService.findSiteEnergyInfoBySiteIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("根据多个站点ID查询能源信息-有数据返回Map")
    void findSiteEnergyInfoBySiteIds_withData_returnsMap() {
        SiteEnergyInfoEntity entity = new SiteEnergyInfoEntity();
        entity.setId("energy-001");
        entity.setSiteId("site-001");
        when(siteEnergyInfoDao.findAllBySiteIdIn(List.of("site-001"))).thenReturn(List.of(entity));

        ResponseResult<Map<String, SiteEnergyInfoDto>> result = siteService.findSiteEnergyInfoBySiteIds(List.of("site-001"));
        assertTrue(result.isSuccess());
    }
}
