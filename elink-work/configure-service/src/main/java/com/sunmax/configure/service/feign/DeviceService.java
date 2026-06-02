package com.sunmax.configure.service.feign;

import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.InterflowDeviceVo;
import com.sunmax.common.vo.device.SiteInfoChangeVo;
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
@RestController
@RequestMapping("/device/feign/configure")
public interface DeviceService {

    @PostMapping("saveOrUpdateInterflowSite")
    @ApiOperation("批量新增或编辑互联互通站点数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<Void> saveOrUpdateInterflowSite(@RequestBody List<SiteInfoChangeVo> siteInfoChangeVos);

    @PostMapping("saveOrUpdateInterflowDevice")
    @ApiOperation("批量新增或编辑互联互通设备数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<Void> saveOrUpdateInterflowDevice(@RequestBody List<InterflowDeviceVo> interflowDeviceVos);

    @PostMapping("findDeviceBasicInfoByCodes")
    @ApiOperation("根据多个设备编码查询设备详情数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(@RequestBody List<String> deviceCodeList);

    @PostMapping("getDeviceNumberList")
    @ApiOperation("获取设备序列号列表数据")
    @ApiOperationSupport(order = 4)
    ResponseResult<Set<String>> getDeviceNumberList(@RequestParam(required = false) Integer accessType);

    @PostMapping("findSiteBasicInfoByIds")
    @ApiOperation("根据多个站点id查询站点详情数据")
    @ApiOperationSupport(order = 5)
    ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @ApiOperation("根据多个站点id查询设备列表数据")
    @ApiOperationSupport(order = 6)
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType);

    @PostMapping("findDeviceGunInfoByDeviceIds")
    @ApiOperation("根据多个设备id查询设备电枪数据")
    @ApiOperationSupport(order = 7)
    ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(@RequestBody List<String> deviceIds);

    @PostMapping("findSiteInfoListByUserId")
    @ApiOperation("根据用户id查询站点列表信息")
    @ApiOperationSupport(order = 8)
    ResponseResult<List<SiteInfoDto>> findSiteInfoListByUserId(@RequestParam String userId);

    @PostMapping("findDeviceFunctionListByDeviceId")
    @ApiOperation("根据设备id查询设备功能点列表数据")
    @ApiOperationSupport(order = 9)
    ResponseResult<List<ModelFunctionListDto>> findDeviceFunctionListByDeviceId(@RequestParam String deviceId);

    @PostMapping("findSiteDeviceListBySiteId")
    @ApiOperation("根据站点id查询站点的设备数据")
    @ApiOperationSupport(order = 10)
    ResponseResult<List<SiteDeviceTreeDto>> findSiteDeviceListBySiteId(@RequestParam String siteId);

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @ApiOperation("获取设备功能点实时数据数据")
    @ApiOperationSupport(order = 11)
    ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos, @RequestParam Integer dataType);

    @PostMapping("getModelFunctionListByModelIds")
    @ApiOperation("根据多个模型id和多个功能点标识查询模型功能点列表数据")
    @ApiOperationSupport(order = 12)
    ResponseResult<Map<String, Map<String,ModelFunctionListDto>>> getModelFunctionListByModelIds(@RequestBody Set<String> modelIds, @RequestParam(required = false) String functionLogos);

}
