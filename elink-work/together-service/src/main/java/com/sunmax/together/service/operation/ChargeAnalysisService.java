package com.sunmax.together.service.operation;

import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteIncomeDto;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteInvestIncomeDto;
import com.sunmax.together.dto.operation.chargeAnalysis.SiteOperateIncomeDto;
import com.sunmax.together.vo.operation.chargeAnalysis.SiteIncomeVo;

public interface ChargeAnalysisService {

    /**
     * 添加或编辑站点收益测算数据
     * @param siteIncomeVo 站点收益数据
     * @return 状态码
     */
    ResponseResult<Void> saveSiteIncome(SiteIncomeVo siteIncomeVo);

    /**
     * 根据站点ID查询站点收益数据
     * @param siteId 站点id
     * @return 站点收益数据
     */
    ResponseResult<SiteIncomeDto> findSiteIncomeBySiteId(String siteId);

    /**
     * 统计站点投资收益
     * @param siteId 站点id
     * @return 站点投资收益数据
     */
    ResponseResult<SiteInvestIncomeDto> countSiteInvestIncome(String siteId);

    /**
     * 根据日期统计站点经营收益概况
     * @param siteId 站点id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param dateType 日期类型 1-日 2-月
     * @return 站点经营收益概况
     */
    ResponseResult<SiteOperateIncomeDto> countSiteOperateIncome(String siteId, String startDate, String endDate, Integer dateType);

}
