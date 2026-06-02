package com.sunmax.devops.service.feign;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "device-service")
//@FeignClient(value = "device-service-cbl",url = "http://121.41.109.130:60003")
@RestController
@RequestMapping("/device/feign/devops")
public interface DeviceService {

    @PostMapping("findSiteBasicInfoByIds")
    @ApiOperation("根据多个站点id查询站站点详情数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @ApiOperation("根据多个站点id查询设备列表数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType);

    @PostMapping("findDeviceGunInfoByDeviceIds")
    @ApiOperation("根据多个设备id查询设备电枪数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(@RequestBody List<String> devlceIdList);

    @PostMapping("findDeviceBasicInfoByIds")
    @ApiOperation("根据多个设备id查询设备基本信息")
    @ApiOperationSupport(order = 4)
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList);

    @PostMapping("findDeviceReaListById")
    @ApiOperation("根据设备id查询设备扩展属性列表数据")
    @ApiOperationSupport(order = 5)
    ResponseResult<List<DeviceReaDto>> findDeviceReaListById(@RequestParam String deviceId);

    @PostMapping("findModelFunctionListByModelIds")
    @ApiOperation("根据多个模型id查询模型功能点列表")
    @ApiOperationSupport(order = 6)
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList);

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @ApiOperation("获取设备功能点实时数据数据")
    @ApiOperationSupport(order = 7)
    ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos);

    @PostMapping("findDeviceFunctionDataList")
    @ApiOperation("根据设备id查询设备功能属性列表数据")
    @ApiOperationSupport(order = 8)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    ResponseResult<List<FunctionDataDto>> findDeviceFunctionDataList(@RequestParam String deviceId);

    @PostMapping("findDeviceInfoByParentIds")
    @ApiOperation("根据多个父节点id查询下级设备列表数据")
    @ApiOperationSupport(order = 9)
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceInfoByParentIds(@RequestBody List<String> parentIdList);

    @PostMapping("findSiteSetUpBySiteIds")
    @ApiOperation("根据多个站点id查询站站点设置数据")
    @ApiOperationSupport(order = 10)
    ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(@RequestBody List<String> siteIdList);

    @PostMapping("findAllDeviceEventList")
    @ApiOperation("根据查询条件查询设备告警事件列表")
    @ApiOperationSupport(order = 11)
    ResponseResult<PageDto<DeviceAlarmEventListDto>> findAllDeviceEventList(@RequestBody DeviceAlarmEventQueryVo eventQueryVo);

    @PostMapping("getAssetTypeList")
    @ApiOperation("获取资产分类列表")
    @ApiOperationSupport(order = 12)
    ResponseResult<List<AssetTypeDto>> getAssetTypeList(@RequestParam(required = false) String ids);

}
