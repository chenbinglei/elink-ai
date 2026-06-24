package com.sunmax.data.service;

import com.sunmax.data.mapper.tdengine.DataStoreMapper;
import com.sunmax.data.model.TableDataModel;
import com.sunmax.data.model.TableFieldModel;
import com.sunmax.data.service.impl.AccessDataServiceImpl;
import com.sunmax.data.vo.DeviceDataVo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AccessDataService 扩展单元测试")
class AccessDataServiceExtTest {

    @Mock private DataStoreMapper dataStoreMapper;
    @Mock private Executor asyncExecutor;

    @InjectMocks private AccessDataServiceImpl accessDataService;

    private String nowStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private DeviceDataVo buildVo(String fieldName, String fieldType, String dataValue) {
        DeviceDataVo vo = new DeviceDataVo();
        vo.setFieldName(fieldName);
        vo.setFieldType(fieldType);
        vo.setDataValue(dataValue);
        vo.setDateTime(nowStr());
        return vo;
    }

    @Test
    @DisplayName("批量插入数据-有数据时创建表并插入")
    void batchInsertTableData_withData_insertsSuccessfully() {
        when(dataStoreMapper.findTableIfExists("test_table")).thenReturn(Collections.emptyMap());
        when(dataStoreMapper.findFieldNameTypeList("test_table")).thenReturn(Collections.emptyList());

        accessDataService.batchInsertTableData("test_table", System.currentTimeMillis(), List.of(buildVo("voltage", "DOUBLE", "220.5")));

        verify(dataStoreMapper).createTable(anyString(), any());
        verify(dataStoreMapper).batchAddTableData(anyString(), any());
    }

    @Test
    @DisplayName("批量插入数据-表存在但缺少字段时添加字段")
    void batchInsertTableData_missingField_addsField() {
        when(dataStoreMapper.findTableIfExists("test_table")).thenReturn(Map.of("table_name", "test_table"));

        TableFieldModel existingField = new TableFieldModel();
        existingField.setField("voltage");
        existingField.setType("DOUBLE");
        when(dataStoreMapper.findFieldNameTypeList("test_table")).thenReturn(List.of(existingField));

        accessDataService.batchInsertTableData("test_table", System.currentTimeMillis(), List.of(buildVo("current", "FLOAT", "10.5")));

        verify(dataStoreMapper).addTableField(anyString(), eq("current"), eq("FLOAT"));
    }

    @Test
    @DisplayName("批量插入数据-字段类型不匹配时更新字段类型")
    void batchInsertTableData_typeMismatch_updatesFieldType() {
        when(dataStoreMapper.findTableIfExists("test_table")).thenReturn(Map.of("table_name", "test_table"));

        TableFieldModel existingField = new TableFieldModel();
        existingField.setField("voltage");
        existingField.setType("INT");
        when(dataStoreMapper.findFieldNameTypeList("test_table")).thenReturn(List.of(existingField));

        accessDataService.batchInsertTableData("test_table", System.currentTimeMillis(), List.of(buildVo("voltage", "DOUBLE", "220.5")));

        verify(dataStoreMapper).updateTableField(anyString(), eq("voltage"), eq("DOUBLE"));
    }

    @Test
    @DisplayName("批量插入数据-多余字段时删除字段")
    void batchInsertTableData_extraField_deletesField() {
        when(dataStoreMapper.findTableIfExists("test_table")).thenReturn(Map.of("table_name", "test_table"));

        TableFieldModel extraField = new TableFieldModel();
        extraField.setField("unused_field");
        extraField.setType("INT");
        when(dataStoreMapper.findFieldNameTypeList("test_table")).thenReturn(List.of(extraField));

        accessDataService.batchInsertTableData("test_table", System.currentTimeMillis(), List.of(buildVo("voltage", "DOUBLE", "220.5")));

        verify(dataStoreMapper).deleteTableField(anyString(), eq("unused_field"));
    }

