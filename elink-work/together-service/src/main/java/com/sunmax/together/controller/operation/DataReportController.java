package com.sunmax.together.controller.operation;

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
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("dataReport")
@Api(tags = "数据报表管理")
public class DataReportController {

    @Autowired
    private DataReportService dataReportService;

    @PostMapping("findSiteChargeReport")
    @ApiOperation("查询电站充放电报表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<SiteReportDto> findSiteChargeReport(DataReportQueryVo dataReportQueryVo, String userId) {
        return dataReportService.findSiteChargeReport(dataReportQueryVo, userId);
    }

    @PostMapping("findSiteRunReport")
    @ApiOperation("查询电站运行报表")
    @ApiOperationSupport(order = 2)
    public ResponseResult<?> findSiteRunReport(DataReportQueryVo dataReportQueryVo, String userId) {
        return dataReportService.findSiteRunReport(dataReportQueryVo, userId);
    }

    @PostMapping("findPileChargeReport")
    @ApiOperation("查询电桩充放电报表")
    @ApiOperationSupport(order = 3)
    public ResponseResult<PileReportDto> findPileChargeReport(DataReportQueryVo dataReportQueryVo, String userId) {
        return dataReportService.findPileChargeReport(dataReportQueryVo, userId);
    }

    @PostMapping("findPileRunReport")
    @ApiOperation("查询电桩运行报表")
    @ApiOperationSupport(order = 4)
    public ResponseResult<?> findPileRunReport(DataReportQueryVo dataReportQueryVo, String userId) {
        return dataReportService.findPileRunReport(dataReportQueryVo, userId);
    }

    @PostMapping("findAccountList")
    @ApiOperation("查询商户列表")
    @ApiOperationSupport(order = 5)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", paramType = "query", required = true),
            @ApiImplicitParam(name = "queryMode", value = "查询模式 1-充电 2-放电 3-分账", paramType = "query", required = true)
    })
    public ResponseResult<List<AccountDto>> findAccountList(String userId, Integer queryMode) {
        return dataReportService.findAccountList(userId, queryMode);
    }

    @PostMapping("findSummaryCountData")
    @ApiOperation("查询渠道汇总统计数据")
    @ApiOperationSupport(order = 6)
    public ResponseResult<SummaryCountDto> findSummaryCountData(DataReportQueryVo dataReportQueryVo, String userId) {
        return dataReportService.findSummaryCountData(dataReportQueryVo, userId);
    }

    @PostMapping("findPlatformDetailsList")
    @ApiOperation("查询渠道充电明细列表")
    @ApiOperationSupport(order = 7)
    public ResponseResult<?> findPlatformDetailsList(PlatformDetailsVo platformDetailsVo, String userId) {
        return dataReportService.findPlatformDetailsList(platformDetailsVo, userId);
    }

    @PostMapping("findPvSiteReportList")
    @ApiOperation("分页查询光伏站点报表列表数据")
    @ApiOperationSupport(order = 8)
    public ResponseResult<?> findPvSiteReportList(PvSiteReportQueryVo pvSiteReportVo) {
        return dataReportService.findPvSiteReportList(pvSiteReportVo);
    }

    @PostMapping("findInverterListByUserId")
    @ApiOperation("根据登录用户id查询逆变器设备列表")
    @ApiOperationSupport(order = 9)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "当前登录用户id", paramType = "query", required = true)
    })
    public ResponseResult<List<DeviceTreeListDto>> findInverterListByUserId(String userId) {
        return dataReportService.findInverterListByUserId(userId);
    }

    @PostMapping("findPvInverterReportList")
    @ApiOperation("分页查询逆变器报表列表数据")
    @ApiOperationSupport(order = 10)
    public ResponseResult<?> findPvInverterReportList(InverterReportQueryVo inverterReportQueryVo) {
        return dataReportService.findPvInverterReportList(inverterReportQueryVo);
    }
}
