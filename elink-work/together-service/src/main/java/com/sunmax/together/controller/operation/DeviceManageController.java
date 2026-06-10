package com.sunmax.together.controller.operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PileSetQrVo;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.deviceManage.PileGunListDto;
import com.sunmax.together.service.operation.DeviceManageService;
import com.sunmax.together.vo.operation.deviceManage.AlarmListQueryVo;
import com.sunmax.together.vo.operation.deviceManage.PileListQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("deviceManage")
@Tag(name = "设备管理")
public class DeviceManageController {

    @Autowired
    private DeviceManageService deviceManageService;

    @PostMapping("findPileListByPage")
    @Operation(summary = "分页查询电桩列表信息")
    
    public ResponseResult<PageDto<PileGunListDto>> findPileListByPage(PileListQueryVo pileListQueryVo) {
        return deviceManageService.findPileListByPage(pileListQueryVo);
    }

    @PostMapping("updateDeviceOperateStatus")
    @Operation(summary = "修改设备运营状态")
    @WebLog("桩枪管理-修改设备运营状态")
    
    @Parameters({
            @Parameter(name = "deviceId", description = "设备唯一id"),
            @Parameter(name = "operateStatus", description = "设备运营状态 1-投运 2-检修 3-退役")
    })
    public ResponseResult<String> updateDeviceOperateStatus(String deviceId, Integer operateStatus) {
        return deviceManageService.updateDeviceOperateStatus(deviceId, operateStatus);
    }

    @PostMapping("findAlarmListByPage")
    @Operation(summary = "分页查询告警列表数据")
    
    public ResponseResult<?> findAlarmListByPage(AlarmListQueryVo alarmListQueryVo) {
        return deviceManageService.findAlarmListByPage(alarmListQueryVo);
    }

    @PostMapping("pileSetQr")
    @Operation(summary = "设置二维码")
    @WebLog("桩枪管理-设置二维码")
    
    public ResponseResult<Void> pileSetQr(PileSetQrVo pileSetQrVo) {
        return deviceManageService.pileSetQr(pileSetQrVo);
    }


}
