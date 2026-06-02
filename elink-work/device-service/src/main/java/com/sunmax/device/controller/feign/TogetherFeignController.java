package com.sunmax.device.controller.feign;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.common.vo.together.PileGunChangeVo;
import com.sunmax.device.service.*;
import com.sunmax.device.util.DeviceCommonUtil;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/feign/together")
@Api(tags = "提供给能源聚合服务调用的远程接口")
@ApiIgnore()
public class TogetherFeignController {

    @Autowired
    private SystemFeignService systemFeignService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CrontabFeignService crontabFeignService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private TogetherFeignService togetherFeignService;

    @PostMapping("findSiteBasicInfoByIds")
    @ApiOperation("根据多个站点id查询站站点详情数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteBasicInfoByIds(siteIdList);
    }

    @PostMapping("findDeviceBasicInfoByCodes")
    @ApiOperation("根据多个设备编码查询设备详情数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(@RequestBody List<String> deviceCodeList) {
        return deviceService.findDeviceBasicInfoByCodes(deviceCodeList);
    }

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @ApiOperation("根据多个站点id查询设备列表数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType) {
        return crontabFeignService.findDeviceBasicInfoBySiteIds(siteIdList, deviceType);
    }

    @PostMapping("findSiteEnergyInfoBySiteIds")
    @ApiOperation("根据多个站点id查询能源信息数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Map<String, SiteEnergyInfoDto>> findSiteEnergyInfoBySiteIds(@RequestBody List<String> siteIdList) {
        return siteService.findSiteEnergyInfoBySiteIds(siteIdList);
    }

    @PostMapping("findGatewayChildDeviceById")
    @ApiOperation("根据网关id查询网关下所有子设备信息")
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<DeviceBasicInfoDto>> findGatewayChildDeviceById(@RequestParam String gatWayId) {
        return deviceService.findGatewayChildDeviceById(gatWayId);
    }

    @PostMapping("findDeviceGunInfoByDeviceIds")
    @ApiOperation("根据多个设备id查询设备电枪数据")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(@RequestBody List<String> deviceIds) {
        return deviceService.findDeviceGunInfoByDeviceIds(deviceIds);
    }

