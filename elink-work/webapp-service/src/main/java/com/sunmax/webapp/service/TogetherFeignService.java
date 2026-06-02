package com.sunmax.webapp.service;

import com.sunmax.common.dto.webapp.DischargeTradeDto;
import com.sunmax.common.dto.webapp.DischargeTradeListDto;
import com.sunmax.common.dto.webapp.RechargeTradeDto;
import com.sunmax.common.dto.webapp.RechargeTradeListDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.vo.webapp.DischargeTradeQueryVo;
import com.sunmax.common.vo.webapp.RechargeTradeQueryVo;

import java.math.BigDecimal;
import java.util.List;


public interface TogetherFeignService {

    /**
     * 查询充电交易列表
     * @param rechargeTradeVo 充电交易查询条件
     * @return 充电交易列表数据
     */
    ResponseResult<RechargeTradeListDto> queryRechargeTradeList(RechargeTradeQueryVo rechargeTradeVo);

    /**
     * 根据主键id查询充电交易详情
     * @param id 主键id
     * @return 充电交易详情数据
     */
    ResponseResult<RechargeTradeDto> findRechargeTradeById(String id);

    /**
     * 查询V2G钱包交易列表
     * @param dischargeTradeVo V2G钱包交易查询条件
     * @return V2G钱包交易列表数据
     */
    ResponseResult<DischargeTradeListDto> queryDischargeTradeList(DischargeTradeQueryVo dischargeTradeVo);

    /**
     * 根据主键id查询V2G钱包交易详情
     * @param id 主键id
     * @return V2G钱包交易详情数据
     */
    ResponseResult<DischargeTradeDto> findDischargeTradeById(String id);

    /**
     * 根据订单编号查询该笔订单可退金额
     * @param orderNum 订单编号
     * @return 订单可退金额
     */
    ResponseResult<BigDecimal> findRefundMoneyByOrderNum(String orderNum);

    /**
     * 根据多个订单编号查询该笔订单的充电交易列表
     * @param orderNums 多个订单编号
     * @return 充电交易列表数据
     */
    ResponseResult<List<RechargeTradeDto>> findRechargeTradeListByOrderNums(List<String> orderNums);

}
