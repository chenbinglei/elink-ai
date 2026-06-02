package com.sunmax.together.controller.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteIncomeDto;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteInvestIncomeDto;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteOperateIncomeDto;
import com.sunmax.together.service.operation.ChargeAnalysisService;
import com.sunmax.together.vo.operation.chargeAnalysis.SiteIncomeVo;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("chargeAnalysis")
@Api(tags = "充放电收益分析管理")
public class ChargeAnalysisController {

    @Resource
    private ChargeAnalysisService chargeAnalysisService;

    @PostMapping("saveSiteIncome")
    @ApiOperation("添加或编辑站点收益测算数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveSiteIncome(SiteIncomeVo siteIncomeVo) {
        return chargeAnalysisService.saveSiteIncome(siteIncomeVo);
    }

    @PostMapping("findSiteIncomeBySiteId")
    @ApiOperation("根据站点id查询站点收益测算数据")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "siteId", value = "站点id", required = true)
    public ResponseResult<SiteIncomeDto> findSiteIncomeBySiteId(String siteId) {
        return chargeAnalysisService.findSiteIncomeBySiteId(siteId);
    }

    @PostMapping("countSiteInvestIncome")
    @ApiOperation("统计站点投资收益概况")
    @ApiOperationSupport(order = 3)
    @ApiImplicitParam(name = "siteId", value = "站点id", required = true)
    public ResponseResult<SiteInvestIncomeDto> countSiteInvestIncome(String siteId) {
        return chargeAnalysisService.countSiteInvestIncome(siteId);
    }

    @PostMapping("countSiteOperateIncome")
    @ApiOperation("根据日期统计站点经营收益概况")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "siteId", value = "站点id", required = true),
            @ApiImplicitParam(name = "startDate", value = "开始日期(yyyy-MM-dd)", required = true),
            @ApiImplicitParam(name = "endDate", value = "结束日期(yyyy-MM-dd)", required = true),
            @ApiImplicitParam(name = "dateType", value = "日期类型 1-日 2-月", required = true)
    })
    public ResponseResult<SiteOperateIncomeDto> countSiteOperateIncome(String siteId, String startDate, String endDate, Integer dateType) {
        return chargeAnalysisService.countSiteOperateIncome(siteId, startDate, endDate, dateType);
    }

}
