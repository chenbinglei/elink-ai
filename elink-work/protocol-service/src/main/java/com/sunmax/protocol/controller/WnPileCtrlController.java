package com.sunmax.protocol.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.protocol.service.PlatformPileCtrlService;
import com.sunmax.protocol.vo.PlatformRequestVo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 皖小能电桩控制层
 */
@RestController
@CrossOrigin
@RequestMapping("/v1/wn/pileCtrl")
@Tag(name = "皖能电桩控制层")
@Slf4j
public class WnPileCtrlController {

    @Autowired
    private PlatformPileCtrlService platformPileCtrlService;

    //皖能电桩平台ID 一开始对接没有对 默认给的值
    private static final String PLATFORM_ID = "148949589";

    @PostMapping("getPileRateTemplate")
    @Operation(summary = "根据充电桩编码查询计费数据")
    
    public ResponseResult<String> getPileRateTemplate(@RequestBody PlatformRequestVo requestVo) {
        requestVo.setPlatformId(PLATFORM_ID);
        return platformPileCtrlService.getPileRateTemplate(requestVo);
    }

    @PostMapping("pileStart")
    @Operation(summary = "启动充电桩")
    
    public ResponseResult<String> pileStart(@RequestBody PlatformRequestVo requestVo) {
        requestVo.setPlatformId(PLATFORM_ID);
        return platformPileCtrlService.pileStart(requestVo);
    }

    @PostMapping("pileStop")
    @Operation(summary = "停止充电桩")
    
    public ResponseResult<String> pileStop(@RequestBody PlatformRequestVo requestVo) {
        requestVo.setPlatformId(PLATFORM_ID);
        return platformPileCtrlService.pileStop(requestVo);
    }

}
