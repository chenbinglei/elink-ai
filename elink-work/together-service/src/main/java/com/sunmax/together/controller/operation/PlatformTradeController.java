package com.sunmax.together.controller.operation;

import com.sunmax.common.dto.webapp.DischargeTradeDto;
import com.sunmax.common.dto.webapp.DischargeTradeListDto;
import com.sunmax.common.dto.webapp.RechargeTradeDto;
import com.sunmax.common.dto.webapp.RechargeTradeListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.DischargeTradeQueryVo;
import com.sunmax.common.vo.webapp.RechargeTradeQueryVo;
import com.sunmax.together.service.feign.WebAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiOperationSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("platformTrade")
@Api(tags = "平台交易管理")
public class PlatformTradeController {

    @Autowired
    private WebAppService webAppService;

    @PostMapping("queryRechargeTradeList")
    @ApiOperation("查询充电交易列表")
    @ApiOperationSupport(order = 1)
    public ResponseResult<RechargeTradeListDto> queryRechargeTradeList(RechargeTradeQueryVo rechargeTradeVo) {
        return webAppService.queryRechargeTradeList(rechargeTradeVo);
    }

    @PostMapping("findRechargeTradeById")
    @ApiOperation("根据主键id查询充电交易详情")
    @ApiOperationSupport(order = 2)
    @ApiImplicitParam(name = "id", value = "主键id", paramType = "query", required = true)
    public ResponseResult<RechargeTradeDto> findRechargeTradeById(String id) {
        return webAppService.findRechargeTradeById(id);
    }

    @PostMapping("queryDischargeTradeList")
    @ApiOperation("查询V2G钱包交易列表")
    @ApiOperationSupport(order = 3)
    public ResponseResult<DischargeTradeListDto> queryDischargeTradeList(DischargeTradeQueryVo dischargeTradeVo) {
        return webAppService.queryDischargeTradeList(dischargeTradeVo);
    }

    @PostMapping("findDischargeTradeById")
    @ApiOperation("根据主键id查询V2G钱包交易详情")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParam(name = "id", value = "主键id", paramType = "query", required = true)
    public ResponseResult<DischargeTradeDto> findDischargeTradeById(String id) {
        return webAppService.findDischargeTradeById(id);
    }

}
