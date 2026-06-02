package com.sunmax.crontab.service.feign;

import com.sunmax.common.dto.system.DataForwardDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "system-service")
//@FeignClient(value = "system-service-cbl",url = "http://121.41.109.130:60002")
@RestController
@RequestMapping("/system/feign/crontab")
public interface SystemService {

    @PostMapping("findUserInfoByIds")
    @ApiOperation("根据用户id查询用户信息")
    @ApiOperationSupport(order = 1)
    ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds);

    @PostMapping("getDataForwardList")
    @ApiOperation("查询数据转发列表数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<List<DataForwardDto>> getDataForwardList(@RequestParam(required = false) Integer protocolType, @RequestParam(required = false) Integer status);

    @PostMapping("findDataForwardByClientId")
    @ApiOperation("根据客户端id查询数据转发数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<DataForwardDto> findDataForwardByClientId(@RequestParam String clientId);

}
