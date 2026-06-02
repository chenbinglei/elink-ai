package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.dto.system.StorageDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.ConfigureFeignService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/feign/configure")
@Api(tags = "提供给定时任务服务调用的远程接口")
@ApiIgnore()
public class ConfigureFeignController {

    @Autowired
    private ConfigureFeignService configureFeignService;

    @PostMapping("getPlatformDataForward")
    @ApiOperation("根据平台id和协议标识查询平台数据转发")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "platformId", value = "平台运营商id", dataType = "String", required = true),
            @ApiImplicitParam(name = "protocolCode", value = "协议标识", dataType = "String", required = true)
    })
    @ApiOperationSupport(order = 1)
    public ResponseResult<PlatformDataForwardDto> getPlatformDataForward(@RequestParam String platformId, @RequestParam String protocolCode) {
        return configureFeignService.getPlatformDataForward(platformId, protocolCode);
    }

    @PostMapping("getPlatformDataForwardList")
    @ApiOperation("根据多个协议标识查询平台数据转发")
    @ApiImplicitParam(name = "protocolCodes", value = "多个协议编号", dataType = "Set", required = true)
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<PlatformDataForwardDto>> getPlatformDataForwardList(@RequestBody Set<String> protocolCodes) {
        return configureFeignService.getPlatformDataForwardList(protocolCodes);
    }

    @PostMapping("getStorageDataForwardList")
    @ApiOperation("根据多个协议标识查询储能平台数据转发")
    @ApiImplicitParam(name = "protocolCodes", value = "多个协议标识", dataType = "Set", required = true)
    @ApiOperationSupport(order = 3)
    public ResponseResult<List<StorageDataForwardDto>> getStorageDataForwardList(@RequestBody Set<String> protocolCodes) {
        return configureFeignService.getStorageDataForwardList(protocolCodes);
    }

}
