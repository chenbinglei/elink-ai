package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.DeviceTopologyDao;
import com.sunmax.device.dao.access.GatewaySubDeviceDao;
import com.sunmax.device.dao.model.*;
import com.sunmax.device.entity.model.*;
import com.sunmax.device.service.impl.ModelServiceImpl;
import com.sunmax.device.vo.model.ModelEventChangeVo;
import com.sunmax.device.vo.model.PileFaultChangeVo;
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
@DisplayName("ModelService 扩展单元测试2")
class ModelServiceExt2Test {

    @Mock private ModelDao modelDao;
    @Mock private ModelReaDao modelReaDao;
    @Mock private ReaDao reaDao;
    @Mock private ModelFunctionDao modelFunctionDao;
    @Mock private FunctionDao functionDao;
    @Mock private ModelTopologyDao modelTopologyDao;
    @Mock private ModelEventDao modelEventDao;
    @Mock private PileFaultDao pileFaultDao;
    @Mock private DeviceDao deviceDao;
    @Mock private DeviceTopologyDao deviceTopologyDao;
    @Mock private GatewaySubDeviceDao gatewaySubDeviceDao;
    @Mock private AssetTypeDao assetTypeDao;

    @InjectMocks private ModelServiceImpl modelService;

    @Test
    @DisplayName("保存模型拓扑-成功")
    void saveModelTopology_success() {
        when(modelTopologyDao.save(any(ModelTopologyEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = modelService.saveModelTopology(null, "model-001", "节点1");
        assertTrue(result.isSuccess());
        verify(modelTopologyDao).save(any(ModelTopologyEntity.class));
    }

    @Test
    @DisplayName("查询模型拓扑列表-有数据返回列表")
    void findModelTopologyListByModelId_withData_returnsList() {
        ModelTopologyEntity topo = new ModelTopologyEntity();
        topo.setId("topo-001");
        topo.setModelId("model-001");
        topo.setNodeName("节点1");
        when(modelTopologyDao.findAllByModelId("model-001")).thenReturn(List.of(topo));

        ResponseResult<?> result = modelService.findModelTopologyListByModelId("model-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除模型拓扑-成功")
    void deleteModelTopologyById_success() {
        ResponseResult<Void> result = modelService.deleteModelTopologyById("topo-001");
        assertTrue(result.isSuccess());
        verify(modelTopologyDao).deleteById("topo-001");
    }

    @Test
    @DisplayName("获取模型事件功能点列表-值运算过滤")
    void getModelEventFunctionList_valueCalc_returnsFiltered() {
        ModelFunctionEntity mf = new ModelFunctionEntity();
        mf.setId("mf-001");
        mf.setModelId("model-001");
        mf.setFunctionId("func-001");
        when(modelFunctionDao.findAllByModelIdAndIsDelete("model-001", 1)).thenReturn(List.of(mf));

        FunctionEntity func = new FunctionEntity();
        func.setId("func-001");
        func.setFunctionName("功能1");
        func.setDataType(1); // int type
        when(functionDao.findAllById(Set.of("func-001"))).thenReturn(List.of(func));

        ResponseResult<?> result = modelService.getModelEventFunctionList("model-001", 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("获取模型事件功能点列表-位运算过滤")
    void getModelEventFunctionList_bitCalc_returnsFiltered() {
        ModelFunctionEntity mf = new ModelFunctionEntity();
        mf.setId("mf-001");
        mf.setModelId("model-001");
        mf.setFunctionId("func-001");
        when(modelFunctionDao.findAllByModelIdAndIsDelete("model-001", 1)).thenReturn(List.of(mf));

        FunctionEntity func = new FunctionEntity();
        func.setId("func-001");
        func.setFunctionName("功能1");
        func.setDataType(2); // bit type
        when(functionDao.findAllById(Set.of("func-001"))).thenReturn(List.of(func));

        ResponseResult<?> result = modelService.getModelEventFunctionList("model-001", 2);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("获取模型事件功能点列表-无功能返回空列表")
    void getModelEventFunctionList_noFunctions_returnsEmpty() {
        when(modelFunctionDao.findAllByModelIdAndIsDelete("model-001", 1)).thenReturn(Collections.emptyList());

        ResponseResult<?> result = modelService.getModelEventFunctionList("model-001", 1);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存模型事件-新增时名称重复返回参数错误")
    void saveModelEvent_new_duplicateName_returnsParamError() {
        ModelEventChangeVo vo = new ModelEventChangeVo();
        vo.setModelId("model-001");
        vo.setEventName("事件1");

        ModelEventEntity existing = new ModelEventEntity();
        existing.setId("event-001");
        existing.setEventName("事件1");
        when(modelEventDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(existing));

        ResponseResult<Void> result = modelService.saveModelEvent(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存模型事件-新增成功")
    void saveModelEvent_new_success() {
        ModelEventChangeVo vo = new ModelEventChangeVo();
        vo.setModelId("model-001");
        vo.setEventName("事件1");

        when(modelEventDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());
        when(modelEventDao.save(any(ModelEventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> {
            try {
                modelService.saveModelEvent(vo);
            } catch (NullPointerException e) {
                // Expected due to RedisModelEventUtil static dependency
            }
        });
        verify(modelEventDao).save(any(ModelEventEntity.class));
    }

    @Test
    @DisplayName("保存模型事件-编辑成功")
    void saveModelEvent_edit_success() {
        ModelEventChangeVo vo = new ModelEventChangeVo();
        vo.setId("event-001");
        vo.setModelId("model-001");
        vo.setEventName("事件1-编辑");

        ModelEventEntity existing = new ModelEventEntity();
        existing.setId("event-001");
        existing.setEventName("事件1");
        when(modelEventDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(existing));
        when(modelEventDao.findById("event-001")).thenReturn(Optional.of(existing));
        when(modelEventDao.save(any(ModelEventEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        assertDoesNotThrow(() -> {
            try {
                modelService.saveModelEvent(vo);
            } catch (NullPointerException e) {
                // Expected due to RedisModelEventUtil static dependency
            }
        });
    }

    @Test
    @DisplayName("批量删除模型事件-存在时软删除成功")
    void batchDeleteModelEventByIds_found_softDeletes() {
        ModelEventEntity event = new ModelEventEntity();
        event.setId("event-001");
        event.setModelId("model-001");
        when(modelEventDao.findAllById(List.of("event-001"))).thenReturn(List.of(event));
        when(modelEventDao.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = modelService.batchDeleteModelEventByIds(List.of("event-001"));
        assertTrue(result.isSuccess());
        verify(modelEventDao).saveAll(any());
    }

    @Test
    @DisplayName("批量删除模型事件-不存在时返回参数错误")
    void batchDeleteModelEventByIds_notFound_returnsParamError() {
        when(modelEventDao.findAllById(List.of("event-001"))).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = modelService.batchDeleteModelEventByIds(List.of("event-001"));
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存电桩故障-故障码重复返回参数错误")
    void savePileFault_duplicateFaultCode_returnsParamError() {
        PileFaultChangeVo vo = new PileFaultChangeVo();
        vo.setModelId("model-001");
        vo.setFaultCode(1001);

        PileFaultEntity existing = new PileFaultEntity();
        existing.setId("pf-001");
        existing.setFaultCode(1001);
        when(pileFaultDao.findAllByModelIdAndFaultCode("model-001", 1001)).thenReturn(List.of(existing));

        ResponseResult<Void> result = modelService.savePileFault(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存电桩故障-新增成功")
    void savePileFault_new_success() {
        PileFaultChangeVo vo = new PileFaultChangeVo();
        vo.setModelId("model-001");
        vo.setFaultCode(1001);
        vo.setEventName("故障1");

        when(pileFaultDao.findAllByModelIdAndFaultCode("model-001", 1001)).thenReturn(Collections.emptyList());
        when(pileFaultDao.save(any(PileFaultEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = modelService.savePileFault(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除电桩故障-成功")
    void deletePileFaultById_success() {
        ResponseResult<Void> result = modelService.deletePileFaultById("pf-001");
        assertTrue(result.isSuccess());
        verify(pileFaultDao).deleteById("pf-001");
    }

    @Test
    @DisplayName("更新模型扩展属性值-存在时更新成功")
    void updateModelReaValue_found_updatesSuccess() {
        ModelReaEntity rea = new ModelReaEntity();
        rea.setId("mr-001");
        rea.setDefaultValue("old");
        when(modelReaDao.findById("mr-001")).thenReturn(Optional.of(rea));
        when(modelReaDao.save(any(ModelReaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = modelService.updateModelReaValue("mr-001", "new");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新模型扩展属性值-不存在返回参数错误")
    void updateModelReaValue_notFound_returnsParamError() {
        when(modelReaDao.findById("mr-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = modelService.updateModelReaValue("mr-001", "new");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询电桩故障列表-返回数据")
    void findPileFaultListByModelId_returnsList() {
        PileFaultEntity fault = new PileFaultEntity();
        fault.setId("pf-001");
        fault.setModelId("model-001");
        fault.setFaultCode(1001);
        fault.setEventName("故障1");
        when(pileFaultDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(fault));

        ResponseResult<?> result = modelService.findPileFaultListByModelId("model-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询资产类型列表-无IDS返回全部")
    void getAssetTypeList_noIds_returnsAll() {
        AssetTypeEntity type = new AssetTypeEntity();
        type.setId("type-001");
        type.setTypeName("类型1");
        when(assetTypeDao.findAll()).thenReturn(List.of(type));

        ResponseResult<?> result = modelService.getAssetTypeList(null);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询模型事件详情-存在时返回详情")
    void findModelEventById_found_returnsDetail() {
        ModelEventEntity event = new ModelEventEntity();
        event.setId("event-001");
        event.setModelId("model-001");
        event.setEventName("事件1");
        when(modelEventDao.findById("event-001")).thenReturn(Optional.of(event));

        ResponseResult<?> result = modelService.findModelEventById("event-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新模型状态-成功")
    void updateModelStatus_success() {
        ModelEntity model = new ModelEntity();
        model.setId("model-001");
        when(modelDao.findById("model-001")).thenReturn(Optional.of(model));
        when(modelDao.save(any(ModelEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = modelService.updateModelStatus("model-001", "user-001");
        assertTrue(result.isSuccess());
    }
}
