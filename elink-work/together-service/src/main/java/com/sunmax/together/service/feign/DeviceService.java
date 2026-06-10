package com.sunmax.together.service.feign;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.device.*;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceAlarmEventQueryVo;
import com.sunmax.common.vo.together.PileGunChangeVo;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 接收设备服务提供的接口
 */
@FeignClient(value = "device-service", path = "/device/feign/together")
//@FeignClient(value = "device-service-cbl",url = "http://121.41.109.130:5000")
public interface DeviceService {

    @PostMapping("findSiteBasicInfoByIds")
    @Operation(summary = "根据多个站点id查询站站点详情数据")
    
    ResponseResult<Map<String, SiteInfoDto>> findSiteBasicInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findDeviceBasicInfoByCodes")
    @Operation(summary = "根据多个设备编码查询设备详情数据")
    
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(@RequestBody List<String> deviceCodeList);

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @Operation(summary = "根据多个站点id查询设备列表数据")
    
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIdList, @RequestParam(required = false) Integer deviceType);

    @PostMapping("findSiteEnergyInfoBySiteIds")
    @Operation(summary = "根据多个站点id查询能源信息数据")
    
    ResponseResult<Map<String, SiteEnergyInfoDto>> findSiteEnergyInfoBySiteIds(@RequestBody List<String> siteIdList);

    @PostMapping("findGatewayChildDeviceById")
    @Operation(summary = "根据网关id查询网关下所有子设备信息")
    
    ResponseResult<List<DeviceBasicInfoDto>> findGatewayChildDeviceById(@RequestParam String gatWayId);

    @PostMapping("findDeviceGunInfoByDeviceIds")
    @Operation(summary = "根据多个设备id查询设备电枪数据")
    
    ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceIds(@RequestBody List<String> devlceIdList);

    @PostMapping("findDeviceBasicInfoByIds")
    @Operation(summary = "根据多个设备id查询设备基本信息")
    
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByIds(@RequestBody List<String> deviceIdList);

    @PostMapping("findDeviceReaListById")
    @Operation(summary = "根据设备id查询设备扩展属性列表数据")
    
    ResponseResult<List<DeviceReaDto>> findDeviceReaListById(@RequestParam String deviceId);

    @PostMapping("findAllPileDeviceInfoList")
    @Operation(summary = "查询所有电桩设备信息列表")
    
    ResponseResult<List<DeviceBasicInfoDto>> findAllPileDeviceInfoList();

    @PostMapping("findModelFunctionListByModelIds")
    @Operation(summary = "根据多个模型id查询模型功能点列表")
    
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findModelFunctionListByModelIds(@RequestBody List<String> modelIdList);

    @PostMapping("getDeviceFunctionsRealDataByIds")
    @Operation(summary = "获取设备功能点实时数据数据")
    
    @Parameters({
            @Parameter(name = "deviceIds", description = "多个设备id"),
            @Parameter(name = "functionLogos", description = "多个功能点标识"),
            @Parameter(name = "dataType", description = "数据类型 1-原始缓存数据 2-超过15分钟未上报的数据(置空)")
    })
    ResponseResult<Map<String, Map<String, RealDataModel>>> getDeviceFunctionsRealDataByIds(@RequestBody Set<String> deviceIds,
                                                                                            @RequestParam String functionLogos,
                                                                                            @RequestParam Integer dataType);

    @PostMapping("updatePileRea")
    @Operation(summary = "编辑电桩扩展属性数据")
    
    ResponseResult<Void> updatePileRea(@RequestParam String id, @RequestParam String userId, @RequestParam String readwriteObject);

    @PostMapping("updatePileGun")
    @Operation(summary = "编辑电枪数据")
    
    ResponseResult<Void> updatePileGun(@RequestBody PileGunChangeVo pileGunChangeVo);

    @PostMapping("updateSiteStatusById")
    @Operation(summary = "根据站点id修改站点状态")
    
    @Parameters({
            @Parameter(name = "siteId", description = "所属站点id"),
            @Parameter(name = "siteStatus", description = "站点状态 1-正常投运 2-关闭下线 3-维护中 4-建设中")
    })
    ResponseResult<String> updateSiteStateById(@RequestParam String siteId, @RequestParam Integer siteStatus);

    @PostMapping("findSiteAffiliatesInfoByIds")
    @Operation(summary = "根据多个站点id查询关联方信息数据")
    
    ResponseResult<Map<String, List<AffiliatesInfoDto>>> findSiteAffiliatesInfoByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findAllSiteBasicInfoList")
    @Operation(summary = "查询全部站点详情列表")
    
    ResponseResult<List<SiteInfoDto>> findAllSiteBasicInfoList(@RequestParam(required = false) String siteNameLike);

    @PostMapping("checkSitePassword")
    @Operation(summary = "校验站点密码是否正确")
    
    ResponseResult<Integer> checkSitePassword(@RequestParam String siteId, @RequestParam String password);

    @PostMapping("updateDeviceOperateStatus")
    @Operation(summary = "修改设备运营状态")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备唯一id"),
            @Parameter(name = "operateStatus", description = "设备运营状态 1-投运 2-检修 3-退役")
    })
    ResponseResult<String> updateDeviceOperateStatus(@RequestParam String deviceId, @RequestParam Integer operateStatus);

    @PostMapping("findDeviceFunctionListByIds")
    @Operation(summary = "根据多个设备id查询设备功能属性列表数据")
    
    ResponseResult<Map<String, List<ModelFunctionListDto>>> findDeviceFunctionListByIds(@RequestBody List<String> deviceIdList);

    @PostMapping("findDeviceNotRecoveEventList")
    @Operation(summary = "根据设备id查询设备所有未恢复事件告警数据")
    
    ResponseResult<List<DeviceAlarmEventListDto>> findDeviceNotRecoveEventList(@RequestParam String deviceId);

    @PostMapping("findDeviceFunctionDataList")
    @Operation(summary = "根据设备id查询设备功能属性列表数据")
    
    @Parameter(name = "deviceId", description = "设备id")
    ResponseResult<List<FunctionDataDto>> findDeviceFunctionDataList(@RequestParam String deviceId);

    @PostMapping("findDeviceTxStatus")
    @Operation(summary = "查询设备通信状态")
    
    ResponseResult<Map<String, Integer>> findDeviceTxStatus(@RequestBody List<DeviceBasicInfoDto> deviceBasicInfoDtos);

    @PostMapping("findDeviceInfoByParentIds")
    @Operation(summary = "根据多个父节点id查询下级设备列表数据")
    
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceInfoByParentIds(@RequestBody List<String> parentIdList);

    @PostMapping("findSiteInfoListByIds")
    @Operation(summary = "根据多个站点id查询站站点详情数据(只查询站点本体表结构数据，关于其他关联和设计别的表字段信息不查询返回,使用时看清楚)")
    
    ResponseResult<List<SiteInfoDto>> findSiteInfoListByIds(@RequestBody List<String> siteIdList);

    @PostMapping("findSiteSetUpBySiteIds")
    @Operation(summary = "根据多个站点id查询站站点设置数据")
    
    ResponseResult<Map<String, SiteSetUpDto>> findSiteSetUpBySiteIds(@RequestBody List<String> siteIdList);

    @PostMapping("findSiteMeasureIdBySiteIds")
    @Operation(summary = "根据多个站点id查询站点关联计量设备id")
    
    @Parameters({
            @Parameter(name = "nodeType", description = "节点类型 0-计量节点 1-设备节点"),
            @Parameter(name = "deviceType", description = "设备类型 1-光伏 2-储能 3-电桩 4-其他")
    })
    ResponseResult<Map<String, List<String>>> findSiteMeasureIdBySiteIds(@RequestBody List<String> siteIdList, @RequestParam Integer nodeType, @RequestParam Integer deviceType);

    @PostMapping("findSiteIdBySubId")
    @Operation(summary = "根据子系统id查询站点id")
    
    ResponseResult<String> findSiteIdBySubId(@RequestParam String subId);

    @PostMapping("findAllDeviceEventList")
    @Operation(summary = "根据查询条件查询设备告警事件列表")
    
    ResponseResult<PageDto<DeviceAlarmEventListDto>> findAllDeviceEventList(@RequestBody DeviceAlarmEventQueryVo eventQueryVo);

    @PostMapping("updateEventIgnoreStatus")
    @Operation(summary = "修改设备事件忽略状态")
    
    ResponseResult<Void> updateEventIgnoreStatus(@RequestParam String id, @RequestParam Integer ignoreStatus);

    @PostMapping("findSiteTopDataListBySiteId")
    @Operation(summary = "根据站点id查询拓扑节点实时数据")
    
    ResponseResult<List<SiteTopDataDto>> findSiteTopDataListBySiteId(@RequestParam String siteId);

    @PostMapping("findSiteTopNodeBySiteId")
    @Operation(summary = "根据站点id查询拓扑节点静态数据")
    
    ResponseResult<List<SiteTopNodeDto>> findSiteTopNodeBySiteId(@RequestParam String siteId, @RequestParam(required = false) Integer nodeType);

}
