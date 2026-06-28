package com.sunmax.device.service;

import com.sunmax.common.dto.device.SiteSetUpDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.AssetTypeDao;
import com.sunmax.device.dao.model.ModelDao;
import com.sunmax.device.dao.model.ModelReaDao;
import com.sunmax.device.dao.model.ReaDao;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.mapper.access.SiteInfoMapper;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.feign.TogetherService;
import com.sunmax.device.service.impl.SiteInfoServiceImpl;
import com.sunmax.device.vo.SiteSetUpChangeVo;
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
@DisplayName("SiteInfoService 单元测试")
class SiteInfoServiceTest {

    @Mock private SiteInfoDao siteInfoDao;
    @Mock private ScenarioTypeDao scenarioTypeDao;
    @Mock private SystemService systemService;
    @Mock private ModelReaDao modelReaDao;
    @Mock private ReaDao reaDao;
    @Mock private DeviceDao deviceDao;
    @Mock private AffiliatesInfoDao affiliatesInfoDao;
    @Mock private ProvinceDao provinceDao;
    @Mock private CityDao cityDao;
    @Mock private AreaDao areaDao;
    @Mock private ModelDao modelDao;
    @Mock private SiteInfoMapper siteInfoMapper;
    @Mock private TogetherService togetherService;
    @Mock private SiteSetUpDao siteSetUpDao;
    @Mock private SiteTopNodeDao siteTopNodeDao;
    @Mock private SiteTopItemDao siteTopItemDao;
    @Mock private AssetTypeDao assetTypeDao;

    @InjectMocks private SiteInfoServiceImpl siteInfoService;

