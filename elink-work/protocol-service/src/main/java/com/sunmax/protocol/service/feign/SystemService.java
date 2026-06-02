package com.sunmax.protocol.service.feign;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "system-service")
@RestController
@RequestMapping("/system/feign/protocol")
public interface SystemService {

    @PostMapping("getPlatformDataForwardList")
    @ApiOperation("根据多个协议标识查询平台数据转发")
    @ApiImplicitParam(name = "protocolCodes", value = "多个协议标识", dataType = "Set", required = true)
    @ApiOperationSupport(order = 1)
    ResponseResult<List<PlatformDataForwardDto>> getPlatformDataForwardList(@RequestBody Set<String> protocolCodes);

}
