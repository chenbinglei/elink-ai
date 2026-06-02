package com.sunmax.configure.service.feign;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.dto.system.StorageDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "system-service")
@RestController
@RequestMapping("/system/feign/configure")
public interface SystemService {

    @PostMapping("getPlatformDataForward")
    @ApiOperation("根据平台id和协议标识查询平台数据转发")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platformId", value = "平台运营商id", dataType = "String", required = true),
            @ApiImplicitParam(name = "protocolCode", value = "协议标识", dataType = "String", required = true)
    })
    @ApiOperationSupport(order = 1)
    ResponseResult<PlatformDataForwardDto> getPlatformDataForward(@RequestParam String platformId, @RequestParam String protocolCode);

    @PostMapping("getPlatformDataForwardList")
    @ApiOperation("根据多个协议标识查询平台数据转发")
    @ApiImplicitParam(name = "protocolCodes", value = "多个协议标识", dataType = "Set", required = true)
    @ApiOperationSupport(order = 2)
    ResponseResult<List<PlatformDataForwardDto>> getPlatformDataForwardList(@RequestBody Set<String> protocolCodes);

    @PostMapping("getStorageDataForwardList")
    @ApiOperation("根据多个协议标识查询储能平台数据转发")
    @ApiImplicitParam(name = "protocolCodes", value = "多个协议标识", dataType = "Set", required = true)
    @ApiOperationSupport(order = 3)
    ResponseResult<List<StorageDataForwardDto>> getStorageDataForwardList(@RequestBody Set<String> protocolCodes);

}
