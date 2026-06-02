package com.sunmax.together.controller.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.protocol.PileSetQrVo;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.deviceManage.PileGunListDto;
import com.sunmax.together.service.operation.DeviceManageService;
import com.sunmax.together.vo.operation.deviceManage.AlarmListQueryVo;
import com.sunmax.together.vo.operation.deviceManage.PileListQueryVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("deviceManage")
@Api(tags = "设备管理")
public class DeviceManageController {

    @Autowired
    private DeviceManageService deviceManageService;

    @PostMapping("findPileListByPage")
    @ApiOperation("分页查询电桩列表信息")
    @ApiOperationSupport(order = 1)
    public ResponseResult<PageDto<PileGunListDto>> findPileListByPage(PileListQueryVo pileListQueryVo) {
        return deviceManageService.findPileListByPage(pileListQueryVo);
    }

    @PostMapping("updateDeviceOperateStatus")
    @ApiOperation("修改设备运营状态")
    @WebLog("桩枪管理-修改设备运营状态")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deviceId", value = "设备唯一id", paramType = "query", required = true),
            @ApiImplicitParam(name = "operateStatus", value = "设备运营状态 1-投运 2-检修 3-退役", paramType = "query", required = true)
    })
    public ResponseResult<String> updateDeviceOperateStatus(String deviceId, Integer operateStatus) {
        return deviceManageService.updateDeviceOperateStatus(deviceId, operateStatus);
    }

    @PostMapping("findAlarmListByPage")
    @ApiOperation("分页查询告警列表数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<?> findAlarmListByPage(AlarmListQueryVo alarmListQueryVo) {
        return deviceManageService.findAlarmListByPage(alarmListQueryVo);
    }

    @PostMapping("pileSetQr")
    @ApiOperation("设置二维码")
    @WebLog("桩枪管理-设置二维码")
    @ApiOperationSupport(order = 4)
    public ResponseResult<Void> pileSetQr(PileSetQrVo pileSetQrVo) {
        return deviceManageService.pileSetQr(pileSetQrVo);
    }


}
