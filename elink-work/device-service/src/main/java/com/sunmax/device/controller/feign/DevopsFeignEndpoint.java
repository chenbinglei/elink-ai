package com.sunmax.device.controller.feign;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.common.dto.device.AssetTypeDto;
import com.sunmax.device.service.*;
import com.sunmax.device.util.DeviceCommonUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import com.sunmax.common.feign.devops.DevopsDeviceFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/devops")
@Tag(name = "提供给运维小程序服务调用的远程接口")
@Hidden()
public class DevopsFeignEndpoint implements DevopsDeviceFeignClient {

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
    @Operation(summary = "根据多个站点id查询站站点详情数据")
    
    public ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteBasicInfoByIds(siteIdList);
    }

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @Operation(summary = "根据多个站点id查询设备列表数据")
    
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType) {
        return crontabFeignService.findDeviceBasicInfoBySiteIds(siteIdList, deviceType);
    }

    @PostMapping("findDeviceGunInfoByDeviceIds")
    @Operation(summary = "根据多个设备id查询设备电枪数据")
    
    public ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(@RequestBody List<String> deviceIds) {
        return deviceService.findDeviceGunInfoByDeviceIds(deviceIds);
    }

    @PostMapping("findDeviceBasicInfoByIds")
    @Operation(summary = "根据多个设备id查询设备基本信息")
    
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList) {
        return crontabFeignService.findDeviceBasicInfoByIds(deviceIdList);
    }

    @PostMapping("findDeviceReaListById")
    @Operation(summary = "根据设备id查询设备扩展属性列表数据")
    
    public ResponseResult<List<DeviceReaDto>> findDeviceReaListById(@RequestParam String deviceId) {
        return deviceService.findDeviceReaListById(deviceId);
    }

    @PostMapping("findModelFunctionListByModelIds")
    @Operation(summary = "根据多个模型id查询模型功能点列表")
    
    public ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList) {
        return crontabFeignService.findModelFunctionListByModelIds(modelIdList);
    }

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @Operation(summary = "获取设备功能点实时数据数据")
    
    public ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos) {
        return ResponseResult.ok(DeviceCommonUtil.getDeviceFunctions(deviceIds, Arrays.stream(functionLogos.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toSet())));
    }

    @PostMapping("findDeviceFunctionDataList")
    @Operation(summary = "根据设备id查询设备功能属性列表数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    public ResponseResult<List<FunctionDataDto>> findDeviceFunctionDataList(@RequestParam String deviceId) {
        return deviceService.findDeviceFunctionDataList(deviceId);
    }

    @PostMapping("findDeviceInfoByParentIds")
    @Operation(summary = "根据多个父节点id查询下级设备列表数据")
    
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceInfoByParentIds(@RequestBody List<String> parentIdList) {
        return crontabFeignService.findDeviceInfoByParentIds(parentIdList);
    }

    @PostMapping("findSiteSetUpBySiteIds")
    @Operation(summary = "根据多个站点id查询站站点设置数据")
    
    public ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteSetUpBySiteIds(siteIdList);
    }

    @PostMapping("findAllDeviceEventList")
    @Operation(summary = "根据查询条件查询设备告警事件列表")
    
    public ResponseResult<PageDto<DeviceAlarmEventListDto>> findAllDeviceEventList(@RequestBody DeviceAlarmEventQueryVo eventQueryVo) {
        return togetherFeignService.findAllDeviceEventList(eventQueryVo);
    }

    @PostMapping("getAssetTypeList")
    @Operation(summary = "获取资产分类列表")
    
    public ResponseResult<List<AssetTypeDto>> getAssetTypeList(@RequestParam(required = false) String ids) {
        return modelService.getAssetTypeList(ids);
    }

}
