package com.sunmax.common.feign.devops;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "device-service", path = "/device/feign/devops", fallbackFactory = GenericFeignFallbackFactory.class)
public interface DevopsDeviceFeignClient {

    @PostMapping("findSiteBasicInfoByIds")
    @Operation(summary = "根据多个站点id查询站站点详情数据")
    
    ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @Operation(summary = "根据多个站点id查询设备列表数据")
    
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType);

    @PostMapping("findDeviceGunInfoByDeviceIds")
    @Operation(summary = "根据多个设备id查询设备电枪数据")
    
    ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(@RequestBody List<String> devlceIdList);

    @PostMapping("findDeviceBasicInfoByIds")
    @Operation(summary = "根据多个设备id查询设备基本信息")
    
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList);

    @PostMapping("findDeviceReaListById")
    @Operation(summary = "根据设备id查询设备扩展属性列表数据")
    
    ResponseResult<List<DeviceReaDto>> findDeviceReaListById(@RequestParam String deviceId);

    @PostMapping("findModelFunctionListByModelIds")
    @Operation(summary = "根据多个模型id查询模型功能点列表")
    
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList);

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @Operation(summary = "获取设备功能点实时数据数据")
    
    ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos);

    @PostMapping("findDeviceFunctionDataList")
    @Operation(summary = "根据设备id查询设备功能属性列表数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    ResponseResult<List<FunctionDataDto>> findDeviceFunctionDataList(@RequestParam String deviceId);

    @PostMapping("findDeviceInfoByParentIds")
    @Operation(summary = "根据多个父节点id查询下级设备列表数据")
    
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceInfoByParentIds(@RequestBody List<String> parentIdList);

    @PostMapping("findSiteSetUpBySiteIds")
    @Operation(summary = "根据多个站点id查询站站点设置数据")
    
    ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(@RequestBody List<String> siteIdList);

    @PostMapping("findAllDeviceEventList")
    @Operation(summary = "根据查询条件查询设备告警事件列表")
    
    ResponseResult<PageDto<DeviceAlarmEventListDto>> findAllDeviceEventList(@RequestBody DeviceAlarmEventQueryVo eventQueryVo);

    @PostMapping("getAssetTypeList")
    @Operation(summary = "获取资产分类列表")
    
    ResponseResult<List<AssetTypeDto>> getAssetTypeList(@RequestParam(required = false) String ids);

}
