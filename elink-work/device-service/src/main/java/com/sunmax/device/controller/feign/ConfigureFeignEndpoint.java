package com.sunmax.device.controller.feign;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.device.InterflowDeviceVo;
import com.sunmax.common.vo.device.SiteInfoChangeVo;
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
import com.sunmax.common.feign.configure.ConfigureDeviceFeignClient;

@RestController
@CrossOrigin
@RequestMapping("/feign/configure")
@Tag(name = "提供给配置服务调用的远程接口")
@Hidden()
public class ConfigureFeignEndpoint implements ConfigureDeviceFeignClient {

    @Autowired
    private SiteInfoService siteInfoService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private ProtocolFeignService protocolFeignService;

    @Autowired
    private SystemFeignService systemFeignService;

    @Autowired
    private CrontabFeignService crontabFeignService;

    @PostMapping("saveOrUpdateInterflowSite")
    @Operation(summary = "批量新增或编辑互联互通站点数据")
    
    @Override
    public ResponseResult<Void> saveOrUpdateInterflowSite(@RequestBody List<SiteInfoChangeVo> siteInfoChangeVos) {
        return siteInfoService.saveOrUpdateInterflowSite(siteInfoChangeVos);
    }

    @PostMapping("saveOrUpdateInterflowDevice")
    @Operation(summary = "批量新增或编辑互联互通设备数据")
    
    @Override
    public ResponseResult<Void> saveOrUpdateInterflowDevice(@RequestBody List<InterflowDeviceVo> interflowDeviceVos) {
        return deviceService.saveOrUpdateInterflowDevice(interflowDeviceVos);
    }

    @PostMapping("findDeviceBasicInfoByCodes")
    @Operation(summary = "根据多个设备编码查询设备详情数据")
    
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(@RequestBody List<String> deviceCodeList) {
        return deviceService.findDeviceBasicInfoByCodes(deviceCodeList);
    }

    @PostMapping("getDeviceNumberList")
    @Operation(summary = "获取设备序列号列表数据")
    
    public ResponseResult<Set<String>> getDeviceNumberList(@RequestParam(required = false) Integer accessType) {
        return protocolFeignService.getDeviceNumberList(accessType);
    }

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

    @PostMapping("findSiteInfoListByUserId")
    @Operation(summary = "根据用户id查询站点列表信息")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id")
    })
    public ResponseResult<List<SiteInfoDto>> findSiteInfoListByUserId(@RequestParam String userId) {
        return siteInfoService.findSiteInfoListByUserId(userId);
    }

    @PostMapping("findDeviceFunctionListByDeviceId")
    @Operation(summary = "根据设备id查询设备功能点列表数据")
    
    public ResponseResult<List<ModelFunctionListDto>> findDeviceFunctionListByDeviceId(@RequestParam String deviceId) {
        return deviceService.findDeviceFunctionListByDeviceId(deviceId);
    }

    @PostMapping("findSiteDeviceListBySiteId")
    @Operation(summary = "根据站点id查询站点的设备数据")
    
    public ResponseResult<List<SiteDeviceTreeDto>> findSiteDeviceListBySiteId(@RequestParam String siteId) {
        return deviceService.findSiteDeviceListBySiteId(siteId);
    }

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @Operation(summary = "获取设备功能点实时数据数据")
    
    public ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos, @RequestParam Integer dataType) {
        return ResponseResult.ok(DeviceCommonUtil.getDeviceFunctions(deviceIds, Arrays.stream(functionLogos.split(FileUtil.COMMA)).collect(Collectors.toSet()), dataType));
    }

    @PostMapping("getModelFunctionListByModelIds")
    @Operation(summary = "根据多个模型id和多个功能点标识查询模型功能点列表数据")
    
    public ResponseResult<Map<String, Map<String,ModelFunctionListDto>>> getModelFunctionListByModelIds(@RequestBody Set<String> modelIds, @RequestParam(required = false) String functionLogos) {
        return deviceService.getModelFunctionListByModelIds(modelIds, functionLogos);
    }

}
