package com.sunmax.protocol.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.protocol.service.PlatformPileCtrlService;
import com.sunmax.protocol.service.VirtualPileCtrlService;
import com.sunmax.protocol.vo.PlatformRequestVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/v1/virtual/pileCtrl")
@Tag(name = "第三方虚拟电厂平台电桩控制层")
@Slf4j
public class VirtualPileCtrlController {

    @Autowired
    private PlatformPileCtrlService platformPileCtrlService;

    @Autowired
    private VirtualPileCtrlService virtualPileCtrlService;

    @PostMapping("powerCtrl")
    @Operation(summary = "功率控制")
    
    public ResponseResult<String> powerCtrl(@RequestBody PlatformRequestVo requestVo) {
        return platformPileCtrlService.powerCtrl(requestVo);
    }

    @PostMapping("getPileRealList")
    @Operation(summary = "获取电桩实时数据列表")
    
    public ResponseResult<String> getPileRealList(@RequestBody PlatformRequestVo requestVo) {
        return virtualPileCtrlService.getPileRealList(requestVo);
    }

    @PostMapping("getPileQtList")
    @Operation(summary = "获取电桩电量数据列表")
    
    public ResponseResult<String> getPileQtList(@RequestBody PlatformRequestVo requestVo) {
        return virtualPileCtrlService.getPileQtList(requestVo);
    }

}
