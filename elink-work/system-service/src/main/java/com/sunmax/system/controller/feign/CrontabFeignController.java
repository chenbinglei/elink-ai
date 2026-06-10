package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.DataForwardDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.ConfigureCenterService;
import com.sunmax.system.service.DeviceFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;

/**
 * @Author: yqz
 * @注释: 提供给定时任务服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/crontab")
@Tag(name = "提供给定时任务服务调用的远程接口")
@Hidden()
public class CrontabFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @Autowired
    private ConfigureCenterService configureCenterService;

    @PostMapping("findUserInfoByIds")
    @Operation(summary = "根据用户id查询用户信息")
    
    public ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds) {
        return deviceFeignService.findUserInfoByIdsFeign(userIds);
    }

    @PostMapping("getDataForwardList")
    @Operation(summary = "查询数据转发列表数据")
    
    public ResponseResult<List<DataForwardDto>> getDataForwardList(@RequestParam(required = false) Integer protocolType,
                                                                   @RequestParam(required = false) Integer status) {
        return configureCenterService.getDataForwardList(protocolType, status);
    }

    @PostMapping("findDataForwardByClientId")
    @Operation(summary = "根据客户端id查询数据转发数据")
    
    public ResponseResult<DataForwardDto> findDataForwardByClientId(@RequestParam String clientId) {
        return configureCenterService.findDataForwardByClientId(clientId);
    }
}
