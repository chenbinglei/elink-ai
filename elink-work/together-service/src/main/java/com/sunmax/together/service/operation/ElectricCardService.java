package com.sunmax.together.service.operation;

import com.sunmax.common.dto.PageDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.together.dto.operation.electricCard.ElectricCardDetailDto;
import com.sunmax.together.dto.operation.electricCard.ElectricCardDto;
import com.sunmax.together.dto.operation.electricCard.ElectricCardTradeSummaryDto;
import com.sunmax.together.entity.ElectricCardRecordEntity;
import com.sunmax.together.vo.operation.electricCard.ElectricCardQueryVo;
import com.sunmax.together.vo.operation.electricCard.ElectricCardTradeVo;
import com.sunmax.together.vo.operation.electricCard.ElectricCardVo;
import java.util.List;

public interface ElectricCardService {

    /**
     * 新增或编辑电卡数据
     *
     * @param electricCardVo 电卡编辑参数
     * @return 状态码
     */
    ResponseResult<Void> saveElectricCard(ElectricCardVo electricCardVo);

    /**
     * 查询电卡数据
     *
     * @param query 查询参数
     * @return 电卡数据
     */
    ResponseResult<PageDto<ElectricCardDto>> searchElectricCard(ElectricCardQueryVo query);

    /**
     * 查询电卡详情
     *
     * @param id 电卡id
     * @return 电卡详情
     */
    ResponseResult<ElectricCardDetailDto> getElectricCardDetail(String id);


    /**
     * 修改电卡状态
     *
     * @param id     电卡id
     * @param state  状态 1-正常 2-禁用
     * @param userId 用户id
     * @return 状态码
     */
    ResponseResult<Void> updateElectricCardState(String id, Integer state, String userId);
    /**
     * 查询电卡操作记录
     *
     * @param carId 电卡id
     * @return 电卡操作记录
     */
    ResponseResult<List<ElectricCardRecordEntity>> getOperationRecords(String carId);
    /**
     * 删除电卡
     *
     * @param id 电卡id
     * @return 状态码
     */
    ResponseResult<Void> deleteElectricCard(String id);
    /**
     * 增加电卡余额
     *
     * @param id     电卡id
     * @param amount 余额
     * @return 状态码
     */

    ResponseResult<Void> addElectricCardBalance(String id, Double amount);
    /**
     * 退款
     *
     * @param id     电卡id
     * @param amount 余额
     * @return 状态码
     */
    ResponseResult<Void> reduceElectricCardBalance(String id, Double amount);
    /**
     * 查询电卡交易记录
     *
     * @param query 查询参数
     * @return 电卡交易记录
     */
    ResponseResult<ElectricCardTradeSummaryDto> searchTrade(ElectricCardTradeVo query);


}