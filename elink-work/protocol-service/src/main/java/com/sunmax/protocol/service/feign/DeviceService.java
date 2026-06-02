package com.sunmax.protocol.service.feign;

import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.DevicePointDto;
import com.sunmax.common.dto.device.PileFaultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceBatchUpdateVo;
import com.sunmax.common.vo.device.DeviceEventChangeVo;
import com.sunmax.common.vo.protocol.PileSetQrVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 接收数据服务提供的接口
 */
@FeignClient(value = "device-service")
@RestController
@RequestMapping("/device/feign/protocol")
public interface DeviceService {

    @PostMapping("getDeviceNumberList")
    @ApiOperation("获取设备序列号列表数据")
    @ApiOperationSupport(order = 1)
    ResponseResult<Set<String>> getDeviceNumberList(@RequestParam(required = false) Integer accessType);

    @PostMapping("findDeviceEventIds")
    @ApiOperation("查询设备下的事件数据")
    @ApiOperationSupport(order = 2)
    ResponseResult<Map<String, Set<String>>> findDeviceEventIds(@RequestParam(required = false) Integer eventStatus);

    @PostMapping("batchUpdateDeviceEvent")
    @ApiOperation("批量更新设备事件数据")
    @ApiOperationSupport(order = 3)
    ResponseResult<Void> batchUpdateDeviceEvent(@RequestBody List<DeviceEventChangeVo> deviceEventVos);

    @PostMapping("findDeviceModelIds")
    @ApiOperation("查询所有设备的模型id")
    @ApiOperationSupport(order = 4)
    ResponseResult<Map<String, String>> findDeviceModelIds(@RequestParam(required = false) String deviceId);

    @PostMapping("findPileFaultList")
    @ApiOperation("根据设备编号和故障码查询电桩故障列表")
    @ApiOperationSupport(order = 5)
    ResponseResult<Map<Integer, PileFaultDto>> findPileFaultList(@RequestParam String deviceCode, @RequestBody Set<Integer> faultCodes);

    @PostMapping("findDeviceBasicInfoByCodes")
    @ApiOperation("根据多个设备编码查询设备详情数据")
    @ApiOperationSupport(order = 6)
    ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(@RequestBody List<String> deviceCodeList);

    @PostMapping("batchUpdateTask")
    @ApiOperation("批量更新设备任务数据")
    @ApiOperationSupport(order = 7)
    ResponseResult<Void> batchUpdateDeviceTask(@RequestBody DeviceBatchUpdateVo deviceUpdateVo);

    @PostMapping("findAllDevicePointByModelIds")
    @ApiOperation("根据多个模型id查询设备点号数据")
    @ApiOperationSupport(order = 8)
    ResponseResult<Map<String, DevicePointDto>> findAllDevicePointByModelIds(@RequestBody Set<String> modelIds);

    @PostMapping("findAllDeviceInfoByTypeIds")
    @ApiOperation("根据多个类型id查询设备详情数据")
    @ApiOperationSupport(order = 9)
    ResponseResult<Map<String, DeviceBasicInfoDto>> findAllDeviceInfoByTypeIds(@RequestBody Set<String> typeIds);

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @ApiOperation("根据多个站点id查询设备列表数据")
    @ApiOperationSupport(order = 10)
    ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIds, @RequestParam(required = false) Integer deviceType);

    @PostMapping("updateDeviceQrStr")
    @ApiOperation("更新设备二维码地址")
    @ApiOperationSupport(order = 11)
    ResponseResult<Void> updateDeviceQrStr(@RequestBody PileSetQrVo pileSetQrVo);

    @PostMapping("findDeviceGunInfoByDeviceCodes")
    @ApiOperation("根据多个设备编码查询设备电枪数据")
    @ApiOperationSupport(order = 12)
    ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceCodes(@RequestBody List<String> deviceCodes);

}
