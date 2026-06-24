package com.sunmax.data.service;

import com.sunmax.common.dto.data.DeviceHistoryDto;
import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.dto.device.DeviceFieldDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceCountQueryVo;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.common.vo.data.DeviceIndexListQueryVo;
import com.sunmax.common.vo.data.DeviceIndexQueryVo;
import com.sunmax.common.vo.device.DeviceFieldQueryVo;
import com.sunmax.data.mapper.tdengine.DataStoreMapper;
import com.sunmax.data.model.TableDataModel;
import com.sunmax.data.model.TableDifDataModel;
import com.sunmax.data.service.impl.DeviceFeignServiceImpl;
import com.sunmax.data.service.feign.DeviceService;
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
@DisplayName("DeviceFeignService 扩展单元测试")
class DeviceFeignServiceExtTest {

    @Mock private DataStoreMapper dataStoreMapper;
    @Mock private DeviceService deviceService;
    @Mock private AccessDataService accessDataService;

    @InjectMocks private DeviceFeignServiceImpl deviceFeignService;

    private DeviceFieldDto buildFieldDto(String tableName, String fieldName, String functionLogo) {
        DeviceFieldDto dto = new DeviceFieldDto();
        dto.setTableName(tableName);
        dto.setFieldName(fieldName);
        dto.setFunctionLogo(functionLogo);
        dto.setDataType(1);
        dto.setValueRange("");
        dto.setDataObject("");
        return dto;
    }

    @Test
    @DisplayName("findDeviceHistoryValueList-设备字段为空时返回空Map")
    void findDeviceHistoryValueList_emptyFields_returnsEmpty() {
        DeviceHistoryQueryVo vo = new DeviceHistoryQueryVo();
        vo.setDeviceIds(Set.of("dev-001"));
        vo.setFunctionLogos(Set.of("logo-001"));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");
        when(deviceService.queryDeviceFieldList(any(DeviceFieldQueryVo.class)))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = deviceFeignService.findDeviceHistoryValueList(vo);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("findDeviceHistoryValueList-设备字段有数据时正确映射")
    void findDeviceHistoryValueList_withFields_mapsCorrectly() {
        DeviceHistoryQueryVo vo = new DeviceHistoryQueryVo();
        vo.setDeviceIds(Set.of("dev-001"));
        vo.setFunctionLogos(Set.of("voltage"));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");

        DeviceFieldDto fieldDto = buildFieldDto("t_dev001", "f_voltage", "voltage");
        Map<String, List<DeviceFieldDto>> fieldMap = new HashMap<>();
        fieldMap.put("dev-001", List.of(fieldDto));
        when(deviceService.queryDeviceFieldList(any(DeviceFieldQueryVo.class)))
                .thenReturn(ResponseResult.ok(fieldMap));

        TableDataModel dataModel = new TableDataModel();
        dataModel.setFieldName("f_voltage");
        dataModel.setFieldValue("220.5");
        dataModel.setDataTime("2026-01-15 12:00:00");
        Map<String, List<TableDataModel>> fieldDataMap = Map.of("f_voltage", List.of(dataModel));
        Map<String, Map<String, List<TableDataModel>>> tableDataMap = Map.of("t_dev001", fieldDataMap);
        when(accessDataService.queryTableDataList(any(), any(), any(), any(), any()))
                .thenReturn(tableDataMap);

        var result = deviceFeignService.findDeviceHistoryValueList(vo);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().containsKey("dev-001"));
        assertTrue(result.getData().get("dev-001").containsKey("voltage"));
    }

    @Test
    @DisplayName("findDeviceDifferenceListFeign-设备字段为空时返回空Map")
    void findDeviceDifferenceListFeign_emptyFields_returnsEmpty() {
        DeviceHistoryQueryVo vo = new DeviceHistoryQueryVo();
        vo.setDeviceIds(Set.of("dev-001"));
        vo.setFunctionLogos(Set.of("logo-001"));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");
        when(deviceService.queryDeviceFieldList(any(DeviceFieldQueryVo.class)))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = deviceFeignService.findDeviceDifferenceListFeign(vo, 0);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("findDeviceDifferenceListFeign-有字段数据时正确映射")
    void findDeviceDifferenceListFeign_withFields_mapsCorrectly() {
        DeviceHistoryQueryVo vo = new DeviceHistoryQueryVo();
        vo.setDeviceIds(Set.of("dev-001"));
        vo.setFunctionLogos(Set.of("current"));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");

        DeviceFieldDto fieldDto = buildFieldDto("t_dev001", "f_current", "current");
        Map<String, List<DeviceFieldDto>> fieldMap = Map.of("dev-001", List.of(fieldDto));
        when(deviceService.queryDeviceFieldList(any(DeviceFieldQueryVo.class)))
                .thenReturn(ResponseResult.ok(fieldMap));

        TableDataModel dataModel = new TableDataModel();
        dataModel.setFieldName("f_current");
        dataModel.setFieldValue("10.5");
        dataModel.setDataTime("2026-01-15 12:00:00");
        Map<String, List<TableDataModel>> fieldDataMap = Map.of("f_current", List.of(dataModel));
        Map<String, Map<String, List<TableDataModel>>> tableDataMap = Map.of("t_dev001", fieldDataMap);
        when(accessDataService.queryDifferenceTableDataList(any(), any(), any(), any(), any()))
                .thenReturn(tableDataMap);

        var result = deviceFeignService.findDeviceDifferenceListFeign(vo, 0);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().containsKey("dev-001"));
    }

