package com.sunmax.system.controller.feign;

import com.sunmax.common.dto.system.DataForwardDto;
import com.sunmax.common.dto.system.UserDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.system.service.ConfigureCenterService;
import com.sunmax.system.service.DeviceFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

/**
 * @Author: yqz
 * @注释: 提供给定时任务服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/crontab")
@Api(tags = "提供给定时任务服务调用的远程接口")
@ApiIgnore()
public class CrontabFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @Autowired
    private ConfigureCenterService configureCenterService;

    @PostMapping("findUserInfoByIds")
    @ApiOperation("根据用户id查询用户信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, UserDto>> findUserInfoByIdsFeign(@RequestBody List<String> userIds) {
        return deviceFeignService.findUserInfoByIdsFeign(userIds);
    }

    @PostMapping("getDataForwardList")
    @ApiOperation("查询数据转发列表数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<DataForwardDto>> getDataForwardList(@RequestParam(required = false) Integer protocolType,
                                                                   @RequestParam(required = false) Integer status) {
        return configureCenterService.getDataForwardList(protocolType, status);
    }

    @PostMapping("findDataForwardByClientId")
    @ApiOperation("根据客户端id查询数据转发数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<DataForwardDto> findDataForwardByClientId(@RequestParam String clientId) {
        return configureCenterService.findDataForwardByClientId(clientId);
    }
}
