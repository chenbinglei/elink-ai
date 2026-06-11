package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.ConfigureFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Set;
import com.sunmax.common.feign.protocol.ProtocolSystemFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/protocol")
@Tag(name = "提供给协议管理服务调用的远程接口")
@Hidden()
public class ProtocolFeignEndpoint implements ProtocolSystemFeignClient {

    @Autowired
    private ConfigureFeignService configureFeignService;

    @PostMapping("getPlatformDataForwardList")
    @Operation(summary = "根据类型查询平台数据转发")
    @Parameter(name = "protocolCodes", description = "多个协议编号")
    
    public ResponseResult<List<PlatformDataForwardDto>> getPlatformDataForwardList(@RequestBody Set<String> protocolCodes) {
        return configureFeignService.getPlatformDataForwardList(protocolCodes);
    }

}
