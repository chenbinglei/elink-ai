package com.sunmax.auth.service.feign;

import com.sunmax.common.dto.system.AppletDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "system-service")
@RestController
@RequestMapping("/system/feign/sauth")
public interface SystemService {

    @PostMapping("findByAppletCodeAndAppletType")
    @ApiOperation("根据小程序编码和类型查询小程序信息")
    @ApiOperationSupport(order = 1)
    ResponseResult<AppletDto> findByAppletCodeAndAppletType(@RequestParam String appletCode, @RequestParam Integer appletType);
}
