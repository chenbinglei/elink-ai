package com.sunmax.protocol.util;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.constant.CmdConstant;
import com.sunmax.common.constant.IEGConstant;
import com.sunmax.common.constant.WebTopicConstant;
import com.sunmax.common.dto.protocol.mqtt.UpdateInfoDto;
import com.sunmax.common.dto.protocol.mqtt.inter.*;
import com.sunmax.common.dto.protocol.mqtt.web.model.CostFormat;
import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.SunMaxUtil;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.common.vo.protocol.mqtt.web.command.*;
import com.sunmax.common.vo.protocol.mqtt.web.common.IEGTopicVo;
import com.sunmax.common.vo.protocol.mqtt.web.common.ResponseSubscribeVo;
import com.sunmax.protocol.config.emqx.inter.InterMqttConfig;
import com.sunmax.protocol.config.emqx.web.WebMqttConfig;
import com.sunmax.protocol.constant.SMV2gConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class MqttTopicUtil {

    // 通过网关启动充电桩
    public static Boolean sendPileStartTopic(PileStartVo pileStartVo) {
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileStartVo.getPileCode());
        if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) {
            PileStartPublishVo pileStart = new PileStartPublishVo();
            pileStart.setPilesCode(pileStartVo.getPileCode());
            pileStart.setGunCode(Integer.parseInt(pileStartVo.getGunCode()));
            pileStart.setStarter(pileStartVo.getStarter());//发起者
            pileStart.setRunMode(pileStartVo.getRunMode());//运行模式
            pileStart.setReChargeType(1);//充值类型
            if (StringUtil.isEmpty(pileStartVo.getPrepayMoney()) || pileStartVo.getPrepayMoney().compareTo(BigDecimal.ZERO) <= 0) {
                pileStart.setPayValue(50000000);//账户余额
            } else {
                pileStart.setPayValue(pileStartVo.getPrepayMoney().multiply(new BigDecimal(1000)).intValue());//账户余额
            }
            //用户账号数据
            //设置用户信息
            UserAccount userAccount = new UserAccount();
            userAccount.setAccountType(pileStartVo.getAccountType());//app调用 启动
            userAccount.setAccountData(pileStartVo.getAccountData());
            pileStart.setUserAccount(userAccount);//用户账号
            if (pileStartVo.getAccountType() == 3) {
                pileStart.setStopPwd(pileStartVo.getAccountData().substring(pileStartVo.getAccountData().length() - 4));//停止密码
            } else {
                pileStart.setStopPwd("6789");
            }

            //充放电策略
            Strategy strategy = new Strategy();
            if (StringUtil.isNotEmpty(pileStartVo.getType())) {
                //充电方式 0-立即充电 1-定时充电 2-自动充电
                switch (pileStartVo.getType()) {
                    case 0:
                    case 2:
                        strategy.setStartMode(0); //启动方式
                        if (pileRealModel.getMessageType() == 1) {
                            strategy.setStartTime(SunMaxUtil.getSysTime());//启动时间(秒)
                        }
                        if (pileRealModel.getMessageType() == 2) {
                            strategy.setStartTime(SunMaxUtil.getUnixTime());//启动时间(秒)
                        }
                        break;
                    case 1:
                        strategy.setStartMode(1);//启动方式
                        //预约时间为空则时间错误 返回预约失败
                        String clockingTime = pileStartVo.getClockingTime();
                        if (StringUtil.isEmpty(clockingTime)) { //预约时间为空 直接返回失败
                            return false;
                        }
                        if (pileRealModel.getMessageType() == 1) {
                            strategy.setStartTime(SunMaxUtil.dateTime8ToTimeStamp(clockingTime));//启动时间(秒)
                        }
                        if (pileRealModel.getMessageType() == 2) {
                            strategy.setStartTime(SunMaxUtil.dateTime0ToTimeStamp(clockingTime));//启动时间(秒)
                        }
                        break;
                }
            }
            if (StringUtil.isNotEmpty(pileStartVo.getStrategy()) && StringUtil.isNotEmpty(pileStartVo.getStrategyCfg())) {
                strategy.setStrategyType(pileStartVo.getStrategy());
                //充放电策略 0-自动充满 1-soc电量 2-金额 3-电量
                switch (pileStartVo.getStrategy()) {
                    case 0:
                        strategy.setStrategyCfg(100.0);
                        break;
                    case 1:
                        strategy.setStrategyCfg(pileStartVo.getStrategyCfg());
                        break;
                    case 2:
                    case 3:
                        strategy.setStrategyCfg(pileStartVo.getStrategyCfg() * 1000);
                        break;
                }
            } else {
                strategy.setStrategyType(pileStartVo.getStrategy());
                strategy.setStrategyCfg(pileStartVo.getStrategyCfg());
            }
            pileStart.setStrategy(strategy);

            pileStart.setRecordId(pileStartVo.getSerialNum()); //订单id


            //下发充电桩启动主题
            if (pileRealModel.getMessageType() == 1) { //外网发送
                if (StringUtil.isEmpty(pileRealModel.getTerminalCode())) {
                    log.error("外网发送充电桩启动命令,网关编号未获取到: {}", pileRealModel.getTerminalCode());
                    return false;
                }
                //发送控制命令
                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                        .mid(String.valueOf(SMV2gConstant.SMV2G_MSG4000))
                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.PILE_START).paras(JSON.toJSON(pileStart)).build())
                        .type(IEGConstant.Type.CMD_SERVICE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + pileRealModel.getTerminalCode() + WebTopicConstant.SERVICE_COMMAND;
                WebMqttConfig.sendToMqtt(pileRealModel.getTerminalCode(), topicName, iegTopicVo);
                //保存外网启动命令记录
                PileRecordUtil.saveMqttRecord(pileStartVo.getPileCode(), pileStartVo.getGunCode(), 1, 1, 1, IEGConstant.Param.PILE_START, pileStart);
                return true;
            }
            if (pileRealModel.getMessageType() == 2) { //内网发送
                StartCmdDto startCmdDto = new StartCmdDto();
                BeanUtils.copyProperties(pileStart, startCmdDto);
                InterMqttConfig.sendToMqtt(pileStart.getPilesCode(), CmdConstant.CMD_START, startCmdDto);
                //保存内网启动命令记录
                PileRecordUtil.saveMqttRecord(pileStartVo.getPileCode(), pileStartVo.getGunCode(), 1, 1, 2, CmdConstant.CMD_START, startCmdDto);
                return true;
            }
        } else {
            log.error("启动充电桩失败, 失败数据:{}", pileRealModel);
        }
        return false;
    }

    //通过网关停止充电桩
    public static Boolean sendPileStopTopic(PileStopVo pileStopVo) {
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileStopVo.getPileCode());
        if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) {

            PileStopPublishVo pileStop = new PileStopPublishVo();
            pileStop.setPilesCode(pileStopVo.getPileCode());
            pileStop.setGunCode(Integer.parseInt(pileStopVo.getGunCode()));

            //停止编码 type 1 -> 结束充电 2 ->取消预约
            if (pileStopVo.getType() == 1) { //结束充电
                pileStop.setStopReason(0x1004);
            }
            if (pileStopVo.getType() == 2) { //取消预约
                pileStop.setStopReason(0x1005);
            }
            if (StringUtil.isNotEmpty(pileStopVo.getSerialNum())) { //前端传的订单id 否则从redis中获取
                pileStop.setRecordId(pileStopVo.getSerialNum());
            } else {
                PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(pileStopVo.getGunCode());
                if (gunRealModel != null) {
                    pileStop.setRecordId(gunRealModel.getSerialNum());
                }
            }
            //下发充电桩停止主题参数
            if (pileRealModel.getMessageType() == 1) { //外网发送
                if (StringUtil.isEmpty(pileRealModel.getTerminalCode())) {
                    log.error("外网发送停止充电桩命令,网关编号未获取到: {}", pileRealModel.getTerminalCode());
                    return false;
                }
                //发送控制命令
                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                        .mid(String.valueOf(SMV2gConstant.SMV2G_MSG4003))
                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.PILE_STOP).paras(JSON.toJSON(pileStop)).build())
                        .type(IEGConstant.Type.CMD_SERVICE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + pileRealModel.getTerminalCode() + WebTopicConstant.SERVICE_COMMAND;
                WebMqttConfig.sendToMqtt(pileRealModel.getTerminalCode(), topicName, iegTopicVo);
                //保存外网停止命令记录
                PileRecordUtil.saveMqttRecord(pileStopVo.getPileCode(), pileStopVo.getGunCode(), 1, 4, 1, IEGConstant.Param.PILE_STOP, pileStop);
                return true;
            }
            if (pileRealModel.getMessageType() == 2) { //内网发送
                StopCmdDto stopCmd = new StopCmdDto();
                BeanUtils.copyProperties(pileStop, stopCmd);
                InterMqttConfig.sendToMqtt(stopCmd.getPilesCode(), CmdConstant.CMD_STOP, stopCmd);
                //保存内网停止命令记录
                PileRecordUtil.saveMqttRecord(pileStopVo.getPileCode(), pileStopVo.getGunCode(), 1, 4, 2, CmdConstant.CMD_STOP, stopCmd);
                return true;
            }
        } else {
            log.error("停止充电桩失败, 失败数据:{}", pileRealModel);
        }
        return false;
    }

    public static Boolean sendPowerCtrlTopic(PilePowerCtrlVo pilePowerCtrlVo) {
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilePowerCtrlVo.getPileCode());
        if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) {
            PilePowerCtrlPublishVo pilePowerCtrl = new PilePowerCtrlPublishVo();
            pilePowerCtrl.setPilesCode(pilePowerCtrlVo.getPileCode());
            pilePowerCtrl.setGunCode(Integer.parseInt(pilePowerCtrlVo.getGunCode()));
            pilePowerCtrl.setRunMode(pilePowerCtrlVo.getRunMode());
            pilePowerCtrl.setCtrlType(pilePowerCtrlVo.getCtrlType());
            pilePowerCtrl.setOut(pilePowerCtrlVo.getOutPower());

            //下发电桩功率控制主题
            if (pileRealModel.getMessageType() == 1) { //外网发送
                if (StringUtil.isEmpty(pileRealModel.getTerminalCode())) {
                    log.error("外网发送功率控制命令,网关编号未获取到: {}", pileRealModel.getTerminalCode());
                    return false;
                }
                //发送控制命令
                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                        .mid(String.valueOf(SMV2gConstant.SMV2G_MSG4009))
                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.POWER_CTRL).paras(JSON.toJSON(pilePowerCtrl)).build())
                        .type(IEGConstant.Type.CMD_SERVICE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + pileRealModel.getTerminalCode() + WebTopicConstant.SERVICE_COMMAND;
                WebMqttConfig.sendToMqtt(pileRealModel.getTerminalCode(), topicName, iegTopicVo);
                return true;
            }
            if (pileRealModel.getMessageType() == 2) { //内网发送
                PowerControlCmdDto powerControlCmd = new PowerControlCmdDto();
                BeanUtils.copyProperties(pilePowerCtrl, powerControlCmd);
                InterMqttConfig.sendToMqtt(powerControlCmd.getPilesCode(), CmdConstant.CMD_POWERCONTROL, powerControlCmd);
                return true;
            }
        } else {
            log.error("功率控制充电桩失败, 失败数据:{}", pileRealModel);
        }
        return false;
    }

    //发送电桩费率数据
    public static Boolean sendPileRateTopic(PileRateSetVo pileRateSetVo) {
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileRateSetVo.getPileCode());
        if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) {
            PileRateSetPublishVo pileRateSet = new PileRateSetPublishVo();
            pileRateSet.setPilesCode(pileRateSetVo.getPileCode());
            pileRateSet.setType(pileRateSetVo.getType());
            pileRateSet.setRateId(pileRateSetVo.getTemplateId());
            pileRateSet.setTimeFrameNum(pileRateSetVo.getTimeFrameNum());
            pileRateSet.setTimeFrameRates(pileRateSetVo.getTimeFrameRates().stream().sorted(Comparator
                    .comparing(CostFormat::getStartTime)).collect(Collectors.toList()));
            //下发电桩费率数据
            if (pileRealModel.getMessageType() == 1) { //外网发送
                if (StringUtil.isEmpty(pileRealModel.getTerminalCode())) {
                    log.error("外网发送电桩费率数据,网关编号未获取到:{}", pileRealModel.getTerminalCode());
                    return false;
                }
                //发送控制命令
                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                        .mid(String.valueOf(SMV2gConstant.SMV2G_MSG1003))
                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.RATE_SET).paras(JSON.toJSON(pileRateSet)).build())
                        .type(IEGConstant.Type.CMD_SERVICE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + pileRealModel.getTerminalCode() + WebTopicConstant.SERVICE_COMMAND;
                WebMqttConfig.sendToMqtt(pileRealModel.getTerminalCode(), topicName, iegTopicVo);
                return true;
            }
            if (pileRealModel.getMessageType() == 2) { //内网发送
                RateSetCmdDto rateSetCmd = new RateSetCmdDto();
                rateSetCmd.setPilesCode(pileRateSet.getPilesCode());
                rateSetCmd.setType(pileRateSet.getType());
                rateSetCmd.setRateId(pileRateSet.getRateId());
                rateSetCmd.setTimeFrameNum(pileRateSetVo.getTimeFrameNum());
                rateSetCmd.setTimeFrameRates(pileRateSet.getTimeFrameRates());

//                BeanUtils.copyProperties(pileRateSet, rateSetCmd);
                InterMqttConfig.sendToMqtt(rateSetCmd.getPilesCode(), CmdConstant.CMD_RATE_SET, rateSetCmd);
                return true;
            }
        } else {
            log.error("发送电桩费率数据, 失败数据:{}", pileRealModel);
        }
        return false;
    }

    public static UpdateInfoDto getPileUpdateInfoDto(PileUpdateVo pileUpdateVo) {
        UpdateInfoDto result = new UpdateInfoDto();
        BeanUtils.copyProperties(pileUpdateVo, result);
        return result;
    }

    //发送电桩固件升级数据
    public static Boolean sendPileUpdateTopic(PileUpdateVo pileUpdateVo) {
        AtomicReference<Boolean> result = new AtomicReference<>(false);
        RedisGeneralUtil.executePile(pileUpdateVo.getPileCode(), () -> {
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileUpdateVo.getPileCode());
            if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) {
                //下发电桩费率数据
                if (pileRealModel.getMessageType() == 1) { //外网发送
                    if (StringUtil.isNotEmpty(pileRealModel.getTerminalCode())) {
                        PileUpdatePublishVo pileUpdatePublishVo = new PileUpdatePublishVo();
                        pileUpdatePublishVo.setPilesCode(Collections.singletonList(pileUpdateVo.getPileCode()));
                        pileUpdatePublishVo.setSponsor(0x01);//平台下发
                        pileUpdatePublishVo.setForced_update(pileUpdateVo.getUpgradeType());
                        pileUpdatePublishVo.setRequest_majorNo(pileUpdateVo.getHardwareMajorVersion());
                        pileUpdatePublishVo.setRequest_childNo(pileUpdateVo.getHardwareMinorVersion());
                        pileUpdatePublishVo.setDeviceType(pileUpdateVo.getFirmwareType());
                        pileUpdatePublishVo.setMajorNo(pileUpdateVo.getFirmwareMajorVersion());
                        pileUpdatePublishVo.setChildNo(pileUpdateVo.getFirmwareMinorVersion());
                        pileUpdatePublishVo.setBetaNo(pileUpdateVo.getFirmwareInternalVersion());
                        pileUpdatePublishVo.setCompiletime(pileUpdateVo.getFirmwareCompileTime().toLocalTime().toSecondOfDay());
                        pileUpdatePublishVo.setDataLen(pileUpdateVo.getFirmwareSize());
                        pileUpdatePublishVo.setDeviceCRC(pileUpdateVo.getCrc32());

                        //发送控制命令
                        IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                                .mid(String.valueOf(SMV2gConstant.SMV2G_MSG8001))
                                .param(JSON.toJSON(pileUpdatePublishVo))
                                .type(IEGConstant.Type.CMD_DEVICE_UPDATE)
                                .timestamp(SunMaxUtil.getSysTime())
                                .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                                .build();
                        String topicName = WebTopicConstant.TOPIC_PREFIX + pileRealModel.getTerminalCode() + WebTopicConstant.SERVICE_COMMAND;
                        WebMqttConfig.sendToMqtt(pileRealModel.getTerminalCode(), topicName, iegTopicVo);
                        result.set(true);
                    }
                }
                if (pileRealModel.getMessageType() == 2) { //内网发送
                    DevUpdateCmdDto devUpdateCmd = new DevUpdateCmdDto();
                    devUpdateCmd.setSponsor(0x01);
                    devUpdateCmd.setForced_update(pileUpdateVo.getUpgradeType());
                    devUpdateCmd.setRequest_majorNo(pileUpdateVo.getHardwareMajorVersion());
                    devUpdateCmd.setRequest_childNo(pileUpdateVo.getHardwareMinorVersion());
                    devUpdateCmd.setDeviceType(pileUpdateVo.getFirmwareType());
                    devUpdateCmd.setMajorNo(pileUpdateVo.getFirmwareMajorVersion());
                    devUpdateCmd.setChildNo(pileUpdateVo.getFirmwareMinorVersion());
                    devUpdateCmd.setBetaNo(pileUpdateVo.getFirmwareInternalVersion());
                    devUpdateCmd.setCompiletime(pileUpdateVo.getFirmwareCompileTime().toLocalTime().toSecondOfDay());
                    devUpdateCmd.setDataLen(pileUpdateVo.getFirmwareSize());
                    devUpdateCmd.setDeviceCRC(pileUpdateVo.getCrc32());
                    InterMqttConfig.sendToMqtt(pileUpdateVo.getPileCode(), CmdConstant.CMD_DevUpdate, devUpdateCmd);
                    result.set(true);
                }

                //保存电桩升级信息
                if (result.get()) {
                    Map<Integer, UpdateInfoDto> updateInfoMap = pileRealModel.getUpdateInfoMap();
                    updateInfoMap.put(pileUpdateVo.getFirmwareType(), getPileUpdateInfoDto(pileUpdateVo));
                    RedisGeneralUtil.setPileRealModel(pileUpdateVo.getPileCode(), pileRealModel);
                }
            } else {
                log.error("发送电桩升级数据失败, 失败数据:{}", pileRealModel);
            }
        });
        return result.get();
    }

    //批量对电桩升级
    public static Boolean sendBatchPileUpdateTopic(PileBatchUpdateVo pileBatchUpdateVo) {
        //定义网关下的电桩编号
        Map<String, Set<String>> terminalMap = Maps.newHashMap();

        //定义电桩升级实体类
        PileUpdateVo pileUpdateVo = new PileUpdateVo();
        BeanUtils.copyProperties(pileBatchUpdateVo, pileUpdateVo);

        for (String pileCode : pileBatchUpdateVo.getPileCodes()) {
            RedisGeneralUtil.executePile(pileCode, () -> {
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != -1
                        && pileRealModel.getWorkStatus() != 88) {
                    if (pileRealModel.getMessageType() == 1 && StringUtil.isNotEmpty(pileRealModel.getTerminalCode())) {
                        if (terminalMap.containsKey(pileRealModel.getTerminalCode())) {
                            terminalMap.get(pileRealModel.getTerminalCode()).add(pileRealModel.getPileCode());
                        } else {
                            Set<String> pileCodeSet = Sets.newHashSet();
                            pileCodeSet.add(pileRealModel.getPileCode());
                            terminalMap.put(pileRealModel.getTerminalCode(), pileCodeSet);
                        }
                    }
                    //电桩内网 单个升级
                    if (pileRealModel.getMessageType() == 2) {
                        sendInterPileUpdateTopic(pileCode, pileUpdateVo, pileRealModel);
                    }
                }
            });
        }
        //网关下的电桩批量升级
        if (MapUtils.isNotEmpty(terminalMap)) {
            terminalMap.forEach((terminalCode, pileCodes) -> sendWebPileUpdateTopic(terminalCode, pileCodes, pileUpdateVo));
        }
        return true;
    }

    //发送内网电桩固件升级数据
    public static void sendInterPileUpdateTopic(String pileCode, PileUpdateVo pileUpdateVo, PileRealModel pileRealModel) {
        //发送电桩控制升级
        DevUpdateCmdDto devUpdateCmd = new DevUpdateCmdDto();
        devUpdateCmd.setSponsor(0x01);
        devUpdateCmd.setForced_update(pileUpdateVo.getUpgradeType());
        devUpdateCmd.setRequest_majorNo(pileUpdateVo.getHardwareMajorVersion());
        devUpdateCmd.setRequest_childNo(pileUpdateVo.getHardwareMinorVersion());
        devUpdateCmd.setDeviceType(pileUpdateVo.getFirmwareType());
        devUpdateCmd.setMajorNo(pileUpdateVo.getFirmwareMajorVersion());
        devUpdateCmd.setChildNo(pileUpdateVo.getFirmwareMinorVersion());
        devUpdateCmd.setBetaNo(pileUpdateVo.getFirmwareInternalVersion());
        devUpdateCmd.setCompiletime(pileUpdateVo.getFirmwareCompileTime().toLocalTime().toSecondOfDay());
        devUpdateCmd.setDataLen(pileUpdateVo.getFirmwareSize());
        devUpdateCmd.setDeviceCRC(pileUpdateVo.getCrc32());
        InterMqttConfig.sendToMqtt(pileCode, CmdConstant.CMD_DevUpdate, devUpdateCmd);
        //保存电桩升级信息
        Map<Integer, UpdateInfoDto> updateInfoMap = pileRealModel.getUpdateInfoMap();
        updateInfoMap.put(pileUpdateVo.getFirmwareType(), getPileUpdateInfoDto(pileUpdateVo));
        RedisGeneralUtil.setPileRealModel(pileCode, pileRealModel);
    }

    //发送外网固件升级数据
    public static Boolean sendWebPileUpdateTopic(String terminalCode, Set<String> pileCodes, PileUpdateVo pileUpdateVo) {
        AtomicReference<Boolean> result = new AtomicReference<>(false);
        AtomicReference<IEGTopicVo> iegTopicVo = new AtomicReference<>();
        RedisGeneralUtil.executeGateway(terminalCode, () -> {
            GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
            if (gatewayRealModel != null && StringUtil.isNotEmpty(gatewayRealModel.getDeviceStatus()) && gatewayRealModel.getDeviceStatus() != 88) {
                result.set(true);
                PileUpdatePublishVo pileUpdatePublishVo = new PileUpdatePublishVo();
                pileUpdatePublishVo.setPilesCode(new ArrayList<>(pileCodes));
                pileUpdatePublishVo.setSponsor(0x01);//平台下发
                pileUpdatePublishVo.setForced_update(pileUpdateVo.getUpgradeType());
                pileUpdatePublishVo.setRequest_majorNo(pileUpdateVo.getHardwareMajorVersion());
                pileUpdatePublishVo.setRequest_childNo(pileUpdateVo.getHardwareMinorVersion());
                pileUpdatePublishVo.setDeviceType(pileUpdateVo.getFirmwareType());
                pileUpdatePublishVo.setMajorNo(pileUpdateVo.getFirmwareMajorVersion());
                pileUpdatePublishVo.setChildNo(pileUpdateVo.getFirmwareMinorVersion());
                pileUpdatePublishVo.setBetaNo(pileUpdateVo.getFirmwareInternalVersion());
                pileUpdatePublishVo.setCompiletime(pileUpdateVo.getFirmwareCompileTime().toLocalTime().toSecondOfDay());
                pileUpdatePublishVo.setDataLen(pileUpdateVo.getFirmwareSize());
                pileUpdatePublishVo.setDeviceCRC(pileUpdateVo.getCrc32());
                //发送控制命令
                iegTopicVo.set(IEGTopicVo.builder()
                        .mid(String.valueOf(SMV2gConstant.SMV2G_MSG8001))
                        .param(JSON.toJSON(pileUpdatePublishVo))
                        .type(IEGConstant.Type.CMD_DEVICE_UPDATE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build());

                //保存网关升级信息
                Map<Integer, UpdateInfoDto> updateInfoMap = gatewayRealModel.getUpdateInfoMap();
                updateInfoMap.put(pileUpdateVo.getFirmwareType(), getPileUpdateInfoDto(pileUpdateVo));
                gatewayRealModel.setUpdateInfoMap(updateInfoMap);
                Map<Integer, Set<String>> updatePileMap = gatewayRealModel.getUpdatePileMap();
                updatePileMap.put(pileUpdateVo.getFirmwareType(), pileCodes);
                gatewayRealModel.setUpdatePileMap(updatePileMap);
                RedisGeneralUtil.setGatewayRealModel(terminalCode, gatewayRealModel);
            } else {
                log.error("发送网关下的电桩升级数据失败, 失败数据:{}", gatewayRealModel);
            }
        });
        if (result.get() && iegTopicVo.get() != null) {
            String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.SERVICE_COMMAND;
            WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo.get());
            result.set(true);
        } else {
            result.set(false);
        }
        return result.get();
    }

    //控制板信息请求命令
    public static Boolean sendSubModelInfoTopic(String pileCode) {
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
        if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) {
            CmdToPoFwResPublishVo cmdToPoFwResPublishVo = new CmdToPoFwResPublishVo();
            List<String> pilesCode = new ArrayList<>();
            pilesCode.add(pileCode);
            cmdToPoFwResPublishVo.setPilesCode(pilesCode);
            //下发电桩费率数据
            if (pileRealModel.getMessageType() == 1) { //外网发送
                if (StringUtil.isEmpty(pileRealModel.getTerminalCode())) {
                    log.error("外网发送控制板信息请求数据,网关编号未获取到: {}", pileRealModel.getTerminalCode());
                    return false;
                }
                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                        .mid(String.valueOf(SMV2gConstant.SMV2G_MSG6000))
                        .param(JSON.toJSON(cmdToPoFwResPublishVo))
                        .type(IEGConstant.Type.CMD_TOPO_FW_REQ)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + pileRealModel.getTerminalCode() + WebTopicConstant.DEVICE_COMMAND;
                WebMqttConfig.sendToMqtt(pileRealModel.getTerminalCode(), topicName, iegTopicVo);
                return true;
            }
        } else {
            log.error("控制板信息请求失败, 失败数据:{}", pileRealModel);
        }
        return false;
    }

    //设置电桩功率控制主题
    public static void sendLoadParamTopic(GateWayControlVo gateWayControlVo) {
        if (gateWayControlVo != null && StringUtil.isNotEmpty(gateWayControlVo.getDeviceCode())) {
            GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(gateWayControlVo.getDeviceCode());
            if (gatewayRealModel != null && gatewayRealModel.getDeviceStatus() != 88) {
                GwLoadParamSetPublishVo gwLoadParamSetPublishVo = new GwLoadParamSetPublishVo();
//               gwLoadParamSetPublishVo.setEnable(1);//使能
                Integer ctrlType = gateWayControlVo.getControlType();
                String ctrlValue = gateWayControlVo.getControlValue();
                switch (ctrlType) {
                    case 0://只做查询
                        break;
                    case 1://设置是否启用功率控制
                        gwLoadParamSetPublishVo.setEnable(Integer.valueOf(ctrlValue));
                        break;
                    case 2://设置最大负荷
                        gwLoadParamSetPublishVo.setMax_load(Double.valueOf(ctrlValue));
                        break;
                    case 3: //设置最大负荷容差
                        gwLoadParamSetPublishVo.setLoadWaveType(1);//默认绝对值 kW
                        gwLoadParamSetPublishVo.setLoadWaveUpCfg(Double.valueOf(ctrlValue));
                        gwLoadParamSetPublishVo.setLoadWaveDownCfg(Double.valueOf(ctrlValue));
                        break;
                    case 4: //设置单桩最大功率
                        gwLoadParamSetPublishVo.setPile_maxPower(Double.valueOf(ctrlValue));
                        break;
                    case 5: //设置监视周期
                        gwLoadParamSetPublishVo.setMonitorPeriod(Integer.valueOf(ctrlValue));
                        break;
                }
                //发送控制命令
                //下发停止主题参数
                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                        .mid("94009")
                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.LOAD_PARAMSET).paras(JSON.toJSON(gwLoadParamSetPublishVo)).build())
                        .type(IEGConstant.Type.CMD_SERVICE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + gateWayControlVo.getDeviceCode() + WebTopicConstant.SERVICE_COMMAND;
                //下发参数
                WebMqttConfig.sendToMqtt(gateWayControlVo.getDeviceCode(), topicName, iegTopicVo);
            } else {
                log.error("设置电桩功率失败, 失败数据:{}", gatewayRealModel);
            }
        }
    }

    //下发网关控制策略通用参数下发
    public static void sendPolicyParamIssuedTopic(GateWayPolicyVo policyIssuedVo) {

        if (policyIssuedVo != null && StringUtil.isNotEmpty(policyIssuedVo.getDeviceCode())) {
            GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(policyIssuedVo.getDeviceCode());
            if (gatewayRealModel != null && StringUtil.isNotEmpty(gatewayRealModel.getDeviceStatus()) && gatewayRealModel.getDeviceStatus() != 88) {
                GwGeneralParamGetSetPublishVo gwGeneralParamGetSet = new GwGeneralParamGetSetPublishVo();
                gwGeneralParamGetSet.setPolicyId(policyIssuedVo.getPolicyId());
                gwGeneralParamGetSet.setName(policyIssuedVo.getName());
                gwGeneralParamGetSet.setPolicyCfg(policyIssuedVo.getPolicyCfg());
                gwGeneralParamGetSet.setPolicyPeriod(policyIssuedVo.getPolicyPeriod());
                gwGeneralParamGetSet.setCmdType(policyIssuedVo.getCmdType());

                //发送控制命令
                //下发网关控制策略通用主题参数
                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                        .mid("10086")
                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.GENERAL_PARAMGETSET).paras(JSON.toJSON(gwGeneralParamGetSet)).build())
                        .type(IEGConstant.Type.CMD_SERVICE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + policyIssuedVo.getDeviceCode() + WebTopicConstant.SERVICE_COMMAND;
                //下发参数
                WebMqttConfig.sendToMqtt(policyIssuedVo.getDeviceCode(), topicName, iegTopicVo);
            } else {
                log.error("网关控制策略通用参数下发失败, 失败数据:{}", gatewayRealModel);
            }
        }
    }

    //下发调控需求命令参数
    public static void sendControlResponseIssuedTopic(String terminalCode, ControlResponseVo.ControlInfo controlInfo) {
        if (StringUtil.isNotEmpty(terminalCode) && controlInfo != null) {
            GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
            if (gatewayRealModel != null && gatewayRealModel.getDeviceStatus() != 88) {
                GwPVControlSetPublishVo gwPVControlSet = new GwPVControlSetPublishVo();
                BeanUtils.copyProperties(controlInfo, gwPVControlSet);
                gwPVControlSet.setXylx(Integer.parseInt(controlInfo.getXylx()));
                gwPVControlSet.setSjlx(Integer.parseInt(controlInfo.getSjlx()));

                //发送控制命令
                //下发调控需求命令
                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                        .mid("5001")
                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.GATEWAY_PEAK_VALLEY_CONTROL_SET).paras(JSON.toJSON(gwPVControlSet)).build())
                        .type(IEGConstant.Type.CMD_SERVICE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.SERVICE_COMMAND;
                //下发参数
                WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo);
            } else {
                log.error("调控需求命令参数下发失败, 失败数据:{}", gatewayRealModel);
            }
        }
    }

    //下发控制响应终止参数
    public static void sendControlStopIssuedTopic(String terminalCode, ControlStopVo controlStop) {
        if (StringUtil.isNotEmpty(terminalCode) && controlStop != null) {
            GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
            if (gatewayRealModel != null && gatewayRealModel.getDeviceStatus() != 88) {
                GwPVControlStopPublishVo gwPVControlStop = new GwPVControlStopPublishVo();
                gwPVControlStop.setSjbh(controlStop.getSjbh());
                gwPVControlStop.setSjzt(controlStop.getSjzt());
                //发送控制命令
                //下发调控需求命令
                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                        .mid("5002")
                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.GATEWAY_PEAK_VALLEY_CONTROL_STOP).paras(JSON.toJSON(gwPVControlStop)).build())
                        .type(IEGConstant.Type.CMD_SERVICE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.SERVICE_COMMAND;
                //下发参数
                WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo);
            } else {
                log.error("控制响应终止参数下发失败, 失败数据:{}", gatewayRealModel);
            }
        }
    }

    //下发网关设置平台驱动参数
    public static void sendPlatformSetTopic(String terminalCode, Integer policyId, List<PlatformSetVo> platformSetVos) {
        GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
        if (gatewayRealModel != null && gatewayRealModel.getDeviceStatus() != 88) {
            //根据协议驱动进行分组，下发
            Map<String, List<PlatformSetVo>> platformSetMap = platformSetVos.stream().filter(p -> StringUtil.isNotEmpty(p.getProtocolDriver()))
                    .collect(Collectors.groupingBy(PlatformSetVo::getProtocolDriver));
            //设置平台下发参数
            GwPlatformSetPublicVo gwPlatformSet = new GwPlatformSetPublicVo();
            gwPlatformSet.setPolicyId(policyId);
            gwPlatformSet.setCmdType("set");
            gwPlatformSet.setSrvCode(4);
            //组装策略参数
            GwPlatformSetPublicVo.PolicyCfg policyCfg = new GwPlatformSetPublicVo.PolicyCfg();
            policyCfg.setApiCode(0x1000);
            List<GwPlatformSetPublicVo.PlatformDriver> platformDriverList = Lists.newArrayList();
            platformSetMap.forEach((key, value) -> platformDriverList.add(GwPlatformSetPublicVo.PlatformDriver.builder()
                    .comDriver(key)
                    .comType(2)
                    .serverCfg(value.stream().map(platformVo -> GwPlatformSetPublicVo.ServerCfg.builder()
                            .hostAddr(platformVo.getIp())
                            .hostPort(platformVo.getPort())
                            .company_id(platformVo.getPlatformLogo())
                            .build()).collect(Collectors.toList()))
                    .build()));
            policyCfg.setApiParams(platformDriverList);

            gwPlatformSet.setPolicyCfg(policyCfg);

            //下发网关控制策略通用主题参数
            IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                    .mid("10086")
                    .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.GENERAL_PARAMGETSET).paras(JSON.toJSON(gwPlatformSet)).build())
                    .type(IEGConstant.Type.CMD_SERVICE)
                    .timestamp(SunMaxUtil.getSysTime())
                    .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                    .build();
            String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.SERVICE_COMMAND;
            //下发参数
            WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo);
        } else {
            log.error("网关设置平台驱动参数失败, 失败数据:{}", gatewayRealModel);
        }
    }

    //下发网关平台状态查询参数
    public static void sendPlatformStatusTopic(String terminalCode, Integer policyId, List<PlatformStatusVo> platformStatusVos) {
        GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
        if (gatewayRealModel != null && gatewayRealModel.getDeviceStatus() != 88) {
            //设置平台下发参数
            GwPlatformSetPublicVo gwPlatformSet = new GwPlatformSetPublicVo();
            gwPlatformSet.setPolicyId(policyId);
            gwPlatformSet.setCmdType("set");
            gwPlatformSet.setSrvCode(4);
            //组装策略参数
            GwPlatformSetPublicVo.PolicyCfg policyCfg = new GwPlatformSetPublicVo.PolicyCfg();
            policyCfg.setApiCode(0x2000);
            //按照协议驱动查询
            Map<String, List<String>> apiParams = Maps.newHashMap();
            apiParams.put("comDrivers", platformStatusVos.stream().map(PlatformStatusVo::getProtocolDriver).filter(StringUtil::isNotEmpty)
                    .distinct().collect(Collectors.toList()));
            policyCfg.setApiParams(apiParams);

            gwPlatformSet.setPolicyCfg(policyCfg);

            //下发网关控制策略通用主题参数
            IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                    .mid("10086")
                    .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.GENERAL_PARAMGETSET).paras(JSON.toJSON(gwPlatformSet)).build())
                    .type(IEGConstant.Type.CMD_SERVICE)
                    .timestamp(SunMaxUtil.getSysTime())
                    .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                    .build();
            String topicName = WebTopicConstant.TOPIC_PREFIX + terminalCode + WebTopicConstant.SERVICE_COMMAND;
            //下发参数
            WebMqttConfig.sendToMqtt(terminalCode, topicName, iegTopicVo);
        } else {
            log.error("网关平台状态查询失败, 失败数据:{}", gatewayRealModel);
        }
    }

    //下发车辆信息请求
    public static Boolean sendVehicleInfoTopic(String pileCode, String gunCode) {
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
        if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) {
            //下发电桩费率数据
            if (pileRealModel.getMessageType() == 1) { //外网发送
                if (StringUtil.isEmpty(pileRealModel.getTerminalCode())) {
                    log.error("外网发送电桩费率数据,网关编号未获取到:{}", pileRealModel.getTerminalCode());
                    return false;
                }
                //发送控制命令
//                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
//                        .mid(String.valueOf(SMV2gConstant.SMV2G_MSG1003))
//                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.RATE_SET).paras(JSON.toJSON(rateSet)).build())
//                        .type(IEGConstant.Type.CMD_SERVICE)
//                        .timestamp(SunMaxUtil.getSysTime())
//                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
//                        .build();
//                String topicName = WebTopicConstant.TOPIC_PREFIX + pileRealModel.getTerminalCode() + WebTopicConstant.SERVICE_COMMAND;
//                WebMqttConfig.sendToMqtt(pileRealModel.getTerminalCode(), topicName, iegTopicVo);
//                return true;
            }
            if (pileRealModel.getMessageType() == 2) { //内网发送
                VehicleInfoReqDto vehicleInfoReq = new VehicleInfoReqDto();
                vehicleInfoReq.setPilesCode(pileCode);
                vehicleInfoReq.setGunCode(Integer.parseInt(gunCode));
                InterMqttConfig.sendToMqtt(pileCode, CmdConstant.CMD_VehicleInfoRequest, vehicleInfoReq);
                return true;
            }
        } else {
            log.error("发送车辆信息请求数据, 失败数据:{}", pileRealModel);
        }
        return false;
    }

    //发送电桩复位数据
    public static Boolean sendPileLogQuery(PileLogQueryVo pileLogQueryVo) {
        return false;
    }

    //发送电桩复位数据
    public static Boolean sendPileReset(PileResetVo pileResetVo) {
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileResetVo.getPileCode());
        if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) {
            PileResetPublishVo pileReset = new PileResetPublishVo();
            pileReset.setPilesCode(pileResetVo.getPileCode());
            pileReset.setType(pileResetVo.getFirmwareType());

            //下发电桩费率数据
            if (pileRealModel.getMessageType() == 1) { //外网发送
                if (StringUtil.isEmpty(pileRealModel.getTerminalCode())) {
                    log.error("外网发送电桩复位数据,网关编号未获取到:{}", pileRealModel.getTerminalCode());
                    return false;
                }
                //发送控制命令
                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
                        .mid(String.valueOf(SMV2gConstant.SMV2G_MSG6010))
                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.PILE_RESET).paras(JSON.toJSON(pileReset)).build())
                        .type(IEGConstant.Type.CMD_SERVICE)
                        .timestamp(SunMaxUtil.getSysTime())
                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
                        .build();
                String topicName = WebTopicConstant.TOPIC_PREFIX + pileRealModel.getTerminalCode() + WebTopicConstant.SERVICE_COMMAND;
                WebMqttConfig.sendToMqtt(pileRealModel.getTerminalCode(), topicName, iegTopicVo);
                //保存外网电桩复位数据
                PileRecordUtil.saveMqttRecord(pileResetVo.getPileCode(), null, 1, 9, 1, IEGConstant.Param.PILE_RESET, pileReset);
                return true;
            }
            if (pileRealModel.getMessageType() == 2) { //内网发送
                PileResetCmdDto resetCmd = new PileResetCmdDto();
                resetCmd.setType(pileResetVo.getFirmwareType());
                InterMqttConfig.sendToMqtt(pileResetVo.getPileCode(), CmdConstant.CMD_PILE_RESET, resetCmd);
                //保存外网电桩复位数据
                PileRecordUtil.saveMqttRecord(pileResetVo.getPileCode(), null, 1, 9, 2, CmdConstant.CMD_PILE_RESET, resetCmd);
                return true;
            }
        } else {
            log.error("发送电桩复位数据, 失败数据:{}", pileRealModel);
        }
        return false;
    }

    //发送设置二维码数据
    public static Boolean sendPileSetQr(PileSetQrVo pileSetQrVo) {
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileSetQrVo.getPileCode());
        if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus()) && pileRealModel.getWorkStatus() != 88) {
            //下发电桩费率数据
            if (pileRealModel.getMessageType() == 1) { //外网发送
                if (StringUtil.isEmpty(pileRealModel.getTerminalCode())) {
                    log.error("外网发送设置二维码数据,网关编号未获取到:{}", pileRealModel.getTerminalCode());
                    return false;
                }
                //TODO 待定
                //发送控制命令
//                IEGTopicVo iegTopicVo = IEGTopicVo.builder()
//                        .mid(String.valueOf(SMV2gConstant.SMV2G_MSG6010))
//                        .param(ResponseSubscribeVo.builder().cmd(IEGConstant.Param.PILE_RESET).paras(JSON.toJSON(pileReset)).build())
//                        .type(IEGConstant.Type.CMD_SERVICE)
//                        .timestamp(SunMaxUtil.getSysTime())
//                        .expire(SunMaxUtil.getSysTime() + KeyUtil.EXPIRE_TIME)
//                        .build();
//                String topicName = WebTopicConstant.TOPIC_PREFIX + pileRealModel.getTerminalCode() + WebTopicConstant.SERVICE_COMMAND;
//                WebMqttConfig.sendToMqtt(pileRealModel.getTerminalCode(), topicName, iegTopicVo);
                //保存外网电桩复位数据
//                PileRecordUtil.saveMqttRecord(pileResetVo.getPileCode(), null, 1, 11, 1, IEGConstant.Param.PILE_RESET, pileReset);
                return false;
            }
            if (pileRealModel.getMessageType() == 2) { //内网发送
                PileSetQrCmdDto setQrCmd = new PileSetQrCmdDto();
                setQrCmd.setPilesCode(pileSetQrVo.getPileCode());
                setQrCmd.setQrFormat(1); //默认传1 前缀+桩编号+枪编号
                setQrCmd.setQrLen(pileSetQrVo.getQrStr().length());
                setQrCmd.setQrStr(pileSetQrVo.getQrStr());

                InterMqttConfig.sendToMqtt(pileSetQrVo.getPileCode(), CmdConstant.CMD_PILE_SETQR, setQrCmd);
                //保存外网设置二维码数据
                PileRecordUtil.saveMqttRecord(pileSetQrVo.getPileCode(), null, 1, 11, 2, CmdConstant.CMD_PILE_SETQR, setQrCmd);
                return true;
            }
        } else {
            log.error("发送设置二维码数据, 失败数据:{}", pileRealModel);
        }
        return false;
    }

}
