package com.sunmax.devops.controller;

import com.sunmax.common.dto.device.AssetTypeDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.devops.dto.AlarmResultDto;
import com.sunmax.devops.service.AlarmService;
import com.sunmax.devops.vo.AlarmQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("alarm")
@Api(tags = "运维告警管理控制层")
public class AlarmController {

    @Autowired
    private AlarmService alarmService;

    @PostMapping("getAssetTypeList")
    @ApiOperation("获取资产分类列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<List<AssetTypeDto>> getAssetTypeList() {
        return alarmService.getAssetTypeList();
    }

    @PostMapping("queryAlarmList")
    @ApiOperation("查询告警列表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<AlarmResultDto> queryAlarmList(AlarmQueryVo alarmQueryVo) {
        return alarmService.queryAlarmList(alarmQueryVo);
    }

}
