package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.FirmwareDao;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.model.AssetTypeDao;
import com.sunmax.device.entity.access.FirmwareEntity;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.FirmwareServiceImpl;
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
@DisplayName("FirmwareService 单元测试")
class FirmwareServiceTest {

    @Mock private FirmwareDao firmwareDao;
    @Mock private SystemService systemService;
    @Mock private DeviceDao deviceDao;
    @Mock private AssetTypeDao assetTypeDao;

    @InjectMocks
    private FirmwareServiceImpl firmwareService;

    @Test
    @DisplayName("根据ID删除固件-存在时删除成功")
    void deleteFirmwareById_exists_deletesSuccessfully() {
        FirmwareEntity entity = new FirmwareEntity();
        entity.setId("fw-001");
        when(firmwareDao.findById("fw-001")).thenReturn(Optional.of(entity));

        ResponseResult<Void> result = firmwareService.deleteFirmwareById("fw-001");

        assertTrue(result.isSuccess());
        verify(firmwareDao).delete(any(FirmwareEntity.class));
    }

    @Test
    @DisplayName("根据ID删除固件-不存在时返回参数错误")
    void deleteFirmwareById_notExists_returnsParamError() {
        when(firmwareDao.findById("nonexistent")).thenReturn(Optional.empty());

        ResponseResult<Void> result = firmwareService.deleteFirmwareById("nonexistent");

        assertFalse(result.isSuccess());
        verify(firmwareDao, never()).delete(any(FirmwareEntity.class));
    }

    @Test
    @DisplayName("获取设备型号列表-无设备返回空集合")
    void getEquipmentModelList_noDevices_returnsEmpty() {
        when(deviceDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<Set<String>> result = firmwareService.getEquipmentModelList("type-001");

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }
}
