package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.ConfigureFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/feign/protocol")
@Api(tags = "提供给协议管理服务调用的远程接口")
@ApiIgnore()
public class ProtocolFeignController {

    @Autowired
    private ConfigureFeignService configureFeignService;

    @PostMapping("getPlatformDataForwardList")
    @ApiOperation("根据类型查询平台数据转发")
    @ApiImplicitParam(name = "protocolCodes", value = "多个协议编号", dataType = "Set", required = true)
    @ApiOperationSupport(order = 1)
    public ResponseResult<List<PlatformDataForwardDto>> getPlatformDataForwardList(@RequestBody Set<String> protocolCodes) {
        return configureFeignService.getPlatformDataForwardList(protocolCodes);
    }

}
