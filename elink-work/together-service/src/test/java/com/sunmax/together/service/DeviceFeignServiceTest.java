package com.sunmax.together.service;

import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.mapper.OrderRecordMapper;
import com.sunmax.together.model.OrderCountModel;
import com.sunmax.together.service.impl.DeviceFeignServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeviceFeignService 单元测试")
class DeviceFeignServiceTest {

    @Mock
    private OrderRecordMapper orderRecordMapper;

    @InjectMocks
    private DeviceFeignServiceImpl deviceFeignService;

    @Test
    @DisplayName("根据站点ID查询订单统计-无数据返回空Map")
    void findOrderRecordBySiteIds_noData_returnsEmptyMap() {
        List<String> siteIds = Arrays.asList("site-001", "site-002");
        when(orderRecordMapper.countOrderBySiteIds(siteIds, "2026-01-01", "2026-01-31")).thenReturn(Collections.emptyList());

        ResponseResult<Map<String, List<OrderCountDto>>> result = deviceFeignService.findOrderRecordListBySiteIds(siteIds, "2026-01-01", "2026-01-31");

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("根据站点ID查询订单统计-有充电数据返回正确统计")
    void findOrderRecordBySiteIds_withChargeData_returnsStats() {
        List<String> siteIds = Arrays.asList("site-001");
        OrderCountModel model = new OrderCountModel();
        model.setSiteId("site-001");
        model.setPileCode("PILE001");
        model.setRunMode(0);
        model.setTotalCount(10);
        model.setTotalQt(100.0);
        model.setTotalMoney(BigDecimal.valueOf(50.0));
        when(orderRecordMapper.countOrderBySiteIds(siteIds, "2026-01-01", "2026-01-31")).thenReturn(Collections.singletonList(model));

        ResponseResult<Map<String, List<OrderCountDto>>> result = deviceFeignService.findOrderRecordListBySiteIds(siteIds, "2026-01-01", "2026-01-31");

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
        assertTrue(result.getData().containsKey("site-001"));
        List<OrderCountDto> dtos = result.getData().get("site-001");
        assertEquals(10, dtos.get(0).getChargeCount());
    }

    @Test
    @DisplayName("根据站点ID查询订单统计-有放电数据返回正确统计")
    void findOrderRecordBySiteIds_withDischargeData_returnsStats() {
        List<String> siteIds = Arrays.asList("site-001");
        OrderCountModel model = new OrderCountModel();
        model.setSiteId("site-001");
        model.setPileCode("PILE001");
        model.setRunMode(1);
        model.setTotalCount(5);
        model.setTotalQt(50.0);
        model.setTotalMoney(BigDecimal.valueOf(25.0));
        when(orderRecordMapper.countOrderBySiteIds(siteIds, "2026-01-01", "2026-01-31")).thenReturn(Collections.singletonList(model));

        ResponseResult<Map<String, List<OrderCountDto>>> result = deviceFeignService.findOrderRecordListBySiteIds(siteIds, "2026-01-01", "2026-01-31");

        assertTrue(result.isSuccess());
        List<OrderCountDto> dtos = result.getData().get("site-001");
        assertEquals(5, dtos.get(0).getDischargeCount());
    }

    @Test
    @DisplayName("根据站点ID查询订单统计-多站点数据正确分组")
    void findOrderRecordBySiteIds_multiSite_correctGrouping() {
        List<String> siteIds = Arrays.asList("site-001", "site-002");

        OrderCountModel model1 = new OrderCountModel();
        model1.setSiteId("site-001");
        model1.setPileCode("PILE001");
        model1.setRunMode(0);
        model1.setTotalCount(10);
        model1.setTotalQt(100.0);
        model1.setTotalMoney(BigDecimal.valueOf(50.0));

        OrderCountModel model2 = new OrderCountModel();
        model2.setSiteId("site-002");
        model2.setPileCode("PILE002");
        model2.setRunMode(0);
        model2.setTotalCount(5);
        model2.setTotalQt(50.0);
        model2.setTotalMoney(BigDecimal.valueOf(25.0));

        when(orderRecordMapper.countOrderBySiteIds(siteIds, "2026-01-01", "2026-01-31"))
                .thenReturn(Arrays.asList(model1, model2));

        ResponseResult<Map<String, List<OrderCountDto>>> result = deviceFeignService.findOrderRecordListBySiteIds(siteIds, "2026-01-01", "2026-01-31");

        assertTrue(result.getData().containsKey("site-001"));
        assertTrue(result.getData().containsKey("site-002"));
    }

    @Test
    @DisplayName("根据电桩编码查询订单统计-无数据返回空Map")
    void findOrderRecordByPileCodes_noData_returnsEmptyMap() {
        List<String> pileCodes = Arrays.asList("PILE001");
        when(orderRecordMapper.countOrderByPileCodes(pileCodes, "2026-01-01", "2026-01-31")).thenReturn(Collections.emptyList());

        ResponseResult<Map<String, OrderCountDto>> result = deviceFeignService.findOrderRecordListByPileCodes(pileCodes, "2026-01-01", "2026-01-31");

        assertTrue(result.isSuccess());
        assertTrue(result.getData().isEmpty());
    }

    @Test
    @DisplayName("根据电桩编码查询订单统计-有充电数据返回统计")
    void findOrderRecordByPileCodes_withData_returnsStats() {
        List<String> pileCodes = Arrays.asList("PILE001");
        OrderCountModel model = new OrderCountModel();
        model.setPileCode("PILE001");
        model.setRunMode(0);
        model.setTotalCount(10);
        model.setTotalQt(100.0);
        model.setTotalMoney(BigDecimal.valueOf(50.0));
        when(orderRecordMapper.countOrderByPileCodes(pileCodes, "2026-01-01", "2026-01-31")).thenReturn(Collections.singletonList(model));

        ResponseResult<Map<String, OrderCountDto>> result = deviceFeignService.findOrderRecordListByPileCodes(pileCodes, "2026-01-01", "2026-01-31");

        assertTrue(result.isSuccess());
        assertFalse(result.getData().isEmpty());
        assertTrue(result.getData().containsKey("PILE001"));
    }
}
