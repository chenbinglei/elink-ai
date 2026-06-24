package com.sunmax.device.service;

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
import com.sunmax.device.vo.ScenarioTypeChangeVo;
import com.sunmax.device.vo.SiteSetUpChangeVo;
import com.sunmax.device.vo.SiteTopNodeChangeVo;
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
@DisplayName("SiteInfoService 扩展单元测试2")
class SiteInfoServiceExt2Test {

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
    @DisplayName("保存或更新能源场景-新增时更新站点场景字段")
    void saveOrUpdateSiteScenarioType_new_updatesSiteScenarioTypes() {
        ScenarioTypeChangeVo vo = new ScenarioTypeChangeVo();
        vo.setSiteId("site-001");
        vo.setScenarioType(1);

        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setScenarioTypes("2");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(siteInfoDao.save(any(SiteInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = siteInfoService.saveOrUpdateSiteScenarioType(vo);
        assertTrue(result.isSuccess());
        verify(scenarioTypeDao).save(any(ScenarioTypeEntity.class));
        verify(siteInfoDao).save(any(SiteInfoEntity.class));
    }

    @Test
    @DisplayName("保存或更新能源场景-新增时站点场景字段为空")
    void saveOrUpdateSiteScenarioType_new_emptyScenarioTypes() {
        ScenarioTypeChangeVo vo = new ScenarioTypeChangeVo();
        vo.setSiteId("site-001");
        vo.setScenarioType(1);

        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setScenarioTypes(null);
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(siteInfoDao.save(any(SiteInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = siteInfoService.saveOrUpdateSiteScenarioType(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存或更新能源场景-编辑时不更新站点字段")
    void saveOrUpdateSiteScenarioType_edit_noUpdateSite() {
        ScenarioTypeChangeVo vo = new ScenarioTypeChangeVo();
        vo.setId("scenario-001");
        vo.setSiteId("site-001");
        vo.setScenarioType(1);

        ResponseResult<Void> result = siteInfoService.saveOrUpdateSiteScenarioType(vo);
        assertTrue(result.isSuccess());
        verify(scenarioTypeDao).save(any(ScenarioTypeEntity.class));
        verify(siteInfoDao, never()).save(any());
    }

    @Test
    @DisplayName("删除能源场景-存在时删除成功")
    void deleteSiteScenarioTypeById_found_deletesSuccess() {
        ScenarioTypeEntity scenario = new ScenarioTypeEntity();
        scenario.setId("scenario-001");
        scenario.setSiteId("site-001");
        scenario.setScenarioType(1);
        when(scenarioTypeDao.findById("scenario-001")).thenReturn(Optional.of(scenario));
        when(scenarioTypeDao.findAllBySiteIdIn(Collections.singleton("site-001"))).thenReturn(new ArrayList<>(List.of(scenario)));

        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setScenarioTypes("1,2");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));
        when(siteInfoDao.save(any(SiteInfoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = siteInfoService.deleteSiteScenarioTypeById("scenario-001");
        assertTrue(result.isSuccess());
        verify(scenarioTypeDao).deleteById("scenario-001");
    }

    @Test
    @DisplayName("删除能源场景-不存在时返回失败")
    void deleteSiteScenarioTypeById_notFound_returnsFail() {
        when(scenarioTypeDao.findById("scenario-001")).thenReturn(Optional.empty());

        ResponseResult<String> result = siteInfoService.deleteSiteScenarioTypeById("scenario-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询省份列表-返回省份数据")
    void queryProvinceData_returnsList() {
        ProvinceEntity province = new ProvinceEntity();
        province.setId(1L);
        province.setProvinceName("浙江省");
        when(provinceDao.findAll()).thenReturn(List.of(province));

        ResponseResult<?> result = siteInfoService.queryProvinceData();
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询城市列表-根据省份ID返回城市数据")
    void queryCityDataByProvinceId_returnsList() {
        CityEntity city = new CityEntity();
        city.setId(1L);
        city.setCityName("杭州市");
        city.setProvinceId("prov-001");
        when(cityDao.findAllByProvinceIdIn(any())).thenReturn(List.of(city));

        ResponseResult<?> result = siteInfoService.queryCityDataByProvinceId("prov-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询区县列表-根据城市ID返回区县数据")
    void queryAreaDataByCityId_returnsList() {
        AreaEntity area = new AreaEntity();
        area.setId(1L);
        area.setAreaName("西湖区");
        area.setCityId("city-001");
        when(areaDao.findAllByCityIdIn(any())).thenReturn(List.of(area));

        ResponseResult<?> result = siteInfoService.queryAreaDataByCityId("city-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新站点设置-新增模式成功")
    void updateSiteSetUp_new_success() {
        SiteSetUpChangeVo vo = new SiteSetUpChangeVo();
        vo.setSiteId("site-001");
        vo.setAppShow(1);
        vo.setOperatePassword("123456");

        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(null);
        when(siteSetUpDao.save(any(SiteSetUpEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = siteInfoService.updateSiteSetUp(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新站点设置-已有设置时返回失败")
    void updateSiteSetUp_alreadyExists_returnsFail() {
        SiteSetUpChangeVo vo = new SiteSetUpChangeVo();
        vo.setSiteId("site-001");

        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(new SiteSetUpEntity());

        ResponseResult<String> result = siteInfoService.updateSiteSetUp(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新站点设置-编辑模式成功")
    void updateSiteSetUp_edit_success() {
        SiteSetUpChangeVo vo = new SiteSetUpChangeVo();
        vo.setId("setup-001");
        vo.setSiteId("site-001");
        vo.setAppShow(1);

        SiteSetUpEntity existing = new SiteSetUpEntity();
        existing.setId("setup-001");
        when(siteSetUpDao.findById("setup-001")).thenReturn(Optional.of(existing));
        when(siteSetUpDao.save(any(SiteSetUpEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = siteInfoService.updateSiteSetUp(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点设置-站点存在有设置信息")
    void findSiteSetUpBySiteId_found_withSetup() {
        SiteInfoEntity site = new SiteInfoEntity();
        site.setId("site-001");
        site.setSiteName("测试站点名称超过12个字符");
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.of(site));

        SiteSetUpEntity setup = new SiteSetUpEntity();
        setup.setId("setup-001");
        setup.setSiteId("site-001");
        setup.setSystemName(null);
        when(siteSetUpDao.findBySiteId("site-001")).thenReturn(setup);

        ResponseResult<?> result = siteInfoService.findSiteSetUpBySiteId("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询站点设置-站点不存在返回空对象")
    void findSiteSetUpBySiteId_notFound_returnsEmpty() {
        when(siteInfoDao.findById("site-001")).thenReturn(Optional.empty());

        ResponseResult<?> result = siteInfoService.findSiteSetUpBySiteId("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除拓扑节点-存在时递归删除子节点")
    void deleteTopNodeInfoById_found_deletesWithChildren() {
        SiteTopNodeEntity parent = new SiteTopNodeEntity();
        parent.setId("node-001");
        parent.setSiteId("site-001");

        SiteTopNodeEntity child = new SiteTopNodeEntity();
        child.setId("node-002");
        child.setParentId("node-001");
        child.setSiteId("site-001");

        when(siteTopNodeDao.findById("node-001")).thenReturn(Optional.of(parent));
        when(siteTopNodeDao.findAll()).thenReturn(List.of(parent, child));
        when(siteTopItemDao.findAllByNodeIdIn(any())).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = siteInfoService.deleteTopNodeInfoById("node-001");
        assertTrue(result.isSuccess());
        verify(siteTopItemDao).deleteAll(any());
        verify(siteTopNodeDao).deleteAll(any());
    }

    @Test
    @DisplayName("删除拓扑节点-不存在时返回失败")
    void deleteTopNodeInfoById_notFound_returnsFail() {
        when(siteTopNodeDao.findById("node-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = siteInfoService.deleteTopNodeInfoById("node-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询拓扑节点列表-根据站点ID返回数据")
    void findTopNodeListBySiteId_returnsList() {
        SiteTopNodeEntity node = new SiteTopNodeEntity();
        node.setId("node-001");
        node.setSiteId("site-001");
        node.setNodeName("节点1");
        when(siteTopNodeDao.findAllBySiteId("site-001")).thenReturn(List.of(node));

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
    @DisplayName("保存拓扑节点-新增模式成功")
    void saveSiteTopNode_new_success() {
        SiteTopNodeChangeVo vo = new SiteTopNodeChangeVo();
        vo.setSiteId("site-001");
        vo.setNodeName("节点1");

        SiteTopNodeEntity saved = new SiteTopNodeEntity();
        saved.setId("node-001");
        when(siteTopNodeDao.save(any(SiteTopNodeEntity.class))).thenReturn(saved);
        when(siteTopItemDao.saveAll(any())).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = siteInfoService.saveSiteTopNode(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存拓扑节点-新增带拓扑项数据")
    void saveSiteTopNode_new_withItems_success() {
        SiteTopNodeChangeVo vo = new SiteTopNodeChangeVo();
        vo.setSiteId("site-001");
        vo.setNodeName("节点1");
        vo.setSiteTopItems("[{\"fieldCode\":\"test\",\"fieldName\":\"测试\"}]");

        SiteTopNodeEntity saved = new SiteTopNodeEntity();
        saved.setId("node-001");
        when(siteTopNodeDao.save(any(SiteTopNodeEntity.class))).thenReturn(saved);
        when(siteTopItemDao.saveAll(any())).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = siteInfoService.saveSiteTopNode(vo);
        assertTrue(result.isSuccess());
    }
}
