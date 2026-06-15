package com.sunmax.crontab.service.feign;

import com.sunmax.common.dto.system.DataForwardDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 接收系统服务提供的接口
 */
@FeignClient(value = "system-service", path = "/system/feign/crontab")
//@FeignClient(value = "system-service-cbl",url = "http://121.41.109.130:60002")
public interface SystemService {

    @PostMapping("findUserInfoByIds")
    @Operation(summary = "根据用户id查询用户信息")
    
    ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds);

    @PostMapping("getDataForwardList")
    @Operation(summary = "查询数据转发列表数据")
    
    ResponseResult<List<DataForwardDto>> getDataForwardList(@RequestParam(required = false) Integer protocolType, @RequestParam(required = false) Integer status);

    @PostMapping("findDataForwardByClientId")
    @Operation(summary = "根据客户端id查询数据转发数据")
    
    ResponseResult<DataForwardDto> findDataForwardByClientId(@RequestParam String clientId);

}
