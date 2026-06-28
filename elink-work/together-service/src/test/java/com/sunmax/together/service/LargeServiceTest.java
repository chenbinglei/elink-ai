package com.sunmax.together.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dao.LargeSettingDao;
import com.sunmax.together.entity.LargeSettingEntity;
import com.sunmax.together.service.impl.LargeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LargeService 单元测试")
class LargeServiceTest {

    @Mock
    private LargeSettingDao largeSettingDao;

    @InjectMocks
    private LargeServiceImpl largeService;

    @Test
    @DisplayName("大屏设置-参数为空时返回参数错误")
    void largeSetting_emptyParams_returnsParamError() {
        ResponseResult<Void> result = largeService.largeSetting("", "data");
        assertFalse(result.isSuccess());

        result = largeService.largeSetting("userId", "");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("大屏设置-新用户创建设置")
    void largeSetting_newUser_createsSetting() {
        when(largeSettingDao.findOne(any(Example.class))).thenReturn(Optional.empty());
        when(largeSettingDao.save(any(LargeSettingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = largeService.largeSetting("user-001", "{\"theme\":\"dark\"}");

        assertTrue(result.isSuccess());
        verify(largeSettingDao).save(argThat(entity ->
                "user-001".equals(entity.getUserId()) && "{\"theme\":\"dark\"}".equals(entity.getSettingData())
        ));
    }

    @Test
    @DisplayName("大屏设置-已有用户更新设置")
    void largeSetting_existingUser_updatesSetting() {
        LargeSettingEntity existing = LargeSettingEntity.builder().userId("user-001").settingData("{\"theme\":\"light\"}").build();
        when(largeSettingDao.findOne(any(Example.class))).thenReturn(Optional.of(existing));
        when(largeSettingDao.save(any(LargeSettingEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ResponseResult<Void> result = largeService.largeSetting("user-001", "{\"theme\":\"dark\"}");

        assertTrue(result.isSuccess());
        verify(largeSettingDao).save(argThat(entity -> "{\"theme\":\"dark\"}".equals(entity.getSettingData())));
    }

    @Test
    @DisplayName("查询大屏设置-存在时返回数据")
    void queryLargeSetting_exists_returnsData() {
        LargeSettingEntity entity = LargeSettingEntity.builder().userId("user-001").settingData("{\"theme\":\"dark\"}").build();
        when(largeSettingDao.findOne(any(Example.class))).thenReturn(Optional.of(entity));

        ResponseResult<String> result = largeService.queryLargeSetting("user-001");

        assertTrue(result.isSuccess());
        assertEquals("{\"theme\":\"dark\"}", result.getData());
    }

    @Test
    @DisplayName("查询大屏设置-不存在时返回空")
    void queryLargeSetting_notExists_returnsEmpty() {
        when(largeSettingDao.findOne(any(Example.class))).thenReturn(Optional.empty());

        ResponseResult<String> result = largeService.queryLargeSetting("user-001");

        assertTrue(result.isSuccess());
        assertNull(result.getData());
    }
}
