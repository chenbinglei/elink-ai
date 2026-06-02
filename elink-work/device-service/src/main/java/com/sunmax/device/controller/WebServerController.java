package com.sunmax.device.controller;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.webserver.SiteCountDto;
import com.sunmax.device.dto.webserver.SiteLedgerDto;
import com.sunmax.device.service.WebServerService;
import com.sunmax.device.vo.webserver.CountQueryVo;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("webServer")
@Api(tags = "提供web服务调用的接口")
public class WebServerController {

    @Autowired
    private WebServerService webServerService;

//    @PostMapping("findDeviceListByIdsAndType")
//    @ApiOperation("根据多个id和类型和查询设备数据")
//    @ApiOperationSupport(order = 1)
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "ids", value = "多个id 例如\"device1,device2\"", dataType = "String", required = true),
//            @ApiImplicitParam(name = "type", value = "类型 1-站点数据 2-设备数据", dataType = "int", required = true)
//    })
//    public ResponseResult<List<DeviceServerDto>> findDeviceListByIdsAndType(@RequestParam String ids, @RequestParam Integer type) {
//        return webServerService.findDeviceListByIdsAndType(ids, type);
//    }

    @PostMapping("findLedgerListByTenetId")
    @ApiOperation("根据租户id查询站点设备台账数据")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "tenetId", value = "租户id", dataType = "String", required = true)
    public ResponseResult<List<SiteLedgerDto>> findLedgerListByTenetId(@RequestParam String tenetId) {
        return webServerService.findLedgerListByTenetId(tenetId);
    }

    @PostMapping("findCountListByCondition")
    @ApiOperation("根据查询条件查询站点设备统计数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<SiteCountDto>> findCountListByCondition(@RequestBody CountQueryVo countQueryVo) {
        return webServerService.findCountListByCondition(countQueryVo);
    }

}
