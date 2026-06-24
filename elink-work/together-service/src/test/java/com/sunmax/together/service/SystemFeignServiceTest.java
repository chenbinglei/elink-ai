package com.sunmax.together.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dao.asset.GatWayPlatformDao;
import com.sunmax.together.entity.assets.GatWayPlatformEntity;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.feign.ProtocolService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.service.impl.SystemFeignServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SystemFeignService 单元测试")
class SystemFeignServiceTest {

    @Mock
    private GatWayPlatformDao gatWayPlatformDao;

    @Mock
    private ProtocolService protocolService;

    @Mock
    private SystemService systemService;

    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private SystemFeignServiceImpl systemFeignService;

    @Test
    @DisplayName("删除平台网关关联-无关联数据时直接返回成功")
    void deleteAndGatewayPlatformSet_noAssociation_returnsSuccess() {
        when(gatWayPlatformDao.findAllByPlatformId("platform-001")).thenReturn(Collections.emptyList());

        ResponseResult<String> result = systemFeignService.deleteAndGatewayPlatformSet("platform-001");

        assertTrue(result.isSuccess());
    }
}
