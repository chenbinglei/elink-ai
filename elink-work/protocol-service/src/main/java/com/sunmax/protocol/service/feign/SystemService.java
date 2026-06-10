package com.sunmax.protocol.service.feign;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "system-service", path = "/system/feign/protocol")
public interface SystemService {

    @PostMapping("getPlatformDataForwardList")
    @Operation(summary = "根据多个协议标识查询平台数据转发")
    @Parameter(name = "protocolCodes", description = "多个协议标识")
    
    ResponseResult<List<PlatformDataForwardDto>> getPlatformDataForwardList(@RequestBody Set<String> protocolCodes);

}
