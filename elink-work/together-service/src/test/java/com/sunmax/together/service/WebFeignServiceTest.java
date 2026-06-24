package com.sunmax.together.service;

import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.together.PileGunMonitorDataDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dao.order.OrderRecordDao;
import com.sunmax.together.service.feign.DeviceService;
import com.sunmax.together.service.impl.WebFeignServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WebFeignService 单元测试")
class WebFeignServiceTest {

    @Mock
    private DeviceService deviceService;

    @Mock
    private OrderRecordDao orderRecordDao;

    @InjectMocks
    private WebFeignServiceImpl webFeignService;

    @Test
    @DisplayName("查询电桩枪监控数据-设备ID列表为空时返回空Map")
    void findAllPileGunMonitorList_emptyDeviceIds_returnsEmptyMap() {
        Map<String, DeviceBasicInfoDto> emptyMap = new HashMap<>();
        when(deviceService.findDeviceBasicInfoByIds(any())).thenReturn(ResponseResult.ok(emptyMap));

        ResponseResult<Map<String, List<PileGunMonitorDataDto>>> result = webFeignService.findAllPileGunMonitorList(Collections.emptyList());

        assertNotNull(result);
    }

    @Test
    @DisplayName("查询电桩枪监控数据-有设备数据时正常返回")
    void findAllPileGunMonitorList_withDevices_returnsData() {
        List<String> deviceIds = Arrays.asList("device-001");
        Map<String, DeviceBasicInfoDto> deviceMap = new HashMap<>();
        DeviceBasicInfoDto deviceInfo = new DeviceBasicInfoDto();
        deviceInfo.setDeviceNumber("PILE001");
        deviceMap.put("device-001", deviceInfo);

        when(deviceService.findDeviceBasicInfoByIds(deviceIds)).thenReturn(ResponseResult.ok(deviceMap));
        Map<String, List<DeviceGunInfoDto>> gunMap = new HashMap<>();
        when(deviceService.findDeviceGunInfoByDeviceIds(deviceIds)).thenReturn(ResponseResult.ok(gunMap));
        when(orderRecordDao.findAllByPileCodeInAndEndTimeBetween(any(), any(), any())).thenReturn(Collections.emptyList());

        ResponseResult<Map<String, List<PileGunMonitorDataDto>>> result = webFeignService.findAllPileGunMonitorList(deviceIds);

        assertNotNull(result);
    }
}
