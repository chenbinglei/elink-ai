package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.model.ModelReaDao;
import com.sunmax.device.dao.model.ReaDao;
import com.sunmax.device.entity.access.DeviceEntity;
import com.sunmax.device.entity.model.ModelReaEntity;
import com.sunmax.device.entity.model.ReaEntity;
import com.sunmax.device.service.impl.ReaServiceImpl;
import com.sunmax.device.vo.model.ReaChangeVo;
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
@DisplayName("ReaService 单元测试")
class ReaServiceTest {

    @Mock private ReaDao reaDao;
    @Mock private ModelReaDao modelReaDao;
    @Mock private DeviceDao deviceDao;

    @InjectMocks private ReaServiceImpl reaService;

    @Test
    @DisplayName("保存扩展属性-fieldName为空时返回参数错误")
    void saveSea_emptyFieldName_returnsParamError() {
        ReaChangeVo vo = new ReaChangeVo();
        vo.setFieldName("");
        vo.setReaName("test");

        ResponseResult<Void> result = reaService.saveSea(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存扩展属性-fieldName为设备静态字段时返回错误")
    void saveSea_reservedFieldName_returnsError() {
        ReaChangeVo vo = new ReaChangeVo();
        vo.setFieldName("deviceId");
        vo.setReaName("test");

        ResponseResult<Void> result = reaService.saveSea(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存新增扩展属性-名称已存在时返回参数错误")
    void saveSea_newRea_nameExists_returnsParamError() {
        ReaChangeVo vo = new ReaChangeVo();
        vo.setFieldName("custom_field");
        vo.setReaName("测试属性");
        vo.setTypeId("type-001");

        ReaEntity existing = new ReaEntity();
        existing.setId("rea-001");
        existing.setReaName("测试属性");
        when(reaDao.findAll(any(Example.class))).thenReturn(List.of(existing));

        ResponseResult<Void> result = reaService.saveSea(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存新增扩展属性-成功")
    void saveSea_newRea_success() {
        ReaChangeVo vo = new ReaChangeVo();
        vo.setFieldName("custom_field");
        vo.setReaName("测试属性");
        vo.setTypeId("type-001");

        when(reaDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());
        when(reaDao.save(any(ReaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = reaService.saveSea(vo);
        assertTrue(result.isSuccess());
        verify(reaDao).save(any(ReaEntity.class));
    }

    @Test
    @DisplayName("保存编辑扩展属性-ID存在但实体不存在时返回参数错误")
    void saveSea_editRea_notFound_returnsParamError() {
        ReaChangeVo vo = new ReaChangeVo();
        vo.setId("rea-001");
        vo.setFieldName("custom_field");
        vo.setReaName("测试属性");
        vo.setTypeId("type-001");

        when(reaDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());
        when(reaDao.findById("rea-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = reaService.saveSea(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存编辑扩展属性-成功")
    void saveSea_editRea_success() {
        ReaChangeVo vo = new ReaChangeVo();
        vo.setId("rea-001");
        vo.setFieldName("custom_field");
        vo.setReaName("测试属性");
        vo.setTypeId("type-001");

        ReaEntity existing = new ReaEntity();
        existing.setId("rea-001");
        when(reaDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());
        when(reaDao.findById("rea-001")).thenReturn(Optional.of(existing));
        when(reaDao.save(any(ReaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = reaService.saveSea(vo);
        assertTrue(result.isSuccess());
        verify(reaDao).save(any(ReaEntity.class));
    }

    @Test
    @DisplayName("删除扩展属性-不存在时直接返回成功")
    void deleteSeaById_notExists_returnsSuccess() {
        when(reaDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<Void> result = reaService.deleteSeaById("nonexistent");
        assertTrue(result.isSuccess());
        verify(reaDao, never()).delete(any(ReaEntity.class));
    }

    @Test
    @DisplayName("删除扩展属性-存在且无关联模型时删除")
    void deleteSeaById_existsNoModelRea_deletes() {
        ReaEntity rea = new ReaEntity();
        rea.setId("rea-001");
        rea.setFieldName("custom_field");
        when(reaDao.findById("rea-001")).thenReturn(Optional.of(rea));
        when(modelReaDao.findAllByReaId("rea-001")).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = reaService.deleteSeaById("rea-001");
        assertTrue(result.isSuccess());
        verify(reaDao).delete(rea);
    }

    @Test
    @DisplayName("删除扩展属性-有关联模型时删除关联和扩展属性")
    void deleteSeaById_withModelRea_deletesAll() {
        ReaEntity rea = new ReaEntity();
        rea.setId("rea-001");
        rea.setFieldName("custom_field");
        when(reaDao.findById("rea-001")).thenReturn(Optional.of(rea));

        ModelReaEntity modelRea = new ModelReaEntity();
        modelRea.setModelId("model-001");
        when(modelReaDao.findAllByReaId("rea-001")).thenReturn(List.of(modelRea));

        DeviceEntity device = new DeviceEntity();
        device.setId("dev-001");
        device.setReadwriteObject("{\"custom_field\":\"value\"}");
        when(deviceDao.findAllByModelIdInAndIsDelete(Set.of("model-001"), 1))
                .thenReturn(List.of(device));

        ResponseResult<Void> result = reaService.deleteSeaById("rea-001");
        assertTrue(result.isSuccess());
        verify(modelReaDao).deleteInBatch(List.of(modelRea));
        verify(reaDao).delete(rea);
    }

    @Test
    @DisplayName("查询扩展属性详情-不存在时返回空对象")
    void findSeaDetailById_notExists_returnsEmpty() {
        when(reaDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<?> result = reaService.findSeaDetailById("nonexistent");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询扩展属性详情-存在时返回详情")
    void findSeaDetailById_exists_returnsDetail() {
        ReaEntity rea = new ReaEntity();
        rea.setId("rea-001");
        rea.setReaName("测试属性");
        rea.setFieldName("custom_field");
        when(reaDao.findById("rea-001")).thenReturn(Optional.of(rea));

        ResponseResult<?> result = reaService.findSeaDetailById("rea-001");
        assertTrue(result.isSuccess());
    }
}
