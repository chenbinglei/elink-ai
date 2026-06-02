package com.sunmax.together.mapper;

import com.sunmax.together.model.ChargeOrderCostModel;
import com.sunmax.together.model.ChargeOrderQtModel;
import com.sunmax.together.model.OrderCountModel;
import com.sunmax.together.model.OrderSumDataModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface OrderRecordMapper {

    //统计无异常的订单数据(根据站点id)
    List<OrderCountModel> countOrderBySiteIds(@Param("siteIds") List<String> siteIds, @Param("startTime") String startTime, @Param("endTime") String endTime);

    //统计无异常的订单数据(根据电桩编号)
    List<OrderCountModel> countOrderByPileCodes(@Param("pileCodes") List<String> pileCodes, @Param("startTime") String startTime, @Param("endTime") String endTime);

    //统计充电订单正常数量(排除进行中的,启动失败,未进行，订单挂起，无效订单的订单)
    Integer countChargeOrderNormalNum(@Param("siteIds") List<String> siteIds, @Param("startTime") String startTime, @Param("endTime") String endTime);

    //统计充电订单总数量
    Integer countChargeOrderTotalNum(@Param("siteIds") List<String> siteIds, @Param("startTime") String startTime, @Param("endTime") String endTime);

    //统计充电订单正常数量(排除进行中的,启动失败,未进行，订单挂起，无效订单的订单)
    List<Map<String, Object>> countChargeOrderNormalNumBySiteIds(@Param("siteIds") List<String> siteIds, @Param("startTime") String startTime, @Param("endTime") String endTime);

    //统计充电订单总数量
    List<Map<String, Object>> countChargeOrderTotalNumBySiteIds(@Param("siteIds") List<String> siteIds, @Param("startTime") String startTime, @Param("endTime") String endTime);

    //统计充电订单正常数量(排除进行中的,启动失败,未进行，订单挂起，无效订单的订单)
    List<Map<String, Object>> countChargeOrderNormalNumByPileCodes(@Param("pileCodes") List<String> pileCodes, @Param("startTime") String startTime, @Param("endTime") String endTime);

    //统计充电订单总数量
    List<Map<String, Object>> countChargeOrderTotalNumByPileCodes(@Param("pileCodes") List<String> pileCodes, @Param("startTime") String startTime, @Param("endTime") String endTime);

    //统计充电订单异常订单数量
    List<ChargeOrderQtModel> countChargeAbOrderNum(@Param("siteIds") List<String> siteIds, @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                   @Param("dateType") Integer dateType);

    //统计充电订单正常数量(排除进行中的,启动失败,未进行，订单挂起，无效订单的订单) 根据站点分组
    List<ChargeOrderQtModel> countChargeOrderNormalNumList(@Param("siteIds") List<String> siteIds, @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                           @Param("dateType") Integer dateType);

    //统计充电订单总数量 根据站点分组
    List<ChargeOrderQtModel> countChargeOrderTotalNumList(@Param("siteIds") List<String> siteIds, @Param("startTime") String startTime, @Param("endTime") String endTime,
                                                          @Param("dateType") Integer dateType);

    //统计充电订单总数量 根据电桩分组
    List<ChargeOrderQtModel> countOrderDataByPileCodes(@Param("pileCodes") Set<String> pileCodes, @Param("runMode") Integer runMode, @Param("startTime") String startTime,
                                                       @Param("endTime") String endTime, @Param("dateType") Integer dateType);

    //统计订单累计数据
    List<OrderSumDataModel> findSiteOrderSumData(@Param("siteIds") List<String> siteIds, @Param("runMode") Integer runMode, @Param("platformLogo") String platformLogo,
                                                 @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<OrderSumDataModel> findPileOrderSumData(@Param("pileCodeList") List<String> pileCodeList, @Param("runMode") Integer runMode, @Param("platformLogo") String platformLogo,
                                                 @Param("startTime") String startTime, @Param("endTime") String endTime);

    //统计充电订单总电量 根据电桩分组
    ChargeOrderQtModel countOrderTotalQtByPileCodes(@Param("pileCodes") Set<String> pileCodes, @Param("runMode") Integer runMode);

    //根据站点id统计订单总金额
    List<ChargeOrderCostModel> countOrderTotalMoneyBySiteId(@Param("siteId") String siteId, @Param("runMode") Integer runMode, @Param("startTime") String startTime,
                                                            @Param("endTime") String endTime, @Param("dateType") Integer dateType);

    //统计充电订单实付金额
    List<ChargeOrderCostModel> countOrderActualMoneyBySiteId(@Param("siteId") String siteId, @Param("runMode") Integer runMode, @Param("startTime") String startTime,
                                                         @Param("endTime") String endTime, @Param("dateType") Integer dateType);

    //统计无异常的订单数据
    List<ChargeOrderCostModel> countOrderTotalQtGroupByDateType(@Param("pileCodes") List<String> pileCodes, @Param("startTime") String startTime,
                                                            @Param("endTime") String endTime, @Param("dateType") Integer dateType);

}
