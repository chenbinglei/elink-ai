package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.DeviceTopologyDao;
import com.sunmax.device.dao.access.GatewaySubDeviceDao;
import com.sunmax.device.dao.model.*;
import com.sunmax.device.entity.model.ModelEntity;
import com.sunmax.device.entity.model.ModelEventEntity;
import com.sunmax.device.entity.model.ModelReaEntity;
import com.sunmax.device.entity.model.ModelTopologyEntity;
import com.sunmax.device.service.feign.DataService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.ModelServiceImpl;
import com.sunmax.device.vo.model.ModelEventChangeVo;
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
@DisplayName("ModelService 扩展单元测试")
class ModelServiceExtTest {

    @Mock private ModelDao modelDao;
    @Mock private ModelReaDao modelReaDao;
    @Mock private ModelFunctionDao modelFunctionDao;
    @Mock private FunctionDao functionDao;
    @Mock private DeviceDao deviceDao;
    @Mock private ModelTopologyDao modelTopologyDao;
    @Mock private DeviceTopologyDao deviceTopologyDao;
    @Mock private GatewaySubDeviceDao gatewaySubDeviceDao;
    @Mock private SystemService systemService;
    @Mock private DataService dataService;
    @Mock private ModelEventDao modelEventDao;
    @Mock private PileFaultDao pileFaultDao;
    @Mock private AssetTypeDao assetTypeDao;

    @InjectMocks private ModelServiceImpl modelService;

