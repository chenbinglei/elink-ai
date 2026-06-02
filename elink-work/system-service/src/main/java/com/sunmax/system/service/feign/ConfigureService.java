package com.sunmax.system.service.feign;

import com.sunmax.common.dto.system.PlatformDataForwardDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "configure-service")
@RestController
@RequestMapping("/configure/feign/system")
public interface ConfigureService {

    @PostMapping("updateHttpSiteForward")
    @ApiOperation("更新站点转发平台运营商配置")
    @ApiOperationSupport(order = 1)
    ResponseResult<Boolean> updateHttpSiteForward(@RequestBody PlatformDataForwardDto platformDataForward);

}
