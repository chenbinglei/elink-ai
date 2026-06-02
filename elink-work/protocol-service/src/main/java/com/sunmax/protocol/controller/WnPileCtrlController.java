package com.sunmax.protocol.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.protocol.service.PlatformPileCtrlService;
import com.sunmax.protocol.vo.PlatformRequestVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 皖小能电桩控制层
 */
@RestController
@CrossOrigin
@RequestMapping("/v1/wn/pileCtrl")
@Api(tags = "皖能电桩控制层")
@Slf4j
public class WnPileCtrlController {

    @Autowired
    private PlatformPileCtrlService platformPileCtrlService;

    //皖能电桩平台ID 一开始对接没有对 默认给的值
    private static final String PLATFORM_ID = "148949589";

    @PostMapping("getPileRateTemplate")
    @ApiOperation("根据充电桩编码查询计费数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<String> getPileRateTemplate(@RequestBody PlatformRequestVo requestVo) {
        requestVo.setPlatformId(PLATFORM_ID);
        return platformPileCtrlService.getPileRateTemplate(requestVo);
    }

    @PostMapping("pileStart")
    @ApiOperation("启动充电桩")
    @ApiOperationSupport(order = 2)
    public ResponseResult<String> pileStart(@RequestBody PlatformRequestVo requestVo) {
        requestVo.setPlatformId(PLATFORM_ID);
        return platformPileCtrlService.pileStart(requestVo);
    }

    @PostMapping("pileStop")
    @ApiOperation("停止充电桩")
    @ApiOperationSupport(order = 3)
    public ResponseResult<String> pileStop(@RequestBody PlatformRequestVo requestVo) {
        requestVo.setPlatformId(PLATFORM_ID);
        return platformPileCtrlService.pileStop(requestVo);
    }

}
