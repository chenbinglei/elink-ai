package com.sunmax.protocol.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.protocol.dto.virtual.VirtualPileQtDto;
import com.sunmax.protocol.dto.virtual.VirtualPileRealDto;
import com.sunmax.protocol.mapper.OrderRecordMapper;
import com.sunmax.protocol.model.OrderSumModel;
import com.sunmax.protocol.runner.ProtocolRunner;
import com.sunmax.protocol.service.VirtualPileCtrlService;
import com.sunmax.protocol.util.platform.PlatformUtil;
import com.sunmax.protocol.vo.PlatformRequestVo;
import com.sunmax.protocol.vo.virtual.VirtualPileQtVo;
import com.sunmax.protocol.vo.virtual.VirtualPileRealVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VirtualPileCtrlServiceImpl implements VirtualPileCtrlService {

    @Autowired
    private OrderRecordMapper orderRecordMapper;

    @Override
    public ResponseResult<String> getPileRealList(PlatformRequestVo requestVo) {
        VirtualPileRealVo pileRealVo;
        try {
            //解密入参数据
            JSONObject dataMap = PlatformUtil.decodeData(requestVo.getPlatformId(), requestVo.getData());
    //        log.info("提供第三方虚拟电厂平台调用接口, 获取电桩实时数据入参:{}", dataMap);

            if (MapUtils.isEmpty(dataMap)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            pileRealVo = dataMap.toJavaObject(VirtualPileRealVo.class);
            if (pileRealVo == null || StringUtil.isEmpty(pileRealVo.getPileCodes())) {
                return ResponseResult.paramError("获取电桩实时数据入参数据校验失败");
            }
        } catch (Exception e) {
            log.error("提供第三方虚拟电厂平台调用接口, 获取电桩实时数据入参报错:", e);
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        try {
            //返回的集合
            List<VirtualPileRealDto> resultList = Lists.newArrayList();
            //根据多个电桩编号查询电桩实时数据
            for (String pileCode : pileRealVo.getPileCodes().split(FileUtil.COMMA)) {
                //充电桩数据
                VirtualPileRealDto result = new VirtualPileRealDto();
                result.setPileCode(pileCode);
                //获取电桩缓存中的数据
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getPileCode())) {
                    BeanUtils.copyProperties(pileRealModel, result);
                    //获取充电枪数据
                    result.setGunRealDataList(pileRealModel.getGunRealModelMap().values().stream().map(gunRealModel -> {
                        //充电枪数据
                        VirtualPileRealDto.GunRealData gunRealData = new VirtualPileRealDto.GunRealData();
                        BeanUtils.copyProperties(gunRealModel, gunRealData);
                        gunRealData.setGunCode(Integer.parseInt(gunRealModel.getGunCode()));
                        return gunRealData;
                    }).collect(Collectors.toList()));
                }
                resultList.add(result);
            }
            return ResponseResult.ok(PlatformUtil.encryptData(ProtocolRunner.platformDataMap.get(requestVo.getPlatformId()), JSON.toJSONString(resultList)));
        } catch (Exception e) {
            log.error("提供第三方虚拟电厂平台调用接口, 获取电桩实时数据报错:", e);
            return ResponseResult.error("获取电桩实时数据平台处理报错,请联系开发人员！！");
        }
    }

    @Override
    public ResponseResult<String> getPileQtList(PlatformRequestVo requestVo) {

        VirtualPileQtVo pileQtVo;
        List<String> pileCodes;
        try {
            //解密入参数据
            JSONObject dataMap = PlatformUtil.decodeData(requestVo.getPlatformId(), requestVo.getData());
    //        log.info("提供第三方虚拟电厂平台调用接口, 获取电桩电量数据入参:{}", dataMap);

            if (MapUtils.isEmpty(dataMap)) {
                return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
            }
            pileQtVo = dataMap.toJavaObject(VirtualPileQtVo.class);
            // 数据校验
            if (!isValidPileQt(pileQtVo)) {
                return ResponseResult.paramError("输入数据校验失败");
            }

            //多个电桩编号
            pileCodes = Arrays.stream(pileQtVo.getPileCodes().split(FileUtil.COMMA)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(pileCodes)) {
                return ResponseResult.paramError("获取电桩电量数据入参数据校验失败");
            }
        } catch (Exception e) {
            log.error("提供第三方虚拟电厂平台调用接口, 获取电桩电量数据入参报错:", e);
            return ResponseResult.paramError(ResponseResult.PARAM_ERROR);
        }

        try {
            //根据多个电桩编号和开始时间统计电桩充电量数据
            Map<String, List<OrderSumModel>> chargeQtMap = Maps.newHashMap();
            List<OrderSumModel> chargeQtList = orderRecordMapper.countPileOrderSumData(pileCodes, 0, pileQtVo.getStartTime(), pileQtVo.getEndTime());
            if (CollectionUtils.isNotEmpty(chargeQtList)) {
                chargeQtMap = chargeQtList.stream().collect(Collectors.groupingBy(OrderSumModel::getPileCode));
            }
            //根据多个电桩编号和开始时间统计电桩放电量数据
            Map<String, List<OrderSumModel>> disChargeQtMap = Maps.newHashMap();
            List<OrderSumModel> dischargeQtList = orderRecordMapper.countPileOrderSumData(pileCodes, 1, pileQtVo.getStartTime(), pileQtVo.getEndTime());
            if (CollectionUtils.isNotEmpty(dischargeQtList)) {
                disChargeQtMap = dischargeQtList.stream().collect(Collectors.groupingBy(OrderSumModel::getPileCode));
            }

            //对数据进行组装
            Map<String, List<OrderSumModel>> finalChargeQtMap = chargeQtMap;
            Map<String, List<OrderSumModel>> finalDisChargeQtMap = disChargeQtMap;
            //返回的集合
            List<VirtualPileQtDto> resultList = pileCodes.stream().map(pileCode -> {
                //返回的对象
                VirtualPileQtDto result = new VirtualPileQtDto();
                result.setPileCode(pileCode);

                //获取充电枪编号
                Set<Integer> gunCodes = Sets.newHashSet();
                //定义充电枪充电量,放电量map
                Map<Integer, OrderSumModel> gunChargeQtMap = Maps.newHashMap();
                Map<Integer, OrderSumModel> gunDisChargeQtMap = Maps.newHashMap();
                PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
                if (pileRealModel != null && MapUtils.isNotEmpty(pileRealModel.getGunRealModelMap())) {
                    gunCodes.addAll(pileRealModel.getGunRealModelMap().keySet().stream().filter(StringUtil::isNotEmpty)
                            .map(Integer::parseInt).collect(Collectors.toSet()));
                }
                //充电桩充电量数据
                if (finalChargeQtMap.containsKey(pileCode)) {
                    List<OrderSumModel> chargeSumList = finalChargeQtMap.get(pileCode);
                    result.setRecChargeQt(DoubleUtil.getToDouble(chargeSumList.stream().mapToDouble(OrderSumModel::getTotalQt).sum())); //充电桩充电量
                    result.setRecChargeMoney(BigDecimal.valueOf(DoubleUtil.getToDouble(chargeSumList.stream().mapToDouble(OrderSumModel::getTotalCost).sum()))); //充电桩充电金额
                    gunChargeQtMap = chargeSumList.stream().filter(o -> StringUtil.isNotEmpty(o.getGunCode())).collect(Collectors
                            .toMap(OrderSumModel::getGunCode, a -> a, (k1, k2) -> k1));
                    if (MapUtils.isNotEmpty(gunChargeQtMap)) {
                        gunCodes.addAll(gunChargeQtMap.keySet());
                    }
                }
                //充电桩放电量数据
                if (finalDisChargeQtMap.containsKey(pileCode)) {
                    List<OrderSumModel> disChargeSumList = finalDisChargeQtMap.get(pileCode);
                    result.setDisChargeQt(DoubleUtil.getToDouble(disChargeSumList.stream().mapToDouble(OrderSumModel::getTotalQt).sum())); //充电桩放电量
                    result.setDisChargeMoney(BigDecimal.valueOf(DoubleUtil.getToDouble(disChargeSumList.stream().mapToDouble(OrderSumModel::getTotalCost).sum()))); //充电桩放电量金额
                    gunDisChargeQtMap = disChargeSumList.stream().filter(o -> StringUtil.isNotEmpty(o.getGunCode())).collect(Collectors
                            .toMap(OrderSumModel::getGunCode, a -> a, (k1, k2) -> k1));
                    if (MapUtils.isNotEmpty(gunDisChargeQtMap)) {
                        gunCodes.addAll(gunDisChargeQtMap.keySet());
                    }
                }
                //获取充电枪数据
                Map<Integer, OrderSumModel> finalGunChargeQtMap = gunChargeQtMap;
                Map<Integer, OrderSumModel> finalGunDisChargeQtMap = gunDisChargeQtMap;
                result.setGunQtDataList(gunCodes.stream().map(gunCode -> {
                    VirtualPileQtDto.GunQtData gunQtData = new VirtualPileQtDto.GunQtData();
                    gunQtData.setGunCode(gunCode);
                    if (finalGunChargeQtMap.containsKey(gunCode)) {
                        OrderSumModel orderSumModel = finalGunChargeQtMap.get(gunCode);
                        gunQtData.setRecChargeQt(DoubleUtil.getToDouble(orderSumModel.getTotalQt())); //枪充电量
                        gunQtData.setRecChargeMoney(BigDecimal.valueOf(DoubleUtil.getToDouble(orderSumModel.getTotalCost()))); //枪充电金额
                    }
                    if (finalGunDisChargeQtMap.containsKey(gunCode)) {
                        OrderSumModel orderSumModel = finalGunDisChargeQtMap.get(gunCode);
                        gunQtData.setDisChargeQt(DoubleUtil.getToDouble(orderSumModel.getTotalQt())); //枪放电量
                        gunQtData.setDisChargeMoney(BigDecimal.valueOf(DoubleUtil.getToDouble(orderSumModel.getTotalCost()))); //枪放电量金额
                    }
                    return gunQtData;
                }).collect(Collectors.toList()));
                return result;
            }).collect(Collectors.toList());
            return ResponseResult.ok(PlatformUtil.encryptData(ProtocolRunner.platformDataMap.get(requestVo.getPlatformId()), JSON.toJSONString(resultList)));
        } catch (Exception e) {
            log.error("提供第三方虚拟电厂平台调用接口, 获取电桩电量数据报错:", e);
            return ResponseResult.error("获取电桩电量数据平台处理报错,请联系开发人员！！");
        }
    }

    private boolean isValidPileQt(VirtualPileQtVo pileQtVo) {
        // 检查输入对象是否为null
        if (pileQtVo == null) {
            // 可以考虑添加日志记录这里的失败原因
            return false;
        }

        // 定义需要校验的字段数组
        Object[] fieldsToValidate = {
                pileQtVo.getPileCodes(),
                pileQtVo.getStartTime(),
                pileQtVo.getEndTime()
        };

        // 使用Apache Commons Lang3的StringUtils.isEmpty()进行联合检查
        // 它同时处理了空和只含空格的字符串
        return Arrays.stream(fieldsToValidate).noneMatch(StringUtil::isEmpty);
    }

}
