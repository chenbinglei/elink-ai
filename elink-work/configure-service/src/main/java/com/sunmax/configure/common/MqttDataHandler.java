package com.sunmax.configure.common;

import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.*;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.common.vo.protocol.mqtt.web.data.*;
import com.sunmax.common.vo.protocol.mqtt.web.response.*;
import com.sunmax.configure.common.push.CityDataPush;
import com.sunmax.configure.common.push.ProvinceDataPush;
import com.sunmax.configure.dto.city.OrderInfoDto;
import com.sunmax.configure.dto.province.ChargeOrderInfoDto;
import com.sunmax.configure.dto.province.ConnectorStatusDto;
import com.sunmax.configure.dto.province.EquipChargeStatusDto;
import com.sunmax.configure.dto.province.SubstationFieldDto;
import com.sunmax.configure.runner.SubstationRunner;
import com.sunmax.configure.util.DataAccessStringUtil;
import com.sunmax.configure.vo.city.EquipChargeStatusVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Sets;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.sunmax.configure.common.push.ProvinceDataPush.equipChargeStatusMap;
import static com.sunmax.configure.common.push.ProvinceDataPush.getCommonVo;

@Slf4j
@Configuration
public class MqttDataHandler {

    /**
     * 处理离线交易记录
     *
     * @param pileOffLineRecordVo 离线交易记录数据
     */
    public static void offLineRecordRes(PileOffLineRecordSubscribeVo pileOffLineRecordVo) {
        if (pileOffLineRecordVo.getOffLineChargeRecord() != null) {
            for (int i = 0; i < pileOffLineRecordVo.getOffLineChargeRecord().size(); i++) {
                PileRecordSubscribeVo pileRecordSubscribeVo = pileOffLineRecordVo.getOffLineChargeRecord().get(i);
                recordRes(pileRecordSubscribeVo);//保存充电记录
            }
        }
    }

