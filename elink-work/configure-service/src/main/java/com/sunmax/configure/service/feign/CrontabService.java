package com.sunmax.configure.service.feign;

import com.sunmax.common.dto.crontab.ComputeNodeListDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 接收定时服务提供的接口
 */
@FeignClient(value = "crontab-service", path = "/crontab/feign/configure")
public interface CrontabService {

    @PostMapping("findComputeNodeListByDeviceId")
    @Operation(summary = "根据站点/设备id查询计算节点列表")
    @Parameter(name = "deviceId", description = "站点/设备唯一id")
    ResponseResult<List<ComputeNodeListDto>> findComputeNodeListByDeviceId(@RequestParam String deviceId);
}
