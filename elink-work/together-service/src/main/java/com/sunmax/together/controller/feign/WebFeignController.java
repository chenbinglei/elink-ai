package com.sunmax.together.controller.feign;

import com.sunmax.common.dto.together.PileGunMonitorDataDto;
import com.sunmax.common.dto.together.PileMonitorDataDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.operationAnalysis.OperationOverviewDto;
import com.sunmax.together.service.operation.OperationAnalysisService;
import com.sunmax.common.vo.together.OperationOverviewVo;
import com.sunmax.together.service.WebFeignService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/feign/web")
@Api(tags = "提供给webapp服务的接口")
public class WebFeignController {

    @Autowired
    private OperationAnalysisService operationAnalysisService;

    @Autowired
    private WebFeignService webFeignService;

    @PostMapping("countOperationOverviewBySiteId")
    @ApiOperation("根据站点id统计运行总览数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Map<String, OperationOverviewDto>> countOperationOverviewBySiteId(@RequestBody OperationOverviewVo operationOverviewVo) {
        return operationAnalysisService.countOperationOverviewBySiteId(operationOverviewVo);
    }

    @PostMapping("countPileMonitorData")
    @ApiOperation("根据多个数据id和类型统计电桩监控数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<Map<String, PileMonitorDataDto>> countPileMonitorData(@RequestBody List<String> dataIds, @RequestParam Integer type) {
        return operationAnalysisService.countPileMonitorData(dataIds, type);
    }

    @PostMapping("findAllPileGunMonitorList")
    @ApiOperation("根据多个设备id查询电枪监控数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<Map<String, List<PileGunMonitorDataDto>>> findAllPileGunMonitorList(@RequestBody List<String> deviceIds) {
        return webFeignService.findAllPileGunMonitorList(deviceIds);
    }

}
