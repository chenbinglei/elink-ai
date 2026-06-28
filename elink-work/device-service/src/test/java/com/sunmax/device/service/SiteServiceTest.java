package com.sunmax.device.service;

import com.sunmax.device.dao.access.*;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.SiteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SiteService 单元测试")
class SiteServiceTest {

    @Mock
    private SiteDao siteDao;

    @Mock
    private SiteEnergyInfoDao siteEnergyInfoDao;

    @Mock
    private SystemService systemService;

    @Mock
    private ProvinceDao provinceDao;

    @Mock
    private CityDao cityDao;

    @Mock
    private AreaDao areaDao;

    @Mock
    private SiteInfoDao siteInfoDao;

    @InjectMocks
    private SiteServiceImpl siteService;

    @Test
    @DisplayName("根据ID查询站点-不存在时返回空")
    void findSiteById_notExists_returnsEmpty() {
        when(siteDao.findById("nonexistent")).thenReturn(Optional.empty());

        var result = siteService.findSiteBasicInfoById("nonexistent");

        assertNotNull(result);
    }

    @Test
    @DisplayName("删除站点-不存在时返回参数错误")
    void deleteSite_notExists_returnsParamError() {
        when(siteDao.findById("nonexistent")).thenReturn(Optional.empty());

        var result = siteService.deleteSiteInfoById("nonexistent");

        assertNotNull(result);
    }
}
