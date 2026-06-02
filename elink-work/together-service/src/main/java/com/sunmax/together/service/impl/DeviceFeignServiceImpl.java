package com.sunmax.together.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.operate.OrderCountDto;
import com.sunmax.common.dto.operate.OrderQtDto;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.together.mapper.OrderRecordMapper;
import com.sunmax.together.model.ChargeOrderCostModel;
import com.sunmax.together.model.OrderCountModel;
import com.sunmax.together.service.DeviceFeignService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DeviceFeignServiceImpl implements DeviceFeignService {

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Override
    public ResponseResult<Map<String, List<OrderCountDto>>> findOrderRecordListBySiteIds(List<String> siteIds, String startTime, String endTime) {
        //返回的对象
        Map<String, List<OrderCountDto>> resultMap = Maps.newHashMap();

        //根据站点id和日期统计订单记录
        List<OrderCountModel> orderCountModelList = orderRecordMapper.countOrderBySiteIds(siteIds, startTime, endTime);
        if (CollectionUtils.isNotEmpty(orderCountModelList)) {
            Map<String, List<OrderCountModel>> siteOrderCountMap = orderCountModelList.stream().collect(Collectors.groupingBy(OrderCountModel::getSiteId));
            siteIds.forEach(siteId -> {
                List<OrderCountDto> resultList = Lists.newArrayList();
                if (siteOrderCountMap.containsKey(siteId)) {
                    Map<String, List<OrderCountModel>> pileCountMap = siteOrderCountMap.get(siteId).stream().collect(Collectors.groupingBy(OrderCountModel::getPileCode));
                    pileCountMap.forEach((pileCode, pileCountList) -> {
                        OrderCountDto result = new OrderCountDto();
                        result.setSiteId(siteId);
                        result.setPileCode(pileCode);
                        //充电次数,电量,金额
                        Optional<OrderCountModel> chargeOptional = pileCountList.stream().filter(s -> StringUtil.isNotEmpty(s.getRunMode()) && s.getRunMode() == 0).findFirst();
                        if (chargeOptional.isPresent()) {
                            OrderCountModel model = chargeOptional.get();
                            result.setChargeCount(model.getTotalCount());
                            result.setChargeQt(model.getTotalQt());
                            result.setChargeMoney(model.getTotalMoney());
                        }
                        //放电次数,电量,金额
                        List<OrderCountModel> dischargeList = pileCountList.stream().filter(s -> StringUtil.isNotEmpty(s.getRunMode())
                                && (s.getRunMode() == 1 || s.getRunMode() == 2)).collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(dischargeList)) {
                            dischargeList.forEach(s -> {
                                result.setDischargeCount(result.getDischargeCount() + s.getTotalCount());
                                result.setDischargeQt(result.getDischargeQt() + s.getTotalQt());
                                result.setDischargeMoney(result.getDischargeMoney().add(s.getTotalMoney()));
                            });
                        }
                        resultList.add(result);
                    });
                }
                resultMap.put(siteId, resultList);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, OrderCountDto>> findOrderRecordListByPileCodes(List<String> pileCodes, String startTime, String endTime) {
        //返回的对象
        Map<String, OrderCountDto> resultMap = Maps.newHashMap();

        //根据站点id和日期统计订单记录
        List<OrderCountModel> orderCountModelList = orderRecordMapper.countOrderByPileCodes(pileCodes, startTime, endTime);
        if (CollectionUtils.isNotEmpty(orderCountModelList)) {
            Map<String, List<OrderCountModel>> pileOrderCountMap = orderCountModelList.stream().collect(Collectors.groupingBy(OrderCountModel::getPileCode));
            pileCodes.forEach(pileCode -> {
                OrderCountDto result = new OrderCountDto();
                if (pileOrderCountMap.containsKey(pileCode)) {
                    List<OrderCountModel> pileCountList = pileOrderCountMap.get(pileCode);

                    if (CollectionUtils.isNotEmpty(pileCountList)) {
                        result.setSiteId(pileCountList.get(0).getSiteId());
                    }
                    result.setPileCode(pileCode);
                    //充电次数,电量,金额
                    Optional<OrderCountModel> chargeOptional = pileCountList.stream().filter(s -> StringUtil.isNotEmpty(s.getRunMode()) && s.getRunMode() == 0).findFirst();
                    if (chargeOptional.isPresent()) {
                        OrderCountModel model = chargeOptional.get();
                        result.setChargeCount(model.getTotalCount());
                        result.setChargeQt(model.getTotalQt());
                        result.setChargeMoney(model.getTotalMoney());
                    }
                    //放电次数,电量,金额
                    List<OrderCountModel> dischargeList = pileCountList.stream().filter(s -> StringUtil.isNotEmpty(s.getRunMode())
                            && (s.getRunMode() == 1 || s.getRunMode() == 2)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(dischargeList)) {
                        dischargeList.forEach(s -> {
                            result.setDischargeCount(result.getDischargeCount() + s.getTotalCount());
                            result.setDischargeQt(result.getDischargeQt() + s.getTotalQt());
                            result.setDischargeMoney(result.getDischargeMoney().add(s.getTotalMoney()));
                        });
                    }
                }
                resultMap.put(pileCode, result);
            });
        }
        return ResponseResult.ok(resultMap);
    }

    @Override
    public ResponseResult<Map<String, OrderQtDto>> findOrderQtListByPileCodes(List<String> pileCodes, String startTime, String endTime, Integer dateType) {
        //返回的对象
        Map<String, OrderQtDto> resultMap = Maps.newHashMap();
        List<ChargeOrderCostModel> chargeOrderCostList = orderRecordMapper.countOrderTotalQtGroupByDateType(pileCodes, startTime, endTime, dateType);
        if (CollectionUtils.isNotEmpty(chargeOrderCostList)) {
            Map<String, List<ChargeOrderCostModel>> chargeOrderCostMap = chargeOrderCostList.stream().collect(Collectors.groupingBy(ChargeOrderCostModel::getDataTime));
            chargeOrderCostMap.forEach((dataTime, orderCostList) -> {
                OrderQtDto result = new OrderQtDto();
                result.setDataTime(dataTime);
                //充电次数,电量,金额
                Optional<ChargeOrderCostModel> chargeOptional = orderCostList.stream().filter(s -> StringUtil.isNotEmpty(s.getRunMode()) && s.getRunMode() == 0).findFirst();
                if (chargeOptional.isPresent()) {
                    ChargeOrderCostModel model = chargeOptional.get();
                    result.setChargeCount(model.getTotalCount());
                    result.setChargeQt(DoubleUtil.getAbsDouble(model.getTotalQt()));
                    result.setChargeMoney(DoubleUtil.getAbsBigDecimal(model.getTotalCost()));
                }
                //放电次数,电量,金额
                List<ChargeOrderCostModel> dischargeList = orderCostList.stream().filter(s -> StringUtil.isNotEmpty(s.getRunMode())
                        && (s.getRunMode() == 1 || s.getRunMode() == 2)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(dischargeList)) {
                    dischargeList.forEach(model -> {
                        result.setDischargeCount(result.getDischargeCount() + model.getTotalCount());
                        result.setDischargeQt(result.getDischargeQt() + model.getTotalQt());
                        result.setDischargeMoney(result.getDischargeMoney().add(model.getTotalCost()));
                    });
                    result.setDischargeCount(result.getDischargeCount());
                    result.setDischargeQt(DoubleUtil.getAbsDouble(result.getDischargeQt()));
                    result.setDischargeMoney(DoubleUtil.getAbsBigDecimal(result.getDischargeMoney()));
                }
                resultMap.put(dataTime, result);
            });
        }
        return ResponseResult.ok(resultMap);
    }

}
