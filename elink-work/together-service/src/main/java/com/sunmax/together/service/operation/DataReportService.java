package com.sunmax.together.service.operation;

import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.dataReport.DeviceTreeListDto;
import com.sunmax.together.dto.operation.dataReport.PileReportDto;
import com.sunmax.together.dto.operation.dataReport.SiteReportDto;
import com.sunmax.together.dto.operation.dataReport.SummaryCountDto;
import com.sunmax.together.vo.operation.dataReport.DataReportQueryVo;
import com.sunmax.together.vo.operation.dataReport.InverterReportQueryVo;
import com.sunmax.together.vo.operation.dataReport.PlatformDetailsVo;
import com.sunmax.together.vo.operation.dataReport.PvSiteReportQueryVo;

import java.util.List;

public interface DataReportService {

    /**
     * 查询电站充放电报表
     *
     * @param dataReportQueryVo
     * @param userId
     * @return
     */
    ResponseResult<SiteReportDto> findSiteChargeReport(DataReportQueryVo dataReportQueryVo, String userId);

    /**
     * 查询电站运行报表
     * @param dataReportQueryVo
     * @param userId
     * @return
     */
    ResponseResult<?> findSiteRunReport(DataReportQueryVo dataReportQueryVo, String userId);

    /**
     * 查询电桩充放电报表
     * @param dataReportQueryVo
     * @param userId
     * @return
     */
    ResponseResult<PileReportDto> findPileChargeReport(DataReportQueryVo dataReportQueryVo, String userId);

    /**
     * 查询电桩运行报表
     * @param dataReportQueryVo
     * @param userId
     * @return
     */
    ResponseResult<?> findPileRunReport(DataReportQueryVo dataReportQueryVo, String userId);

    /**
     * 查询商户列表
     *
     * @param userId
     * @param queryMode 查询模式  1-充电 2-放电 3-分账
     * @return
     */
    ResponseResult<List<AccountDto>> findAccountList(String userId, Integer queryMode);

    /**
     * 查询渠道汇总统计数据
     * @param dataReportQueryVo
     * @param userId
     * @return
     */
    ResponseResult<SummaryCountDto> findSummaryCountData(DataReportQueryVo dataReportQueryVo, String userId);

    /**
     * 查询渠道充电明细列表
     * @param platformDetailsVo
     * @param userId
     * @return
     */
    ResponseResult<?> findPlatformDetailsList(PlatformDetailsVo platformDetailsVo, String userId);

    /**
     * 分页查询光伏站点报表列表数据
     * @param pvSiteReportVo
     * @return
     */
    ResponseResult<?> findPvSiteReportList(PvSiteReportQueryVo pvSiteReportVo);

    /**
     * 根据登录用户id查询逆变器设备列表
     * @param userId
     * @return
     */
    ResponseResult<List<DeviceTreeListDto>> findInverterListByUserId(String userId);

    /**
     * 分页查询逆变器报表列表数据
     * @param inverterReportQueryVo
     * @return
     */
    ResponseResult<?> findPvInverterReportList(InverterReportQueryVo inverterReportQueryVo);
}
