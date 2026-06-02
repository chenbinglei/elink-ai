package com.sunmax.device.controller.feign;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.common.dto.device.AssetTypeDto;
import com.sunmax.device.service.*;
import com.sunmax.device.util.DeviceCommonUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/feign/devops")
@Api(tags = "提供给运维小程序服务调用的远程接口")
@ApiIgnore()
public class DevopsFeignController {

    @Autowired
    private SystemFeignService systemFeignService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CrontabFeignService crontabFeignService;

    @Autowired
    private TogetherFeignService togetherFeignService;

    @Autowired
    private ModelService modelService;

    @PostMapping("findSiteBasicInfoByIds")
    @ApiOperation("根据多个站点id查询站站点详情数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteBasicInfoByIds(siteIdList);
    }

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @ApiOperation("根据多个站点id查询设备列表数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType) {
        return crontabFeignService.findDeviceBasicInfoBySiteIds(siteIdList, deviceType);
    }

    @PostMapping("findDeviceGunInfoByDeviceIds")
    @ApiOperation("根据多个设备id查询设备电枪数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(@RequestBody List<String> deviceIds) {
        return deviceService.findDeviceGunInfoByDeviceIds(deviceIds);
    }

    @PostMapping("findDeviceBasicInfoByIds")
    @ApiOperation("根据多个设备id查询设备基本信息")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList) {
        return crontabFeignService.findDeviceBasicInfoByIds(deviceIdList);
    }

    @PostMapping("findDeviceReaListById")
    @ApiOperation("根据设备id查询设备扩展属性列表数据")
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<DeviceReaDto>> findDeviceReaListById(@RequestParam String deviceId) {
        return deviceService.findDeviceReaListById(deviceId);
    }

    @PostMapping("findModelFunctionListByModelIds")
    @ApiOperation("根据多个模型id查询模型功能点列表")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList) {
        return crontabFeignService.findModelFunctionListByModelIds(modelIdList);
    }

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @ApiOperation("获取设备功能点实时数据数据")
    @ApiOperationSupport(order = 7)
    public ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos) {
        return ResponseResult.ok(DeviceCommonUtil.getDeviceFunctions(deviceIds, Arrays.stream(functionLogos.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toSet())));
    }

    @PostMapping("findDeviceFunctionDataList")
    @ApiOperation("根据设备id查询设备功能属性列表数据")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<List<FunctionDataDto>> findDeviceFunctionDataList(@RequestParam String deviceId) {
        return deviceService.findDeviceFunctionDataList(deviceId);
    }

    @PostMapping("findDeviceInfoByParentIds")
    @ApiOperation("根据多个父节点id查询下级设备列表数据")
    @ApiOperationSupport(order = 9)
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceInfoByParentIds(@RequestBody List<String> parentIdList) {
        return crontabFeignService.findDeviceInfoByParentIds(parentIdList);
    }

    @PostMapping("findSiteSetUpBySiteIds")
    @ApiOperation("根据多个站点id查询站站点设置数据")
    @ApiOperationSupport(order = 10)
    public ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteSetUpBySiteIds(siteIdList);
    }

    @PostMapping("findAllDeviceEventList")
    @ApiOperation("根据查询条件查询设备告警事件列表")
    @ApiOperationSupport(order = 11)
    public ResponseResult<PageDto<DeviceAlarmEventListDto>> findAllDeviceEventList(@RequestBody DeviceAlarmEventQueryVo eventQueryVo) {
        return togetherFeignService.findAllDeviceEventList(eventQueryVo);
    }

    @PostMapping("getAssetTypeList")
    @ApiOperation("获取资产分类列表")
    @ApiOperationSupport(order = 12)
    public ResponseResult<List<AssetTypeDto>> getAssetTypeList(@RequestParam(required = false) String ids) {
        return modelService.getAssetTypeList(ids);
    }

}
