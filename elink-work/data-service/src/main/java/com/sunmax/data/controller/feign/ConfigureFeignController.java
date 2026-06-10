package com.sunmax.data.controller.feign;

import com.sunmax.common.dto.data.NodeDifHistoryDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.data.DeviceHistoryQueryVo;
import com.sunmax.data.service.DeviceFeignService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/feign/configure")
@Tag(name = "提供给配置服务调用的远程接口")
@Hidden()
public class ConfigureFeignController {

    @Autowired
    private DeviceFeignService deviceFeignService;

    @PostMapping("findNodeDifHistoryListFeign")
    @Operation(summary = "查询设备功能点指定时间段内的last和first历史数据")
    
    public ResponseResult<Map<String, Map<String, List<NodeDifHistoryDto>>>> findNodeDifHistoryListFeign(@RequestBody DeviceHistoryQueryVo deviceQueryVo) {
        return deviceFeignService.findNodeDifHistoryListFeign(deviceQueryVo);
    }
}
