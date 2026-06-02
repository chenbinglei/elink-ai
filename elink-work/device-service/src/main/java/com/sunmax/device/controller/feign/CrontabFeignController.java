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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: yqz
 * @注释: 提供给定时任务服务调用的远程接口
 */
@RestController
@CrossOrigin
@RequestMapping("/feign/scrontab")
@Api(tags = "提供给定时任务服务调用的远程接口")
@ApiIgnore()
public class CrontabFeignController {

    @Autowired
    private CrontabFeignService crontabFeignService;

    @Autowired
    private SystemFeignService systemFeignService;

    @Autowired
    private DeviceService deviceService;

    @PostMapping("findDeviceBasicInfoByIds")
    @ApiOperation("根据多个设备id查询设备基本信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList) {
        return crontabFeignService.findDeviceBasicInfoByIds(deviceIdList);
    }

    @PostMapping("findSiteBasicInfoByIds")
    @ApiOperation("根据多个站点id查询站点详情数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteBasicInfoByIds(siteIdList);
    }

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @ApiOperation("根据多个站点id查询设备列表数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType) {
        return crontabFeignService.findDeviceBasicInfoBySiteIds(siteIdList, deviceType);
    }

    @PostMapping("findDeviceFunctionListByIds")
    @ApiOperation("根据多个设备id查询设备功能属性列表数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Map<String, List<ModelFunctionListDto>>> findDeviceFunctionListByIds(@RequestBody List<String> deviceIdList) {
        return deviceService.findDeviceFunctionListByIds(new HashSet<>(deviceIdList));
    }

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @ApiOperation("获取设备功能点实时数据数据")
    @ApiOperationSupport(order = 5)
    public ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds, @RequestParam String functionLogos) {
        return ResponseResult.ok(DeviceCommonUtil.getDeviceFunctions(deviceIds, Arrays.stream(functionLogos.split(FileUtil.COMMA)).map(String::trim).collect(Collectors.toSet())));
    }

    @PostMapping("findModelDetailByIds")
    @ApiOperation("根据多个模型id查询模型基本信息")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Map<String, ModelDetailDto>> findModelDetailByIds(@RequestBody List<String> modelIdList) {
        return crontabFeignService.findModelDetailByIds(modelIdList);
    }

    @PostMapping("findFunctionDetailByIds")
    @ApiOperation("根据多个功能点id查询功能点基本信息")
    @ApiOperationSupport(order = 7)
    public ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByIds(@RequestBody List<String> functionIdList) {
        return crontabFeignService.findFunctionDetailByIds(functionIdList);
    }

    @PostMapping("findModelFunctionListByModelIds")
    @ApiOperation("根据多个模型id查询模型功能点列表")
    @ApiOperationSupport(order = 8)
    public ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList) {
        return crontabFeignService.findModelFunctionListByModelIds(modelIdList);
    }

    @PostMapping("findDeviceTxStatusById")
    @ApiOperation("根据设备id查询设备通讯状态")
    @ApiOperationSupport(order = 9)
    public ResponseResult<Map<String, Integer>> findDeviceTxStatusById(@RequestBody Map<String, String> deleveIdMap) {
        return crontabFeignService.findDeviceTxStatusById(deleveIdMap);
    }

    @PostMapping("findSiteListByUserId")
    @ApiOperation("根据用户id查询站点列表数据")
    @ApiOperationSupport(order = 10)
    public ResponseResult<List<ConfigurSiteListDto>> findSiteListByUserId(@RequestBody SiteListQueryVo siteListQueryVo) {
        return crontabFeignService.findSiteListByUserId(siteListQueryVo);
    }

    @PostMapping("getSiteDeviceList")
    @ApiOperation("获取站点及设备数据列表")
    @ApiOperationSupport(order = 11)
    public ResponseResult<List<SiteDeviceDto>> getSiteDeviceList(@RequestBody(required = false) Set<String> siteIds) {
        return crontabFeignService.getSiteDeviceList(siteIds);
    }

    @PostMapping("findFunctionDetailByLogos")
    @ApiOperation("根据多个功能点标识查询功能点基本信息")
    @ApiOperationSupport(order = 12)
    public ResponseResult<Map<String, FunctionDetailDto>> findFunctionDetailByLogos(@RequestBody Set<String> functionLogos) {
        return crontabFeignService.findFunctionDetailByLogos(functionLogos);
    }

}
