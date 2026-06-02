package com.sunmax.crontab.service.feign;

import com.sunmax.common.dto.crontab.ConfigurSiteListDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.crontab.SiteListQueryVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@FeignClient(value = "device-service")
//@FeignClient(value = "device-service-cbl",url = "http://121.41.109.130:60003")
@RestController
@RequestMapping("/device/feign/scrontab")
public interface DeviceService {

    @PostMapping("findDeviceBasicInfoByIds")
    @ApiOperation("根据多个设备id查询设备基本信息")
    @ApiOperationSupport(order = 1)
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList);

    @PostMapping("findSiteBasicInfoByIds")
    @ApiOperation("根据多个站点id查询站站点详情数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @ApiOperation("根据多个站点id查询设备列表数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType);

    @PostMapping("findDeviceFunctionListByIds")
    @ApiOperation("根据多个设备id查询设备功能属性列表数据")
    @ApiOperationSupport(order = 4)
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findDeviceFunctionListByIds(@RequestBody List<String> deviceIdList);

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @ApiOperation("获取设备功能点实时数据数据")
    @ApiOperationSupport(order = 5)
    ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos);

    @PostMapping("findModelDetailByIds")
    @ApiOperation("根据多个模型id查询模型基本信息")
    @ApiOperationSupport(order = 6)
    ResponseResult<Map<String, ModelDetailDto>> findModelDetailByIds(@RequestBody List<String> modelIdList);

    @PostMapping("findFunctionDetailByIds")
    @ApiOperation("根据多个功能点id查询功能点基本信息")
    @ApiOperationSupport(order = 7)
    ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByIds(@RequestBody List<String> functionIdList);

    @PostMapping("findModelFunctionListByModelIds")
    @ApiOperation("根据多个模型id查询模型功能点列表")
    @ApiOperationSupport(order = 8)
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList);

    @PostMapping("findDeviceTxStatusById")
    @ApiOperation("根据设备id查询设备通讯状态")
    @ApiOperationSupport(order = 9)
    ResponseResult<Map<String, Integer>> findDeviceTxStatusById(@RequestBody Map<String, String> deleveIdMap);

    @PostMapping("findSiteListByUserId")
    @ApiOperation("根据用户id查询站点列表数据")
    @ApiOperationSupport(order = 10)
    ResponseResult<List<ConfigurSiteListDto>> findSiteListByUserId(@RequestBody SiteListQueryVo siteListQueryVo);

    @PostMapping("getSiteDeviceList")
    @ApiOperation("获取站点及设备数据列表")
    @ApiOperationSupport(order = 11)
    ResponseResult<List<SiteDeviceDto>> getSiteDeviceList(@RequestBody(required = false) Set<String> siteIds);

    @PostMapping("findFunctionDetailByLogos")
    @ApiOperation("根据多个功能点标识查询功能点基本信息")
    @ApiOperationSupport(order = 12)
    ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByLogos(@RequestBody Set<String> functionLogos);
}
