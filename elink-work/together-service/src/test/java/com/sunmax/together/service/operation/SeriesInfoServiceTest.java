package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dao.asset.ModuleLibraryDao;
import com.sunmax.together.dao.asset.SeriesConfigDao;
import com.sunmax.together.entity.assets.ModuleLibraryEntity;
import com.sunmax.together.entity.assets.SeriesConfigEntity;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.operation.impl.SeriesInfoServiceImpl;
import com.sunmax.together.vo.operation.seriesInfo.ModuleLibraryChangeVo;
import com.sunmax.together.vo.operation.seriesInfo.SeriesConfigChangeVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SeriesInfoService 单元测试")
class SeriesInfoServiceTest {

    @Mock private ModuleLibraryDao moduleLibraryDao;
    @Mock private SeriesConfigDao seriesConfigDao;
    @Mock private DeviceService deviceService;
    @Mock private com.sunmax.together.service.feign.SystemService systemService;

    @InjectMocks private SeriesInfoServiceImpl seriesInfoService;

    @Test
    @DisplayName("新增组件库-成功")
    void saveModuleLibrary_new_success() {
        ModuleLibraryChangeVo vo = new ModuleLibraryChangeVo();
        vo.setModuleFactory("厂家A");
        vo.setModuleModel("型号B");
        when(moduleLibraryDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());
        when(moduleLibraryDao.save(any(ModuleLibraryEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = seriesInfoService.saveOrUpdateModuleLibrary(vo, "user-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("新增组件库-重复返回参数错误")
    void saveModuleLibrary_duplicate_returnsParamError() {
        ModuleLibraryChangeVo vo = new ModuleLibraryChangeVo();
        vo.setModuleFactory("厂家A");
        vo.setModuleModel("型号B");
        ModuleLibraryEntity existing = new ModuleLibraryEntity();
        existing.setId("ml-001");
        when(moduleLibraryDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(existing));

        ResponseResult<String> result = seriesInfoService.saveOrUpdateModuleLibrary(vo, "user-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("编辑组件库-成功")
    void updateModuleLibrary_success() {
        ModuleLibraryChangeVo vo = new ModuleLibraryChangeVo();
        vo.setId("ml-001");
        vo.setModuleFactory("厂家A");
        vo.setModuleModel("型号B");
        when(moduleLibraryDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ModuleLibraryEntity existing = new ModuleLibraryEntity();
        existing.setId("ml-001");
        existing.setCreateTime(LocalDateTime.now());
        when(moduleLibraryDao.findById("ml-001")).thenReturn(Optional.of(existing));
        when(moduleLibraryDao.save(any(ModuleLibraryEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = seriesInfoService.saveOrUpdateModuleLibrary(vo, "user-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("批量删除组件库-成功")
    void batchDeleteModuleLibrary_success() {
        ModuleLibraryEntity entity = new ModuleLibraryEntity();
        entity.setId("ml-001");
        when(moduleLibraryDao.findAllById(List.of("ml-001"))).thenReturn(List.of(entity));

        ResponseResult<String> result = seriesInfoService.batchDeleteModuleLibrary("[\"ml-001\"]");
        assertTrue(result.isSuccess());
        verify(moduleLibraryDao).deleteAll(any());
    }

    @Test
    @DisplayName("查询组件厂家列表-返回去重列表")
    void findModuleFactoryList_returnsDistinct() {
        ModuleLibraryEntity e1 = new ModuleLibraryEntity();
        e1.setModuleFactory("厂家A");
        ModuleLibraryEntity e2 = new ModuleLibraryEntity();
        e2.setModuleFactory("厂家B");
        when(moduleLibraryDao.findAll()).thenReturn(List.of(e1, e2));

        ResponseResult<?> result = seriesInfoService.findModuleFactoryList();
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询组件型号列表-厂家为空返回空列表")
    void findModuleModelList_emptyFactory_returnsEmpty() {
        ResponseResult<?> result = seriesInfoService.findModuleModelList("");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询组件型号列表-有数据返回列表")
    void findModuleModelList_withData_returnsList() {
        ModuleLibraryEntity entity = new ModuleLibraryEntity();
        entity.setId("ml-001");
        entity.setModuleFactory("厂家A");
        entity.setModuleModel("型号B");
        when(moduleLibraryDao.findAllByModuleFactory("厂家A")).thenReturn(List.of(entity));

        ResponseResult<?> result = seriesInfoService.findModuleModelList("厂家A");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存组串配置-成功")
    void saveSeriesConfigList_success() {
        SeriesConfigChangeVo vo = new SeriesConfigChangeVo();
        vo.setSeriesName("组串1");
        when(seriesConfigDao.findAllByDeviceId("dev-001")).thenReturn(Collections.emptyList());
        when(seriesConfigDao.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = seriesInfoService.saveSeriesConfigList("dev-001", List.of(vo));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存组串配置-先删后存")
    void saveSeriesConfigList_deleteFirst_success() {
        SeriesConfigChangeVo vo = new SeriesConfigChangeVo();
        vo.setSeriesName("组串1");
        SeriesConfigEntity existing = new SeriesConfigEntity();
        when(seriesConfigDao.findAllByDeviceId("dev-001")).thenReturn(List.of(existing));
        when(seriesConfigDao.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<String> result = seriesInfoService.saveSeriesConfigList("dev-001", List.of(vo));
        assertTrue(result.isSuccess());
        verify(seriesConfigDao).deleteAll(List.of(existing));
    }

    @Test
    @DisplayName("保存组串配置-空列表返回FAIL但isSuccess为true")
    void saveSeriesConfigList_emptyList_returnsFail() {
        ResponseResult<String> result = seriesInfoService.saveSeriesConfigList("dev-001", Collections.emptyList());
        assertTrue(result.isSuccess());
        assertEquals(ResponseResult.FAIL, result.getData());
    }

    @Test
    @DisplayName("查询组串配置-有数据返回列表")
    void findSeriesConfigInfo_withData_returnsList() {
        SeriesConfigEntity entity = new SeriesConfigEntity();
        entity.setId("sc-001");
        entity.setDeviceId("dev-001");
        entity.setSeriesName("组串1");
        when(seriesConfigDao.findAllByDeviceId("dev-001")).thenReturn(List.of(entity));

        ResponseResult<?> result = seriesInfoService.findSeriesConfigInfo("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询组串配置-无数据返回空列表")
    void findSeriesConfigInfo_noData_returnsEmpty() {
        when(seriesConfigDao.findAllByDeviceId("dev-001")).thenReturn(Collections.emptyList());

        ResponseResult<?> result = seriesInfoService.findSeriesConfigInfo("dev-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("清除组串配置-有数据删除成功")
    void purgeSeriesConfigById_withData_success() {
        SeriesConfigEntity entity = new SeriesConfigEntity();
        when(seriesConfigDao.findAllByDeviceIdIn(List.of("dev-001"))).thenReturn(List.of(entity));

        ResponseResult<String> result = seriesInfoService.purgeSeriesConfigById(List.of("dev-001"));
        assertTrue(result.isSuccess());
        verify(seriesConfigDao).deleteAll(List.of(entity));
    }

    @Test
    @DisplayName("清除组串配置-无数据返回FAIL但isSuccess为true")
    void purgeSeriesConfigById_noData_returnsFail() {
        when(seriesConfigDao.findAllByDeviceIdIn(List.of("dev-001"))).thenReturn(Collections.emptyList());

        ResponseResult<String> result = seriesInfoService.purgeSeriesConfigById(List.of("dev-001"));
        assertTrue(result.isSuccess());
        assertEquals(ResponseResult.FAIL, result.getData());
    }
}
