package com.sunmax.device.controller.feign;

import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.device.service.CrontabFeignService;
import com.sunmax.device.service.DeviceService;
import com.sunmax.device.service.SystemFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/feign/swebapp")
@Api(tags = "提供给webapp服务调用的远程接口")
@ApiIgnore()
public class WebAppFeignController {

    @Autowired
    private SystemFeignService systemFeignService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CrontabFeignService crontabFeignService;

    @PostMapping("findSiteBasicInfoByIds")
    @ApiOperation("根据多个站点id查询站站点详情数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteBasicInfoByIds(siteIdList);
    }

    @PostMapping("findDeviceBasicInfoByCodes")
    @ApiOperation("根据多个设备编码查询设备详情数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(@RequestBody List<String> deviceCodeList) {
        return deviceService.findDeviceBasicInfoByCodes(deviceCodeList);
    }

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @ApiOperation("根据多个站点id查询设备列表数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType) {
        return crontabFeignService.findDeviceBasicInfoBySiteIds(siteIdList, deviceType);
    }

    @PostMapping("findDeviceGunInfoByDeviceIds")
    @ApiOperation("根据多个设备id查询设备电枪数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(@RequestBody List<String> deviceIds) {
        return deviceService.findDeviceGunInfoByDeviceIds(deviceIds);
    }
}
