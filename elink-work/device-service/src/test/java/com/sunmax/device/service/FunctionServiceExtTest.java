package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.model.FunctionDao;
import com.sunmax.device.dao.model.ModelFunctionDao;
import com.sunmax.device.entity.model.FunctionEntity;
import com.sunmax.device.entity.model.ModelFunctionEntity;
import com.sunmax.device.service.impl.FunctionServiceImpl;
import com.sunmax.device.vo.model.FunctionChangeVo;
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
@DisplayName("FunctionService 扩展单元测试")
class FunctionServiceExtTest {

    @Mock private FunctionDao functionDao;
    @Mock private ModelFunctionDao modelFunctionDao;
    @Mock private DeviceDao deviceDao;

    @InjectMocks private FunctionServiceImpl functionService;

    @Test
    @DisplayName("保存功能-标识符已存在返回参数错误")
    void saveFunction_logoExists_returnsParamError() {
        FunctionChangeVo vo = new FunctionChangeVo();
        vo.setTypeId("type-001");
        vo.setFunctionName("新功能");
        vo.setFunctionLogo("func_logo");

        // 名称不存在但标识符存在
        FunctionEntity logoExisting = new FunctionEntity();
        logoExisting.setId("func-002");
        logoExisting.setFunctionLogo("func_logo");
        when(functionDao.findAll(any(Example.class))).thenReturn(List.of(logoExisting));

        ResponseResult<Void> result = functionService.saveFunction(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存功能-编辑时名称与其他冲突返回参数错误")
    void saveFunction_edit_nameConflict_returnsParamError() {
        FunctionChangeVo vo = new FunctionChangeVo();
        vo.setId("func-001");
        vo.setTypeId("type-001");
        vo.setFunctionName("已存在名称");

        FunctionEntity other = new FunctionEntity();
        other.setId("func-002");
        other.setFunctionName("已存在名称");
        when(functionDao.findAll(any(Example.class))).thenReturn(List.of(other));

        ResponseResult<Void> result = functionService.saveFunction(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存功能-编辑时实体不存在返回参数错误")
    void saveFunction_edit_notFound_returnsParamError() {
        FunctionChangeVo vo = new FunctionChangeVo();
        vo.setId("func-001");
        vo.setTypeId("type-001");
        vo.setFunctionName("测试功能");

        when(functionDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());
        when(functionDao.findById("func-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = functionService.saveFunction(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存功能-编辑成功")
    void saveFunction_edit_success() {
        FunctionChangeVo vo = new FunctionChangeVo();
        vo.setId("func-001");
        vo.setTypeId("type-001");
        vo.setFunctionName("测试功能");
        vo.setFunctionLogo("func_logo");

        FunctionEntity existing = new FunctionEntity();
        existing.setId("func-001");
        when(functionDao.findAll(any(Example.class))).thenReturn(List.of(existing));
        when(functionDao.findById("func-001")).thenReturn(Optional.of(existing));
        when(functionDao.save(any(FunctionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = functionService.saveFunction(vo);
        assertTrue(result.isSuccess());
        verify(functionDao).save(any(FunctionEntity.class));
    }

    @Test
    @DisplayName("删除功能-不存在时返回参数错误")
    void deleteFunctionById_notExists_returnsParamError() {
        when(functionDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<Void> result = functionService.deleteFunctionById("nonexistent", true);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除功能-软删除成功")
    void deleteFunctionById_softDelete_success() {
        FunctionEntity func = new FunctionEntity();
        func.setId("func-001");
        func.setFunctionLogo("voltage");
        when(functionDao.findById("func-001")).thenReturn(Optional.of(func));
        when(modelFunctionDao.findAllByFunctionIdAndIsDelete("func-001", 1))
                .thenReturn(Collections.emptyList());

        ResponseResult<Void> result = functionService.deleteFunctionById("func-001", false);
        assertTrue(result.isSuccess());
        verify(functionDao).save(any(FunctionEntity.class));
    }

    @Test
    @DisplayName("删除功能-硬删除无关联模型时成功")
    void deleteFunctionById_hardDelete_noModelFunction_success() {
        FunctionEntity func = new FunctionEntity();
        func.setId("func-001");
        func.setFunctionLogo("voltage");
        when(functionDao.findById("func-001")).thenReturn(Optional.of(func));
        when(modelFunctionDao.findAllByFunctionIdAndIsDelete("func-001", 1))
                .thenReturn(Collections.emptyList());

        ResponseResult<Void> result = functionService.deleteFunctionById("func-001", true);
        assertTrue(result.isSuccess());
        verify(functionDao).delete(func);
    }

    @Test
    @DisplayName("删除功能-硬删除有关联模型时删除关联")
    void deleteFunctionById_hardDelete_withModelFunction_deletesAll() {
        FunctionEntity func = new FunctionEntity();
        func.setId("func-001");
        func.setFunctionLogo("voltage");
        when(functionDao.findById("func-001")).thenReturn(Optional.of(func));

        ModelFunctionEntity mf = new ModelFunctionEntity();
        mf.setId("mf-001");
        mf.setModelId("model-001");
        mf.setFunctionId("func-001");
        when(modelFunctionDao.findAllByFunctionIdAndIsDelete("func-001", 1))
                .thenReturn(List.of(mf));

        when(deviceDao.findAllByModelIdInAndIsDelete(Set.of("model-001"), 1))
                .thenReturn(Collections.emptyList());

        ResponseResult<Void> result = functionService.deleteFunctionById("func-001", true);
        assertTrue(result.isSuccess());
        verify(modelFunctionDao).deleteInBatch(List.of(mf));
        verify(functionDao).delete(func);
    }

    @Test
    @DisplayName("查询功能详情-不存在时返回空对象")
    void findFunctionDetailById_notExists_returnsEmpty() {
        when(functionDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<?> result = functionService.findFunctionDetailById("nonexistent");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询功能详情-存在时返回详情")
    void findFunctionDetailById_exists_returnsDetail() {
        FunctionEntity func = new FunctionEntity();
        func.setId("func-001");
        func.setFunctionName("电压");
        func.setFunctionLogo("voltage");
        when(functionDao.findById("func-001")).thenReturn(Optional.of(func));

        ResponseResult<?> result = functionService.findFunctionDetailById("func-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("获取充电桩实时字段-类型1返回工作状态字段")
    void getPileRealFieldList_type1_returnsWorkStatusFields() {
        ResponseResult<?> result = functionService.getPileRealFieldList(1);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("获取充电桩实时字段-类型3返回温度和功率字段")
    void getPileRealFieldList_type3_returnsTempAndPowerFields() {
        ResponseResult<?> result = functionService.getPileRealFieldList(3);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("获取充电桩实时字段-类型8返回充电枪字段")
    void getPileRealFieldList_type8_returnsGunFields() {
        ResponseResult<?> result = functionService.getPileRealFieldList(8);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("获取充电桩实时字段-无效类型返回空列表")
    void getPileRealFieldList_invalidType_returnsEmpty() {
        ResponseResult<?> result = functionService.getPileRealFieldList(99);
        assertTrue(result.isSuccess());
    }
}
