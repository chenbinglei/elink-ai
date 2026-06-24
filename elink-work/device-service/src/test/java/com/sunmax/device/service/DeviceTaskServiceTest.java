package com.sunmax.device.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dao.access.*;
import com.sunmax.device.dao.model.AssetTypeDao;
import com.sunmax.device.entity.access.*;
import com.sunmax.device.service.feign.ProtocolService;
import com.sunmax.device.service.feign.SystemService;
import com.sunmax.device.service.impl.DeviceTaskServiceImpl;
import com.sunmax.device.vo.task.DeviceTaskQueryVo;
import com.sunmax.device.vo.task.DeviceTaskRecordVo;
import com.sunmax.device.vo.task.DeviceUpdateQueryVo;
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
@DisplayName("DeviceTaskService 单元测试")
class DeviceTaskServiceTest {

    @Mock private FirmwareDao firmwareDao;
    @Mock private SystemService systemService;
    @Mock private DeviceDao deviceDao;
    @Mock private SiteInfoDao siteInfoDao;
    @Mock private DeviceTaskDao deviceTaskDao;
    @Mock private DeviceTaskRecordDao deviceTaskRecordDao;
    @Mock private AssetTypeDao assetTypeDao;
    @Mock private ProtocolService protocolService;

    @InjectMocks private DeviceTaskServiceImpl deviceTaskService;

    @Test
    @DisplayName("查询固件列表-无数据返回空列表")
    void getFirmwareListByTypeId_noData_returnsEmpty() {
        when(firmwareDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceTaskService.getFirmwareListByTypeId("type-001");
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备升级列表-无数据返回空列表")
    void queryDeviceUpdateList_noData_returnsEmpty() {
        DeviceUpdateQueryVo vo = new DeviceUpdateQueryVo();
        vo.setTypeId("type-001");
        vo.setPage(1);
        vo.setSize(10);

        when(deviceDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceTaskService.queryDeviceUpdateList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备任务列表-无数据返回空列表")
    void queryDeviceTaskList_noData_returnsEmpty() {
        DeviceTaskQueryVo vo = new DeviceTaskQueryVo();
        vo.setPage(1);
        vo.setSize(10);

        when(deviceTaskDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceTaskService.queryDeviceTaskList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询设备任务列表-有数据返回列表")
    void queryDeviceTaskList_withData_returnsList() {
        DeviceTaskQueryVo vo = new DeviceTaskQueryVo();
        vo.setPage(1);
        vo.setSize(10);

        DeviceTaskEntity task = new DeviceTaskEntity();
        task.setId("task-001");
        task.setCreateId("user-001");
        task.setTypeId("type-001");
        when(deviceTaskDao.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(task));

        DeviceTaskRecordEntity record = new DeviceTaskRecordEntity();
        record.setId("rec-001");
        record.setTaskId("task-001");
        when(deviceTaskRecordDao.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(List.of(record));
        when(systemService.findUserInfoByIdsFeign(any())).thenReturn(ResponseResult.ok(Collections.emptyMap()));
        when(assetTypeDao.findAllById(any())).thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceTaskService.queryDeviceTaskList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询任务记录列表-无数据返回空列表")
    void queryDeviceTaskRecordList_noData_returnsEmpty() {
        DeviceTaskRecordVo vo = new DeviceTaskRecordVo();
        vo.setTaskId("task-001");

        when(deviceTaskRecordDao.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(Collections.emptyList());

        ResponseResult<?> result = deviceTaskService.queryDeviceTaskRecordList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("查询任务记录列表-有数据返回列表")
    void queryDeviceTaskRecordList_withData_returnsList() {
        DeviceTaskRecordVo vo = new DeviceTaskRecordVo();
        vo.setTaskId("task-001");

        DeviceTaskRecordEntity record = new DeviceTaskRecordEntity();
        record.setId("rec-001");
        record.setTaskId("task-001");
        record.setDeviceName("设备1");
        record.setDeviceNumber("DEV001");
        when(deviceTaskRecordDao.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(List.of(record));

        ResponseResult<?> result = deviceTaskService.queryDeviceTaskRecordList(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("删除设备任务-空参数返回参数错误")
    void deleteDeviceTaskById_emptyParams_returnsParamError() {
        ResponseResult<Void> result = deviceTaskService.deleteDeviceTaskById(null, null);
        assertFalse(result.isSuccess());
    }

    @Test
    @DisplayName("删除设备任务-类型1删除任务及记录")
    void deleteDeviceTaskById_type1_deletesTaskAndRecords() {
        DeviceTaskEntity task = new DeviceTaskEntity();
        task.setId("task-001");
        when(deviceTaskDao.findById("task-001")).thenReturn(Optional.of(task));
        when(deviceTaskRecordDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(Collections.emptyList());

        ResponseResult<Void> result = deviceTaskService.deleteDeviceTaskById("task-001", 1);
        assertTrue(result.isSuccess());
        verify(deviceTaskDao).delete(task);
    }

    @Test
    @DisplayName("删除设备任务-类型1有记录时同时删除记录")
    void deleteDeviceTaskById_type1_withRecords_deletesAll() {
        DeviceTaskEntity task = new DeviceTaskEntity();
        task.setId("task-001");
        when(deviceTaskDao.findById("task-001")).thenReturn(Optional.of(task));

        DeviceTaskRecordEntity record = new DeviceTaskRecordEntity();
        record.setId("rec-001");
        record.setTaskId("task-001");
        when(deviceTaskRecordDao.findAll(any(org.springframework.data.domain.Example.class))).thenReturn(List.of(record));

        ResponseResult<Void> result = deviceTaskService.deleteDeviceTaskById("task-001", 1);
        assertTrue(result.isSuccess());
        verify(deviceTaskDao).delete(task);
        verify(deviceTaskRecordDao).deleteAll(List.of(record));
    }

    @Test
    @DisplayName("删除设备任务-类型2删除单条记录")
    void deleteDeviceTaskById_type2_deletesRecord() {
        DeviceTaskRecordEntity record = new DeviceTaskRecordEntity();
        record.setId("rec-001");
        when(deviceTaskRecordDao.findById("rec-001")).thenReturn(Optional.of(record));

        ResponseResult<Void> result = deviceTaskService.deleteDeviceTaskById("rec-001", 2);
        assertTrue(result.isSuccess());
        verify(deviceTaskRecordDao).delete(record);
    }

    @Test
    @DisplayName("删除设备任务-类型2记录不存在时仍返回成功")
    void deleteDeviceTaskById_type2_notFound_returnsSuccess() {
        when(deviceTaskRecordDao.findById("rec-001")).thenReturn(Optional.empty());

        ResponseResult<Void> result = deviceTaskService.deleteDeviceTaskById("rec-001", 2);
        assertTrue(result.isSuccess());
    }
}
