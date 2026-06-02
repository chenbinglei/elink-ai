package com.sunmax.configure.common.push;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.sunmax.configure.dao.CityRecordDao;
import com.sunmax.configure.dto.city.ConnectorStatusDto;
import com.sunmax.configure.dto.city.OrderInfoDto;
import com.sunmax.configure.util.city.CityRequestUtil;
import com.sunmax.configure.vo.RequestCommonVo;
import com.sunmax.configure.vo.city.CityMethodVo;
import com.sunmax.configure.vo.city.EquipChargeStatusVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;

@Configuration
@Slf4j
public class CityDataPush {

    @Resource
    private CityRecordDao cityRecordDao;

    private static CityRecordDao cityRecordDao1;

    @PostConstruct
    private void init() {
        cityRecordDao1 = cityRecordDao;
    }

    /**
     * 市设备状态变化推送
     */
    private static final Map<String, ConnectorStatusDto> connectorStatusMap = Maps.newConcurrentMap();

    /**
     * 市推送充电状态
     */
    private static final Map<String, EquipChargeStatusVo> equipChargeStatusMap = Maps.newConcurrentMap();

    /**
     * 市订单信息推送
     */
    private static final Map<String, OrderInfoDto> orderInfoMap = Maps.newConcurrentMap();

    /**
     * 设备状态变化推送
     *
     * @param connectorStatus 设备状态数据
     */
    public static void pileStatus(RequestCommonVo commonVo, ConnectorStatusDto connectorStatus) {
        log.info("推送市平台电桩状态公共数据：{}", commonVo);
        log.info("推送市平台电桩状态数据：{}", JSON.toJSONString(connectorStatus));
        Map<String, Object> dataMap = Maps.newConcurrentMap();
        dataMap.put("ConnectorStatusInfo", connectorStatus);
        if (connectorStatusMap.containsKey(connectorStatus.getConnectorId())) {
            ConnectorStatusDto cacheData = connectorStatusMap.get(connectorStatus.getConnectorId());
            if (!Objects.equals(connectorStatus, cacheData)) {
                //数据不一致 则推送
                CityRequestUtil.sendData(commonVo, CityMethodVo.NOTIFICATION_STATION_STATUS, JSON.toJSONString(dataMap), cityRecordDao1);
                connectorStatusMap.put(connectorStatus.getConnectorId(), connectorStatus);
            }
        } else {
            CityRequestUtil.sendData(commonVo, CityMethodVo.NOTIFICATION_STATION_STATUS, JSON.toJSONString(dataMap), cityRecordDao1);
            connectorStatusMap.put(connectorStatus.getConnectorId(), connectorStatus);
        }
    }

    /**
     * 推送充电状态
     *
     * @param equipChargeStatus 推送充电状态参数
     */
    public static void equipChargeStatus(RequestCommonVo commonVo, EquipChargeStatusVo equipChargeStatus) {
        log.info("推送市平台电桩充电过程中状态公共数据：{}", commonVo);
        log.info("推送市平台电桩充电过程中状态数据：{}", JSON.toJSONString(equipChargeStatus));
        if (equipChargeStatusMap.containsKey(equipChargeStatus.getConnectorId())) {
            EquipChargeStatusVo cacheData = equipChargeStatusMap.get(equipChargeStatus.getConnectorId());
            if (!Objects.equals(equipChargeStatus, cacheData)) {
                //数据不一致 则推送
                CityRequestUtil.sendData(commonVo, CityMethodVo.NOTIFICATION_EQUIP_CHARGE_STATUS, JSON.toJSONString(equipChargeStatus), cityRecordDao1);
                equipChargeStatusMap.put(equipChargeStatus.getConnectorId(), equipChargeStatus);
            }
        } else {
            CityRequestUtil.sendData(commonVo, CityMethodVo.NOTIFICATION_EQUIP_CHARGE_STATUS, JSON.toJSONString(equipChargeStatus), cityRecordDao1);
            equipChargeStatusMap.put(equipChargeStatus.getConnectorId(), equipChargeStatus);
        }
    }

    /**
     * 推送充电订单
     *
     * @param orderInfo 订单信息
     */
    public static void orderInfo(RequestCommonVo commonVo, OrderInfoDto orderInfo) {
        log.info("推送市平台电桩订单公共数据：{}", commonVo);
        log.info("推送市平台电桩订单状态数据：{}", JSON.toJSONString(orderInfo));
        if (orderInfoMap.containsKey(orderInfo.getConnectorId())) {
            OrderInfoDto cacheData = orderInfoMap.get(orderInfo.getConnectorId());
            if (!Objects.equals(orderInfo, cacheData)) {
                //数据不一致 则推送
                CityRequestUtil.sendData(commonVo, CityMethodVo.NOTIFICATION_CHARGE_ORDER_INFO, JSON.toJSONString(orderInfo), cityRecordDao1);
                orderInfoMap.put(orderInfo.getConnectorId(), orderInfo);
            }
        } else {
            CityRequestUtil.sendData(commonVo, CityMethodVo.NOTIFICATION_CHARGE_ORDER_INFO, JSON.toJSONString(orderInfo), cityRecordDao1);
            orderInfoMap.put(orderInfo.getConnectorId(), orderInfo);
        }
    }

}
