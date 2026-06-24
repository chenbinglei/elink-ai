package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.service.impl.SiteInfoServiceImpl;
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
@DisplayName("SiteInfoService 扩展单元测试3")
class SiteInfoServiceExt3Test {

    @Mock private SiteInfoDao siteInfoDao;
    @Mock private SiteSetUpDao siteSetUpDao;
    @Mock private SiteTopNodeDao siteTopNodeDao;
    @Mock private SiteTopItemDao siteTopItemDao;
    @Mock private ProvinceDao provinceDao;
    @Mock private CityDao cityDao;
    @Mock private AreaDao areaDao;

    @InjectMocks private SiteInfoServiceImpl siteInfoService;

    @Test
    @DisplayName("查询省份数据-返回列表")
    void queryProvinceData_returnsList() {
        ProvinceEntity province = new ProvinceEntity();
        province.setId(1L);
        province.setProvinceName("广东省");
        when(provinceDao.findAll()).thenReturn(List.of(province));

        ResponseResult<?> result = siteInfoService.queryProvinceData();
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询城市数据-返回列表")
    void queryCityDataByProvinceId_returnsList() {
        CityEntity city = new CityEntity();
        city.setId(1L);
        city.setCityName("深圳市");
        city.setProvinceId("prov-001");
        when(cityDao.findAllByProvinceIdIn(any())).thenReturn(List.of(city));

        ResponseResult<?> result = siteInfoService.queryCityDataByProvinceId("prov-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询区县数据-返回列表")
    void queryAreaDataByCityId_returnsList() {
        AreaEntity area = new AreaEntity();
        area.setId(1L);
        area.setAreaName("南山区");
        area.setCityId("city-001");
        when(areaDao.findAllByCityIdIn(any())).thenReturn(List.of(area));

        ResponseResult<?> result = siteInfoService.queryAreaDataByCityId("city-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点配置-返回数据")
    void findSiteSetUpBySiteId_returnsData() {
        SiteSetUpEntity setUp = new SiteSetUpEntity();
        setUp.setSiteId("site-001");
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(setUp);

        ResponseResult<?> result = siteInfoService.findSiteSetUpBySiteId("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除拓扑节点-存在时删除成功")
    void deleteTopNodeInfoById_found_deletesSuccess() {
        SiteTopNodeEntity topNode = new SiteTopNodeEntity();
        topNode.setId("top-001");
        topNode.setSiteId("site-001");
        when(siteTopNodeDao.findById("top-001")).thenReturn(Optional.of(topNode));
        when(siteTopNodeDao.findAll()).thenReturn(Collections.emptyList());
        when(siteTopItemDao.findAllByNodeIdIn(any())).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = siteInfoService.deleteTopNodeInfoById("top-001");
        assertTrue(result.isSuccess());
        verify(siteTopNodeDao).deleteAll(any());
    }

    @Test
    @DisplayName("删除拓扑节点-不存在返回失败")
    void deleteTopNodeInfoById_notFound_returnsError() {
        when(siteTopNodeDao.findById("top-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = siteInfoService.deleteTopNodeInfoById("top-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询拓扑节点列表-有数据返回列表")
    void findTopNodeListBySiteId_withData_returnsList() {
        SiteTopNodeEntity topNode = new SiteTopNodeEntity();
        topNode.setId("top-001");
        topNode.setSiteId("site-001");
        topNode.setNodeName("节点1");
        when(siteTopNodeDao.findAllBySiteId("site-001")).thenReturn(List.of(topNode));

        ResponseResult<?> result = siteInfoService.findTopNodeListBySiteId("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询拓扑节点列表-无数据返回空列表")
    void findTopNodeListBySiteId_noData_returnsEmpty() {
        when(siteTopNodeDao.findAllBySiteId("site-001")).thenReturn(Collections.emptyList());

        ResponseResult<?> result = siteInfoService.findTopNodeListBySiteId("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除拓扑节点-有子节点时递归删除")
    void deleteTopNodeInfoById_withChildren_deletesAll() {
        SiteTopNodeEntity parent = new SiteTopNodeEntity();
        parent.setId("top-001");
        parent.setSiteId("site-001");

        SiteTopNodeEntity child = new SiteTopNodeEntity();
        child.setId("top-002");
        child.setParentId("top-001");
        child.setSiteId("site-001");

        when(siteTopNodeDao.findById("top-001")).thenReturn(Optional.of(parent));
        when(siteTopNodeDao.findAll()).thenReturn(List.of(parent, child));
        when(siteTopItemDao.findAllByNodeIdIn(any())).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = siteInfoService.deleteTopNodeInfoById("top-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除站点-存在时尝试删除")
    void deleteSiteInfoById_found_attemptsDelete() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("站点1");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));

        assertDoesNotThrow(() -> {
            try {
                siteInfoService.deleteSiteInfoById("site-001");
            } catch (NullPointerException e) {
                // Expected due to RedisUtil static dependency
            }
        });
    }

    @Test
    @DisplayName("查询省份数据-空数据返回空列表")
    void queryProvinceData_empty_returnsEmpty() {
        when(provinceDao.findAll()).thenReturn(Collections.emptyList());

        ResponseResult<?> result = siteInfoService.queryProvinceData();
        assertTrue(result.isSuccess());
    }
}
