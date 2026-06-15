package com.sunmax.protocol.service.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.protocol.PileLogResultDto;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.crontab.PileStartControlVo;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.common.vo.protocol.mqtt.inter.VehicleInfoResVo;
import com.sunmax.protocol.constant.SMV2gConstant;
import com.sunmax.protocol.demand.GatewayDemand;
import com.sunmax.protocol.demand.PileDemand;
import com.sunmax.protocol.model.GatewayDemandModel;
import com.sunmax.protocol.model.PileDemandModel;
import com.sunmax.protocol.service.PileCtrlService;
import com.sunmax.protocol.service.feign.CrontabService;
import com.sunmax.protocol.service.feign.DeviceService;
import com.sunmax.protocol.task.ControlTask;
import com.sunmax.protocol.util.MqttTopicUtil;
import com.sunmax.protocol.util.PileControlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j
public class PileCtrlServiceImpl implements PileCtrlService {

    @Autowired
    private CrontabService crontabService;

    @Autowired
    private DeviceService deviceService;

    //电桩日志查询结果缓存
    public static Map<String, List<PileLogResultDto>> pileLogResultMap = Maps.newConcurrentMap();

    @Override
    public ResponseResult<PileResultDto> pileStart(PileStartVo pileStartVo) {
        // 数据校验
        if (!PileControlUtil.isValidPileStart(pileStartVo)) {
            return ResponseResult.paramError("输入数据校验失败");
        }

        // 初始化返回结果对象
        PileResultDto result = new PileResultDto();
        String pileCode = pileStartVo.getPileCode();
        String gunCode = String.valueOf(pileStartVo.getGunCode());
        result.setPileCode(pileCode);
        result.setGunCode(pileStartVo.getGunCode());
        result.setType(pileStartVo.getRunMode());

        PileStartControlVo pileControlVo = new PileStartControlVo();

        // 计算权重，生成启动命令
        String keyId = pileCode + gunCode + SMV2gConstant.STARTCMD;
        try {

            if (PileDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }

            // 判断是否重复启动，并校验时间间隔
            if (PileControlUtil.isDuplicateStart(keyId)) {
                return ResponseResult.paramError(SMV2gConstant.StatusWorking);
            }

            // 创建订单号
            if (StringUtil.isEmpty(pileStartVo.getSerialNum())) {
                pileStartVo.setSerialNum(PileControlUtil.createOrderNum(pileCode, gunCode));
            }

            // 创建查询请求对象
            PileDemandModel demandModel = PileDemand.createDemand(keyId, pileStartVo.getPileCode(), pileStartVo.getGunCode());

            // 预缓存订单信息到缓存中
            PileControlUtil.cacheOrderInfo(pileStartVo, pileCode, gunCode);

            // 校验平台启动自动控制
            pileControlVo = PileControlUtil.checkPileControl(pileStartVo, crontabService);

            // 下发启动命令
            if (!MqttTopicUtil.sendPileStartTopic(pileStartVo)) {
                result.setResult(1);
                //下发失败移除需求
                PileDemand.deleteDemand(keyId);
                return ResponseResult.error("下发启动命令失败", result);
            }

            // 开启查询线程等待返回结果
            JSONObject asyncTaskResult = ControlTask.ctrPileTaskFunc(demandModel, 60);

            // 移除需求模型
            PileDemand.deleteDemand(keyId);

            // 根据异步任务结果映射中的消息内容，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                result.setResult(recCode);
                pileControlVo.setResult(recCode);
                result.setFailDetailReason(asyncTaskResult.getString("recMsg"));
                if (recCode == 0) { //成功返回流水号
                    result.setSerialNum(asyncTaskResult.getString("serialNum")); // 设置订单编号为结果对象的序列号
                }
            }

            // 更新控制校验
            PileControlUtil.updatePileControl(pileControlVo, crontabService);

            // 返回处理后的结果
            if (result.getResult() == 0) {
                return ResponseResult.ok(result);
            } else {
                return ResponseResult.error("启动充电桩失败", result);
            }
        } catch (Exception e) {
            log.error("启动充电桩失败", e);
            PileDemand.deleteDemand(keyId);
            result.setResult(500); // 未知错误
            // 更新控制校验
            pileControlVo.setResult(500);
            PileControlUtil.updatePileControl(pileControlVo, crontabService);
            return ResponseResult.error("启动充电桩异常", result);
        }
    }

    @Override
    public ResponseResult<PileResultDto> pileStop(PileStopVo pileStopVo) {
        // 数据校验
        if (!PileControlUtil.isValidPileStop(pileStopVo)) {
            return new ResponseResult<>(ResponseResult.CodeStatus.FAIL, "输入数据校验失败");
        }
        // 初始化结果对象
        PileResultDto result = new PileResultDto();

        // 设置基础结果信息
        String pileCode = pileStopVo.getPileCode();
        String gunCode = pileStopVo.getGunCode();
        result.setPileCode(pileCode);
        result.setGunCode(gunCode);

        // 计算下发停止命令的权重
        String keyId = pileCode + gunCode + SMV2gConstant.STOPCMD;
        try {
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }

            // 创建停止命令的查询请求模型
            PileDemandModel demandModel = PileDemand.createDemand(keyId, pileCode, gunCode);

            // 下发停止命令
            if (!MqttTopicUtil.sendPileStopTopic(pileStopVo)) {
                result.setResult(1);
                //下发失败移除需求
                PileDemand.deleteDemand(keyId);
                return ResponseResult.error("下发停止命令失败", result);
            }

            // 开启异步任务，等待停止命令执行结果
            JSONObject asyncTaskResult = ControlTask.ctrPileTaskFunc(demandModel, 10);

            // 从系统中移除该需求模型
            PileDemand.deleteDemand(keyId);

            // 根据异步任务执行结果，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                result.setSerialNum(asyncTaskResult.getString("serialNum")); // 设置订单编号为结果对象的序列号
                result.setResult(recCode);
                result.setFailDetailReason(asyncTaskResult.getString("recMsg"));
                // 返回操作结果，结果数据经过加密处理
                if (recCode == 0) { //成功返回流水号
                    return ResponseResult.ok(result);
                }
            }
            return ResponseResult.error("停止充电桩失败", result);
        } catch (Exception e) {
            // 记录停止操作失败的异常日志
            log.error("停止充电桩失败", e);
            PileDemand.deleteDemand(keyId);
            // 设置未知错误的失败原因
            result.setResult(500);
            return ResponseResult.error("停止充电桩处理异常", result);
        }
    }

    @Override
    public ResponseResult<PileResultDto> pilePowerCtrl(PilePowerCtrlVo pilePowerCtrlVo) {
        // 数据校验
        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                pilePowerCtrlVo.getPileCode(),
                pilePowerCtrlVo.getGunCode(),
                pilePowerCtrlVo.getRunMode(),
                pilePowerCtrlVo.getCtrlType(),
                pilePowerCtrlVo.getOutPower()
        };

        // 使用StringUtil.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        if (Arrays.stream(fieldsToValidate).anyMatch(StringUtil::isEmpty)) {
            return ResponseResult.paramError("输入数据校验失败");
        }

        // 初始化结果对象
        PileResultDto result = new PileResultDto();
        result.setPileCode(pilePowerCtrlVo.getPileCode());
        result.setGunCode(pilePowerCtrlVo.getGunCode());
        //计算权重   命令码+枪号+地址
        String keyId = pilePowerCtrlVo.getPileCode() + pilePowerCtrlVo.getGunCode() + SMV2gConstant.POWERCTRL;
        try {
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }
            //创建查询请求
            PileDemandModel demandModel = PileDemand.createDemand(keyId, pilePowerCtrlVo.getPileCode(), pilePowerCtrlVo.getGunCode());

            //发送请求
            // 下发停止命令
            if (!MqttTopicUtil.sendPowerCtrlTopic(pilePowerCtrlVo)) {
                result.setResult(1);
                //下发失败移除需求
                PileDemand.deleteDemand(keyId);
                return ResponseResult.error("下发功率控制充电桩失败", result);
            }

            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrPileTaskFunc(demandModel, 10);

            //移除需求
            PileDemand.deleteDemand(keyId);

            // 根据异步任务执行结果，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                result.setResult(recCode);
                // 返回操作结果，结果数据经过加密处理
                if (recCode == 0) { //成功返回流水号
                    return ResponseResult.ok(result);
                }
            }
            return ResponseResult.error("功率控制充电桩失败", result);
        } catch (Exception e) {
            PileDemand.deleteDemand(keyId);
            // 记录操作失败的异常日志
            log.error("功率控制充电桩处理异常", e);
            result.setResult(500);
            return ResponseResult.error("功率控制充电桩处理异常", result);
        }
    }

    @Override
    public ResponseResult<PileResultDto> pileRateSet(PileRateSetVo pileRateSetVo) {
        // 数据校验
        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                pileRateSetVo.getPileCode(),
                pileRateSetVo.getType(),
                pileRateSetVo.getTemplateId()
        };

        // 使用StringUtil.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        if (Arrays.stream(fieldsToValidate).anyMatch(StringUtil::isEmpty)) {
            return ResponseResult.paramError("输入数据校验失败");
        }

        // 初始化结果对象
        PileResultDto result = new PileResultDto();
        result.setPileCode(pileRateSetVo.getPileCode());
        result.setType(pileRateSetVo.getType());
        //计算权重
        String keyId = pileRateSetVo.getPileCode() + pileRateSetVo.getType() + pileRateSetVo.getTemplateId() + SMV2gConstant.RATESET;//费率类型-充电费率/放电
        try {
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }

            //创建查询请求
            PileDemandModel demandModel = PileDemand.createDemand(keyId, pileRateSetVo.getPileCode(), null);

            //发送设置费率请求
            if (!MqttTopicUtil.sendPileRateTopic(pileRateSetVo)) {
                result.setResult(1);
                //下发失败移除需求
                PileDemand.deleteDemand(keyId);
                return ResponseResult.error("下发充电桩费率失败", result);
            }

            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrPileTaskFunc(demandModel, 30);

            //移除需求
            PileDemand.deleteDemand(keyId);

            // 根据异步任务执行结果，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                result.setResult(recCode);
                // 返回操作结果
                if (recCode == 0) {
                    return ResponseResult.ok(result);
                }
            }
            return ResponseResult.error("电桩费率下发失败", result);
        } catch (Exception e) {
            // 记录操作失败的异常日志
            log.error("下发充电桩费率处理异常", e);
            //移除需求
            PileDemand.deleteDemand(keyId);
            result.setResult(500);
            return ResponseResult.error("下发充电桩费率处理异常", result);
        }
    }

    @Override
    public ResponseResult<PileResultDto> pileUpdate(PileUpdateVo pileUpdateVo) {
        //数据校验
        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                pileUpdateVo.getTaskId(),
                pileUpdateVo.getPileCode(),
                pileUpdateVo.getUpgradeType()
        };

        // 使用Apache Commons Lang3的StringUtils.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        if (Arrays.stream(fieldsToValidate).anyMatch(StringUtil::isEmpty)) {
            return ResponseResult.paramError("输入数据校验失败");
        }

        // 初始化结果对象
        PileResultDto result = new PileResultDto();
        result.setPileCode(pileUpdateVo.getPileCode());
        //计算权重
        String keyId = pileUpdateVo.getPileCode() + pileUpdateVo.getFirmwareMajorVersion() + pileUpdateVo.getFirmwareMinorVersion() + SMV2gConstant.PILEUPDATE;//充电桩编号+命令码
        try {

            if (PileDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }

            //创建查询请求
            PileDemandModel demandModel = PileDemand.createDemand(keyId, pileUpdateVo.getPileCode(), null);

            //发送设置费率请求
            if (!MqttTopicUtil.sendPileUpdateTopic(pileUpdateVo)) {
                result.setResult(1);
                //下发失败移除需求
                PileDemand.deleteDemand(keyId);
                return ResponseResult.error("电桩升级失败", result);
            }

            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrPileTaskFunc(demandModel, 30);

            //移除需求
            PileDemand.deleteDemand(keyId);

            // 根据异步任务执行结果，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                result.setResult(recCode);
                // 返回操作结果
                if (recCode == 0) {
                    return ResponseResult.ok(result);
                }
            }
            return ResponseResult.error("电桩升级下发失败", result);
        } catch (Exception e) {
            // 记录操作失败的异常日志
            log.error("电桩升级处理异常", e);
            //移除需求
            PileDemand.deleteDemand(keyId);
            result.setResult(500);
            return ResponseResult.error("电桩升级处理异常", result);
        }
    }

    @Override
    public ResponseResult<List<PileResultDto>> pileUpdateByIeg(String terminalCode, Set<String> pileCodes, PileUpdateVo pileUpdateVo) {

        // 数据校验
        if (StringUtil.isEmpty(terminalCode) || StringUtil.isEmpty(pileCodes) || pileUpdateVo == null) {
            return ResponseResult.paramError("输入数据校验失败");
        }
        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                pileUpdateVo.getTaskId(),
                pileUpdateVo.getPileCode(),
                pileUpdateVo.getUpgradeType()
        };

        // 使用Apache Commons Lang3的StringUtils.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        if (Arrays.stream(fieldsToValidate).anyMatch(StringUtil::isEmpty)) {
            return ResponseResult.paramError("输入数据校验失败");
        }

        // 初始化结果对象
        List<PileResultDto> resultList = pileCodes.stream().map(pileCode -> {
            PileResultDto result = new PileResultDto();
            result.setPileCode(pileCode);
            result.setResult(1);
            return result;
        }).collect(Collectors.toList());
        //计算权重
        String keyId = terminalCode + pileUpdateVo.getFirmwareMajorVersion() + pileUpdateVo.getFirmwareMinorVersion() + SMV2gConstant.PILEUPDATEBYIEG;//终端编号+命令码
        try {

            if (GatewayDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }

            //创建查询请求
            GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, terminalCode);

            //发送设置费率请求
            if (!MqttTopicUtil.sendWebPileUpdateTopic(terminalCode, pileCodes, pileUpdateVo)) {
                resultList = resultList.stream().peek(result -> result.setFailDetailReason("网关未在线")).collect(Collectors.toList());
                //下发失败移除需求
                GatewayDemand.deleteDemand(keyId);
                return ResponseResult.error("网关电桩升级失败", resultList);
            }

            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 300);

            //移除需求
            GatewayDemand.deleteDemand(keyId);

            // 根据异步任务执行结果，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                // 返回操作结果
                if (recCode == 0) {
                    resultList = pileCodes.stream().map(pileCode -> {
                        PileResultDto result = new PileResultDto();
                        result.setPileCode(pileCode);
                        result.setResult(0);
                        return result;
                    }).collect(Collectors.toList());
                    return ResponseResult.ok(resultList);
                }
            }
            resultList = resultList.stream().peek(result -> result.setFailDetailReason("电桩升级下发失败")).collect(Collectors.toList());
            return ResponseResult.error("电桩升级下发失败", resultList);
        } catch (Exception e) {
            // 记录操作失败的异常日志
            log.error("电桩升级处理异常", e);
            //移除需求
            GatewayDemand.deleteDemand(keyId);
            resultList = resultList.stream().peek(result -> result.setFailDetailReason("电桩升级异常")).collect(Collectors.toList());
            return ResponseResult.error("电桩升级处理异常", resultList);
        }
    }

    @Override
    public ResponseResult<Void> pileSubModelInfoReq(String pileCode) {
        // 数据校验
        if (StringUtil.isEmpty(pileCode)) {
            return ResponseResult.paramError("输入数据校验失败");
        }
        //计算权重   命令码+桩编码
        String keyId = pileCode + SMV2gConstant.SUB_MODEL_INFO_REQ;
        try {
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }
            //创建查询请求
            PileDemandModel demandModel = PileDemand.createDemand(keyId, pileCode, null);
            //下发请求
            if (!MqttTopicUtil.sendSubModelInfoTopic(pileCode)) {
                //移除需求
                PileDemand.deleteDemand(keyId);
                return ResponseResult.error("下发控制板请求失败");
            }
            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrPileTaskFunc(demandModel, 10);

            //移除需求
            PileDemand.deleteDemand(keyId);

            // 根据异步任务执行结果，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                // 返回操作结果
                if (recCode == 0) {
                    return ResponseResult.ok();
                }
            }
            return ResponseResult.error("控制板请求失败");
        } catch (Exception e) {
            // 记录操作失败的异常日志
            log.error("控制板请求处理异常", e);
            //移除需求
            PileDemand.deleteDemand(keyId);
            return ResponseResult.error("控制板请求处理异常");
        }
    }

    @Override
    public ResponseResult<VehicleInfoResVo> vehicleInfoRequest(String pileCode, String gunCode) {
        // 数据校验
        if (StringUtil.isEmpty(pileCode) || StringUtil.isEmpty(gunCode)) {
            return ResponseResult.paramError("输入数据校验失败");
        }

        //计算权重   命令码+桩编码
        String keyId = pileCode + gunCode + SMV2gConstant.VEHICLE_INFO_REQUEST;
        try {
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }
            //创建查询请求
            PileDemandModel demandModel = PileDemand.createDemand(keyId, pileCode, gunCode);
            //下发请求
            if (!MqttTopicUtil.sendVehicleInfoTopic(pileCode, gunCode)) {
                //移除需求
                PileDemand.deleteDemand(keyId);
                return ResponseResult.error("下发车辆信息请求失败");
            }
            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrPileTaskFunc(demandModel, 10);

            //移除需求
            PileDemand.deleteDemand(keyId);

            //成功返回状态
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                    case -1:
                        return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                    case 0:
                        return ResponseResult.ok(JSON.parseObject(asyncTaskResult.getString("recMsg"), VehicleInfoResVo.class));
                    case 1:
                        return ResponseResult.error(SMV2gConstant.StatusFailed);
                    case 255:
                        return ResponseResult.error(SMV2gConstant.StatusOther);
                }
            }
            return ResponseResult.paramError(ResponseResult.FAIL);
        } catch (Exception e) {
            // 记录操作失败的异常日志
            log.error("车辆信息请求处理异常", e);
            //移除需求
            PileDemand.deleteDemand(keyId);
            return ResponseResult.error("车辆信息请求处理异常");
        }
    }

    @Override
    public ResponseResult<List<PileLogResultDto>> queryPileLogList(PileLogQueryVo pileLogQueryVo) {
        // 数据校验
        if (pileLogQueryVo == null) {
            return ResponseResult.paramError("输入数据校验失败");
        }
        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                pileLogQueryVo.getPileCode(),
                pileLogQueryVo.getGunCode(),
                pileLogQueryVo.getLogType(),
                pileLogQueryVo.getStartTime(),
                pileLogQueryVo.getEndTime()
        };

        // 使用Apache Commons Lang3的StringUtils.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        if (Arrays.stream(fieldsToValidate).anyMatch(StringUtil::isEmpty)) {
            return ResponseResult.paramError("输入数据校验失败");
        }

        //创建查询流水号
        String logSerialNumber = PileControlUtil.createOrderNum(pileLogQueryVo.getPileCode(), pileLogQueryVo.getGunCode());

        //计算权重   命令码+桩编码
        String keyId = logSerialNumber + SMV2gConstant.PILE_LOGQUERY;
        try {
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }
            //创建查询请求
            PileDemandModel demandModel = PileDemand.createDemand(keyId, pileLogQueryVo.getPileCode(), pileLogQueryVo.getGunCode());
            //下发请求
            if (!MqttTopicUtil.sendPileLogQuery(pileLogQueryVo)) {
                //移除需求
                PileDemand.deleteDemand(keyId);
                return ResponseResult.error("电桩日志查询处理失败");
            }

            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrPileTaskFunc(demandModel, 10);

            //移除需求
            PileDemand.deleteDemand(keyId);

            // 根据异步任务执行结果
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                if (pileLogResultMap.containsKey(keyId)) {
                    pileLogResultMap.remove(keyId);
                    return ResponseResult.ok(pileLogResultMap.get(keyId));
                }
            }
            return ResponseResult.error("电桩日志查询处理失败");
        } catch (Exception e) {
            // 记录操作失败的异常日志
            log.error("电桩日志查询处理异常", e);
            //移除需求
            PileDemand.deleteDemand(keyId);
            pileLogResultMap.remove(keyId);
            return ResponseResult.error("电桩日志查询处理异常");
        }
    }

    @Override
    public ResponseResult<Void> pileReset(PileResetVo pileResetVo) {
        // 数据校验
        if (StringUtil.isEmpty(pileResetVo.getPileCode()) || StringUtil.isEmpty(pileResetVo.getFirmwareType())) {
            return ResponseResult.paramError("输入数据校验失败");
        }
        //计算权重   命令码+桩编码
        String keyId = pileResetVo.getPileCode() + pileResetVo.getFirmwareType() + SMV2gConstant.PILE_RESET;
        try {
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }
            //创建查询请求
            PileDemandModel demandModel = PileDemand.createDemand(keyId, pileResetVo.getPileCode(), null);
            //下发请求
            if (!MqttTopicUtil.sendPileReset(pileResetVo)) {
                //移除需求
                PileDemand.deleteDemand(keyId);
                return ResponseResult.error("电桩复位处理失败");
            }

            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrPileTaskFunc(demandModel, 3);

            //移除需求
            PileDemand.deleteDemand(keyId);

            // 根据异步任务执行结果，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult) && asyncTaskResult.containsKey("recCode")) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                // 返回操作结果
                switch (recCode) {
                    case 0:
                        return ResponseResult.ok();
                    case 1:
                        return ResponseResult.error("当前状态不允许复位");
                    case 255:
                        return ResponseResult.error("其他原因");
                }
            }
            return ResponseResult.error("电桩复位处理失败");
        } catch (Exception e) {
            // 记录操作失败的异常日志
            log.error("电桩复位处理异常", e);
            //移除需求
            PileDemand.deleteDemand(keyId);
            return ResponseResult.error("电桩复位处理异常");
        }
    }

    @Override
    public ResponseResult<Void> pileSetQr(PileSetQrVo pileSetQrVo) {
        // 数据校验
        if (StringUtil.isEmpty(pileSetQrVo.getPileCode()) || StringUtil.isEmpty(pileSetQrVo.getQrStr())) {
            return ResponseResult.paramError("输入数据校验失败");
        }
        //计算权重   命令码+桩编码
        String keyId = pileSetQrVo.getPileCode() + SMV2gConstant.PILE_SETQR;
        try {
            if (PileDemand.demandModelMap.containsKey(keyId)) {
                return ResponseResult.paramError("请勿重复操作");
            }
            //创建查询请求
            PileDemandModel demandModel = PileDemand.createDemand(keyId, pileSetQrVo.getPileCode(), null);
            //下发请求
            if (!MqttTopicUtil.sendPileSetQr(pileSetQrVo)) {
                //移除需求
                PileDemand.deleteDemand(keyId);
                return ResponseResult.error("设置二维码下发失败");
            }

            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrPileTaskFunc(demandModel, 3);

            //移除需求
            PileDemand.deleteDemand(keyId);

            // 根据异步任务执行结果，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult) && asyncTaskResult.containsKey("recCode")) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                // 返回操作结果
                if (recCode == 0) {
                    if (!deviceService.updateDeviceQrStr(pileSetQrVo).isSuccess()) {
                        log.error("更新枪的二维码地址失败,桩编号:{}", pileSetQrVo.getPileCode());
                    }
                    return ResponseResult.ok();
                }
                return ResponseResult.error("设置二维码处理失败");
            }
            return ResponseResult.error("设置二维码处理失败");
        } catch (Exception e) {
            // 记录操作失败的异常日志
            log.error("设置二维码处理异常", e);
            //移除需求
            PileDemand.deleteDemand(keyId);
            return ResponseResult.error("设置二维码处理异常");
        }
    }


}
