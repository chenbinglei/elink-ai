package com.sunmax.together.controller.operation;


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
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("electricCard")
@Api(tags = "电卡管理")
public class ElectricCardController {
    @Autowired
    private ElectricCardService electricCardService ;

    @PostMapping("saveElectricCard")
    @ApiOperation("新增或编辑电卡数据")
    @WebLog("电卡管理-新增或编辑电卡数据")
    @ApiOperationSupport(order = 1)
    public ResponseResult<Void> saveElectricCard(ElectricCardVo electricCardVo) {
        return electricCardService.saveElectricCard(electricCardVo);
    }

    // 查询电卡详情
    @PostMapping("searchElectricCard")
    @ApiOperation("查询电卡")
    @ApiOperationSupport(order = 2)
    public ResponseResult<PageDto<ElectricCardDto>> queryElectricCards(ElectricCardQueryVo query) {
        return electricCardService.searchElectricCard(query);
    }

    @PostMapping("getElectricCardById")
    @ApiOperation("电卡详情")
    @ApiOperationSupport(order = 3)
    public ResponseResult<ElectricCardDetailDto> getElectricCardById(String id) {
        return electricCardService.getElectricCardDetail(id);

    }

    @PostMapping("updateElectricCardState")
    @ApiOperation("修改电卡状态")
    @WebLog("电卡管理-修改电卡状态")
    @ApiOperationSupport(order = 4)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cardId", value = "电卡id", required = true),
            @ApiImplicitParam(name = "state", value = "状态 1-正常 2-禁用", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true)
    })
    public ResponseResult<Void> updateElectricCardState(String id, Integer state, String userId) {
        return electricCardService.updateElectricCardState(id, state, userId);
    }


    @PostMapping("searchRecordById")
    @ApiOperation("操作记录")
    @ApiOperationSupport(order = 5)
    public ResponseResult<List<ElectricCardRecordEntity>> searchRecordById(String carId) {
        return electricCardService.getOperationRecords(carId);
    }
    @PostMapping("deleteElectricCardById")
    @ApiOperation("删除电卡")
    @WebLog("电卡管理-删除电卡")
    @ApiOperationSupport(order = 6)
    public ResponseResult<Void> deleteElectricCardById(String id){
        return electricCardService.deleteElectricCard(id);
    }



    @PostMapping("addElectricCardBalanceById")
    @ApiOperation("充值电卡")
    @WebLog("电卡管理-充值电卡")
    @ApiOperationSupport(order = 7)
    public ResponseResult<Void> addElectricCardBalanceById(String id, Double amount) {
        return electricCardService.addElectricCardBalance(id, amount);
    }

    @PostMapping("reduceElectricCardBalanceById")
    @ApiOperation("退款电卡")
    @WebLog("电卡管理-退款电卡")
    @ApiOperationSupport(order = 8)
    public ResponseResult<Void> reduceElectricCardBalanceById(String id, Double amount) {
        return electricCardService.reduceElectricCardBalance(id, amount);
    }


    @PostMapping("searchTradeByType")
    @ApiOperation("交易查询")
    @ApiOperationSupport(order = 9)
    public ResponseResult<ElectricCardTradeSummaryDto> searchTradeByType(ElectricCardTradeVo query) {
        return electricCardService.searchTrade(query);
    }
}