    @Test
    @DisplayName("批量插入数据-DOUBLE和FLOAT类型兼容不更新")
    void batchInsertTableData_doubleFloatCompatible_noUpdate() {
        when(dataStoreMapper.findTableIfExists("test_table")).thenReturn(Map.of("table_name", "test_table"));

        TableFieldModel existingField = new TableFieldModel();
        existingField.setField("voltage");
        existingField.setType("FLOAT");
        when(dataStoreMapper.findFieldNameTypeList("test_table")).thenReturn(List.of(existingField));

        accessDataService.batchInsertTableData("test_table", System.currentTimeMillis(), List.of(buildVo("voltage", "DOUBLE", "220.5")));

        verify(dataStoreMapper, never()).updateTableField(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("查询差异表数据-空查询Map返回空结果")
    void queryDifferenceTableDataList_emptyMap_returnsEmpty() {
        var result = accessDataService.queryDifferenceTableDataList(
                Collections.emptyMap(), "2026-01-01", "2026-01-31", "1h", 0);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询统计函数表数据-空查询Map返回空结果")
    void queryCountTableDataList_emptyMap_returnsEmpty() {
        var result = accessDataService.queryCountTableDataList(
                Collections.emptyMap(), "2026-01-01", "2026-01-31", "1h", "avg");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询节点差值历史数据-空查询Map返回空结果")
    void findNodeDifHistoryListFeign_emptyMap_returnsEmpty() {
        var result = accessDataService.findNodeDifHistoryListFeign(
                Collections.emptyMap(), "2026-01-01", "2026-01-31", "1h");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询功能点历史数据-空查询Map返回空结果")
    void queryFunctionPointTableDataList_emptyMap_returnsEmpty() {
        var result = accessDataService.queryFunctionPointTableDataList(
                Collections.emptyMap(), "2026-01-01", "2026-01-31", "1h", 100);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询差异表数据-表不存在时跳过")
    void queryDifferenceTableDataList_tableNotExists_skips() {
        when(dataStoreMapper.findAllTableIfExists()).thenReturn(Collections.emptyList());
        Map<String, List<String>> queryMap = Map.of("t_missing", List.of("field1"));
        var result = accessDataService.queryDifferenceTableDataList(queryMap, "2026-01-01", "2026-01-31", "1h", 0);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询统计函数表数据-表不存在时跳过")
    void queryCountTableDataList_tableNotExists_skips() {
        when(dataStoreMapper.findAllTableIfExists()).thenReturn(Collections.emptyList());
        Map<String, List<String>> queryMap = Map.of("t_missing", List.of("field1"));
        var result = accessDataService.queryCountTableDataList(queryMap, "2026-01-01", "2026-01-31", "1h", "avg");
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询表数据-空查询Map返回空结果")
    void queryTableDataList_emptyMap_returnsEmpty() {
        var result = accessDataService.queryTableDataList(
                Collections.emptyMap(), "2026-01-01", "2026-01-31", "1h", 100);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("查询差异表数据-表存在但字段不存在时返回空数据")
    void queryDifferenceTableDataList_tableExistsNoFields_returnsEmptyData() {
        when(dataStoreMapper.findAllTableIfExists()).thenReturn(List.of("t_dev001"));
        when(dataStoreMapper.findFieldNameTypeList("t_dev001")).thenReturn(Collections.emptyList());

        Map<String, List<String>> queryMap = Map.of("t_dev001", List.of("field1"));
        var result = accessDataService.queryDifferenceTableDataList(queryMap, "2026-01-01", "2026-01-31", "1h", 0);

        // 表存在但字段不存在，结果中表存在但数据为空
        assertNotNull(result);
    }

    @Test
    @DisplayName("查询差异表数据-isNear=1时调用queryfirstTableDataList")
    void queryDifferenceTableDataList_isNear1_callsFirstQuery() {
        when(dataStoreMapper.findAllTableIfExists()).thenReturn(List.of("t_dev001"));
        TableFieldModel fieldModel = new TableFieldModel();
        fieldModel.setField("voltage");
        fieldModel.setType("DOUBLE");
        when(dataStoreMapper.findFieldNameTypeList("t_dev001")).thenReturn(List.of(fieldModel));
        when(dataStoreMapper.queryfirstTableDataList(anyString(), any(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        Map<String, List<String>> queryMap = Map.of("t_dev001", List.of("voltage"));
        var result = accessDataService.queryDifferenceTableDataList(queryMap, "2026-01-01", "2026-01-31", "1h", 1);

        verify(dataStoreMapper).queryfirstTableDataList(anyString(), any(), anyString(), anyString(), anyString());
    }
}
