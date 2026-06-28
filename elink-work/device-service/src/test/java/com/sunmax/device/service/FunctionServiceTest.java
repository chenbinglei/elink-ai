package com.sunmax.device.service;

import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisLockUtil;
import com.sunmax.common.config.redis.RedisUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.model.FunctionDao;
import com.sunmax.device.dao.model.ModelFunctionDao;
import com.sunmax.device.entity.model.FunctionEntity;
import com.sunmax.device.service.impl.FunctionServiceImpl;
import com.sunmax.device.vo.model.FunctionChangeVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FunctionService 单元测试")
class FunctionServiceTest {

    @Mock
    private FunctionDao functionDao;

    @Mock
    private ModelFunctionDao modelFunctionDao;

    @Mock
    private DeviceDao deviceDao;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private RedisDeviceUtil redisDeviceUtil;

    @Mock
    private RedisLockUtil redisLockUtil;

    @InjectMocks
    private FunctionServiceImpl functionService;

    @Test
    @DisplayName("保存功能-名称已存在返回参数错误")
    void saveFunction_nameExists_returnsParamError() {
        FunctionChangeVo vo = new FunctionChangeVo();
        vo.setTypeId("type-001");
        vo.setFunctionName("测试功能");

        FunctionEntity existing = new FunctionEntity();
        existing.setId("func-001");
        existing.setFunctionName("测试功能");
        when(functionDao.findAll(any(Example.class))).thenReturn(Collections.singletonList(existing));

        ResponseResult<Void> result = functionService.saveFunction(vo);

        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("保存功能-名称不存在时创建新功能")
    void saveFunction_nameNotExists_createsNew() {
        FunctionChangeVo vo = new FunctionChangeVo();
        vo.setTypeId("type-001");
        vo.setFunctionName("新功能");
        vo.setFieldCode("FUNC_001");

        when(functionDao.findAll(any(Example.class))).thenReturn(Collections.emptyList());
        when(functionDao.save(any(FunctionEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = functionService.saveFunction(vo);

        // 验证save被调用
        verify(functionDao).save(any(FunctionEntity.class));
    }
}
