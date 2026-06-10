package com.sunmax.together.controller;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.service.LargeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("large")
@Tag(name = "大屏管理控制层")
public class LargeController {

    @Autowired
    private LargeService largeService;

    @PostMapping("largeSetting")
    @Operation(summary = "大屏设置")
    @WebLog("大屏-用户大屏设置")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "settingData", description = "数据设置")
    })
    public ResponseResult<Void> largeSetting(String userId, String settingData) {
        return largeService.largeSetting(userId, settingData);
    }

    @PostMapping("queryLargeSetting")
    @Operation(summary = "查询大屏设置")
    
    @Parameter(name = "userId", description = "用户id")
    public ResponseResult<String> queryLargeSetting(String userId) {
        return largeService.queryLargeSetting(userId);
    }

}