    /**
     * 更新电桩状态
     *
     * @param pileStateMap 电桩状态数据
     */
    public static void updateStatus(Map<String, PileStateSubscribeVo> pileStateMap) {
        for (Map.Entry<String, PileStateSubscribeVo> pileState : pileStateMap.entrySet()) {
            String pileCode = pileState.getKey();
            PileStateSubscribeVo pileStateSubscribeVo = pileState.getValue();
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
            pileStateSubscribeVo.getGun_state().forEach(itfStatus -> {
                //定义输出电压，输出电流，已充电量，已充时间，剩余电量，开始时间
                double outCurrent = 0.0, outVolt = 0.0, soc = 0.0, totalQt = 0.0, totalCost = 0.0, reqVolt = 0.0, reqCurrent = 0.0;
                Set<Integer> faultCodes = Sets.newHashSet();
                long totalTime = 0L;
                String startTime = null, orderNum = null;
                Integer workState = itfStatus.getWorkState();
                Integer gunWorkState= itfStatus.getWorkState(); //枪工作状态
                Integer vehicleConnectState = itfStatus.getVehicleConnState();
                if (pileRealModel != null && !pileRealModel.getGunRealModelMap().isEmpty() &&
                        pileRealModel.getGunRealModelMap().containsKey(String.valueOf(itfStatus.getGunCode()))) {
                    PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(String.valueOf(itfStatus.getGunCode()));
                    if (gunRealModel != null) {
                        if (CollectionUtils.isNotEmpty(gunRealModel.getFaultCodes())) {
                            faultCodes = gunRealModel.getFaultCodes();
                        }
                        orderNum = gunRealModel.getSerialNum();
                        Integer startSoc = gunRealModel.getStartSoc();

                        if (StringUtil.isNotEmpty(gunRealModel.getOutCurrent())) {
                            outCurrent = DoubleUtil.getToDouble(gunRealModel.getOutCurrent());
                        }
                        if (StringUtil.isNotEmpty(gunRealModel.getOutVolt())) {
                            outVolt = DoubleUtil.getToDouble(gunRealModel.getOutVolt());
                        }
                        if (StringUtil.isNotEmpty(gunRealModel.getReqVolt())) {
                            reqVolt = gunRealModel.getReqVolt();
                        }
                        if (StringUtil.isNotEmpty(gunRealModel.getReqCurrent())) {
                            reqCurrent = gunRealModel.getReqCurrent();
                        }
                        if (StringUtil.isNotEmpty(gunRealModel.getBatterySoc())) {
                            soc = 100 - gunRealModel.getBatterySoc();
                        }
                        if (StringUtil.isNotEmpty(startSoc) && StringUtil.isNotEmpty(gunRealModel.getBatterySoc())) {
                            totalQt = (double) gunRealModel.getBatterySoc() - startSoc;
                        }
                        if (StringUtil.isNotEmpty(gunRealModel.getTotalQt())) {
                            totalQt = gunRealModel.getTotalQt();
                        }
                        if (StringUtil.isNotEmpty(gunRealModel.getTotalCost())) {
                            totalCost = gunRealModel.getTotalCost().doubleValue();
                        }
                        if (StringUtil.isNotEmpty(gunRealModel.getStartTime())) {
                            startTime = gunRealModel.getStartTime();
                            totalTime = Duration.between(DateUtil.strToLocalDateTime(gunRealModel.getStartTime()), LocalDateTime.now()).toMinutes();
                        }
                    }
                }
                Integer workStatus = DataAccessStringUtil.getProvinceGunStatus(workState, gunWorkState, vehicleConnectState);
                //工作状态为空时 说明放电 放电不做推送
                if (StringUtil.isNotEmpty(workStatus)) {
                    List<Integer> gunWorkStates = Arrays.asList(1, 2, 3, 7);
                    SubstationFieldDto provinceOperate = SubstationRunner.getPileOperateField(pileCode, 1);
                    if (provinceOperate != null) {
                        //1. 推送省设备接口状态信息
                        ConnectorStatusDto provinceStatus = new ConnectorStatusDto();
                        provinceStatus.setOperatorId(provinceOperate.getOperatorId());
                        provinceStatus.setEquipmentOwnerId(provinceOperate.getEquipmentOwnerId());
                        provinceStatus.setStationId(provinceOperate.getStationId());
                        provinceStatus.setEquipmentId(pileCode);
                        provinceStatus.setConnectorId(pileCode + itfStatus.getGunCode());
                        provinceStatus.setStatus(workStatus);
                        provinceStatus.setParkStatus(DataAccessStringUtil.getVehicleStatus(vehicleConnectState));
                        provinceStatus.setUpdateTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                        ProvinceDataPush.pileStatus(getCommonVo(provinceOperate), provinceStatus);

                        //推送省充电订单状态信息
                        if (gunWorkStates.contains(gunWorkState) && StringUtil.isNotEmpty(orderNum)) {
                            EquipChargeStatusDto provinceChargeStatus = new EquipChargeStatusDto();
                            provinceChargeStatus.setOperatorId(provinceOperate.getOperatorId());
                            provinceChargeStatus.setEquipmentOwnerId(provinceOperate.getEquipmentOwnerId());
                            provinceChargeStatus.setStationId(provinceOperate.getStationId());
                            provinceChargeStatus.setEquipmentId(pileCode);
                            provinceChargeStatus.setConnectorId(pileCode + itfStatus.getGunCode());
                            provinceChargeStatus.setOrderNo(orderNum);
                            switch (itfStatus.getWorkState()) {
                                case 1:
                                    provinceChargeStatus.setOrderStatus(1);
                                    break;
                                case 2:
                                    provinceChargeStatus.setOrderStatus(2);
                                    break;
                                case 3:
                                    provinceChargeStatus.setOrderStatus(4);
                                    break;
                                case 7:
                                    provinceChargeStatus.setOrderStatus(5);
                            }
                            provinceChargeStatus.setPushTimeStamp(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                            provinceChargeStatus.setConnectorStatus(workStatus);
                            provinceChargeStatus.setCurrentA(DoubleUtil.getToDouble(outCurrent, 1));
                            provinceChargeStatus.setVoltageA(DoubleUtil.getToDouble(outVolt, 1));
                            provinceChargeStatus.setSoc(soc);
                            provinceChargeStatus.setStartTime(startTime);
                            provinceChargeStatus.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                            provinceChargeStatus.setTotalPower(DoubleUtil.getToDouble(totalQt, 4));
                            provinceChargeStatus.setTotalMoney(DoubleUtil.getToDouble(totalCost, 2));
                            provinceChargeStatus.setEventTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                            if (StringUtil.isNotEmpty(reqVolt)) {
                                provinceChargeStatus.setBclNeedVoltage((int) (reqVolt / 10));
                            }
                            if (StringUtil.isNotEmpty(reqCurrent)) {
                                provinceChargeStatus.setBclNeedCurrent((int) (reqCurrent / 10));
                            }
                            provinceChargeStatus.setChargeVoltage((int) (outVolt / 10));
                            provinceChargeStatus.setChargeCurrent((int) (outCurrent / 10));
                            if (!equipChargeStatusMap.containsKey(provinceChargeStatus.getOrderNo())) {
                                ProvinceDataPush.pileChargeStatus(getCommonVo(provinceOperate), provinceChargeStatus);
                            }
                            equipChargeStatusMap.put(provinceChargeStatus.getOrderNo(), provinceChargeStatus);
                        }
                    }

                    //获取市设备状态数据
                    SubstationFieldDto cityOperate = SubstationRunner.getPileOperateField(pileCode, 2);
                    if (cityOperate != null) {
                        String connectorId = pileCode.substring(pileCode.length() - 12) + itfStatus.getGunCode();
                        com.sunmax.configure.dto.city.ConnectorStatusDto cityStatus = new com.sunmax.configure.dto.city.ConnectorStatusDto();
                        cityStatus.setConnectorId(connectorId);
                        cityStatus.setStatus(workStatus);
                        cityStatus.setParkStatus(DataAccessStringUtil.getVehicleStatus(itfStatus.getVehicleConnState()));
                        cityStatus.setSoc(soc);
                        if (CollectionUtils.isNotEmpty(faultCodes)) {
                            cityStatus.setFaultType(DataAccessStringUtil.getFaultType(new ArrayList<>(faultCodes).get(0)));
                        }
                        cityStatus.setEdtime((int) totalTime);
                        cityStatus.setEdpq(totalQt);
                        CityDataPush.pileStatus(getCommonVo(cityOperate), cityStatus);

                        //推送市充电状态
                        if (gunWorkStates.contains(gunWorkState)) {
                            EquipChargeStatusVo cityChargeStatus = new EquipChargeStatusVo();
                            //获取市订单号
                            if (orderNum != null && StringUtil.isNotEmpty(orderNum)) {
                                cityChargeStatus.setStartChargeSeq(orderNum.substring(orderNum.length() - 27));
                            }
                            cityChargeStatus.setStartChargeSeqStat(2);
                            cityChargeStatus.setConnectorId(connectorId);
                            cityChargeStatus.setConnectorStatus(3);
                            cityChargeStatus.setCurrentA(outCurrent);
                            cityChargeStatus.setVoltageA(outVolt);
                            cityChargeStatus.setSoc(soc);
                            cityChargeStatus.setStartTime(startTime);
                            cityChargeStatus.setEndTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                            cityChargeStatus.setTotalPower(totalQt);
                            CityDataPush.equipChargeStatus(getCommonVo(cityOperate), cityChargeStatus);
                        }
                    }
                }
            });
        }
    }

    /**
     * 记录上报
     *
     * @param pileRecord 记录上报数据
     */
    public static void recordRes(PileRecordSubscribeVo pileRecord) {
        if (pileRecord != null) {
            String pileCode = pileRecord.getPilesCode();
            ////1.校验是否执行 2.充电模式才做推送
            if (pileRecord.getRunMode() == 0) {
//                if (StringUtil.isNotEmpty(pileRecord.getTotalCost())) {
//                    pileRecord.setTotalCost(pileRecord.getTotalCost().multiply(new BigDecimal("0.001")));
//                }
//                if (StringUtil.isNotEmpty(pileRecord.getTotalQ())) {
//                    pileRecord.setTotalQ(pileRecord.getTotalQ() * 0.001);
//                }
                Integer gunCode = pileRecord.getGunCode();
                SubstationFieldDto provinceOperate = SubstationRunner.getPileOperateField(pileCode, 1);
                if (provinceOperate != null) {
                    //获取省订单信息
                    ChargeOrderInfoDto provinceOrder = new ChargeOrderInfoDto();
                    provinceOrder.setOperatorId(provinceOperate.getOperatorId());
                    provinceOrder.setEquipmentOwnerId(provinceOperate.getEquipmentOwnerId());
                    provinceOrder.setStationId(provinceOperate.getStationId());
                    provinceOrder.setEquipmentId(pileCode);
                    provinceOrder.setConnectorId(pileCode + pileRecord.getGunCode());
                    provinceOrder.setOrderNo(pileRecord.getRecordId());
                    provinceOrder.setVin(pileRecord.getBusVin());
                    UserAccount userAccount = pileRecord.getUserAccount();
                    if (userAccount != null && StringUtil.isNotEmpty(userAccount.getAccountType()) && userAccount.getAccountType() == 3) {
                        provinceOrder.setPhone(userAccount.getAccountData().replace(FileUtil.BAR, FileUtil.separator));
                    }
                    //尖峰平谷电量和金额
                    if (StringUtil.isNotEmpty(pileRecord.getSharpQ())) { //尖电量
                        provinceOrder.setCuspElect(DoubleUtil.getAbsDouble(pileRecord.getSharpQ() / 1000, 4));
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getPeakQ())) { //峰电量
                        provinceOrder.setPeakElect(DoubleUtil.getAbsDouble(pileRecord.getPeakQ() / 1000, 4));
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getFlatQ())) { //平电量
                        provinceOrder.setFlatElect(DoubleUtil.getAbsDouble(pileRecord.getFlatQ() / 1000, 4));
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getValleyQ())) { //谷电量
                        provinceOrder.setValleyElect(DoubleUtil.getAbsDouble(pileRecord.getValleyQ() / 1000, 4));
                    }
                    //本次充电电量，金额
                    if (StringUtil.isNotEmpty(pileRecord.getTotalElecFee())) { //总电费
                        provinceOrder.setTotalElecMoney(DoubleUtil.getAbsBigDecimal(pileRecord.getTotalElecFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4).doubleValue());
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getTotalServiceFee())) { //总服务费
                        provinceOrder.setTotalServiceMoney(DoubleUtil.getAbsBigDecimal(pileRecord.getTotalServiceFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4).doubleValue());
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getTotalCost())) { //总费用
                        provinceOrder.setTotalMoney(DoubleUtil.getAbsBigDecimal(pileRecord.getTotalCost().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4).doubleValue());
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getTotalQ())) { //总电量
                        provinceOrder.setTotalElect(DoubleUtil.getAbsDouble(pileRecord.getTotalQ() / 1000, 4));
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getStartTime())) {
                        provinceOrder.setStartTime(SunMaxUtil.timeStamp8Date(pileRecord.getStartTime()));
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getEndTime())) {
                        provinceOrder.setEndTime(SunMaxUtil.timeStamp8Date(pileRecord.getEndTime()));
                    }
                    provinceOrder.setPayChannel(6);
                    if (StringUtil.isNotEmpty(pileRecord.getStopReason())) {
                        provinceOrder.setStopReason(String.valueOf(pileRecord.getStopReason()));
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getStopDetail())) {
                        provinceOrder.setStopDesc(String.valueOf(pileRecord.getStopDetail()));
                    }
                    provinceOrder.setPushTime(DateUtil.localDateTimeToStr(LocalDateTime.now()));
                    ProvinceDataPush.orderInfo(getCommonVo(provinceOperate), provinceOrder);
                }
                SubstationFieldDto cityOperate = SubstationRunner.getPileOperateField(pileCode, 2);
                if (cityOperate != null) {
                    //获取市订单信息
                    OrderInfoDto cityOrder = new OrderInfoDto();
                    cityOrder.setConnectorId(pileCode.substring(pileCode.length() - 12) + gunCode);
                    String cityOrderNum = null;
                    if (StringUtil.isNotEmpty(pileRecord.getRecordId())) {
                        cityOrderNum = pileRecord.getRecordId().substring(pileRecord.getRecordId().length() - 27);
                    }
                    cityOrder.setStartChargeSeq(cityOrderNum);
                    if (StringUtil.isNotEmpty(pileRecord.getStartTime())) {
                        cityOrder.setStartTime(SunMaxUtil.timeStamp8Date(pileRecord.getStartTime()));
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getEndTime())) {
                        cityOrder.setEndTime(SunMaxUtil.timeStamp8Date(pileRecord.getEndTime()));
                    }
                    //尖峰平谷电量和金额
                    if (StringUtil.isNotEmpty(pileRecord.getSharpQ())) { //尖电量
                        cityOrder.setToppkPower(DoubleUtil.getAbsDouble(pileRecord.getSharpQ() / 1000, 4));
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getPeakQ())) { //峰电量
                        cityOrder.setPeakPower(DoubleUtil.getAbsDouble(pileRecord.getPeakQ() / 1000, 4));
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getFlatQ())) { //平电量
                        cityOrder.setFlatPower(DoubleUtil.getAbsDouble(pileRecord.getFlatQ() / 1000, 4));
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getValleyQ())) { //谷电量
                        cityOrder.setValleyPower(DoubleUtil.getAbsDouble(pileRecord.getValleyQ() / 1000, 4));
                    }
                    //本次充电电量，金额
                    if (StringUtil.isNotEmpty(pileRecord.getTotalElecFee())) { //总电费
                        cityOrder.setTotalElecMoney(DoubleUtil.getAbsBigDecimal(pileRecord.getTotalElecFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4).doubleValue());
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getTotalServiceFee())) { //总服务费
                        cityOrder.setTotalSeviceMoney(DoubleUtil.getAbsBigDecimal(pileRecord.getTotalServiceFee().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4).doubleValue());
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getTotalCost())) { //总费用
                        cityOrder.setTotalMoney(DoubleUtil.getAbsBigDecimal(pileRecord.getTotalCost().divide(new BigDecimal(1000), 4, RoundingMode.UP), 4).doubleValue());
                    }
                    if (StringUtil.isNotEmpty(pileRecord.getTotalQ())) { //总电量
                        cityOrder.setTotalPower(DoubleUtil.getAbsDouble(pileRecord.getTotalQ() / 1000, 4));
                    }
                    //获取电桩停止详细原因
                    Long featureCode = null;
                    PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                    if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getFeatureCode())) {
                        featureCode = pileRealModel.getFeatureCode();
                    }
                    cityOrder.setStopReason(DataAccessStringUtil.getSMV2GStopReason(StringUtil.isEmpty(featureCode) ? Long.valueOf(43605L) : featureCode, pileRecord.getStopReason()));
                    CityDataPush.orderInfo(getCommonVo(cityOperate), cityOrder);
                }
            }
        }
    }
}