    @PostMapping("findDeviceBasicInfoByIds")
    @ApiOperation("根据多个设备id查询设备基本信息")
    @ApiOperationSupport(order = 7)
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList) {
        return crontabFeignService.findDeviceBasicInfoByIds(deviceIdList);
    }

    @PostMapping("findDeviceReaListById")
    @ApiOperation("根据设备id查询设备扩展属性列表数据")
    @ApiOperationSupport(order = 8)
    public ResponseResult<List<DeviceReaDto>> findDeviceReaListById(@RequestParam String deviceId) {
        return deviceService.findDeviceReaListById(deviceId);
    }

    @PostMapping("findAllPileDeviceInfoList")
    @ApiOperation("查询所有电桩设备信息列表")
    @ApiOperationSupport(order = 9)
    public ResponseResult<List<DeviceBasicInfoDto>> findAllPileDeviceInfoList() {
        return deviceService.findAllPileDeviceInfoList();
    }

    @PostMapping("findModelFunctionListByModelIds")
    @ApiOperation("根据多个模型id查询模型功能点列表")
    @ApiOperationSupport(order = 10)
    public ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList) {
        return crontabFeignService.findModelFunctionListByModelIds(modelIdList);
    }

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @ApiOperation("获取设备功能点实时数据数据")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIds", value = "多个设备id", required = true, dataType = "Set<String>"),
            @ApiImplicitParam(name = "functionLogos", value = "多个功能点标识", required = true, dataType = "String"),
            @ApiImplicitParam(name = "dataType", value = "数据类型 1-原始缓存数据 2-超过15分钟未上报的数据(置空)", required = true, dataType = "Integer")
    })
    public ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds,
                                                                                                   @RequestParam String functionLogos,
                                                                                                   @RequestParam Integer dataType) {
        return ResponseResult.ok(DeviceCommonUtil.getDeviceFunctions(deviceIds, Arrays.stream(functionLogos.split(FileUtil.COMMA))
                .map(String::trim).collect(Collectors.toSet()), dataType));
    }

    @PostMapping("updatePileRea")
    @ApiOperation("编辑电桩扩展属性数据")
    @ApiOperationSupport(order = 12)
    public ResponseResult<Void> updatePileRea(@RequestParam String id, @RequestParam String userId, @RequestParam String readwriteObject) {
        return togetherFeignService.updatePileRea(id, userId, readwriteObject);
    }

    @PostMapping("updatePileGun")
    @ApiOperation("编辑电枪数据")
    @ApiOperationSupport(order = 13)
    public ResponseResult<Void> updatePileGun(@RequestBody PileGunChangeVo pileGunChangeVo) {
        return togetherFeignService.updatePileGun(pileGunChangeVo);
    }

    @PostMapping("updateSiteStatusById")
    @ApiOperation("根据站点id修改站点状态")
    @ApiOperationSupport(order = 14)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "所属站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "siteStatus", value = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中", paramType = "query", required = true)
    })
    public ResponseResult<String> updateSiteStateById(@RequestParam String siteId, @RequestParam Integer siteStatus) {
        return togetherFeignService.updateSiteStateById(siteId, siteStatus);
    }

    @PostMapping("findSiteAffiliatesInfoByIds")
    @ApiOperation("根据多个站点id查询关联方信息数据")
    @ApiOperationSupport(order = 15)
    public ResponseResult<Map<String, List<AffiliatesInfoDto>>> findSiteAffiliatesInfoByIds(@RequestBody List<String> siteIdList) {
        return togetherFeignService.findSiteAffiliatesInfoByIds(siteIdList);
    }

    @PostMapping("findAllSiteBasicInfoList")
    @ApiOperation("查询全部站点详情列表")
    @ApiOperationSupport(order = 16)
    public ResponseResult<List<SiteInfoDto>> findAllSiteBasicInfoList(@RequestParam(required = false) String siteNameLike) {
        return systemFeignService.findAllSiteBasicInfoList(siteNameLike);
    }

    @PostMapping("checkSitePassword")
    @ApiOperation("校验站点密码是否正确")
    @ApiOperationSupport(order = 17)
    public ResponseResult<Integer> checkSitePassword(@RequestParam String siteId, @RequestParam String password) {
        return togetherFeignService.checkSitePassword(siteId, password);
    }

    @PostMapping("updateDeviceOperateStatus")
    @ApiOperation("修改设备运营状态")
    @ApiOperationSupport(order = 18)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "operateStatus", value = "设备运营状态 1-投运 2-检修 3-退役", paramType = "query", required = true)
    })
    public ResponseResult<String> updateDeviceOperateStatus(@RequestParam String deviceId, @RequestParam Integer operateStatus) {
        return togetherFeignService.updateDeviceOperateStatus(deviceId, operateStatus);
    }

    @PostMapping("findDeviceFunctionListByIds")
    @ApiOperation("根据多个设备id查询设备功能属性列表数据")
    @ApiOperationSupport(order = 19)
    public ResponseResult<Map<String, List<ModelFunctionListDto>>> findDeviceFunctionListByIds(@RequestBody List<String> deviceIdList) {
        return deviceService.findDeviceFunctionListByIds(new HashSet<>(deviceIdList));
    }

    @PostMapping("findDeviceNotRecoveEventList")
    @ApiOperation("根据设备id查询设备所有未恢复事件告警数据")
    @ApiOperationSupport(order = 20)
    public ResponseResult<List<DeviceAlarmEventListDto>> findDeviceNotRecoveEventList(@RequestParam String deviceId) {
        return deviceService.findDeviceNotRecoveEventList(deviceId);
    }

    @PostMapping("findDeviceFunctionDataList")
    @ApiOperation("根据设备id查询设备功能属性列表数据")
    @ApiOperationSupport(order = 21)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    public ResponseResult<List<FunctionDataDto>> findDeviceFunctionDataList(@RequestParam String deviceId) {
        return deviceService.findDeviceFunctionDataList(deviceId);
    }

    @PostMapping("findDeviceTxStatus")
    @ApiOperation("查询设备通信状态")
    @ApiOperationSupport(order = 22)
    public ResponseResult<Map<String, Integer>> findDeviceTxStatus(@RequestBody List<DeviceBasicInfoDto> deviceBasicInfoDtos) {
        return togetherFeignService.findDeviceTxStatus(deviceBasicInfoDtos);
    }

    @PostMapping("findDeviceInfoByParentIds")
    @ApiOperation("根据多个父节点id查询下级设备列表数据")
    @ApiOperationSupport(order = 23)
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceInfoByParentIds(@RequestBody List<String> parentIdList) {
        return crontabFeignService.findDeviceInfoByParentIds(parentIdList);
    }

    @PostMapping("findSiteInfoListByIds")
    @ApiOperation("根据多个站点id查询站站点详情数据(只查询站点本体表结构数据，关于其他关联和设计别的表字段信息不查询返回,使用时看清楚)")
    @ApiOperationSupport(order = 24)
    public ResponseResult<List<SiteInfoDto>> findSiteInfoListByIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteInfoListByIds(siteIdList);
    }

    @PostMapping("findSiteSetUpBySiteIds")
    @ApiOperation("根据多个站点id查询站站点设置数据")
    @ApiOperationSupport(order = 25)
    public ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(@RequestBody List<String> siteIdList) {
        return systemFeignService.findSiteSetUpBySiteIds(siteIdList);
    }

    @PostMapping("findSiteMeasureIdBySiteIds")
    @ApiOperation("根据多个站点id查询站点关联计量设备id")
    @ApiOperationSupport(order = 26)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nodeType", value = "节点类型 0-计量节点 1-设备节点", paramType = "query", required = true),
            @ApiImplicitParam(name = "deviceType", value = "设备类型 1-光伏 2-储能 3-电桩 4-其他", paramType = "query", required = true)
    })
    public ResponseResult<Map<String, List<String>>> findSiteMeasureIdBySiteIds(@RequestBody List<String> siteIdList, @RequestParam Integer nodeType, @RequestParam Integer deviceType) {
        return togetherFeignService.findSiteMeasureIdBySiteIds(siteIdList, nodeType, deviceType);
    }

    @PostMapping("findSiteIdBySubId")
    @ApiOperation("根据子系统id查询站点id")
    @ApiOperationSupport(order = 27)
    public ResponseResult<String> findSiteIdBySubId(@RequestParam String subId) {
        return togetherFeignService.findSiteIdBySubId(subId);
    }

    @PostMapping("findAllDeviceEventList")
    @ApiOperation("根据查询条件查询设备告警事件列表")
    @ApiOperationSupport(order = 28)
    public ResponseResult<PageDto<DeviceAlarmEventListDto>> findAllDeviceEventList(@RequestBody DeviceAlarmEventQueryVo eventQueryVo) {
        return togetherFeignService.findAllDeviceEventList(eventQueryVo);
    }

    @PostMapping("updateEventIgnoreStatus")
    @ApiOperation("修改设备事件忽略状态")
    @ApiOperationSupport(order = 29)
    public ResponseResult<Void> updateEventIgnoreStatus(@RequestParam String id, @RequestParam Integer ignoreStatus) {
        return togetherFeignService.updateEventIgnoreStatus(id, ignoreStatus);
    }

    @PostMapping("findSiteTopDataListBySiteId")
    @ApiOperation("根据站点id查询拓扑节点实时数据")
    @ApiOperationSupport(order = 30)
    public ResponseResult<List<SiteTopDataDto>> findSiteTopDataListBySiteId(@RequestParam String siteId) {
        return togetherFeignService.findSiteTopDataListBySiteId(siteId);
    }

    @PostMapping("findSiteTopNodeBySiteId")
    @ApiOperation("根据站点id查询拓扑节点静态数据")
    @ApiOperationSupport(order = 31)
    public ResponseResult<List<SiteTopNodeDto>> findSiteTopNodeBySiteId(@RequestParam String siteId, @RequestParam(required = false) Integer nodeType) {
        return togetherFeignService.findSiteTopNodeBySiteId(siteId, nodeType);
    }

}
