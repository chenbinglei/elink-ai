package com.sunmax.protocol.controller;


import com.sunmax.common.dto.protocol.ControlResponseDto;
import com.sunmax.common.dto.protocol.StationInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.ControlResponseVo;
import com.sunmax.common.vo.protocol.ControlStopVo;
import com.sunmax.protocol.service.DispatchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/dispatch")
@Tag(name = "电桩调度管理")
@Slf4j
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;

    @PostMapping("queryStationInfo")
    @Operation(summary = "根据场站id查询场站信息")
    @Parameter(name = "stationId", description = "站点id")
    
    public ResponseResult<List<StationInfoDto>> queryStationInfo(@RequestParam Long stationId) {
        return dispatchService.queryStationInfo(stationId);
    }

    @PostMapping("demandControlResponse")
    @Operation(summary = "下发需求响应命令")
    
    public ResponseResult<ControlResponseDto> demandControlResponse(@RequestBody ControlResponseVo controlResponseVo) {
        return dispatchService.demandControlResponse(controlResponseVo);
    }

    @PostMapping("demandControlStop")
    @Operation(summary = "下发需求响应终止命令")
    
    public ResponseResult<ControlResponseDto> demandControlStop(@RequestBody ControlStopVo controlStopVo) {
        return dispatchService.demandControlStop(controlStopVo);
    }

}
