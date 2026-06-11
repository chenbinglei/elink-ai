package com.sunmax.common.feign.configure;

import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.InterflowDeviceVo;
import com.sunmax.common.vo.device.SiteInfoChangeVo;
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
@FeignClient(value = "device-service", path = "/device/feign/configure", fallbackFactory = GenericFeignFallbackFactory.class)
public interface ConfigureDeviceFeignClient {

    @PostMapping("saveOrUpdateInterflowSite")
    @Operation(summary = "批量新增或编辑互联互通站点数据")
    
    ResponseResult<Void> saveOrUpdateInterflowSite(@RequestBody List<SiteInfoChangeVo> siteInfoChangeVos);

    @PostMapping("saveOrUpdateInterflowDevice")
    @Operation(summary = "批量新增或编辑互联互通设备数据")
    
    ResponseResult<Void> saveOrUpdateInterflowDevice(@RequestBody List<InterflowDeviceVo> interflowDeviceVos);

    @PostMapping("findDeviceBasicInfoByCodes")
    @Operation(summary = "根据多个设备编码查询设备详情数据")
    
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(@RequestBody List<String> deviceCodeList);

    @PostMapping("getDeviceNumberList")
    @Operation(summary = "获取设备序列号列表数据")
    
    ResponseResult<Set<String>> getDeviceNumberList(@RequestParam(required = false) Integer accessType);

    @PostMapping("findSiteBasicInfoByIds")
    @Operation(summary = "根据多个站点id查询站点详情数据")
    
    ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @Operation(summary = "根据多个站点id查询设备列表数据")
    
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType);

    @PostMapping("findDeviceGunInfoByDeviceIds")
    @Operation(summary = "根据多个设备id查询设备电枪数据")
    
    ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(@RequestBody List<String> deviceIds);

    @PostMapping("findSiteInfoListByUserId")
    @Operation(summary = "根据用户id查询站点列表信息")
    
    ResponseResult<List<SiteInfoDto>> findSiteInfoListByUserId(@RequestParam String userId);

    @PostMapping("findDeviceFunctionListByDeviceId")
    @Operation(summary = "根据设备id查询设备功能点列表数据")
    
    ResponseResult<List<ModelFunctionListDto>> findDeviceFunctionListByDeviceId(@RequestParam String deviceId);

    @PostMapping("findSiteDeviceListBySiteId")
    @Operation(summary = "根据站点id查询站点的设备数据")
    
    ResponseResult<List<SiteDeviceTreeDto>> findSiteDeviceListBySiteId(@RequestParam String siteId);

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @Operation(summary = "获取设备功能点实时数据数据")
    
    ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos, @RequestParam Integer dataType);

    @PostMapping("getModelFunctionListByModelIds")
    @Operation(summary = "根据多个模型id和多个功能点标识查询模型功能点列表数据")
    
    ResponseResult<Map<String, Map<String,ModelFunctionListDto>>> getModelFunctionListByModelIds(@RequestBody Set<String> modelIds, @RequestParam(required = false) String functionLogos);

}
