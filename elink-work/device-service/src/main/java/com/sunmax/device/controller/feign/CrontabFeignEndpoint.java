package com.sunmax.device.controller.feign;

import com.sunmax.common.dto.crontab.ConfigurSiteListDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.crontab.SiteListQueryVo;
import com.sunmax.device.service.CrontabFeignService;
import com.sunmax.device.service.DeviceService;
import com.sunmax.device.service.SystemFeignService;
import com.sunmax.device.util.DeviceCommonUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.*;
import java.util.stream.Collectors;
import com.sunmax.common.feign.crontab.CrontabDeviceFeignClient;

/**
 * @Author: yqz
 * @注释: 提供给定时任务服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/scrontab")
@Tag(name = "提供给定时任务服务调用的远程接口")
@Hidden()
public class CrontabFeignEndpoint implements CrontabDeviceFeignClient {

    @Autowired
    private CrontabFeignService crontabFeignService;

    @Autowired
    private SystemFeignService systemFeignService;

    @Autowired
    private DeviceService deviceService;

    @PostMapping("findDeviceBasicInfoByIds")
    @Operation(summary = "根据多个设备id查询设备基本信息")
    
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList) {
        return crontabFeignService.findDeviceBasicInfoByIds(deviceIdList);
    }

    @PostMapping("findSiteBasicInfoByIds")
    @Operation(summary = "根据多个站点id查询站点详情数据")
    
    public ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteBasicInfoByIds(siteIdList);
    }

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @Operation(summary = "根据多个站点id查询设备列表数据")
    
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType) {
        return crontabFeignService.findDeviceBasicInfoBySiteIds(siteIdList, deviceType);
    }

    @PostMapping("findDeviceFunctionListByIds")
    @Operation(summary = "根据多个设备id查询设备功能属性列表数据")
    
    public ResponseResult<Map<String, List<ModelFunctionListDto>>> findDeviceFunctionListByIds(@RequestBody List<String> deviceIdList) {
        return deviceService.findDeviceFunctionListByIds(new HashSet<>(deviceIdList));
    }

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @Operation(summary = "获取设备功能点实时数据数据")
    
    public ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos) {
        return ResponseResult.ok(DeviceCommonUtil.getDeviceFunctions(deviceIds, Arrays.stream(functionLogos.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toSet())));
    }

    @PostMapping("findModelDetailByIds")
    @Operation(summary = "根据多个模型id查询模型基本信息")
    
    public ResponseResult<Map<String, ModelDetailDto>> findModelDetailByIds(@RequestBody List<String> modelIdList) {
        return crontabFeignService.findModelDetailByIds(modelIdList);
    }

    @PostMapping("findFunctionDetailByIds")
    @Operation(summary = "根据多个功能点id查询功能点基本信息")
    
    public ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByIds(@RequestBody List<String> functionIdList) {
        return crontabFeignService.findFunctionDetailByIds(functionIdList);
    }

    @PostMapping("findModelFunctionListByModelIds")
    @Operation(summary = "根据多个模型id查询模型功能点列表")
    
    public ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList) {
        return crontabFeignService.findModelFunctionListByModelIds(modelIdList);
    }

    @PostMapping("findDeviceTxStatusById")
    @Operation(summary = "根据设备id查询设备通讯状态")
    
    public ResponseResult<Map<String, Integer>> findDeviceTxStatusById(@RequestBody Map<String, String> deleveIdMap) {
        return crontabFeignService.findDeviceTxStatusById(deleveIdMap);
    }

    @PostMapping("findSiteListByUserId")
    @Operation(summary = "根据用户id查询站点列表数据")
    
    public ResponseResult<List<ConfigurSiteListDto>> findSiteListByUserId(@RequestBody SiteListQueryVo siteListQueryVo) {
        return crontabFeignService.findSiteListByUserId(siteListQueryVo);
    }

    @PostMapping("getSiteDeviceList")
    @Operation(summary = "获取站点及设备数据列表")
    
    public ResponseResult<List<SiteDeviceDto>> getSiteDeviceList(@RequestBody(required = false) Set<String> siteIds) {
        return crontabFeignService.getSiteDeviceList(siteIds);
    }

    @PostMapping("findFunctionDetailByLogos")
    @Operation(summary = "根据多个功能点标识查询功能点基本信息")
    
    public ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByLogos(@RequestBody Set<String> functionLogos) {
        return crontabFeignService.findFunctionDetailByLogos(functionLogos);
    }

}
