package com.sunmax.configure.common.push;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.configure.dao.ProvinceOrderDao;
import com.sunmax.configure.dao.ProvinceRecordDao;
import com.sunmax.configure.dto.ResponseDto;
import com.sunmax.configure.dto.province.*;
import com.sunmax.configure.entity.ProvinceOrderEntity;
import com.sunmax.configure.util.province.ProvinceRequestUtil;
import com.sunmax.configure.vo.RequestCommonVo;
import com.sunmax.configure.vo.province.ProvinceMethodVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Example;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
@Slf4j
public class ProvinceDataPush {

    /**
     * 省设备状态变化推送
     */
    private static final Map<String, ConnectorStatusDto> connectorStatusMap = Maps.newConcurrentMap();

    /**
     * 省充电订单状态信息推送(5分钟)
     */
    public static final Map<String, EquipChargeStatusDto> equipChargeStatusMap = Maps.newConcurrentMap();

    /**
     * 省订单信息推送
     */
    public static final Map<String, ChargeOrderInfoDto> orderInfoMap = Maps.newConcurrentMap();

    @Resource
    private ProvinceRecordDao provinceRecordDao;

    @Resource
    private ProvinceOrderDao provinceOrderDao;

    private static ProvinceRecordDao provinceRecordDao1;

    private static ProvinceOrderDao provinceOrderDao1;

    @PostConstruct
    private void init() {
        provinceRecordDao1 = provinceRecordDao;
        provinceOrderDao1 = provinceOrderDao;
    }

    public static RequestCommonVo getCommonVo(SubstationFieldDto substationField) {
        return RequestCommonVo.builder()
                .url(substationField.getUrl())
                .platformId(substationField.getPlatformId())
                .platformSecret(substationField.getPlatformSecret())
                .dataSecret(substationField.getDataSecret())
                .dataSecretIv(substationField.getDataSecretIv())
                .sigSecret(substationField.getSigSecret())
                .operatorId(substationField.getOperatorId()).build();
    }

    public static void stationInfo(RequestCommonVo commonVo, StationInfoDto stationInfo) {
        log.info("推送省平台站点实时功率公共数据：{}", commonVo);
        log.info("推送省平台站点实时功率数据：{}", JSON.toJSONString(stationInfo));
        //推送省平台站点实时功率数据
        ProvinceRequestUtil.sendData(commonVo, ProvinceMethodVo.NOTIFICATION_STATION_INFO, JSON.toJSONString(stationInfo), provinceRecordDao1);
    }

    public static void pileStatus(RequestCommonVo commonVo, ConnectorStatusDto connectorStatus) {
        log.info("推送省平台电桩状态公共数据：{}", commonVo);
        log.info("推送省平台电桩状态数据：{}", JSON.toJSONString(connectorStatus));
        if (connectorStatusMap.containsKey(connectorStatus.getConnectorId())) {
            ConnectorStatusDto cacheData = connectorStatusMap.get(connectorStatus.getConnectorId());
            if (!Objects.equals(connectorStatus.getStatus(), cacheData.getStatus())) {
                //数据不一致 则推送
                ProvinceRequestUtil.sendData(commonVo, ProvinceMethodVo.NOTIFICATION_STATION_STATUS, JSON.toJSONString(connectorStatus), provinceRecordDao1);
                connectorStatusMap.put(connectorStatus.getConnectorId(), connectorStatus);
            }
        } else {
            ProvinceRequestUtil.sendData(commonVo, ProvinceMethodVo.NOTIFICATION_STATION_STATUS, JSON.toJSONString(connectorStatus), provinceRecordDao1);
            connectorStatusMap.put(connectorStatus.getConnectorId(), connectorStatus);
        }
    }

    public static void pileChargeStatus(RequestCommonVo commonVo, EquipChargeStatusDto supEquipChargeStatus) {
        log.info("推送省平台电桩充电过程中状态公共数据：{}", commonVo);
        log.info("推送省平台电桩充电过程中状态数据：{}", JSON.toJSONString(supEquipChargeStatus));
        ProvinceRequestUtil.sendData(commonVo, ProvinceMethodVo.NOTIFICATION_EQUIP_CHARGE_STATUS, JSON.toJSONString(supEquipChargeStatus), provinceRecordDao1);
    }

