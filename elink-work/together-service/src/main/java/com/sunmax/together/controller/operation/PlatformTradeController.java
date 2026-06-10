package com.sunmax.together.controller.operation;

import com.sunmax.common.dto.webapp.DischargeTradeDto;
import com.sunmax.common.dto.webapp.DischargeTradeListDto;
import com.sunmax.common.dto.webapp.RechargeTradeDto;
import com.sunmax.common.dto.webapp.RechargeTradeListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.DischargeTradeQueryVo;
import com.sunmax.common.vo.webapp.RechargeTradeQueryVo;
import com.sunmax.together.service.feign.WebAppService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@RequestMapping("platformTrade")
@Tag(name = "平台交易管理")
public class PlatformTradeController {

    @Autowired
    private WebAppService webAppService;

    @PostMapping("queryRechargeTradeList")
    @Operation(summary = "查询充电交易列表")
    
    public ResponseResult<RechargeTradeListDto> queryRechargeTradeList(RechargeTradeQueryVo rechargeTradeVo) {
        return webAppService.queryRechargeTradeList(rechargeTradeVo);
    }

    @PostMapping("findRechargeTradeById")
    @Operation(summary = "根据主键id查询充电交易详情")
    
    @Parameter(name = "id", description = "主键id")
    public ResponseResult<RechargeTradeDto> findRechargeTradeById(String id) {
        return webAppService.findRechargeTradeById(id);
    }

    @PostMapping("queryDischargeTradeList")
    @Operation(summary = "查询V2G钱包交易列表")
    
    public ResponseResult<DischargeTradeListDto> queryDischargeTradeList(DischargeTradeQueryVo dischargeTradeVo) {
        return webAppService.queryDischargeTradeList(dischargeTradeVo);
    }

    @PostMapping("findDischargeTradeById")
    @Operation(summary = "根据主键id查询V2G钱包交易详情")
    
    @Parameter(name = "id", description = "主键id")
    public ResponseResult<DischargeTradeDto> findDischargeTradeById(String id) {
        return webAppService.findDischargeTradeById(id);
    }

}