    @Test
    @DisplayName("更新模型状态-模型不存在返回参数错误")
    void updateModelStatus_notFound_returnsParamError() {
        when(modelDao.findById("model-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = modelService.updateModelStatus("model-001", "user-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("更新模型状态-模型存在时更新成功")
    void updateModelStatus_found_updatesSuccess() {
        ModelEntity model = new ModelEntity();
        model.setId("model-001");
        when(modelDao.findById("model-001")).thenReturn(Optional.of(model));
        when(modelDao.save(any(ModelEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = modelService.updateModelStatus("model-001", "user-001");
        assertTrue(result.isSuccess());
        verify(modelDao).save(any(ModelEntity.class));
    }

    @Test
    @DisplayName("删除模型-模型不存在返回参数错误")
    void deleteModelById_notFound_returnsParamError() {
        when(modelDao.findById("model-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = modelService.deleteModelById("model-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除模型-模型存在时删除成功")
    void deleteModelById_found_deletesSuccess() {
        ModelEntity model = new ModelEntity();
        model.setId("model-001");
        when(modelDao.findById("model-001")).thenReturn(Optional.of(model));
        when(modelReaDao.findAllByModelId("model-001")).thenReturn(Collections.emptyList());
        when(modelFunctionDao.findAllByModelId("model-001")).thenReturn(Collections.emptyList());
        when(modelTopologyDao.findAllByModelId("model-001")).thenReturn(Collections.emptyList());
        when(deviceDao.findAllByModelIdInAndIsDelete(Collections.singleton("model-001"), 1))
                .thenReturn(Collections.emptyList());

        ResponseResult<Void> result = modelService.deleteModelById("model-001");
        assertTrue(result.isSuccess());
        verify(modelDao).delete(model);
    }

    @Test
    @DisplayName("删除模型拓扑-成功")
    void deleteModelTopologyById_success() {
        ResponseResult<Void> result = modelService.deleteModelTopologyById("topo-001");
        assertTrue(result.isSuccess());
        verify(modelTopologyDao).deleteById("topo-001");
    }

    @Test
    @DisplayName("批量删除模型事件-有数据时成功")
    void batchDeleteModelEventByIds_withData_success() {
        ModelEventEntity event = new ModelEventEntity();
        event.setId("evt-001");
        event.setModelId("model-001");
        when(modelEventDao.findAllById(List.of("evt-001"))).thenReturn(List.of(event));
        when(modelEventDao.saveAll(any())).thenReturn(List.of(event));

        ResponseResult<Void> result = modelService.batchDeleteModelEventByIds(List.of("evt-001"));
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("批量删除模型事件-无数据时返回参数错误")
    void batchDeleteModelEventByIds_empty_returnsParamError() {
        when(modelEventDao.findAllById(List.of("evt-001"))).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = modelService.batchDeleteModelEventByIds(List.of("evt-001"));
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除充电桩故障-成功")
    void deletePileFaultById_success() {
        ResponseResult<Void> result = modelService.deletePileFaultById("fault-001");
        assertTrue(result.isSuccess());
        verify(pileFaultDao).deleteById("fault-001");
    }

    @Test
    @DisplayName("更新模型扩展属性默认值-存在时更新成功")
    void updateModelReaValue_found_success() {
        ModelReaEntity rea = new ModelReaEntity();
        rea.setId("rea-001");
        when(modelReaDao.findById("rea-001")).thenReturn(Optional.of(rea));
        when(modelReaDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = modelService.updateModelReaValue("rea-001", "default_value");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("更新模型扩展属性默认值-不存在时返回参数错误")
    void updateModelReaValue_notFound_returnsParamError() {
        when(modelReaDao.findById("rea-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = modelService.updateModelReaValue("rea-001", "default_value");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询模型拓扑列表-有数据返回列表")
    void findModelTopologyListByModelId_withData_returnsList() {
        ModelTopologyEntity entity = new ModelTopologyEntity();
        entity.setId("topo-001");
        entity.setModelId("model-001");
        when(modelTopologyDao.findAllByModelId("model-001")).thenReturn(List.of(entity));

        ResponseResult<?> result = modelService.findModelTopologyListByModelId("model-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询模型拓扑列表-无数据返回空列表")
    void findModelTopologyListByModelId_empty_returnsEmpty() {
        when(modelTopologyDao.findAllByModelId("model-001")).thenReturn(Collections.emptyList());

        ResponseResult<?> result = modelService.findModelTopologyListByModelId("model-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询充电桩故障列表-有数据返回列表")
    void findPileFaultListByModelId_withData_returnsList() {
        when(pileFaultDao.findAllByModelId("model-001")).thenReturn(Collections.emptyList());

        ResponseResult<?> result = modelService.findPileFaultListByModelId("model-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存模型事件-名称已存在返回参数错误")
    void saveModelEvent_nameExists_returnsParamError() {
        ModelEventChangeVo vo = new ModelEventChangeVo();
        vo.setModelId("model-001");
        vo.setEventName("已存在事件");

        ModelEventEntity existing = new ModelEventEntity();
        existing.setId("evt-001");
        existing.setEventName("已存在事件");
        when(modelEventDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(existing));

        ResponseResult<Void> result = modelService.saveModelEvent(vo);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存充电桩故障-成功")
    void savePileFault_success() {
        com.sunmax.device.vo.model.PileFaultChangeVo vo = new com.sunmax.device.vo.model.PileFaultChangeVo();
        vo.setModelId("model-001");
        vo.setFaultCode(1);
        when(pileFaultDao.findAllByModelIdAndFaultCode("model-001", 1)).thenReturn(Collections.emptyList());
        when(pileFaultDao.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = modelService.savePileFault(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("保存充电桩故障-故障码已存在返回参数错误")
    void savePileFault_faultCodeExists_returnsParamError() {
        com.sunmax.device.vo.model.PileFaultChangeVo vo = new com.sunmax.device.vo.model.PileFaultChangeVo();
        vo.setModelId("model-001");
        vo.setFaultCode(1);
        com.sunmax.device.entity.model.PileFaultEntity existing = new com.sunmax.device.entity.model.PileFaultEntity();
        existing.setId("fault-001");
        when(pileFaultDao.findAllByModelIdAndFaultCode("model-001", 1)).thenReturn(List.of(existing));

        ResponseResult<Void> result = modelService.savePileFault(vo);
        assertFalse(result.isSuccess());
    }
}
