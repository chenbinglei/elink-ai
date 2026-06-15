package com.sunmax.common.feign.crontab;

import com.sunmax.common.dto.crontab.ConfigurSiteListDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.SiteListQueryVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

@FeignClient(value = "device-service", path = "/device/feign/scrontab", fallbackFactory = GenericFeignFallbackFactory.class)
public interface CrontabDeviceFeignClient {

    @PostMapping("findDeviceBasicInfoByIds")
    @Operation(summary = "根据多个设备id查询设备基本信息")
    
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList);

    @PostMapping("findSiteBasicInfoByIds")
    @Operation(summary = "根据多个站点id查询站站点详情数据")
    
    ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @Operation(summary = "根据多个站点id查询设备列表数据")
    
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType);

    @PostMapping("findDeviceFunctionListByIds")
    @Operation(summary = "根据多个设备id查询设备功能属性列表数据")
    
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findDeviceFunctionListByIds(@RequestBody List<String> deviceIdList);

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @Operation(summary = "获取设备功能点实时数据数据")
    
    ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos);

    @PostMapping("findModelDetailByIds")
    @Operation(summary = "根据多个模型id查询模型基本信息")
    
    ResponseResult<Map<String, ModelDetailDto>> findModelDetailByIds(@RequestBody List<String> modelIdList);

    @PostMapping("findFunctionDetailByIds")
    @Operation(summary = "根据多个功能点id查询功能点基本信息")
    
    ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByIds(@RequestBody List<String> functionIdList);

    @PostMapping("findModelFunctionListByModelIds")
    @Operation(summary = "根据多个模型id查询模型功能点列表")
    
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList);

    @PostMapping("findDeviceTxStatusById")
    @Operation(summary = "根据设备id查询设备通讯状态")
    
    ResponseResult<Map<String, Integer>> findDeviceTxStatusById(@RequestBody Map<String, String> deleveIdMap);

    @PostMapping("findSiteListByUserId")
    @Operation(summary = "根据用户id查询站点列表数据")
    
    ResponseResult<List<ConfigurSiteListDto>> findSiteListByUserId(@RequestBody SiteListQueryVo siteListQueryVo);

    @PostMapping("getSiteDeviceList")
    @Operation(summary = "获取站点及设备数据列表")
    
    ResponseResult<List<SiteDeviceDto>> getSiteDeviceList(@RequestBody(required = false) Set<String> siteIds);

    @PostMapping("findFunctionDetailByLogos")
    @Operation(summary = "根据多个功能点标识查询功能点基本信息")
    
    ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByLogos(@RequestBody Set<String> functionLogos);
}
