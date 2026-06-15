package com.sunmax.device.controller;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.dto.webserver.SiteCountDto;
import com.sunmax.device.dto.webserver.SiteLedgerDto;
import com.sunmax.device.service.WebServerService;
import com.sunmax.device.vo.webserver.CountQueryVo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("webServer")
@Tag(name = "提供web服务调用的接口")
public class WebServerController {

    @Autowired
    private WebServerService webServerService;

//    @PostMapping("findDeviceListByIdsAndType")
//    @Operation(summary = "根据多个id和类型和查询设备数据")
//    
//    @Parameters({
//            @Parameter(name = "ids", description = "多个id 例如\"device1,device2\""),
//            @Parameter(name = "type", description = "类型 1-站点数据 2-设备数据")
//    })
//    public ResponseResult<List<DeviceServerDto>> findDeviceListByIdsAndType(@RequestParam String ids, @RequestParam Integer type) {
//        return webServerService.findDeviceListByIdsAndType(ids, type);
//    }

    @PostMapping("findLedgerListByTenetId")
    @Operation(summary = "根据租户id查询站点设备台账数据")
    
    @Parameter(name = "tenetId", description = "租户id")
    public ResponseResult<List<SiteLedgerDto>> findLedgerListByTenetId(@RequestParam String tenetId) {
        return webServerService.findLedgerListByTenetId(tenetId);
    }

    @PostMapping("findCountListByCondition")
    @Operation(summary = "根据查询条件查询站点设备统计数据")
    
    public ResponseResult<List<SiteCountDto>> findCountListByCondition(@RequestBody CountQueryVo countQueryVo) {
        return webServerService.findCountListByCondition(countQueryVo);
    }

}
