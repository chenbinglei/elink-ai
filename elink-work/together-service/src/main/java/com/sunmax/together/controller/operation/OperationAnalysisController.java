package com.sunmax.together.controller.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.operationAnalysis.OperationCurveDto;
import com.sunmax.together.dto.operation.operationAnalysis.OperationOverviewDto;
import com.sunmax.together.dto.operation.operationAnalysis.PvOperationAnalysisDto;
import com.sunmax.together.service.operation.OperationAnalysisService;
import com.sunmax.common.vo.together.OperationCurveVo;
import com.sunmax.common.vo.together.OperationOverviewVo;
import com.sunmax.together.vo.operation.operationAnalysis.PvOperationAnalysisVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@CrossOrigin
@RequestMapping("operationAnalysis")
@Api(tags = "经营分析管理")
public class OperationAnalysisController {

    @Autowired
    private OperationAnalysisService operationAnalysisService;

    @PostMapping("countOperationOverview")
    @ApiOperation("统计充电站运营总览数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<OperationOverviewDto> countOperationOverview(OperationOverviewVo operationOverviewVo) {
        return operationAnalysisService.countOperationOverview(operationOverviewVo);
    }

    @PostMapping("countOperationCurve")
    @ApiOperation("统计充电站运营总览曲线数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<OperationCurveDto> countOperationCurve(OperationCurveVo operationCurveVo) {
        return operationAnalysisService.countOperationCurve(operationCurveVo);
    }

    @PostMapping("addAllSiteCountRecord")
    @ApiOperation("批量添加站点统计数据")
    @WebLog("经营分析-批量添加站点统计数据")
    @ApiOperationSupport(order = 3)
    @ApiIgnore
    public ResponseResult<Void> addAllSiteCountRecord(String startDate, String endDate) {
        return operationAnalysisService.addAllSiteCountRecord(startDate, endDate);
    }

    @PostMapping("countPvOperationAnalysis")
    @ApiOperation("统计光伏运营运行分析数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<PvOperationAnalysisDto> countPvOperationAnalysis(PvOperationAnalysisVo pvOperationAnalysisVo) {
        return operationAnalysisService.countPvOperationAnalysis(pvOperationAnalysisVo);
    }

}
