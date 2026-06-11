package com.sunmax.common.feign.configure;

import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收定时服务提供的接口
 */
@FeignClient(value = "data-service", path = "/data/feign/configure", fallbackFactory = GenericFeignFallbackFactory.class)
public interface ConfigureDataFeignClient {

    @PostMapping("findNodeDifHistoryListFeign")
    @Operation(summary = "查询设备功能点指定时间段内的last和first历史数据")
    
    ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo);

}
