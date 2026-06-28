package com.sunmax.data.service;

import com.sunmax.data.mapper.tdengine.DataStoreMapper;
import com.sunmax.data.service.impl.AccessDataServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccessDataService 单元测试")
class AccessDataServiceTest {

    @Mock
    private DataStoreMapper dataStoreMapper;

    @Mock
    private Executor asyncExecutor;

    @InjectMocks
    private AccessDataServiceImpl accessDataService;

    @Test
    @DisplayName("批量插入数据-空列表时仍会检查并创建表")
    void batchInsertTableData_emptyList_stillChecksTable() {
        when(dataStoreMapper.findTableIfExists("test_table")).thenReturn(Collections.emptyMap());
        when(dataStoreMapper.findFieldNameTypeList("test_table")).thenReturn(Collections.emptyList());

        accessDataService.batchInsertTableData("test_table", System.currentTimeMillis(), Collections.emptyList());

        // 空列表时代码仍会检查表是否存在，不存在则创建空表
        verify(dataStoreMapper).findTableIfExists("test_table");
    }

    @Test
    @DisplayName("批量插入数据-表存在时不创建新表")
    void batchInsertTableData_tableExists_noCreateTable() {
        when(dataStoreMapper.findTableIfExists("test_table")).thenReturn(Map.of("table_name", "test_table"));
        when(dataStoreMapper.findFieldNameTypeList("test_table")).thenReturn(Collections.emptyList());

        accessDataService.batchInsertTableData("test_table", System.currentTimeMillis(), Collections.emptyList());

        verify(dataStoreMapper, never()).createTable(anyString(), any());
    }

    @Test
    @DisplayName("批量插入数据-表不存在时创建新表")
    void batchInsertTableData_tableNotExists_createsTable() {
        when(dataStoreMapper.findTableIfExists("test_table")).thenReturn(Collections.emptyMap());
        when(dataStoreMapper.findFieldNameTypeList("test_table")).thenReturn(Collections.emptyList());

        accessDataService.batchInsertTableData("test_table", System.currentTimeMillis(), Collections.emptyList());

        verify(dataStoreMapper).createTable(anyString(), any());
    }
}
