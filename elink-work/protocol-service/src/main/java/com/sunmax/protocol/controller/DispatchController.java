package com.sunmax.protocol.controller;


import com.sunmax.common.dto.protocol.ControlResponseDto;
import com.sunmax.common.dto.protocol.StationInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.ControlResponseVo;
import com.sunmax.common.vo.protocol.ControlStopVo;
import com.sunmax.protocol.service.DispatchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/dispatch")
@Api(tags = "电桩调度管理")
@Slf4j
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    @PostMapping("queryStationInfo")
    @ApiOperation("根据场站id查询场站信息")
    @ApiImplicitParam(name = "stationId", value = "站点id", dataType = "Long", required = true)
    @ApiOperationSupport(order = 1)
    public ResponseResult<List<StationInfoDto>> queryStationInfo(@RequestParam Long stationId) {
        return dispatchService.queryStationInfo(stationId);
    }

    @PostMapping("demandControlResponse")
    @ApiOperation("下发需求响应命令")
    @ApiOperationSupport(order = 2)
    public ResponseResult<ControlResponseDto> demandControlResponse(@RequestBody ControlResponseVo controlResponseVo) {
        return dispatchService.demandControlResponse(controlResponseVo);
    }

    @PostMapping("demandControlStop")
    @ApiOperation("下发需求响应终止命令")
    @ApiOperationSupport(order = 3)
    public ResponseResult<ControlResponseDto> demandControlStop(@RequestBody ControlStopVo controlStopVo) {
        return dispatchService.demandControlStop(controlStopVo);
    }

}
