package com.sunmax.protocol.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.protocol.PileResultDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.protocol.*;
import com.sunmax.protocol.constant.SMV2gConstant;
import com.sunmax.protocol.dao.ControlRecordDao;
import com.sunmax.protocol.entity.ControlRecordEntity;
import com.sunmax.protocol.service.PileBatchCtrlService;
import com.sunmax.protocol.service.PileCtrlService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PileBatchCtrlServiceImpl implements PileBatchCtrlService {

    @Autowired
    private ControlRecordDao controlRecordDao;

    @Autowired
    private PileCtrlService pileCtrlService;

    @Override
    public ResponseResult<List<PileResultDto>> batchPileStart(List<PileStartVo> pileStartVos) {
        //返回的执行结果
        List<PileResultDto> resultList = Lists.newArrayList();

        if (pileStartVos != null && !pileStartVos.isEmpty()) {
            //1.创建启动控制记录
            Map<String, ControlRecordEntity> controlRecordMap = this.createForceControlStartRecord(pileStartVos);
            try {
                //2.创建线程池
                final ExecutorService executor = Executors.newFixedThreadPool(pileStartVos.size());
                //3.执行充电桩命令，提交到任务里面
                pileStartVos.forEach(pileStartVo -> executor.submit(() -> {
                    try {
                        String keyId = pileStartVo.getPileCode() + pileStartVo.getGunCode() + SMV2gConstant.STARTCMD;
                        //4.执行充电桩命令
                        PileResultDto pileResult = pileCtrlService.pileStart(pileStartVo).getData();
                        //5.存储执行结果
                        resultList.add(pileResult);
                        //6.更新控制记录
                        if (controlRecordMap.containsKey(keyId)) {
                            ControlRecordEntity controlRecord = controlRecordMap.get(keyId);
                            controlRecord.setExecuteStatus(pileResult.getResult());
                            controlRecord.setUpdateTime(LocalDateTime.now());
                            controlRecordMap.put(keyId, controlRecord);
                        }
                    } catch (Exception e) {
                        log.error("执行启动充电桩报错", e);
                    }
                }));
                // 关闭线程池
                executor.shutdown();
                do {
                    Thread.sleep(500);
                } while (!executor.isTerminated());
            } catch (Exception e) {
                log.error("执行启动充电桩任务报错", e);
            }
            //更新控制记录状态
            controlRecordDao.saveAll(controlRecordMap.values().stream().filter(c -> Objects.equals(c.getIsStore(), true)).collect(Collectors.toList()));

        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<PileResultDto>> batchPileStop(List<PileStopVo> pileStopVos) {
        //返回的执行结果
        List<PileResultDto> resultList = Lists.newArrayList();

        if (pileStopVos != null && !pileStopVos.isEmpty()) {
            //1.创建停止记录
            Map<String, ControlRecordEntity> controlRecordMap = this.createForceControlStopRecord(pileStopVos);
            try {
                //2.创建线程池
                final ExecutorService executor = Executors.newFixedThreadPool(pileStopVos.size());
                //3.执行充电桩命令，提交到任务里面
                pileStopVos.forEach(pileStopVo -> executor.submit(() -> {
                    try {
                        String keyId = pileStopVo.getPileCode() + pileStopVo.getGunCode() + SMV2gConstant.STOPCMD;
                        //4.执行充电桩命令
                        PileResultDto pileResult = pileCtrlService.pileStop(pileStopVo).getData();
                        //5.存储执行结果
                        resultList.add(pileResult);
                        //6.更新控制记录
                        if (controlRecordMap.containsKey(keyId)) {
                            ControlRecordEntity controlRecord = controlRecordMap.get(keyId);
                            controlRecord.setExecuteStatus(pileResult.getResult());
                            controlRecord.setUpdateTime(LocalDateTime.now());
                            controlRecordMap.put(keyId, controlRecord);
                        }
                    } catch (Exception e) {
                        log.error("执行停止充电桩报错", e);
                    }
                }));
                // 关闭线程池
                executor.shutdown();
                do {
                    Thread.sleep(500);
                } while (!executor.isTerminated());
            } catch (Exception e) {
                log.error("批量执行停止充电桩任务报错", e);
            }
            //更新控制记录状态
            controlRecordDao.saveAll(controlRecordMap.values().stream().filter(c -> Objects.equals(c.getIsStore(), true)).collect(Collectors.toList()));
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<PileResultDto>> batchPilePowerCtrl(List<PilePowerCtrlVo> pilePowerCtrlVos) {
        //返回的执行结果
        List<PileResultDto> resultList = Lists.newArrayList();

        if (pilePowerCtrlVos != null && !pilePowerCtrlVos.isEmpty()) {
            //1.创建停止记录
            Map<String, ControlRecordEntity> controlRecordMap = this.createForcePowerControlRecord(pilePowerCtrlVos);
            try {
                //2.创建线程池
                final ExecutorService executor = Executors.newFixedThreadPool(pilePowerCtrlVos.size());
                //3.执行充电桩命令，提交到任务里面
                pilePowerCtrlVos.forEach(pilePowerCtrlVo -> executor.submit(() -> {
                    try {
                        String keyId = pilePowerCtrlVo.getPileCode() + pilePowerCtrlVo.getGunCode() + SMV2gConstant.POWERCTRL;
                        //4.执行充电桩命令
                        PileResultDto pileResult = pileCtrlService.pilePowerCtrl(pilePowerCtrlVo).getData();
                        //5.存储执行结果
                        resultList.add(pileResult);
                        //6.更新控制记录
                        if (controlRecordMap.containsKey(keyId)) {
                            ControlRecordEntity controlRecord = controlRecordMap.get(keyId);
                            controlRecord.setExecuteStatus(pileResult.getResult());
                            controlRecord.setUpdateTime(LocalDateTime.now());
                            controlRecordMap.put(keyId, controlRecord);
                        }
                    } catch (Exception e) {
                        log.error("执行停止充电桩报错", e);
                    }
                }));
                // 关闭线程池
                executor.shutdown();
                do {
                    Thread.sleep(500);
                } while (!executor.isTerminated());
            } catch (Exception e) {
                log.error("批量执行停止充电桩任务报错", e);
            }
            //更新控制记录状态
            controlRecordDao.saveAll(controlRecordMap.values());
        }
        return ResponseResult.ok(resultList);
    }


    @Override
    public ResponseResult<List<PileResultDto>> batchPileRateSet(List<PileRateSetVo> pileRateSetVos) {
        //返回的集合
        List<PileResultDto> resultList = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(pileRateSetVos)) {
            //创建线程池
            final ExecutorService executor = Executors.newFixedThreadPool(pileRateSetVos.size());
            pileRateSetVos.forEach(pileRateSetVo -> {
                try {
                    //3.执行充电桩命令，提交到任务里面
                    String templateId = StringUtil.getToString(String.valueOf(pileRateSetVo.getTemplateId()), 16);
                    pileRateSetVo.setTemplateId(templateId);
                    //计费下发
                    executor.submit(() -> resultList.add(pileCtrlService.pileRateSet(pileRateSetVo).getData()));
                } catch (Exception e) {
                    log.error("电桩费率下发报错", e);
                }
            });
            try {
                //关闭线程池
                executor.shutdown();
                do {
                    Thread.sleep(500);
                } while (!executor.isTerminated());
            } catch (Exception e) {
                log.error("线程池关闭失败", e);
            }
        }
        return ResponseResult.ok(resultList);
    }

    @Override
    public ResponseResult<List<PileResultDto>> batchPileUpdate(PileBatchUpdateVo pileBatchUpdateVo) {
        // 初始化结果集合
        List<PileResultDto> resultList = Lists.newArrayList();

        //前置电桩编号
        Set<String> pileCodes = Sets.newHashSet();
        //网关下的电桩编号
        Map<String, Set<String>> terminalMap = Maps.newHashMap();
        for (String pileCode : pileBatchUpdateVo.getPileCodes()) {
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
                if (pileRealModel.getMessageType() == 2) {
                    pileCodes.add(pileRealModel.getPileCode());
                }
            }
        }
        final ExecutorService executorService = Executors.newFixedThreadPool(pileCodes.size() + terminalMap.size());
        //前置电桩升级
        if (CollectionUtils.isNotEmpty(pileCodes)) {
            pileCodes.forEach(pileCode -> {
                PileUpdateVo pileUpdateVo = new PileUpdateVo();
                BeanUtils.copyProperties(pileBatchUpdateVo, pileUpdateVo);
                pileUpdateVo.setPileCode(pileCode);
                executorService.submit(() -> resultList.add(pileCtrlService.pileUpdate(pileUpdateVo).getData()));
            });
        }
        //网关下的电桩升级
        if (MapUtils.isNotEmpty(terminalMap)) {
            PileUpdateVo pileUpdateVo = new PileUpdateVo();
            BeanUtils.copyProperties(pileBatchUpdateVo, pileUpdateVo);
            terminalMap.forEach((terminalCode, pileCodeSet) -> executorService.submit(() -> resultList.addAll(pileCtrlService.pileUpdateByIeg(terminalCode, pileCodeSet, pileUpdateVo).getData())));
        }
        // 关闭线程池，这里使用shutdown()方法而不是shutdownNow()，因为我们希望等待所有任务完成
        executorService.shutdown();
        try {
            // 等待所有任务完成或者超时（这里设置为等待很长时间，实际上是为了演示目的）
            if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
                // 如果超时，则尝试停止所有正在执行的任务
                executorService.shutdownNow();
                if (!executorService.awaitTermination(300, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ex) {
            // 如果当前线程在等待过程中被中断，则尝试停止所有正在执行的任务
            executorService.shutdownNow();
            Thread.currentThread().interrupt(); // 重新设置中断状态
        }
        return ResponseResult.ok(resultList);
    }

    /**
     * 创建电桩启动控制记录数据
     *
     * @param pileStartVos 电桩启动参数
     */
    private Map<String, ControlRecordEntity> createForceControlStartRecord(List<PileStartVo> pileStartVos) {
        //创建控制记录
        List<ControlRecordEntity> controlRecordList = pileStartVos.stream().map(pileStartVo -> {
            ControlRecordEntity controlRecord = new ControlRecordEntity();

            controlRecord.setDeviceCode(pileStartVo.getPileCode());
            //充电
            if (pileStartVo.getRunMode() == 0) {
                controlRecord.setOperateContent(pileStartVo.getGunCode() + "枪启动充电");
            }
            //放电
            if (pileStartVo.getRunMode() == 1) {
                controlRecord.setOperateContent(pileStartVo.getGunCode() + "枪启动放电");
            }
            //获取控制参数
            controlRecord.setControlParam(this.getControlParam(pileStartVo.getRunMode(), pileStartVo.getType(), pileStartVo.getStrategy(), pileStartVo.getStrategyCfg()));

            //获取终端编号
            controlRecord.setTerminalCode(Objects.requireNonNull(RedisGeneralUtil.getPileRealModel(pileStartVo.getPileCode())).getTerminalCode());
            controlRecord.setControlType(1);

            //交互keyId
            controlRecord.setKeyId(pileStartVo.getPileCode() + pileStartVo.getGunCode() + SMV2gConstant.STARTCMD);

            //是否存储
            controlRecord.setIsStore(pileStartVo.getIsStore());
            return controlRecord;
        }).collect(Collectors.toList());
        //若插入条数大于0 则认为是平台控制启动 否则认为是定时任务启动
        long insertTotal = controlRecordList.stream().filter(c -> Objects.equals(c.getIsStore(), true)).count();
        if (insertTotal > 0) {
            return controlRecordDao.saveAll(controlRecordList).stream().collect(Collectors.toMap(ControlRecordEntity::getKeyId, a -> a, (k1, k2) -> k1));
        } else {
            //批量插入控制记录数据 并返回数据
            return controlRecordList.stream().collect(Collectors.toMap(ControlRecordEntity::getKeyId, a -> a, (k1, k2) -> k1));
        }
    }

    /**
     * 创建电桩停止控制记录数据
     *
     * @param pileStopVos 电桩停止参数
     */
    private Map<String, ControlRecordEntity> createForceControlStopRecord(List<PileStopVo> pileStopVos) {
        List<ControlRecordEntity> controlRecordList = pileStopVos.stream().map(pileStopVo -> {
            ControlRecordEntity controlRecord = new ControlRecordEntity();

            controlRecord.setDeviceCode(pileStopVo.getPileCode());
            //充电
            if (pileStopVo.getType() == 1) {
                controlRecord.setOperateContent(pileStopVo.getGunCode() + "枪停止服务");
            }
            //放电
            if (pileStopVo.getType() == 2) {
                controlRecord.setOperateContent(pileStopVo.getGunCode() + "枪取消预约");
            }
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileStopVo.getPileCode());
            if (pileRealModel != null) {
                //获取终端编号
                controlRecord.setTerminalCode(pileRealModel.getTerminalCode());
                Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
                if (gunRealModelMap.containsKey(pileStopVo.getGunCode())) {
                    PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(pileStopVo.getGunCode());
                    //获取控制参数
                    controlRecord.setControlParam(this.getControlParam(gunRealModel.getRunMode(), gunRealModel.getType(), gunRealModel.getStrategy(), gunRealModel.getStrategyCfg()));

                }
            }

            controlRecord.setControlType(1);

            //交互keyId
            controlRecord.setKeyId(pileStopVo.getPileCode() + pileStopVo.getGunCode() + SMV2gConstant.STOPCMD);

            //是否存储
            controlRecord.setIsStore(pileStopVo.getIsStore());

            return controlRecord;
        }).collect(Collectors.toList());
        //批量插入控制记录数据 并返回数据
        //若插入条数大于0 则认为是平台控制启动 否则认为是定时任务启动
        long insertTotal = controlRecordList.stream().filter(c -> Objects.equals(c.getIsStore(), true)).count();
        if (insertTotal > 0) {
            return controlRecordDao.saveAll(controlRecordList).stream().collect(Collectors.toMap(ControlRecordEntity::getKeyId, a -> a, (k1, k2) -> k1));
        } else {
            //批量插入控制记录数据 并返回数据
            return controlRecordList.stream().collect(Collectors.toMap(ControlRecordEntity::getKeyId, a -> a, (k1, k2) -> k1));
        }
    }

    /**
     * 创建功率控制记录数据
     *
     * @param pilePowerCtrlVos 电桩功率调节参数
     */
    private Map<String, ControlRecordEntity> createForcePowerControlRecord(List<PilePowerCtrlVo> pilePowerCtrlVos) {
        List<ControlRecordEntity> controlRecordList = pilePowerCtrlVos.stream().map(pilePowerCtrlVo -> {
            ControlRecordEntity controlRecord = new ControlRecordEntity();
            controlRecord.setDeviceCode(pilePowerCtrlVo.getPileCode());
            //组装操作内容,控制参数
            if (pilePowerCtrlVo.getRunMode() == 0) {
                controlRecord.setOperateContent(pilePowerCtrlVo.getGunCode() + "枪充电功率控制");
                controlRecord.setControlParam("{'充电功率':" + pilePowerCtrlVo.getOutPower() + "}");
            }
            if (pilePowerCtrlVo.getRunMode() == 1) {
                controlRecord.setOperateContent(pilePowerCtrlVo.getGunCode() + "枪放电功率控制");
                controlRecord.setControlParam("{'放电功率':" + pilePowerCtrlVo.getOutPower() + "}");
            }
            //获取终端编号
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pilePowerCtrlVo.getPileCode());
            if (pileRealModel != null) {
                //获取终端编号
                controlRecord.setTerminalCode(pileRealModel.getTerminalCode());
            }
            controlRecord.setControlType(1);
            //交互keyId
            controlRecord.setKeyId(pilePowerCtrlVo.getPileCode() + pilePowerCtrlVo.getGunCode() + SMV2gConstant.POWERCTRL);
            return controlRecord;
        }).collect(Collectors.toList());
        //批量插入控制记录数据 并返回数据
        return controlRecordDao.saveAll(controlRecordList).stream().collect(Collectors.toMap(ControlRecordEntity::getKeyId, a -> a, (k1, k2) -> k1));
    }

    //获取充电桩启动和停止控制记录的控制参数
    private String getControlParam(Integer direction, Integer startType, Integer strategy, Double strategyCfg) {
        //获取控制参数
        StringBuilder controlParam = new StringBuilder();
        controlParam.append("'{启动方式':");
        switch (startType) {
            case 0:
                controlParam.append("'立即启动',");
                break;
            case 1:
                controlParam.append("'定时启动',");
                break;
            case 2:
                controlParam.append("'自动启动',");
                break;
            default:
                controlParam.append("'',");
                break;
        }
        controlParam.append("'策略':");
        switch (strategy) {
            case 0:
                controlParam.append(direction == 0 ? "'自动充满'," : "'自动放空',");
                break;
            case 1:
                controlParam.append("'定soc',");
                break;
            case 2:
                controlParam.append("'定金额',");
                break;
            case 3:
                controlParam.append("'定电量',");
                break;
            default:
                controlParam.append("'',");
                break;
        }
        controlParam.append("'策略参数':");
        if (StringUtil.isNotEmpty(strategyCfg)) {
            controlParam.append(strategyCfg).append("}");
        } else {
            controlParam.append("''}");
        }
        return controlParam.toString();
    }

}
