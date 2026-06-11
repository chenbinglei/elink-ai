package com.sunmax.common.feign.protocol;

import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.DevicePointDto;
import com.sunmax.common.dto.device.PileFaultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceBatchUpdateVo;
import com.sunmax.common.vo.device.DeviceEventChangeVo;
import com.sunmax.common.vo.protocol.PileSetQrVo;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.sunmax.common.feign.fallback.GenericFeignFallbackFactory;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "device-service", path = "/device/feign/protocol", fallbackFactory = GenericFeignFallbackFactory.class)
public interface ProtocolDeviceFeignClient {

    @PostMapping("getDeviceNumberList")
    @Operation(summary = "获取设备序列号列表数据")
    
    ResponseResult<Set<String>> getDeviceNumberList(@RequestParam(required = false) Integer accessType);

    @PostMapping("findDeviceEventIds")
    @Operation(summary = "查询设备下的事件数据")
    
    ResponseResult<Map<String, Set<String>>> findDeviceEventIds(@RequestParam(required = false) Integer eventStatus);

    @PostMapping("batchUpdateDeviceEvent")
    @Operation(summary = "批量更新设备事件数据")
    
    ResponseResult<Void> batchUpdateDeviceEvent(@RequestBody List<DeviceEventChangeVo> deviceEventVos);

    @PostMapping("findDeviceModelIds")
    @Operation(summary = "查询所有设备的模型id")
    
    ResponseResult<Map<String, String>> findDeviceModelIds(@RequestParam(required = false) String deviceId);

    @PostMapping("findPileFaultList")
    @Operation(summary = "根据设备编号和故障码查询电桩故障列表")
    
    ResponseResult<Map<Integer, PileFaultDto>> findPileFaultList(@RequestParam String deviceCode, @RequestBody Set<Integer> faultCodes);

    @PostMapping("findDeviceBasicInfoByCodes")
    @Operation(summary = "根据多个设备编码查询设备详情数据")
    
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(@RequestBody List<String> deviceCodeList);

    @PostMapping("batchUpdateTask")
    @Operation(summary = "批量更新设备任务数据")
    
    ResponseResult<Void> batchUpdateDeviceTask(@RequestBody DeviceBatchUpdateVo deviceUpdateVo);

    @PostMapping("findAllDevicePointByModelIds")
    @Operation(summary = "根据多个模型id查询设备点号数据")
    
    ResponseResult<Map<String, DevicePointDto>> findAllDevicePointByModelIds(@RequestBody Set<String> modelIds);

    @PostMapping("findAllDeviceInfoByTypeIds")
    @Operation(summary = "根据多个类型id查询设备详情数据")
    
    ResponseResult<Map<String, DeviceBasicInfoDto>> findAllDeviceInfoByTypeIds(@RequestBody Set<String> typeIds);

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @Operation(summary = "根据多个站点id查询设备列表数据")
    
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIds, @RequestParam(required = false) Integer deviceType);

    @PostMapping("updateDeviceQrStr")
    @Operation(summary = "更新设备二维码地址")
    
    ResponseResult<Void> updateDeviceQrStr(@RequestBody PileSetQrVo pileSetQrVo);

    @PostMapping("findDeviceGunInfoByDeviceCodes")
    @Operation(summary = "根据多个设备编码查询设备电枪数据")
    
    ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceCodes(@RequestBody List<String> deviceCodes);

}