    @Test
    @DisplayName("查询省份数据-有数据返回列表")
    void queryProvinceData_withData_returnsList() {
        ProvinceEntity entity = new ProvinceEntity();
        entity.setProvinceName("浙江省");
        when(provinceDao.findAll()).thenReturn(List.of(entity));

        ResponseResult<?> result = siteInfoService.queryProvinceData();
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询省份数据-无数据返回空列表")
    void queryProvinceData_empty_returnsEmpty() {
        when(provinceDao.findAll()).thenReturn(Collections.emptyList());

        ResponseResult<?> result = siteInfoService.queryProvinceData();
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询城市数据-有数据返回列表")
    void queryCityDataByProvinceId_withData_returnsList() {
        CityEntity entity = new CityEntity();
        entity.setCityName("杭州市");
        when(cityDao.findAllByProvinceIdIn(any())).thenReturn(List.of(entity));

        ResponseResult<?> result = siteInfoService.queryCityDataByProvinceId("prov-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询区县数据-有数据返回列表")
    void queryAreaDataByCityId_withData_returnsList() {
        AreaEntity entity = new AreaEntity();
        entity.setAreaName("西湖区");
        when(areaDao.findAllByCityIdIn(any())).thenReturn(List.of(entity));

        ResponseResult<?> result = siteInfoService.queryAreaDataByCityId("city-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新站点设置-新增成功")
    void updateSiteSetUp_new_success() {
        SiteSetUpChangeVo vo = new SiteSetUpChangeVo();
        vo.setSiteId("site-001");
        vo.setAppShow(1);
        vo.setOperatePassword("123456");

        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(null);
        when(siteSetUpDao.save(any(SiteSetUpEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<?> result = siteInfoService.updateSiteSetUp(vo);
        assertTrue(result.isSuccess());
        verify(siteSetUpDao).save(any(SiteSetUpEntity.class));
    }

    @Test
    @DisplayName("更新站点设置-已存在设置时返回错误")
    void updateSiteSetUp_alreadyExists_returnsError() {
        SiteSetUpChangeVo vo = new SiteSetUpChangeVo();
        vo.setSiteId("site-001");

        SiteSetUpEntity existing = new SiteSetUpEntity();
        existing.setId("setup-001");
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(existing);

        ResponseResult<?> result = siteInfoService.updateSiteSetUp(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新站点设置-编辑成功")
    void updateSiteSetUp_edit_success() {
        SiteSetUpChangeVo vo = new SiteSetUpChangeVo();
        vo.setId("setup-001");
        vo.setSiteId("site-001");
        vo.setAppShow(1);

        SiteSetUpEntity existing = new SiteSetUpEntity();
        existing.setId("setup-001");
        when(siteSetUpDao.findById("setup-001")).thenReturn(Optional.of(existing));
        when(siteSetUpDao.save(any(SiteSetUpEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<?> result = siteInfoService.updateSiteSetUp(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点设置-站点不存在返回空对象")
    void findSiteSetUpBySiteId_siteNotFound_returnsEmpty() {
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.empty());

        ResponseResult<SiteSetUpDto> result = siteInfoService.findSiteSetUpBySiteId("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点设置-站点存在但无设置返回空对象")
    void findSiteSetUpBySiteId_noSetUp_returnsEmpty() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("测试站点");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(null);

        ResponseResult<SiteSetUpDto> result = siteInfoService.findSiteSetUpBySiteId("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点设置-有设置返回设置信息")
    void findSiteSetUpBySiteId_withSetUp_returnsSetUp() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("测试站点");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));

        SiteSetUpEntity setUp = new SiteSetUpEntity();
        setUp.setId("setup-001");
        setUp.setSiteId("site-001");
        setUp.setSystemName("测试系统");
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(setUp);

        ResponseResult<SiteSetUpDto> result = siteInfoService.findSiteSetUpBySiteId("site-001");
        assertTrue(result.isSuccess());
        assertEquals("测试系统", result.getData().getSystemName());
    }

    @Test
    @DisplayName("查询站点设置-系统名称为空时截取站点名称")
    void findSiteSetUpBySiteId_emptySystemName_usesSiteName() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("测试站点名称很长");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));

        SiteSetUpEntity setUp = new SiteSetUpEntity();
        setUp.setId("setup-001");
        setUp.setSiteId("site-001");
        setUp.setSystemName(null);
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(setUp);

        ResponseResult<SiteSetUpDto> result = siteInfoService.findSiteSetUpBySiteId("site-001");
        assertTrue(result.isSuccess());
        assertEquals("测试站点名称很长", result.getData().getSystemName());
    }

    @Test
    @DisplayName("删除拓扑节点-不存在时返回错误")
    void deleteTopNodeInfoById_notExists_returnsError() {
        when(siteTopNodeDao.findById("node-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = siteInfoService.deleteTopNodeInfoById("node-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除拓扑节点-存在时删除节点及子节点")
    void deleteTopNodeInfoById_exists_deletesNode() {
        SiteTopNodeEntity node = new SiteTopNodeEntity();
        node.setId("node-001");
        node.setParentId(null);
        when(siteTopNodeDao.findById("node-001")).thenReturn(Optional.of(node));
        when(siteTopNodeDao.findAll()).thenReturn(Collections.emptyList());
        when(siteTopItemDao.findAllByNodeIdIn(any())).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = siteInfoService.deleteTopNodeInfoById("node-001");
        assertTrue(result.isSuccess());
        verify(siteTopNodeDao).deleteAll(any());
    }

    @Test
    @DisplayName("查询拓扑节点列表-有数据返回列表")
    void findTopNodeListBySiteId_withData_returnsList() {
        SiteTopNodeEntity entity = new SiteTopNodeEntity();
        entity.setId("node-001");
        entity.setSiteId("site-001");
        when(siteTopNodeDao.findAllBySiteId("site-001")).thenReturn(List.of(entity));

        ResponseResult<?> result = siteInfoService.findTopNodeListBySiteId("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询拓扑节点列表-无数据返回空列表")
    void findTopNodeListBySiteId_empty_returnsEmpty() {
        when(siteTopNodeDao.findAllBySiteId("site-001")).thenReturn(Collections.emptyList());

        ResponseResult<?> result = siteInfoService.findTopNodeListBySiteId("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除站点-不存在时返回错误")
    void deleteSiteInfoById_notExists_returnsError() {
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = siteInfoService.deleteSiteInfoById("site-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除站点-存在且有设备时软删除成功")
    void deleteSiteInfoById_hasDevices_softDeletes() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(deviceDao.findAllBySiteIdAndIsDelete("site-001", 1))
                .thenReturn(List.of(new DeviceEntity()));
        when(siteInfoDao.save(any(SiteInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(deviceDao.saveAll(any())).thenReturn(Collections.emptyList());

        ResponseResult<?> result = siteInfoService.deleteSiteInfoById("site-001");
        assertTrue(result.isSuccess());
        verify(siteInfoDao).save(any(SiteInfoEntity.class));
    }

    @Test
    @DisplayName("删除站点-存在且无设备时软删除成功")
    void deleteSiteInfoById_noDevices_softDeletes() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(deviceDao.findAllBySiteIdAndIsDelete("site-001", 1))
                .thenReturn(Collections.emptyList());
        when(siteInfoDao.save(any(SiteInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<?> result = siteInfoService.deleteSiteInfoById("site-001");
        assertTrue(result.isSuccess());
        verify(siteInfoDao).save(any(SiteInfoEntity.class));
    }

    @Test
    @DisplayName("删除能源场景-不存在时返回错误")
    void deleteSiteScenarioTypeById_notExists_returnsError() {
        when(scenarioTypeDao.findById("scenario-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = siteInfoService.deleteSiteScenarioTypeById("scenario-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除能源场景-存在时删除成功")
    void deleteSiteScenarioTypeById_exists_deletes() {
        ScenarioTypeEntity entity = new ScenarioTypeEntity();
        entity.setId("scenario-001");
        entity.setSiteId("site-001");
        when(scenarioTypeDao.findById("scenario-001")).thenReturn(Optional.of(entity));
        when(scenarioTypeDao.findAllBySiteIdIn(any())).thenReturn(Collections.emptyList());

        ResponseResult<?> result = siteInfoService.deleteSiteScenarioTypeById("scenario-001");
        assertTrue(result.isSuccess());
        verify(scenarioTypeDao).deleteById("scenario-001");
    }
}
