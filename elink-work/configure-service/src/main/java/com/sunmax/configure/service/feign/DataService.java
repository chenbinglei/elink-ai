package com.sunmax.configure.service.feign;

import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 接收定时服务提供的接口
 */
@FeignClient(value = "data-service")
@RestController
@RequestMapping("/data/feign/configure")
public interface DataService {

    @PostMapping("findNodeDifHistoryListFeign")
    @ApiOperation("查询设备功能点指定时间段内的last和first历史数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo);

}
