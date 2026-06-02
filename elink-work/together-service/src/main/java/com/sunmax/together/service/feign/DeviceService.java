package com.sunmax.together.service.feign;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.common.vo.together.PileGunChangeVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
//@FeignClient(value = "device-service-cbl",url = "http://121.41.109.130:5000")
@RestController
@RequestMapping("/device/feign/together")
public interface DeviceService {

    @PostMapping("findSiteBasicInfoByIds")
    @ApiOperation("根据多个站点id查询站站点详情数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findDeviceBasicInfoByCodes")
    @ApiOperation("根据多个设备编码查询设备详情数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(@RequestBody List<String> deviceCodeList);

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @ApiOperation("根据多个站点id查询设备列表数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType);

    @PostMapping("findSiteEnergyInfoBySiteIds")
    @ApiOperation("根据多个站点id查询能源信息数据")
    @ApiOperationSupport(order = 4)
    ResponseResult<Map<String, SiteEnergyInfoDto>> findSiteEnergyInfoBySiteIds(@RequestBody List<String> siteIdList);

    @PostMapping("findGatewayChildDeviceById")
    @ApiOperation("根据网关id查询网关下所有子设备信息")
    @ApiOperationSupport(order = 5)
    ResponseResult<List<DeviceBasicInfoDto>> findGatewayChildDeviceById(@RequestParam String gatWayId);

    @PostMapping("findDeviceGunInfoByDeviceIds")
    @ApiOperation("根据多个设备id查询设备电枪数据")
    @ApiOperationSupport(order = 6)
    ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(@RequestBody List<String> devlceIdList);

    @PostMapping("findDeviceBasicInfoByIds")
    @ApiOperation("根据多个设备id查询设备基本信息")
    @ApiOperationSupport(order = 7)
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList);

    @PostMapping("findDeviceReaListById")
    @ApiOperation("根据设备id查询设备扩展属性列表数据")
    @ApiOperationSupport(order = 8)
    ResponseResult<List<DeviceReaDto>> findDeviceReaListById(@RequestParam String deviceId);

    @PostMapping("findAllPileDeviceInfoList")
    @ApiOperation("查询所有电桩设备信息列表")
    @ApiOperationSupport(order = 9)
    ResponseResult<List<DeviceBasicInfoDto>> findAllPileDeviceInfoList();

    @PostMapping("findModelFunctionListByModelIds")
    @ApiOperation("根据多个模型id查询模型功能点列表")
    @ApiOperationSupport(order = 10)
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList);

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @ApiOperation("获取设备功能点实时数据数据")
    @ApiOperationSupport(order = 11)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceIds", value = "多个设备id", required = true, dataType = "Set<String>"),
            @ApiImplicitParam(name = "functionLogos", value = "多个功能点标识", required = true, dataType = "String"),
            @ApiImplicitParam(name = "dataType", value = "数据类型 1-原始缓存数据 2-超过15分钟未上报的数据(置空)", required = true, dataType = "Integer")
    })
    ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds,
                                                                                            @RequestParam String functionLogos,
                                                                                            @RequestParam Integer dataType);

    @PostMapping("updatePileRea")
    @ApiOperation("编辑电桩扩展属性数据")
    @ApiOperationSupport(order = 12)
    ResponseResult<Void> updatePileRea(@RequestParam String id, @RequestParam String userId, @RequestParam String readwriteObject);

    @PostMapping("updatePileGun")
    @ApiOperation("编辑电枪数据")
    @ApiOperationSupport(order = 13)
    ResponseResult<Void> updatePileGun(@RequestBody PileGunChangeVo pileGunChangeVo);

    @PostMapping("updateSiteStatusById")
    @ApiOperation("根据站点id修改站点状态")
    @ApiOperationSupport(order = 14)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "所属站点id", paramType = "query", required = true),
            @ApiImplicitParam(name = "siteStatus", value = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中", paramType = "query", required = true)
    })
    ResponseResult<String> updateSiteStateById(@RequestParam String siteId, @RequestParam Integer siteStatus);

    @PostMapping("findSiteAffiliatesInfoByIds")
    @ApiOperation("根据多个站点id查询关联方信息数据")
    @ApiOperationSupport(order = 15)
    ResponseResult<Map<String, List<AffiliatesInfoDto>>> findSiteAffiliatesInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findAllSiteBasicInfoList")
    @ApiOperation("查询全部站点详情列表")
    @ApiOperationSupport(order = 16)
    ResponseResult<List<SiteInfoDto>> findAllSiteBasicInfoList(@RequestParam(required = false) String siteNameLike);

    @PostMapping("checkSitePassword")
    @ApiOperation("校验站点密码是否正确")
    @ApiOperationSupport(order = 17)
    ResponseResult<Integer> checkSitePassword(@RequestParam String siteId, @RequestParam String password);

    @PostMapping("updateDeviceOperateStatus")
    @ApiOperation("修改设备运营状态")
    @ApiOperationSupport(order = 18)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "operateStatus", value = "设备运营状态 1-投运 2-检修 3-退役", paramType = "query", required = true)
    })
    ResponseResult<String> updateDeviceOperateStatus(@RequestParam String deviceId, @RequestParam Integer operateStatus);

    @PostMapping("findDeviceFunctionListByIds")
    @ApiOperation("根据多个设备id查询设备功能属性列表数据")
    @ApiOperationSupport(order = 19)
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findDeviceFunctionListByIds(@RequestBody List<String> deviceIdList);

    @PostMapping("findDeviceNotRecoveEventList")
    @ApiOperation("根据设备id查询设备所有未恢复事件告警数据")
    @ApiOperationSupport(order = 20)
    ResponseResult<List<DeviceAlarmEventListDto>> findDeviceNotRecoveEventList(@RequestParam String deviceId);

    @PostMapping("findDeviceFunctionDataList")
    @ApiOperation("根据设备id查询设备功能属性列表数据")
    @ApiOperationSupport(order = 21)
    @ApiImplicitParam(name = "deviceId", value = "设备id", dataType = "String", required = true)
    ResponseResult<List<FunctionDataDto>> findDeviceFunctionDataList(@RequestParam String deviceId);

    @PostMapping("findDeviceTxStatus")
    @ApiOperation("查询设备通信状态")
    @ApiOperationSupport(order = 22)
    ResponseResult<Map<String, Integer>> findDeviceTxStatus(@RequestBody List<DeviceBasicInfoDto> deviceBasicInfoDtos);

    @PostMapping("findDeviceInfoByParentIds")
    @ApiOperation("根据多个父节点id查询下级设备列表数据")
    @ApiOperationSupport(order = 23)
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceInfoByParentIds(@RequestBody List<String> parentIdList);

    @PostMapping("findSiteInfoListByIds")
    @ApiOperation("根据多个站点id查询站站点详情数据(只查询站点本体表结构数据，关于其他关联和设计别的表字段信息不查询返回,使用时看清楚)")
    @ApiOperationSupport(order = 24)
    ResponseResult<List<SiteInfoDto>> findSiteInfoListByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findSiteSetUpBySiteIds")
    @ApiOperation("根据多个站点id查询站站点设置数据")
    @ApiOperationSupport(order = 25)
    ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(@RequestBody List<String> siteIdList);

    @PostMapping("findSiteMeasureIdBySiteIds")
    @ApiOperation("根据多个站点id查询站点关联计量设备id")
    @ApiOperationSupport(order = 26)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nodeType", value = "节点类型 0-计量节点 1-设备节点", paramType = "query", required = true),
            @ApiImplicitParam(name = "deviceType", value = "设备类型 1-光伏 2-储能 3-电桩 4-其他", paramType = "query", required = true)
    })
    ResponseResult<Map<String, List<String>>> findSiteMeasureIdBySiteIds(@RequestBody List<String> siteIdList, @RequestParam Integer nodeType, @RequestParam Integer deviceType);

    @PostMapping("findSiteIdBySubId")
    @ApiOperation("根据子系统id查询站点id")
    @ApiOperationSupport(order = 27)
    ResponseResult<String> findSiteIdBySubId(@RequestParam String subId);

    @PostMapping("findAllDeviceEventList")
    @ApiOperation("根据查询条件查询设备告警事件列表")
    @ApiOperationSupport(order = 28)
    ResponseResult<PageDto<DeviceAlarmEventListDto>> findAllDeviceEventList(@RequestBody DeviceAlarmEventQueryVo eventQueryVo);

    @PostMapping("updateEventIgnoreStatus")
    @ApiOperation("修改设备事件忽略状态")
    @ApiOperationSupport(order = 29)
    ResponseResult<Void> updateEventIgnoreStatus(@RequestParam String id, @RequestParam Integer ignoreStatus);

    @PostMapping("findSiteTopDataListBySiteId")
    @ApiOperation("根据站点id查询拓扑节点实时数据")
    @ApiOperationSupport(order = 30)
    ResponseResult<List<SiteTopDataDto>> findSiteTopDataListBySiteId(@RequestParam String siteId);

    @PostMapping("findSiteTopNodeBySiteId")
    @ApiOperation("根据站点id查询拓扑节点静态数据")
    @ApiOperationSupport(order = 31)
    ResponseResult<List<SiteTopNodeDto>> findSiteTopNodeBySiteId(@RequestParam String siteId, @RequestParam(required = false) Integer nodeType);

}
