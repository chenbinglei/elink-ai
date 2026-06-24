package com.sunmax.data.service;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.data.mapper.tdengine.DataStoreMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DeviceFeignService 单元测试")
class DeviceFeignServiceTest {

    @Mock
    private DataStoreMapper dataStoreMapper;

    @Mock
    private DeviceService deviceService;

    @Mock
    private AccessDataService accessDataService;

    @InjectMocks
    private DeviceFeignServiceImpl deviceFeignService;

    @Test
    @DisplayName("删除所有数据存储表-空集合直接返回成功")
    void deleteAllDataStoreTable_emptySet_returnsSuccess() {
        Set<String> tableNames = new HashSet<>();
        ResponseResult<Void> result = deviceFeignService.deleteAllDataStoreTable(tableNames);

        assertTrue(result.isSuccess());
        verify(dataStoreMapper, never()).deleteTable(any());
    }

    @Test
    @DisplayName("删除所有数据存储表-有表名时逐个删除")
    void deleteAllDataStoreTable_withTableNames_deletesAll() {
        Set<String> tableNames = new HashSet<>();
        tableNames.add("table_001");
        tableNames.add("table_002");

        ResponseResult<Void> result = deviceFeignService.deleteAllDataStoreTable(tableNames);

        assertTrue(result.isSuccess());
        verify(dataStoreMapper, times(2)).deleteTable(any());
    }

    @Test
    @DisplayName("删除所有数据存储表-单个表名时删除一次")
    void deleteAllDataStoreTable_singleTableName_deletesOnce() {
        Set<String> tableNames = new HashSet<>();
        tableNames.add("table_001");

        ResponseResult<Void> result = deviceFeignService.deleteAllDataStoreTable(tableNames);

        assertTrue(result.isSuccess());
        verify(dataStoreMapper, times(1)).deleteTable("table_001");
    }
}
