package com.sunmax.together.controller.operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteIncomeDto;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteInvestIncomeDto;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteOperateIncomeDto;
import com.sunmax.together.service.operation.ChargeAnalysisService;
import com.sunmax.together.vo.operation.chargeAnalysis.SiteIncomeVo;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

@RestController
@CrossOrigin
@RequestMapping("chargeAnalysis")
@Tag(name = "充放电收益分析管理")
public class ChargeAnalysisController {

    @Resource
    private ChargeAnalysisService chargeAnalysisService;

    @PostMapping("saveSiteIncome")
    @Operation(summary = "添加或编辑站点收益测算数据")
    
    public ResponseResult<Void> saveSiteIncome(SiteIncomeVo siteIncomeVo) {
        return chargeAnalysisService.saveSiteIncome(siteIncomeVo);
    }

    @PostMapping("findSiteIncomeBySiteId")
    @Operation(summary = "根据站点id查询站点收益测算数据")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<SiteIncomeDto> findSiteIncomeBySiteId(String siteId) {
        return chargeAnalysisService.findSiteIncomeBySiteId(siteId);
    }

    @PostMapping("countSiteInvestIncome")
    @Operation(summary = "统计站点投资收益概况")
    
    @Parameter(name = "siteId", description = "站点id")
    public ResponseResult<SiteInvestIncomeDto> countSiteInvestIncome(String siteId) {
        return chargeAnalysisService.countSiteInvestIncome(siteId);
    }

    @PostMapping("countSiteOperateIncome")
    @Operation(summary = "根据日期统计站点经营收益概况")
    
    @Parameters({
            @Parameter(name = "siteId", description = "站点id"),
            @Parameter(name = "startDate", description = "开始日期(yyyy-MM-dd)"),
            @Parameter(name = "endDate", description = "结束日期(yyyy-MM-dd)"),
            @Parameter(name = "dateType", description = "日期类型 1-日 2-月")
    })
    public ResponseResult<SiteOperateIncomeDto> countSiteOperateIncome(String siteId, String startDate, String endDate, Integer dateType) {
        return chargeAnalysisService.countSiteOperateIncome(siteId, startDate, endDate, dateType);
    }

}
