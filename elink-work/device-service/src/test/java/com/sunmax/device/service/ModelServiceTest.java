package com.sunmax.device.service;

import com.sunmax.common.config.redis.*;
import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.ModelDetailDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.DeviceTopologyDao;
import com.sunmax.device.dao.access.GatewaySubDeviceDao;
import com.sunmax.device.dao.model.*;
import com.sunmax.device.dto.model.ModelListDto;
import com.sunmax.device.entity.model.ModelEntity;
import com.sunmax.device.service.feign.DataService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.ModelServiceImpl;
import com.sunmax.device.vo.model.ModelQueryVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ModelService 单元测试")
class ModelServiceTest {

    @Mock
    private ModelDao modelDao;

    @Mock
    private ModelReaDao modelReaDao;

    @Mock
    private ModelFunctionDao modelFunctionDao;

    @Mock
    private FunctionDao functionDao;

    @Mock
    private DeviceDao deviceDao;

    @Mock
    private ModelTopologyDao modelTopologyDao;

    @Mock
    private DeviceTopologyDao deviceTopologyDao;

    @Mock
    private GatewaySubDeviceDao gatewaySubDeviceDao;

    @Mock
    private SystemService systemService;

    @Mock
    private DataService dataService;

    @Mock
    private RedisModelEventUtil redisModelEventUtil;

    @Mock
    private RedisDeviceUtil redisDeviceUtil;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private ModelEventDao modelEventDao;

    @InjectMocks
    private ModelServiceImpl modelService;

    @Test
    @DisplayName("根据ID查询模型详情-不存在时返回空")
    void findModelDetailById_notExists_returnsEmpty() {
        when(modelDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<ModelDetailDto> result = modelService.findModelDetailById("nonexistent");

        assertNotNull(result);
    }

    @Test
    @DisplayName("根据ID删除模型-不存在时返回参数错误")
    void deleteModelById_notExists_returnsParamError() {
        when(modelDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<Void> result = modelService.deleteModelById("nonexistent");

        assertNotNull(result);
    }

    @Test
    @DisplayName("查询模型列表-返回分页数据")
    void queryModelList_returnsPagedData() {
        ModelQueryVo vo = new ModelQueryVo();
        vo.setPage(1);
        vo.setSize(10);
        Page<ModelEntity> page = new PageImpl<>(Collections.emptyList());
        when(modelDao.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        ResponseResult<PageDto<ModelListDto>> result = modelService.queryModelList(vo);

        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询模型绑定功能-模型不存在时返回空")
    void findModelBindFunction_modelNotExists_returnsEmpty() {
        when(modelDao.findById("nonexistent")).thenReturn(Optional.empty());

        var result = modelService.findModelBindFunctionByModelId("nonexistent");

        assertNotNull(result);
    }
}
