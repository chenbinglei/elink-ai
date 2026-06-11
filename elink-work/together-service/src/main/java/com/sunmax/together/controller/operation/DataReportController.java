package com.sunmax.together.controller.operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.dataReport.DeviceTreeListDto;
import com.sunmax.together.dto.operation.dataReport.PileReportDto;
import com.sunmax.together.dto.operation.dataReport.SiteReportDto;
import com.sunmax.together.dto.operation.dataReport.SummaryCountDto;
import com.sunmax.together.service.operation.DataReportService;
import com.sunmax.together.vo.operation.dataReport.DataReportQueryVo;
import com.sunmax.together.vo.operation.dataReport.InverterReportQueryVo;
import com.sunmax.together.vo.operation.dataReport.PlatformDetailsVo;
import com.sunmax.together.vo.operation.dataReport.PvSiteReportQueryVo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("dataReport")
@Tag(name = "数据报表管理")
public class DataReportController {

    @Autowired
    private DataReportService dataReportService;

    @PostMapping("findSiteChargeReport")
    @Operation(summary = "查询电站充放电报表")
    
    public ResponseResult<SiteReportDto> findSiteChargeReport(DataReportQueryVo dataReportQueryVo, String userId) {
        return dataReportService.findSiteChargeReport(dataReportQueryVo, userId);
    }

    @PostMapping("findSiteRunReport")
    @Operation(summary = "查询电站运行报表")
    
    public ResponseResult<?> findSiteRunReport(DataReportQueryVo dataReportQueryVo, String userId) {
        return dataReportService.findSiteRunReport(dataReportQueryVo, userId);
    }

    @PostMapping("findPileChargeReport")
    @Operation(summary = "查询电桩充放电报表")
    
    public ResponseResult<PileReportDto> findPileChargeReport(DataReportQueryVo dataReportQueryVo, String userId) {
        return dataReportService.findPileChargeReport(dataReportQueryVo, userId);
    }

    @PostMapping("findPileRunReport")
    @Operation(summary = "查询电桩运行报表")
    
    public ResponseResult<?> findPileRunReport(DataReportQueryVo dataReportQueryVo, String userId) {
        return dataReportService.findPileRunReport(dataReportQueryVo, userId);
    }

    @PostMapping("findAccountList")
    @Operation(summary = "查询商户列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "用户id"),
            @Parameter(name = "queryMode", description = "查询模式 1-充电 2-放电 3-分账")
    })
    public ResponseResult<List<AccountDto>> findAccountList(String userId, Integer queryMode) {
        return dataReportService.findAccountList(userId, queryMode);
    }

    @PostMapping("findSummaryCountData")
    @Operation(summary = "查询渠道汇总统计数据")
    
    public ResponseResult<SummaryCountDto> findSummaryCountData(DataReportQueryVo dataReportQueryVo, String userId) {
        return dataReportService.findSummaryCountData(dataReportQueryVo, userId);
    }

    @PostMapping("findPlatformDetailsList")
    @Operation(summary = "查询渠道充电明细列表")
    
    public ResponseResult<?> findPlatformDetailsList(PlatformDetailsVo platformDetailsVo, String userId) {
        return dataReportService.findPlatformDetailsList(platformDetailsVo, userId);
    }

    @PostMapping("findPvSiteReportList")
    @Operation(summary = "分页查询光伏站点报表列表数据")
    
    public ResponseResult<?> findPvSiteReportList(PvSiteReportQueryVo pvSiteReportVo) {
        return dataReportService.findPvSiteReportList(pvSiteReportVo);
    }

    @PostMapping("findInverterListByUserId")
    @Operation(summary = "根据登录用户id查询逆变器设备列表")
    
    @Parameters({
            @Parameter(name = "userId", description = "当前登录用户id")
    })
    public ResponseResult<List<DeviceTreeListDto>> findInverterListByUserId(String userId) {
        return dataReportService.findInverterListByUserId(userId);
    }

    @PostMapping("findPvInverterReportList")
    @Operation(summary = "分页查询逆变器报表列表数据")
    
    public ResponseResult<?> findPvInverterReportList(InverterReportQueryVo inverterReportQueryVo) {
        return dataReportService.findPvInverterReportList(inverterReportQueryVo);
    }
}