    @Test
    @DisplayName("findDeviceCountFunListFeign-设备字段为空时返回空Map")
    void findDeviceCountFunListFeign_emptyFields_returnsEmpty() {
        DeviceCountQueryVo vo = new DeviceCountQueryVo();
        vo.setDeviceIds(Set.of("dev-001"));
        vo.setFunctionLogos(Set.of("voltage"));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");
        vo.setCuntFun("avg");
        when(deviceService.queryDeviceFieldList(any(DeviceFieldQueryVo.class)))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = deviceFeignService.findDeviceCountFunListFeign(vo);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("findDeviceCountFunListFeign-有字段数据时返回结果")
    void findDeviceCountFunListFeign_withFields_returnsResult() {
        DeviceCountQueryVo vo = new DeviceCountQueryVo();
        vo.setDeviceIds(Set.of("dev-001"));
        vo.setFunctionLogos(Set.of("voltage"));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");
        vo.setCuntFun("avg");

        DeviceFieldDto fieldDto = buildFieldDto("t_dev001", "f_voltage", "voltage");
        Map<String, List<DeviceFieldDto>> fieldMap = Map.of("dev-001", List.of(fieldDto));
        when(deviceService.queryDeviceFieldList(any(DeviceFieldQueryVo.class)))
                .thenReturn(ResponseResult.ok(fieldMap));

        Map<String, Map<String, List<TableDataModel>>> tableDataMap = new HashMap<>();
        when(accessDataService.queryCountTableDataList(any(), any(), any(), any(), any()))
                .thenReturn(tableDataMap);

        var result = deviceFeignService.findDeviceCountFunListFeign(vo);

        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("findDeviceHistoryValueList-字段不在表数据中时返回空列表")
    void findDeviceHistoryValueList_fieldNotInTableData_returnsEmptyList() {
        DeviceHistoryQueryVo vo = new DeviceHistoryQueryVo();
        vo.setDeviceIds(Set.of("dev-001"));
        vo.setFunctionLogos(Set.of("voltage"));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");

        DeviceFieldDto fieldDto = buildFieldDto("t_dev001", "f_voltage", "voltage");
        Map<String, List<DeviceFieldDto>> fieldMap = Map.of("dev-001", List.of(fieldDto));
        when(deviceService.queryDeviceFieldList(any(DeviceFieldQueryVo.class)))
                .thenReturn(ResponseResult.ok(fieldMap));

        // 表数据不包含该字段
        Map<String, Map<String, List<TableDataModel>>> tableDataMap = Map.of(
                "t_dev001", Map.of("other_field", List.of())
        );
        when(accessDataService.queryTableDataList(any(), any(), any(), any(), any()))
                .thenReturn(tableDataMap);

        var result = deviceFeignService.findDeviceHistoryValueList(vo);

        assertTrue(result.isSuccess());
        assertTrue(result.getData().containsKey("dev-001"));
        // voltage字段应在结果中但列表为空
        assertTrue(result.getData().get("dev-001").get("voltage").isEmpty());
    }

    @Test
    @DisplayName("deleteAllDataStoreTable-null集合时抛NPE")
    void deleteAllDataStoreTable_nullSet_throwsNPE() {
        assertThrows(NullPointerException.class, () -> deviceFeignService.deleteAllDataStoreTable(null));
    }

    @Test
    @DisplayName("deleteAllDataStoreTable-正常删除表")
    void deleteAllDataStoreTable_validSet_deletesTables() {
        Set<String> tables = Set.of("t_dev001", "t_dev002");
        var result = deviceFeignService.deleteAllDataStoreTable(tables);
        assertTrue(result.isSuccess());
        verify(dataStoreMapper).deleteTable("t_dev001");
        verify(dataStoreMapper).deleteTable("t_dev002");
    }

    @Test
    @DisplayName("findDeviceHistoryIndexValueList-空字段返回空Map")
    void findDeviceHistoryIndexValueList_emptyFields_returnsEmpty() {
        DeviceIndexQueryVo vo = new DeviceIndexQueryVo();
        vo.setDeviceFuctionMap(Map.of("dev-001", Set.of("voltage")));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");
        when(deviceService.queryDeviceFieldListByMap(any()))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = deviceFeignService.findDeviceHistoryIndexValueList(vo);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("findDeviceHistoryIndexValueList-有字段数据时返回结果")
    void findDeviceHistoryIndexValueList_withFields_returnsResult() {
        DeviceIndexQueryVo vo = new DeviceIndexQueryVo();
        vo.setDeviceFuctionMap(Map.of("dev-001", Set.of("voltage")));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");

        DeviceFieldDto fieldDto = buildFieldDto("t_dev001", "f_voltage", "voltage");
        Map<String, List<DeviceFieldDto>> fieldMap = Map.of("dev-001", List.of(fieldDto));
        when(deviceService.queryDeviceFieldListByMap(any()))
                .thenReturn(ResponseResult.ok(fieldMap));

        TableDataModel dataModel = new TableDataModel();
        dataModel.setFieldName("f_voltage");
        dataModel.setFieldValue("220.5");
        dataModel.setDataTime("2026-01-15 12:00:00");
        Map<String, List<TableDataModel>> fieldDataMap = Map.of("f_voltage", List.of(dataModel));
        Map<String, Map<String, List<TableDataModel>>> tableDataMap = Map.of("t_dev001", fieldDataMap);
        when(accessDataService.queryTableDataList(any(), any(), any(), any(), any()))
                .thenReturn(tableDataMap);

        var result = deviceFeignService.findDeviceHistoryIndexValueList(vo);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().containsKey("dev-001"));
    }

    @Test
    @DisplayName("findDeviceDiffIndexValueListFeign-空字段返回空Map")
    void findDeviceDiffIndexValueListFeign_emptyFields_returnsEmpty() {
        DeviceIndexQueryVo vo = new DeviceIndexQueryVo();
        vo.setDeviceFuctionMap(Map.of("dev-001", Set.of("voltage")));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");
        when(deviceService.queryDeviceFieldListByMap(any()))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = deviceFeignService.findDeviceDiffIndexValueListFeign(vo);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("findDeviceDiffIndexValueListFeign-有字段数据时返回结果")
    void findDeviceDiffIndexValueListFeign_withFields_returnsResult() {
        DeviceIndexQueryVo vo = new DeviceIndexQueryVo();
        vo.setDeviceFuctionMap(Map.of("dev-001", Set.of("voltage")));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");

        DeviceFieldDto fieldDto = buildFieldDto("t_dev001", "f_voltage", "voltage");
        Map<String, List<DeviceFieldDto>> fieldMap = Map.of("dev-001", List.of(fieldDto));
        when(deviceService.queryDeviceFieldListByMap(any()))
                .thenReturn(ResponseResult.ok(fieldMap));

        Map<String, Map<String, List<TableDataModel>>> tableDataMap = new HashMap<>();
        when(accessDataService.queryDifferenceTableDataList(any(), any(), any(), any(), any()))
                .thenReturn(tableDataMap);

        var result = deviceFeignService.findDeviceDiffIndexValueListFeign(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("findIndexListHistorValueFeign-空字段返回空Map")
    void findIndexListHistorValueFeign_emptyFields_returnsEmpty() {
        DeviceIndexListQueryVo vo = new DeviceIndexListQueryVo();
        vo.setDeviceFuctionMap(Map.of("dev-001", Set.of("voltage")));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");
        when(deviceService.queryDeviceFieldListByMap(any()))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = deviceFeignService.findIndexListHistorValueFeign(vo);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("findIndexListHistorValueFeign-有字段数据时返回结果")
    void findIndexListHistorValueFeign_withFields_returnsResult() {
        DeviceIndexListQueryVo vo = new DeviceIndexListQueryVo();
        vo.setDeviceFuctionMap(Map.of("dev-001", Set.of("voltage")));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");

        DeviceFieldDto fieldDto = buildFieldDto("t_dev001", "f_voltage", "voltage");
        Map<String, List<DeviceFieldDto>> fieldMap = Map.of("dev-001", List.of(fieldDto));
        when(deviceService.queryDeviceFieldListByMap(any()))
                .thenReturn(ResponseResult.ok(fieldMap));

        Map<String, Map<String, List<TableDataModel>>> tableDataMap = new HashMap<>();
        when(accessDataService.queryFunctionPointTableDataList(any(), any(), any(), any(), any()))
                .thenReturn(tableDataMap);

        var result = deviceFeignService.findIndexListHistorValueFeign(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("findNodeDifHistoryIndexListFeign-空字段返回空Map")
    void findNodeDifHistoryIndexListFeign_emptyFields_returnsEmpty() {
        DeviceIndexQueryVo vo = new DeviceIndexQueryVo();
        vo.setDeviceFuctionMap(Map.of("dev-001", Set.of("voltage")));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");
        when(deviceService.queryDeviceFieldListByMap(any()))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = deviceFeignService.findNodeDifHistoryIndexListFeign(vo);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("findNodeDifHistoryIndexListFeign-有字段数据时返回结果")
    void findNodeDifHistoryIndexListFeign_withFields_returnsResult() {
        DeviceIndexQueryVo vo = new DeviceIndexQueryVo();
        vo.setDeviceFuctionMap(Map.of("dev-001", Set.of("voltage")));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");

        DeviceFieldDto fieldDto = buildFieldDto("t_dev001", "f_voltage", "voltage");
        Map<String, List<DeviceFieldDto>> fieldMap = Map.of("dev-001", List.of(fieldDto));
        when(deviceService.queryDeviceFieldListByMap(any()))
                .thenReturn(ResponseResult.ok(fieldMap));

        Map<String, Map<String, List<TableDifDataModel>>> tableDataMap = new HashMap<>();
        when(accessDataService.findNodeDifHistoryListFeign(any(), any(), any(), any()))
                .thenReturn(tableDataMap);

        var result = deviceFeignService.findNodeDifHistoryIndexListFeign(vo);
        assertTrue(result.isSuccess());
    }

    @Test
    @DisplayName("findNodeDifHistoryListFeign-空字段返回空Map")
    void findNodeDifHistoryListFeign_emptyFields_returnsEmpty() {
        DeviceHistoryQueryVo vo = new DeviceHistoryQueryVo();
        vo.setDeviceIds(Set.of("dev-001"));
        vo.setFunctionLogos(Set.of("voltage"));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");
        when(deviceService.queryDeviceFieldList(any(DeviceFieldQueryVo.class)))
                .thenReturn(ResponseResult.ok(Collections.emptyMap()));

        var result = deviceFeignService.findNodeDifHistoryListFeign(vo);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("findNodeDifHistoryListFeign-有字段数据时正确映射")
    void findNodeDifHistoryListFeign_withFields_mapsCorrectly() {
        DeviceHistoryQueryVo vo = new DeviceHistoryQueryVo();
        vo.setDeviceIds(Set.of("dev-001"));
        vo.setFunctionLogos(Set.of("voltage"));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");

        DeviceFieldDto fieldDto = buildFieldDto("t_dev001", "f_voltage", "voltage");
        Map<String, List<DeviceFieldDto>> fieldMap = Map.of("dev-001", List.of(fieldDto));
        when(deviceService.queryDeviceFieldList(any(DeviceFieldQueryVo.class)))
                .thenReturn(ResponseResult.ok(fieldMap));

        TableDifDataModel difModel = new TableDifDataModel();
        difModel.setFieldName("f_voltage");
        difModel.setFirstFieldValue("100.0");
        difModel.setLastFieldValue("200.0");
        difModel.setFirstDataTime("2026-01-01 00:00:00");
        difModel.setLastDataTime("2026-01-31 23:59:59");
        Map<String, List<TableDifDataModel>> fieldDataMap = Map.of("f_voltage", List.of(difModel));
        Map<String, Map<String, List<TableDifDataModel>>> tableDataMap = Map.of("t_dev001", fieldDataMap);
        when(accessDataService.findNodeDifHistoryListFeign(any(), any(), any(), any()))
                .thenReturn(tableDataMap);

        var result = deviceFeignService.findNodeDifHistoryListFeign(vo);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().containsKey("dev-001"));
        assertTrue(result.getData().get("dev-001").containsKey("voltage"));
    }

    @Test
    @DisplayName("findDeviceHistoryValueList-表数据不包含设备字段时返回空列表")
    void findDeviceHistoryValueList_tableNotInData_returnsEmptyList() {
        DeviceHistoryQueryVo vo = new DeviceHistoryQueryVo();
        vo.setDeviceIds(Set.of("dev-001"));
        vo.setFunctionLogos(Set.of("voltage"));
        vo.setStartTime("2026-01-01 00:00:00");
        vo.setEndTime("2026-01-31 23:59:59");

        DeviceFieldDto fieldDto = buildFieldDto("t_dev001", "f_voltage", "voltage");
        Map<String, List<DeviceFieldDto>> fieldMap = Map.of("dev-001", List.of(fieldDto));
        when(deviceService.queryDeviceFieldList(any(DeviceFieldQueryVo.class)))
                .thenReturn(ResponseResult.ok(fieldMap));

        // 表数据为空Map
        when(accessDataService.queryTableDataList(any(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyMap());

        var result = deviceFeignService.findDeviceHistoryValueList(vo);
        assertTrue(result.isSuccess());
        assertTrue(result.getData().containsKey("dev-001"));
        assertTrue(result.getData().get("dev-001").get("voltage").isEmpty());
    }
}
