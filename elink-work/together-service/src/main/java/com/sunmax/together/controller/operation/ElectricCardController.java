package com.sunmax.together.controller.operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;


import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.log.config.WebLog;
import com.sunmax.together.dto.operation.electricCard.ElectricCardDetailDto;
import com.sunmax.together.dto.operation.electricCard.ElectricCardDto;
import com.sunmax.together.dto.operation.electricCard.ElectricCardTradeSummaryDto;
import com.sunmax.together.vo.operation.electricCard.ElectricCardQueryVo;
import com.sunmax.together.vo.operation.electricCard.ElectricCardTradeVo;
import com.sunmax.together.vo.operation.electricCard.ElectricCardVo;
import com.sunmax.together.entity.ElectricCardRecordEntity;
import com.sunmax.together.service.operation.ElectricCardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("electricCard")
@Tag(name = "电卡管理")
public class ElectricCardController {
    @Autowired
    private ElectricCardService electricCardService ;

    @PostMapping("saveElectricCard")
    @Operation(summary = "新增或编辑电卡数据")
    @WebLog("电卡管理-新增或编辑电卡数据")
    
    public ResponseResult<Void> saveElectricCard(ElectricCardVo electricCardVo) {
        return electricCardService.saveElectricCard(electricCardVo);
    }

    // 查询电卡详情
    @PostMapping("searchElectricCard")
    @Operation(summary = "查询电卡")
    
    public ResponseResult<PageDto<ElectricCardDto>> queryElectricCards(ElectricCardQueryVo query) {
        return electricCardService.searchElectricCard(query);
    }

    @PostMapping("getElectricCardById")
    @Operation(summary = "电卡详情")
    
    public ResponseResult<ElectricCardDetailDto> getElectricCardById(String id) {
        return electricCardService.getElectricCardDetail(id);

    }

    @PostMapping("updateElectricCardState")
    @Operation(summary = "修改电卡状态")
    @WebLog("电卡管理-修改电卡状态")
    
    @Parameters({
            @Parameter(name = "cardId", description = "电卡id"),
            @Parameter(name = "state", description = "状态 1-正常 2-禁用"),
            @Parameter(name = "userId", description = "用户id")
    })
    public ResponseResult<Void> updateElectricCardState(String id, Integer state, String userId) {
        return electricCardService.updateElectricCardState(id, state, userId);
    }


    @PostMapping("searchRecordById")
    @Operation(summary = "操作记录")
    
    public ResponseResult<List<ElectricCardRecordEntity>> searchRecordById(String carId) {
        return electricCardService.getOperationRecords(carId);
    }
    @PostMapping("deleteElectricCardById")
    @Operation(summary = "删除电卡")
    @WebLog("电卡管理-删除电卡")
    
    public ResponseResult<Void> deleteElectricCardById(String id){
        return electricCardService.deleteElectricCard(id);
    }



    @PostMapping("addElectricCardBalanceById")
    @Operation(summary = "充值电卡")
    @WebLog("电卡管理-充值电卡")
    
    public ResponseResult<Void> addElectricCardBalanceById(String id, Double amount) {
        return electricCardService.addElectricCardBalance(id, amount);
    }

    @PostMapping("reduceElectricCardBalanceById")
    @Operation(summary = "退款电卡")
    @WebLog("电卡管理-退款电卡")
    
    public ResponseResult<Void> reduceElectricCardBalanceById(String id, Double amount) {
        return electricCardService.reduceElectricCardBalance(id, amount);
    }


    @PostMapping("searchTradeByType")
    @Operation(summary = "交易查询")
    
    public ResponseResult<ElectricCardTradeSummaryDto> searchTradeByType(ElectricCardTradeVo query) {
        return electricCardService.searchTrade(query);
    }
}

