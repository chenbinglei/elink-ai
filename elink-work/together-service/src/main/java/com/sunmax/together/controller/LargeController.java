package com.sunmax.together.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.service.LargeService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("large")
@Api(tags = "大屏管理控制层")
public class LargeController {

    @Autowired
    private LargeService largeService;

    @PostMapping("largeSetting")
    @ApiOperation("大屏设置")
    @WebLog("大屏-用户大屏设置")
    @ApiOperationSupport(order = 1)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", dataType = "String", required = true),
            @ApiImplicitParam(name = "settingData", value = "数据设置", dataType = "String", required = true)
    })
    public ResponseResult<Void> largeSetting(String userId, String settingData) {
        return largeService.largeSetting(userId, settingData);
    }

    @PostMapping("queryLargeSetting")
    @ApiOperation("查询大屏设置")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", dataType = "String", required = true)
    public ResponseResult<String> queryLargeSetting(String userId) {
        return largeService.queryLargeSetting(userId);
    }

}
