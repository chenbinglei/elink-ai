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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Hidden;

@RestController
@CrossOrigin
@RequestMapping("operationAnalysis")
@Tag(name = "经营分析管理")
public class OperationAnalysisController {

    @Autowired
    private OperationAnalysisService operationAnalysisService;

    @PostMapping("countOperationOverview")
    @Operation(summary = "统计充电站运营总览数据")
    
    public ResponseResult<OperationOverviewDto> countOperationOverview(OperationOverviewVo operationOverviewVo) {
        return operationAnalysisService.countOperationOverview(operationOverviewVo);
    }

    @PostMapping("countOperationCurve")
    @Operation(summary = "统计充电站运营总览曲线数据")
    
    public ResponseResult<OperationCurveDto> countOperationCurve(OperationCurveVo operationCurveVo) {
        return operationAnalysisService.countOperationCurve(operationCurveVo);
    }

    @PostMapping("addAllSiteCountRecord")
    @Operation(summary = "批量添加站点统计数据")
    @WebLog("经营分析-批量添加站点统计数据")
    
    @Hidden
    public ResponseResult<Void> addAllSiteCountRecord(String startDate, String endDate) {
        return operationAnalysisService.addAllSiteCountRecord(startDate, endDate);
    }

    @PostMapping("countPvOperationAnalysis")
    @Operation(summary = "统计光伏运营运行分析数据")
    
    public ResponseResult<PvOperationAnalysisDto> countPvOperationAnalysis(PvOperationAnalysisVo pvOperationAnalysisVo) {
        return operationAnalysisService.countPvOperationAnalysis(pvOperationAnalysisVo);
    }

}
