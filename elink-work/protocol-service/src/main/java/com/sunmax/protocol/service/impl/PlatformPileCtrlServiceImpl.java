package com.sunmax.protocol.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.SunMaxUtil;
import com.sunmax.common.vo.crontab.PileStartControlVo;
import com.sunmax.common.vo.protocol.PilePowerCtrlVo;
import com.sunmax.common.vo.protocol.PileStartVo;
import com.sunmax.common.vo.protocol.PileStopVo;
import com.sunmax.common.vo.protocol.mqtt.web.response.EventRateReqPublicVo;
import com.sunmax.protocol.constant.SMV2gConstant;
import com.sunmax.protocol.dto.platform.PlatformPileRateDto;
import com.sunmax.protocol.dto.platform.PlatformPileStartDto;
import com.sunmax.protocol.dto.platform.PlatformPileStopDto;
import com.sunmax.protocol.dto.platform.PlatformPowerCtrlDto;
import com.sunmax.protocol.service.PlatformPileCtrlService;
import com.sunmax.protocol.service.feign.CrontabService;
import com.sunmax.protocol.service.feign.TogetherService;
import com.sunmax.protocol.util.MqttTopicUtil;
import com.sunmax.protocol.util.PileControlUtil;
import com.sunmax.protocol.util.PileRecordUtil;
import com.sunmax.protocol.util.platform.PlatformControlDemand;
import com.sunmax.protocol.util.platform.PlatformDemandModel;
import com.sunmax.protocol.util.platform.PlatformUtil;
import com.sunmax.protocol.vo.PlatformRequestVo;
import com.sunmax.protocol.vo.platform.PlatformPileStartVo;
import com.sunmax.protocol.vo.platform.PlatformPileStopVo;
import com.sunmax.protocol.vo.platform.PlatformPowerCtrlVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlatformPileCtrlServiceImpl implements PlatformPileCtrlService {

    @Autowired
    private TogetherService togetherService;

    @Autowired
    private CrontabService crontabService;

    @Override
    public ResponseResult<String> getPileRateTemplate(PlatformRequestVo requestVo) {
        //解密充电桩计费请求数据
        String pileCode;
        try {
            JSONObject dataMap = PlatformUtil.decodeData(requestVo.getPlatformId(), requestVo.getData());
            log.info("获取充电桩计费模板入参:{}", dataMap);
            if (MapUtils.isEmpty(dataMap)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            pileCode = dataMap.getString("pileCode");
            if (StringUtil.isEmpty(pileCode)) {
                return ResponseResult.paramError("获取充电桩计费模板入参校验失败");
            }
        } catch (Exception e) {
            log.error("获取充电桩计费模板入参报错:", e);
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        //返回的对象
        PlatformPileRateDto result = new PlatformPileRateDto();
        //根据充电桩编号查询电桩计费数据
        EventRateReqPublicVo eventRateReqPublicVo = togetherService.findSiteRateInfoByPileCodes(Collections.singletonList(pileCode)).getData().get(pileCode);
        if (eventRateReqPublicVo != null) {
            result.setCRateId(eventRateReqPublicVo.getCRateId());
            result.setCTimeFrameNum(eventRateReqPublicVo.getCTimeFrameNum());
            if (CollectionUtils.isNotEmpty(eventRateReqPublicVo.getCTimeFrameRate())) {
                result.setCTimeFrameRate(eventRateReqPublicVo.getCTimeFrameRate().stream().map(c -> {
                    PlatformPileRateDto.TimeFrameRate timeFrameRate = new PlatformPileRateDto.TimeFrameRate();
                    timeFrameRate.setType(c.getType());
                    timeFrameRate.setStartTime(SunMaxUtil.timeStamp8Date(c.getStartTime()).substring(11, 16));
                    String endTime = SunMaxUtil.timeStamp8Date(c.getEndTime()).substring(11, 16);
                    if (Objects.equals(endTime, "00:00")) {
                        timeFrameRate.setEndTime("24:00");
                    } else {
                        timeFrameRate.setEndTime(endTime);
                    }
                    timeFrameRate.setPrice((double)c.getPrice() / 1000);
                    timeFrameRate.setServiceCharger((double)c.getServiceCharger() / 1000);
                    return timeFrameRate;
                }).collect(Collectors.toList()));
            }
            result.setDRateId(eventRateReqPublicVo.getDRateId());
            result.setDTimeFrameNum(eventRateReqPublicVo.getDTimeFrameNum());
            if (CollectionUtils.isNotEmpty(eventRateReqPublicVo.getDTimeFrameRate())) {
                result.setDTimeFrameRate(eventRateReqPublicVo.getDTimeFrameRate().stream().map(d -> {
                    PlatformPileRateDto.TimeFrameRate timeFrameRate = new PlatformPileRateDto.TimeFrameRate();
                    timeFrameRate.setType(d.getType());
                    timeFrameRate.setStartTime(SunMaxUtil.timeStamp8Date(d.getStartTime()).substring(11, 16));
                    String endTime = SunMaxUtil.timeStamp8Date(d.getEndTime()).substring(11, 16);
                    if (Objects.equals(endTime, "00:00")) {
                        timeFrameRate.setEndTime("24:00");
                    } else {
                        timeFrameRate.setEndTime(endTime);
                    }
                    timeFrameRate.setPrice((double) d.getPrice() / 1000);
                    timeFrameRate.setServiceCharger((double) d.getServiceCharger() / 1000);
                    return timeFrameRate;
                }).collect(Collectors.toList()));
            }
        }

        log.info("获取充电桩计费模板出参:{}", result);
        return ResponseResult.ok(PlatformUtil.encryptData(pileCode, JSON.toJSONString(result)));
    }

    @Override
    public ResponseResult<String> pileStart(PlatformRequestVo requestVo) {
        //解密启动充电桩请求数据
        PlatformPileStartVo platformPileStartVo;
        try {
            JSONObject dataMap = PlatformUtil.decodeData(requestVo.getPlatformId(), requestVo.getData());
            log.info("启动充电桩入参:{}", dataMap);
            if (MapUtils.isEmpty(dataMap)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            platformPileStartVo = JSONObject.toJavaObject(dataMap, PlatformPileStartVo.class);
            // 数据校验
            if (!isValidStart(platformPileStartVo)) {
                return ResponseResult.paramError("启动充电桩入参数据校验失败");
            }
        } catch (Exception e) {
            log.error("启动充电桩入参报错:", e);
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        String pileCode = platformPileStartVo.getPileCode();
        String gunCode = String.valueOf(platformPileStartVo.getGunCode());
        PileStartVo pileStartVo = new PileStartVo();
        pileStartVo.setSiteId(PileRecordUtil.getSiteId(pileCode));
        pileStartVo.setPileCode(pileCode);
        pileStartVo.setGunCode(gunCode);
        pileStartVo.setSerialNum(PileControlUtil.createOrderNum(pileCode, gunCode));
        pileStartVo.setStarter(PlatformUtil.starter);
        pileStartVo.setType(0);
        pileStartVo.setStrategy(platformPileStartVo.getStrategy());
        pileStartVo.setStrategyCfg(platformPileStartVo.getStrategyCfg());
        pileStartVo.setRunMode(platformPileStartVo.getDirection());
        pileStartVo.setAccountType(platformPileStartVo.getAccountType());
        pileStartVo.setAccountData(platformPileStartVo.getAccountData());

        // 初始化返回结果对象
        PileResultDto resultData = new PileResultDto();
        resultData.setPileCode(pileCode);
        resultData.setGunCode(pileStartVo.getGunCode());
        resultData.setType(pileStartVo.getRunMode());
        PileStartControlVo pileControlVo = new PileStartControlVo();
        try {
            // 计算权重，生成启动命令
            String keyId = pileCode + gunCode + SMV2gConstant.STARTCMD;

            // 判断是否重复启动，并校验时间间隔
            if (PlatformControlDemand.isDuplicateStart(keyId)) {
                return ResponseResult.paramError(SMV2gConstant.StatusWorking);
            }

            // 创建查询请求对象
            PlatformDemandModel demandModel = PlatformControlDemand.createDemand(keyId, pileStartVo.getPileCode(), pileStartVo.getGunCode());

            // 预缓存订单信息到缓存中
            PileControlUtil.cacheOrderInfo(pileStartVo, pileCode, gunCode);

            // 校验平台启动自动控制
            pileControlVo = PileControlUtil.checkPileControl(pileStartVo, crontabService);

            // 下发启动命令
            if (!MqttTopicUtil.sendPileStartTopic(pileStartVo)) {
                resultData.setResult(1);
                //下发失败移除需求
                PlatformControlDemand.deleteDemand(keyId);
            }

            // 开启查询线程等待返回结果
            JSONObject asyncTaskResult = PlatformControlDemand.ctrPileTaskFunc(demandModel, 3);

            // 移除需求模型
            PlatformControlDemand.deleteDemand(keyId);

            // 根据异步任务结果映射中的消息内容，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                resultData.setResult(recCode);
                pileControlVo.setResult(recCode);
                resultData.setFailDetailReason(asyncTaskResult.getString("recMsg"));
                if (recCode == 0) { //成功返回流水号
                    resultData.setSerialNum(asyncTaskResult.getString("serialNum")); // 设置订单编号为结果对象的序列号
                }
            }

            // 更新控制校验
            PileControlUtil.updatePileControl(pileControlVo, crontabService);

        } catch (Exception e) {
            log.error("启动充电桩失败", e);
            resultData.setResult(500); // 未知错误
            // 更新控制校验
            pileControlVo.setResult(500);
            PileControlUtil.updatePileControl(pileControlVo, crontabService);
        }


        // 初始化返回结果对象
        PlatformPileStartDto result = new PlatformPileStartDto();
        result.setPileCode(platformPileStartVo.getPileCode());
        result.setGunCode(platformPileStartVo.getGunCode());
        result.setFailReason(resultData.getResult());
        if (resultData.getResult() == -1) {
            result.setFailReason(checkRecCode(platformPileStartVo.getPileCode(), platformPileStartVo.getGunCode(),  1));
        }
        if (result.getFailReason() == 0) {
            //保存电桩运行缓存
            PlatformUtil.pileRunMap.put(platformPileStartVo.getPileCode(), platformPileStartVo.getDirection());
        }
        if (StringUtil.isNotEmpty(resultData.getFailDetailReason()) && StringUtil.isNumber(resultData.getFailDetailReason())) {
            result.setFailDetailReason(Integer.parseInt(resultData.getFailDetailReason()));
        }
        result.setSerialNum(resultData.getSerialNum());
        return ResponseResult.ok(PlatformUtil.encryptData(platformPileStartVo.getPileCode(), JSON.toJSONString(result)));
    }


    /**
     * 验证开始堆叠的合法性。
     *
     * @param pileStartVo 包含堆叠开始信息的对象。不可为null。
     * @return boolean 如果所有验证字段都不为空，则返回true；如果任何字段为空或只包含空格，则返回false。
     */
    private static boolean isValidStart(PlatformPileStartVo pileStartVo) {
        // 检查输入对象是否为null
        if (pileStartVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                pileStartVo.getPileCode(),
                pileStartVo.getGunCode(),
                pileStartVo.getStrategy(),
                pileStartVo.getStrategyCfg(),
                pileStartVo.getDirection(),
                pileStartVo.getAccountType(),
                pileStartVo.getAccountData()
        };

        // 使用Apache Commons Lang3的StringUtils.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }


    @Override
    public ResponseResult<String> pileStop(PlatformRequestVo requestVo) {
        //解密停止充电桩请求数据
        PlatformPileStopVo platformPileStopVo;
        try {
            JSONObject dataMap = PlatformUtil.decodeData(requestVo.getPlatformId(), requestVo.getData());
            log.info("停止充电桩入参:{}", dataMap);
            if (MapUtils.isEmpty(dataMap)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            platformPileStopVo = JSONObject.toJavaObject(dataMap, PlatformPileStopVo.class);
            // 数据校验
            if (!isValidStop(platformPileStopVo)) {
                return ResponseResult.paramError("停止充电桩入参校验失败");
            }
        } catch (Exception e) {
            log.error("停止充电桩入参报错:", e);
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        PileStopVo pileStopVo = new PileStopVo();
        pileStopVo.setPileCode(platformPileStopVo.getPileCode());
        pileStopVo.setGunCode(String.valueOf(platformPileStopVo.getGunCode()));
        pileStopVo.setSerialNum(platformPileStopVo.getSerialNum());
        pileStopVo.setType(platformPileStopVo.getType());

        // 初始化结果对象
        PileResultDto resultData = new PileResultDto();
        try {
            // 设置基础结果信息
            String pileCode = pileStopVo.getPileCode();
            String gunCode = pileStopVo.getGunCode();
            resultData.setPileCode(pileCode);
            resultData.setGunCode(gunCode);

            // 计算下发停止命令的权重
            String keyId = pileCode + gunCode + SMV2gConstant.STOPCMD;

            // 创建停止命令的查询请求模型
            PlatformDemandModel demandModel = PlatformControlDemand.createDemand(keyId, pileCode, gunCode);

            // 下发停止命令
            if (!MqttTopicUtil.sendPileStopTopic(pileStopVo)) {
                resultData.setResult(1);
                //下发失败移除需求
                PlatformControlDemand.deleteDemand(keyId);
            }

            // 开启异步任务，等待停止命令执行结果
            JSONObject asyncTaskResult = PlatformControlDemand.ctrPileTaskFunc(demandModel, 10);

            // 从系统中移除该需求模型
            PlatformControlDemand.deleteDemand(keyId);

            // 根据异步任务执行结果，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                resultData.setSerialNum(asyncTaskResult.getString("serialNum")); // 设置订单编号为结果对象的序列号
                resultData.setResult(recCode);
                resultData.setFailDetailReason(asyncTaskResult.getString("recMsg"));
            }
        } catch (Exception e) {
            // 记录停止操作失败的异常日志
            log.error("停止充电桩失败", e);
            // 设置未知错误的失败原因
            resultData.setResult(500);
        }


        // 初始化结果对象
        PlatformPileStopDto result = new PlatformPileStopDto();
        // 设置基础结果信息
        String pileCode = platformPileStopVo.getPileCode();
        Integer gunCode = platformPileStopVo.getGunCode();
        result.setPileCode(pileCode);
        result.setGunCode(gunCode);
        result.setFailReason(resultData.getResult());
        if (resultData.getResult() == -1) {
            result.setFailReason(checkRecCode(platformPileStopVo.getPileCode(), platformPileStopVo.getGunCode(),  2));
        }
        if (StringUtil.isNotEmpty(resultData.getFailDetailReason()) && StringUtil.isNumber(resultData.getFailDetailReason())) {
            result.setFailDetailReason(Integer.parseInt(resultData.getFailDetailReason()));
        }
        result.setSerialNum(resultData.getSerialNum());
        return ResponseResult.ok(PlatformUtil.encryptData(platformPileStopVo.getPileCode(), JSON.toJSONString(result)));
    }

    private static boolean isValidStop(PlatformPileStopVo pileStopVo) {
        // 检查输入对象是否为null
        if (pileStopVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                pileStopVo.getPileCode(),
                pileStopVo.getGunCode(),
                pileStopVo.getSerialNum(),
                pileStopVo.getType()
        };

        // 使用Apache Commons Lang3的StringUtils.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }

    @Override
    public ResponseResult<String> powerCtrl(PlatformRequestVo requestVo) {
        //解密功率控制请求数据
        PlatformPowerCtrlVo platformPowerCtrlVo;
        try {
            JSONObject dataMap = PlatformUtil.decodeData(requestVo.getPlatformId(), requestVo.getData());
            log.info("功率控制入参:{}", dataMap);
            if (MapUtils.isEmpty(dataMap)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            platformPowerCtrlVo = JSONObject.toJavaObject(dataMap, PlatformPowerCtrlVo.class);
            // 数据校验
            if (!isValidCtrl(platformPowerCtrlVo)) {
                return ResponseResult.paramError("功率控制入参数据校验失败");
            }
        } catch (Exception e) {
            log.error("功率控制入参报错:", e);
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        //下发电桩功率控制入参
        PilePowerCtrlVo pilePowerCtrlVo = new PilePowerCtrlVo();
        pilePowerCtrlVo.setPileCode(platformPowerCtrlVo.getPileCode());
        pilePowerCtrlVo.setGunCode(String.valueOf(platformPowerCtrlVo.getGunCode()));
        pilePowerCtrlVo.setRunMode(platformPowerCtrlVo.getRunMode());
        pilePowerCtrlVo.setCtrlType(platformPowerCtrlVo.getCtrlType());
        pilePowerCtrlVo.setOutPower(platformPowerCtrlVo.getOutPower());

        // 初始化结果对象
        PlatformPowerCtrlDto result = new PlatformPowerCtrlDto();
        try {
            // 设置基础结果信息
            String pileCode = pilePowerCtrlVo.getPileCode();
            String gunCode = pilePowerCtrlVo.getGunCode();
            result.setPileCode(pileCode);
            result.setGunCode(platformPowerCtrlVo.getGunCode());

            // 计算下发停止命令的权重
            String keyId = pileCode + gunCode + SMV2gConstant.POWERCTRL;

            // 创建停止命令的查询请求模型
            PlatformDemandModel demandModel = PlatformControlDemand.createDemand(keyId, pileCode, gunCode);

            // 下发停止命令
            if (!MqttTopicUtil.sendPowerCtrlTopic(pilePowerCtrlVo)) {
                result.setFailReason(1);
                //下发失败移除需求
                PlatformControlDemand.deleteDemand(keyId);
            }

            // 开启异步任务，等待停止命令执行结果
            JSONObject asyncTaskResult = PlatformControlDemand.ctrPileTaskFunc(demandModel, 10);

            // 从系统中移除该需求模型
            PlatformControlDemand.deleteDemand(keyId);

            // 根据异步任务执行结果，设置失败原因
            if (MapUtils.isNotEmpty(asyncTaskResult)) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                result.setFailReason(recCode);
            }
        } catch (Exception e) {
            // 记录停止操作失败的异常日志
            log.error("充电桩功率控制处理异常", e);
            // 设置未知错误的失败原因
            result.setFailReason(500);
        }
        return ResponseResult.ok(PlatformUtil.encryptData(platformPowerCtrlVo.getPileCode(), JSON.toJSONString(result)));
    }

    private static boolean isValidCtrl(PlatformPowerCtrlVo powerCtrlVo) {
        // 检查输入对象是否为null
        if (powerCtrlVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                powerCtrlVo.getPileCode(),
                powerCtrlVo.getGunCode(),
                powerCtrlVo.getRunMode(),
                powerCtrlVo.getCtrlType(),
                powerCtrlVo.getOutPower()
        };

        // 使用Apache Commons Lang3的StringUtils.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }

    private static Integer checkRecCode(String pileCode, Integer gunCode, Integer type) {
        PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
        if (pileRealModel != null && pileRealModel.getGunRealModelMap().containsKey(String.valueOf(gunCode))) {
            PileRealModel.GunRealModel gunRealModel = pileRealModel.getGunRealModelMap().get(String.valueOf(gunCode));
            List<Integer> gunWorkStatus = Arrays.asList(1, 2, 4, 5);
            if (type == 1) { //启动
                if (gunWorkStatus.contains(gunRealModel.getGunStatus())) {
                    return 0;
                }
            } else if (type == 2) { //停止
                if (!gunWorkStatus.contains(gunRealModel.getGunStatus())) {
                    return 0;
                }
            }
        }
        return 1;
    }

}
