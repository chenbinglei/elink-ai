package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.DeviceDao;
import com.sunmax.device.dao.access.FirmwareDao;
import com.sunmax.device.dao.model.AssetTypeDao;
import com.sunmax.device.entity.access.FirmwareEntity;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.FirmwareServiceImpl;
import com.sunmax.device.vo.firmware.FirmwareQueryVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("FirmwareService 扩展单元测试")
class FirmwareServiceExtTest {

    @Mock private FirmwareDao firmwareDao;
    @Mock private DeviceDao deviceDao;
    @Mock private SystemService systemService;
    @Mock private AssetTypeDao assetTypeDao;

    @InjectMocks private FirmwareServiceImpl firmwareService;

    @Test
    @DisplayName("删除固件-存在时删除成功")
    void deleteFirmwareById_found_deletesSuccess() {
        FirmwareEntity firmware = new FirmwareEntity();
        firmware.setId("fw-001");
        firmware.setFirmwarePath(null);
        when(firmwareDao.findById("fw-001")).thenReturn(Optional.of(firmware));

        ResponseResult<Void> result = firmwareService.deleteFirmwareById("fw-001");
        assertTrue(result.isSuccess());
        verify(firmwareDao).delete(firmware);
    }

    @Test
    @DisplayName("删除固件-不存在时返回参数错误")
    void deleteFirmwareById_notFound_returnsParamError() {
        when(firmwareDao.findById("fw-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = firmwareService.deleteFirmwareById("fw-001");
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("查询固件列表-空参数返回空列表")
    void queryFirmwareList_nullParams_returnsEmpty() {
        FirmwareQueryVo vo = new FirmwareQueryVo();
        vo.setPage(1);
        vo.setSize(10);

        when(firmwareDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Sort.class)))
                .thenReturn(Collections.emptyList());

        ResponseResult<?> result = firmwareService.queryFirmwareList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询固件列表-有数据返回列表")
    void queryFirmwareList_withData_returnsList() {
        FirmwareQueryVo vo = new FirmwareQueryVo();
        vo.setPage(1);
        vo.setSize(10);
        vo.setTypeId("type-001");

        FirmwareEntity fw = new FirmwareEntity();
        fw.setId("fw-001");
        fw.setTypeId("type-001");
        fw.setCreateId("user-001");
        when(firmwareDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Sort.class)))
                .thenReturn(List.of(fw));
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(assetTypeDao.findAllById(any())).thenReturn(Collections.emptyList());

        ResponseResult<?> result = firmwareService.queryFirmwareList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备型号列表-无设备返回空集合")
    void getEquipmentModelList_noDevice_returnsEmpty() {
        when(deviceDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<Set<String>> result = firmwareService.getEquipmentModelList("type-001");
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("查询设备型号列表-有设备返回型号集合")
    void getEquipmentModelList_withDevice_returnsSet() {
        com.sunmax.device.entity.access.DeviceEntity device = new com.sunmax.device.entity.access.DeviceEntity();
        device.setId("dev-001");
        device.setTypeId("type-001");
        device.setReadwriteObject("{\"EQUIPMENT_MODEL\":\"Model-X\"}");
        device.setIsDelete(1);
        when(deviceDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(device));

        ResponseResult<Set<String>> result = firmwareService.getEquipmentModelList("type-001");
        assertTrue(result.isSuccess());
    }
}
