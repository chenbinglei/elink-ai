package com.sunmax.device.controller.feign;

import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.dto.device.DevicePointDto;
import com.sunmax.common.dto.device.PileFaultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.device.DeviceBatchUpdateVo;
import com.sunmax.common.vo.device.DeviceEventChangeVo;
import com.sunmax.common.vo.protocol.PileSetQrVo;
import com.sunmax.device.service.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("/feign/protocol")
@Tag(name = "提供给协议服务调用的远程接口")
@Hidden()
public class ProtocolFeignController {

    @Autowired
    private ProtocolFeignService protocolFeignService;

    @Autowired
    private DataFeignService dataFeignService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceTaskService deviceTaskService;

    @Autowired
    private CrontabFeignService crontabFeignService;

    @PostMapping("getDeviceNumberList")
    @Operation(summary = "获取设备序列号列表数据")
    
    public ResponseResult<Set<String>> getDeviceNumberList(@RequestParam(required = false) Integer accessType) {
        return protocolFeignService.getDeviceNumberList(accessType);
    }

    @PostMapping("findDeviceEventIds")
    @Operation(summary = "查询设备下的事件数据")
    
    public ResponseResult<Map<String, Set<String>>> findDeviceEventIds(@RequestParam(required = false) Integer eventStatus) {
        return dataFeignService.findDeviceEventIds(eventStatus);
    }

    @PostMapping("batchUpdateDeviceEvent")
    @Operation(summary = "批量更新设备事件数据")
    
    public ResponseResult<Void> batchUpdateDeviceEvent(@RequestBody List<DeviceEventChangeVo> deviceEventVos) {
        return dataFeignService.batchUpdateDeviceEvent(deviceEventVos);
    }

    @PostMapping("findDeviceModelIds")
    @Operation(summary = "查询所有设备的模型id")
    
    public ResponseResult<Map<String, String>> findDeviceModelIds(@RequestParam(required = false) String deviceId) {
        return dataFeignService.findDeviceModelIds(deviceId);
    }

    @PostMapping("findPileFaultList")
    @Operation(summary = "根据设备编号和故障码查询电桩故障列表")
    
    public ResponseResult<Map<Integer, PileFaultDto>> findPileFaultList(@RequestParam String deviceCode, @RequestBody Set<Integer> faultCodes) {
        return protocolFeignService.findPileFaultList(deviceCode, faultCodes);
    }

    @PostMapping("findDeviceBasicInfoByCodes")
    @Operation(summary = "根据多个设备编码查询设备详情数据")
    
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findDeviceBasicInfoByCodes(@RequestBody List<String> deviceCodeList) {
        return deviceService.findDeviceBasicInfoByCodes(deviceCodeList);
    }

    @PostMapping("batchUpdateTask")
    @Operation(summary = "批量更新设备任务数据")
    
    public ResponseResult<Void> batchUpdateDeviceTask(@RequestBody DeviceBatchUpdateVo deviceUpdateVo) {
        return deviceTaskService.batchUpdateDeviceTask(deviceUpdateVo);
    }

    @PostMapping("findAllDevicePointByModelIds")
    @Operation(summary = "根据多个模型id查询设备点号数据")
    
    public ResponseResult<Map<String, DevicePointDto>> findAllDevicePointByModelIds(@RequestBody Set<String> modelIds) {
        return protocolFeignService.findAllDevicePointByModelIds(modelIds);
    }

    @PostMapping("findAllDeviceInfoByTypeIds")
    @Operation(summary = "根据多个类型id查询设备详情数据")
    
    public ResponseResult<Map<String, DeviceBasicInfoDto>> findAllDeviceInfoByTypeIds(@RequestBody Set<String> typeIds) {
        return protocolFeignService.findAllDeviceInfoByTypeIds(typeIds);
    }

    @PostMapping("findDeviceBasicInfoBySiteIds")
    @Operation(summary = "根据多个站点id查询设备列表数据")
    
    public ResponseResult<Map<String, List<DeviceBasicInfoDto>>> findDeviceBasicInfoBySiteIds(@RequestBody List<String> siteIds, @RequestParam(required = false) Integer deviceType) {
        return crontabFeignService.findDeviceBasicInfoBySiteIds(siteIds, deviceType);
    }

    @PostMapping("updateDeviceQrStr")
    @Operation(summary = "更新设备二维码地址")
    
    public ResponseResult<Void> updateDeviceQrStr(@RequestBody PileSetQrVo pileSetQrVo) {
        return protocolFeignService.updateDeviceQrStr(pileSetQrVo);
    }

    @PostMapping("findDeviceGunInfoByDeviceCodes")
    @Operation(summary = "根据多个设备编码查询设备电枪数据")
    
    public ResponseResult<Map<String, List<DeviceGunInfoDto>>> findDeviceGunInfoByDeviceCodes(@RequestBody List<String> deviceCodes) {
        return protocolFeignService.findDeviceGunInfoByDeviceCodes(deviceCodes);
    }

}
