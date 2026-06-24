package com.sunmax.together.service.asset;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dao.asset.ElectConfigDao;
import com.sunmax.together.dao.asset.ElectTimeFrameDao;
import com.sunmax.together.dto.asset.electConfig.ElectConfigDetailDto;
import com.sunmax.together.entity.assets.ElectConfigEntity;
import com.sunmax.together.entity.assets.ElectTimeFrameEntity;
import com.sunmax.together.service.asset.impl.ElectConfigServiceImpl;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.vo.operation.electConfig.ElectConfigChangeVo;
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
@DisplayName("ElectConfigService 单元测试")
class ElectConfigServiceTest {

    @Mock private ElectConfigDao electConfigDao;
    @Mock private ElectTimeFrameDao electTimeFrameDao;
    @Mock private DeviceService deviceService;

    @InjectMocks private ElectConfigServiceImpl electConfigService;

    @Test
    @DisplayName("保存电价配置-参数校验失败返回错误")
    void saveElectConfig_invalidParams_returnsError() {
        ElectConfigChangeVo vo = new ElectConfigChangeVo();
        // 缺少必填字段
        ResponseResult<Void> result = electConfigService.saveElectConfig(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存电价配置-新增成功")
    void saveElectConfig_new_success() {
        ElectConfigChangeVo vo = new ElectConfigChangeVo();
        vo.setSiteId("site-001");
        vo.setModuleType(1);
        vo.setStrategyName("策略1");
        vo.setStartDate("2026-01-01");
        vo.setEndDate("2026-12-31");
        vo.setPriceType(1);
        vo.setElectTimeFrames("[{\"startTime\":\"08:00\",\"endTime\":\"12:00\"}]");

        when(electConfigDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());
        ElectConfigEntity saved = new ElectConfigEntity();
        saved.setId("ec-001");
        when(electConfigDao.save(any(ElectConfigEntity.class))).thenReturn(saved);

        ResponseResult<Void> result = electConfigService.saveElectConfig(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存电价配置-策略名称已存在返回错误")
    void saveElectConfig_duplicateName_returnsError() {
        ElectConfigChangeVo vo = new ElectConfigChangeVo();
        vo.setSiteId("site-001");
        vo.setModuleType(1);
        vo.setStrategyName("策略1");
        vo.setStartDate("2026-01-01");
        vo.setEndDate("2026-12-31");
        vo.setPriceType(1);
        vo.setElectTimeFrames("[{\"startTime\":\"08:00\",\"endTime\":\"12:00\"}]");

        ElectConfigEntity existing = new ElectConfigEntity();
        existing.setId("ec-001");
        existing.setStrategyName("策略1");
        existing.setStartDate("2025-01-01");
        existing.setEndDate("2025-12-31");
        when(electConfigDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(existing));

        ResponseResult<Void> result = electConfigService.saveElectConfig(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除电价配置-成功")
    void deleteAllElectConfigByIds_success() {
        ElectConfigEntity entity = new ElectConfigEntity();
        entity.setId("ec-001");
        when(electConfigDao.findAllById(List.of("ec-001"))).thenReturn(List.of(entity));
        when(electTimeFrameDao.findAllByElectConfigIdIn(List.of("ec-001"))).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = electConfigService.deleteAllElectConfigByIds(List.of("ec-001"));
        assertTrue(result.isSuccess());
        verify(electConfigDao).deleteAll(List.of(entity));
    }

    @Test
    @DisplayName("删除电价配置-无数据也返回成功")
    void deleteAllElectConfigByIds_noData_returnsSuccess() {
        when(electConfigDao.findAllById(List.of("ec-001"))).thenReturn(Collections.emptyList());
        when(electTimeFrameDao.findAllByElectConfigIdIn(List.of("ec-001"))).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = electConfigService.deleteAllElectConfigByIds(List.of("ec-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询电价配置列表-有数据返回Map")
    void queryElectConfigList_withData_returnsMap() {
        ElectConfigEntity entity = new ElectConfigEntity();
        entity.setId("ec-001");
        entity.setSiteId("site-001");
        entity.setModuleType(1);
        entity.setStrategyName("策略1");
        when(electConfigDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(entity));
        when(electTimeFrameDao.findAllByElectConfigIdIn(Set.of("ec-001"))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = electConfigService.queryElectConfigList("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询电价配置列表-无数据返回空Map")
    void queryElectConfigList_noData_returnsEmptyMap() {
        when(electConfigDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = electConfigService.queryElectConfigList("site-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询电价配置详情-存在返回详情")
    void findElectConfigById_found_returnsDetail() {
        ElectConfigEntity entity = new ElectConfigEntity();
        entity.setId("ec-001");
        entity.setSiteId("site-001");
        entity.setStrategyName("策略1");
        when(electConfigDao.findById("ec-001")).thenReturn(Optional.of(entity));
        when(electTimeFrameDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<ElectConfigDetailDto> result = electConfigService.findElectConfigById("ec-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询电价配置详情-不存在返回空")
    void findElectConfigById_notFound_returnsEmpty() {
        when(electConfigDao.findById("ec-001")).thenReturn(Optional.empty());

        ResponseResult<ElectConfigDetailDto> result = electConfigService.findElectConfigById("ec-001");
        assertTrue(result.isSuccess());
    }

    // === 深度覆盖测试 ===

    @Test
    @DisplayName("应用电价配置到其他站点-成功")
    void applyElectConfigToOtherSite_success() {
        ElectConfigEntity config = new ElectConfigEntity();
        config.setId("ec-001");
        config.setSiteId("site-001");
        config.setStrategyName("策略1");
        config.setModuleType(1);
        config.setPriceType(1);
        config.setStartDate("2026-01-01");
        config.setEndDate("2026-12-31");
        when(electConfigDao.findAllById(List.of("ec-001"))).thenReturn(List.of(config));
        when(electTimeFrameDao.findAllByElectConfigIdIn(Set.of("ec-001"))).thenReturn(Collections.emptyList());
        when(electConfigDao.save(any(ElectConfigEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<List<String>> result = electConfigService.applyElectConfigToOtherSite(List.of("ec-001"), List.of("site-002"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("应用电价配置到其他站点-无配置返回参数错误")
    void applyElectConfigToOtherSite_noConfig_returnsParamError() {
        when(electConfigDao.findAllById(List.of("ec-001"))).thenReturn(Collections.emptyList());

        ResponseResult<List<String>> result = electConfigService.applyElectConfigToOtherSite(List.of("ec-001"), List.of("site-002"));
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存电价配置-更新已有配置成功")
    void saveElectConfig_update_success() {
        ElectConfigChangeVo vo = new ElectConfigChangeVo();
        vo.setId("ec-001");
        vo.setSiteId("site-001");
        vo.setModuleType(1);
        vo.setStrategyName("策略1");
        vo.setStartDate("2026-01-01");
        vo.setEndDate("2026-12-31");
        vo.setPriceType(1);
        vo.setElectTimeFrames("[{\"startTime\":\"08:00\",\"endTime\":\"12:00\"}]");

        when(electConfigDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());
        ElectConfigEntity existing = new ElectConfigEntity();
        existing.setId("ec-001");
        existing.setCreateTime(java.time.LocalDateTime.of(2026, 1, 1, 0, 0));
        when(electConfigDao.findById("ec-001")).thenReturn(Optional.of(existing));
        ElectConfigEntity saved = new ElectConfigEntity();
        saved.setId("ec-001");
        when(electConfigDao.save(any(ElectConfigEntity.class))).thenReturn(saved);
        when(electTimeFrameDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = electConfigService.saveElectConfig(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除电价配置-有时段数据一并删除")
    void deleteAllElectConfigByIds_withTimeFrames_success() {
        ElectConfigEntity entity = new ElectConfigEntity();
        entity.setId("ec-001");
        ElectTimeFrameEntity timeFrame = new ElectTimeFrameEntity();
        timeFrame.setId("tf-001");
        timeFrame.setElectConfigId("ec-001");
        when(electConfigDao.findAllById(List.of("ec-001"))).thenReturn(List.of(entity));
        when(electTimeFrameDao.findAllByElectConfigIdIn(List.of("ec-001"))).thenReturn(List.of(timeFrame));

        ResponseResult<Void> result = electConfigService.deleteAllElectConfigByIds(List.of("ec-001"));
        assertTrue(result.isSuccess());
        verify(electTimeFrameDao).deleteAll(List.of(timeFrame));
        verify(electConfigDao).deleteAll(List.of(entity));
    }

    @Test
    @DisplayName("查询电价配置列表-有配置和时段返回完整数据")
    void queryElectConfigList_withTimeFrames_returnsFullData() {
        ElectConfigEntity entity = new ElectConfigEntity();
        entity.setId("ec-001");
        entity.setSiteId("site-001");
        entity.setModuleType(1);
        entity.setStrategyName("策略1");
        entity.setPriceType(1);
        entity.setStartDate("2026-01-01");
        entity.setEndDate("2026-12-31");
        when(electConfigDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(entity));

        ElectTimeFrameEntity timeFrame = new ElectTimeFrameEntity();
        timeFrame.setId("tf-001");
        timeFrame.setElectConfigId("ec-001");
        timeFrame.setStartTime("08:00");
        timeFrame.setEndTime("12:00");
        when(electTimeFrameDao.findAllByElectConfigIdIn(Set.of("ec-001"))).thenReturn(List.of(timeFrame));

        ResponseResult<?> result = electConfigService.queryElectConfigList("site-001");
        assertTrue(result.isSuccess());
    }
}