    public static void orderInfo(RequestCommonVo commonVo, ChargeOrderInfoDto orderInfo) {
        log.info("推送省平台电桩订单公共数据：{}", commonVo);
        log.info("推送省平台电桩订单状态数据：{}", JSON.toJSONString(orderInfo));
        ResponseDto response = ProvinceRequestUtil.sendData(commonVo, ProvinceMethodVo.NOTIFICATION_CHARGE_ORDER_INFO, JSON.toJSONString(orderInfo), provinceRecordDao1);
        if (StringUtil.isNotEmpty(response.getRet()) && response.getRet() == 4015) {
            ProvinceOrderEntity provinceOrder = ProvinceOrderEntity.builder()
                    .stationId(orderInfo.getStationId())
                    .pileCode(orderInfo.getEquipmentId())
                    .gunCode(orderInfo.getConnectorId().replace(orderInfo.getEquipmentId(), FileUtil.separator))
                    .orderNum(orderInfo.getOrderNo())
                    .chargeQt(orderInfo.getTotalElect())
                    .chargeMoney(orderInfo.getTotalMoney())
                    .createTime(LocalDateTime.now()).build();
            provinceOrderDao1.findOne(Example.of(ProvinceOrderEntity.builder().orderNum(orderInfo.getOrderNo()).build()))
                    .ifPresent(p -> provinceOrder.setId(p.getId()));
            provinceOrderDao1.save(provinceOrder);
            orderInfoMap.remove(orderInfo.getOrderNo());
            return;
        }
        if (StringUtil.isNotEmpty(response.getRet()) && response.getRet() != 0) {
            orderInfoMap.put(orderInfo.getOrderNo(), orderInfo);
        } else if (StringUtil.isNotEmpty(response.getData())) {
            JSONObject jsonObject = JSON.parseObject(response.getData());
            String orderNo = jsonObject.getString("OrderNo");
            Integer confirmResult = jsonObject.getInteger("ConfirmResult");
            if (!Objects.equals(orderNo, orderInfo.getOrderNo()) || !Objects.equals(confirmResult, 0)) {
                orderInfoMap.put(orderInfo.getOrderNo(), orderInfo);
            } else {
                ProvinceOrderEntity provinceOrder = ProvinceOrderEntity.builder()
                        .stationId(orderInfo.getStationId())
                        .pileCode(orderInfo.getEquipmentId())
                        .gunCode(orderInfo.getConnectorId().replace(orderInfo.getEquipmentId(), FileUtil.separator))
                        .orderNum(orderInfo.getOrderNo())
                        .chargeQt(orderInfo.getTotalElect())
                        .chargeMoney(orderInfo.getTotalMoney())
                        .createTime(LocalDateTime.now()).build();
                provinceOrderDao1.findOne(Example.of(ProvinceOrderEntity.builder().orderNum(orderInfo.getOrderNo()).build()))
                        .ifPresent(p -> provinceOrder.setId(p.getId()));
                provinceOrderDao1.save(provinceOrder);
                orderInfoMap.remove(orderInfo.getOrderNo());
            }
        }
    }

    public static void stationPowerInfos(RequestCommonVo commonVo, List<StationPowerInfoDto> stationPowerInfos) {
        log.info("推送省平台站点实时功率公共数据：{}", commonVo);
        log.info("推送省平台站点实时功率数据：{}", JSON.toJSONString(stationPowerInfos));
        if (CollectionUtils.isNotEmpty(stationPowerInfos)) {
            Map<String, Object> dataMap = Maps.newConcurrentMap();
            dataMap.put("SupStationPowerInfos", stationPowerInfos);
            ProvinceRequestUtil.sendData(commonVo, ProvinceMethodVo.NOTIFICATION_REALTIME_POWER_INFO, JSON.toJSONString(dataMap), provinceRecordDao1);
        }
    }

}
