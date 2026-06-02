package com.sunmax.configure.service.feign;

import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 接收定时服务提供的接口
 */
@FeignClient(value = "scrontab-service")
@RestController
@RequestMapping("/scrontab/feign/configure")
public interface CrontabService {

    @PostMapping("findComputeNodeListByDeviceId")
    @ApiOperation("根据站点/设备id查询计算节点列表")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParam(name = "deviceId", value = "站点/设备唯一id", dataType = "String", required = true)
    ResponseResult<List<ComputeNodeListDto>> findComputeNodeListByDeviceId(@RequestParam String deviceId);
}
