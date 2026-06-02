package com.sunmax.together.dao.order;

import com.sunmax.together.entity.order.OrderRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;

public interface OrderRecordDao extends JpaRepository<OrderRecordEntity, String>, JpaSpecificationExecutor<OrderRecordEntity> {

    //根据多个电桩编码，查询相关订单数据
    List<OrderRecordEntity> findAllByRunModeInAndPileCodeIn(List<Integer> orderTypes, List<String> pileCodeList);

    //根据指定多个电桩编码查询所有订单记录数据
    List<OrderRecordEntity> findAllByPileCodeIn(List<String> pileCodeList);

    //根据结束时间为主，查询多个电桩编码的订单记录数据
    List<OrderRecordEntity> findAllByPileCodeInAndEndTimeBetween(List<String> pileCodes, String startTime, String endTime);

    //查询最后一条订单
    OrderRecordEntity findFirstByOrderByCreateTimeDesc();

    //根据电桩编码和订单类型查询指定时间段内的订单记录数据
    List<OrderRecordEntity> findAllByPileCodeInAndRunModeAndEndTimeBetween(List<String> pileCodeList, Integer runMode, String startTime, String endTime);

    //根据订单编号查询订单数据
    OrderRecordEntity findByOrderNum(String orderNum);

    //根据多个订单编号查询订单数据
    List<OrderRecordEntity> findAllByOrderNumIn(Collection<String> orderNums);

    //根据多个账号数据查询订单列表
    List<OrderRecordEntity> findAllByAccountDataIn(List<String> accountDataList);

    //根据多个账号数据和运行模式查询指定类型的订单列表
    List<OrderRecordEntity> findAllByAccountDataInAndRunMode(List<String> accountDataList, Integer runMode);

    //根据账号类型和账号数据和订单状态和运行模式查询订单列表
    List<OrderRecordEntity> findAllByAccountTypeAndAccountDataAndOrderStatusInAndRunMode(Integer accountType, String accountData, List<Integer> orderStatus, Integer orderType);

    //根据账号类型和账号数据和订单状态查询订单列表
    List<OrderRecordEntity> findAllByAccountTypeAndAccountDataAndOrderStatusIn(Integer accountType, String accountData, List<Integer> orderStatus);

    //根据多个站点id，查询订单数据
    List<OrderRecordEntity> findAllBySiteIdIn(List<String> siteIdList);

    //根据多个站点id和查询时间查询订单数据
    List<OrderRecordEntity> findAllBySiteIdInAndEndTimeBetween(List<String> siteIdList, String startTime, String endTime);

    //根据多个站点id和结束时间范围和运行模式查询订单数据
    List<OrderRecordEntity> findAllBySiteIdInAndEndTimeBetweenAndRunMode(List<String> siteIdList, String startTime, String endTime, Integer runMode);

    //根据多个站点id查询订单数据
    List<OrderRecordEntity> findAllBySiteIdInAndEndTimeIsNotNullAndAbnormalCodeIsNull(List<String> siteIdList);
}
