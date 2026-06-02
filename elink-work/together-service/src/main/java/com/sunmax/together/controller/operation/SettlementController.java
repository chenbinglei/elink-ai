package com.sunmax.together.controller.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.dto.system.AccountDto;
import com.sunmax.common.dto.system.TenantDetailsDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.settlement.SiteAccountDetailDto;
import com.sunmax.together.dto.operation.settlement.SiteAccountListDto;
import com.sunmax.together.service.operation.SettlementService;
import com.sunmax.together.service.feign.SystemService;
import com.sunmax.together.vo.operation.settlement.SiteAccountChangeVo;
import com.sunmax.together.vo.operation.settlement.SiteAccountQueryVo;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("settlement")
@Api(tags = "结算管理控制层")
public class SettlementController {

    @Autowired
    private SettlementService settlementService;

    @Autowired
    private SystemService systemService;

    @PostMapping("querySiteAccountList")
    @ApiOperation("查询站点账户数据列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<PageDto<SiteAccountListDto>> querySiteAccountList(SiteAccountQueryVo siteAccountQueryVo) {
        return settlementService.querySiteAccountList(siteAccountQueryVo);
    }

    @PostMapping("findSiteAccountListBySiteIdAndType")
    @ApiOperation("根据站点id和类型查询站点账户数据")
    @ApiOperationSupport(order = 2)
    public ResponseResult<List<SiteAccountDetailDto>> findSiteAccountListBySiteIdAndType(String siteId, Integer type) {
        return settlementService.findSiteAccountListBySiteIdAndType(siteId, type);
    }

    @PostMapping("findTenantListByUserId")
    @ApiOperation("根据用户id查询所有租户列表数据")
    @ApiOperationSupport(order = 3)
    public ResponseResult<List<TenantDetailsDto>> findTenantListByUserId(String userId) {
        return systemService.findTenantListByUserId(userId);
    }

    @PostMapping("findAccountByAccountId")
    @ApiOperation("根据账户id查询账户数据")
    @ApiOperationSupport(order = 4)
    public ResponseResult<AccountDto> findAccountByAccountId(String accountId) {
        return settlementService.findAccountByAccountId(accountId);
    }

    @PostMapping("saveSiteAccount")
    @ApiOperation("新增或编辑站点账户数据")
    @WebLog("结算管理-设置站点账户数据")
    @ApiOperationSupport(order = 5)
    public ResponseResult<Void> saveSiteAccount(SiteAccountChangeVo siteAccountChangeVo) {
        return settlementService.saveSiteAccount(siteAccountChangeVo);
    }

    @PostMapping("deleteSiteAccountById")
    @ApiOperation("根据主键id删除站点账户数据")
    @WebLog("结算管理-根据主键id删除站点账户数据")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Void> deleteSiteAccountById(String id) {
        return settlementService.deleteSiteAccountById(id);
    }

    @PostMapping("findAccountListByUserId")
    @ApiOperation("根据用户id和平台类型查询账户数据列表")
    @ApiOperationSupport(order = 7)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "platformType", value = "平台类型 1-微信 2-支付宝", required = true, dataType = "Integer")
    })
    public ResponseResult<List<AccountDto>> findAccountListByUserId(String userId, Integer platformType) {
        return settlementService.findAccountListByUserId(userId, platformType);
    }

}
