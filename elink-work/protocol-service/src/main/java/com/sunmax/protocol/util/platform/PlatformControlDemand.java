package com.sunmax.protocol.util.platform;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 皖能控制需求方法
 */
@Slf4j
public class PlatformControlDemand {

    //缓存充电桩需求的映射
    public static Map<String, PlatformDemandModel> demandModelMap = Maps.newConcurrentMap();

    /**
     * 创建需求
     */
    public static PlatformDemandModel createDemand(String keyId, String pileCode, String gunCode) {
        PlatformDemandModel demandModel = new PlatformDemandModel();
        demandModel.setPileCode(pileCode);
        demandModel.setGunCode(gunCode);
        demandModel.setRecFlag(false);
        demandModel.setTimeOut(false);
        demandModel.setCreateTime(LocalDateTime.now());
        addDemand(keyId, demandModel);
        return demandModel;
    }

    /**
     * 添加需求
     *
     * @param demandModel 请求参数需求
     */
    public static void addDemand(String keyId, PlatformDemandModel demandModel) {
        demandModelMap.put(keyId, demandModel);
    }

    /**
     * 删除需求
     *
     * @param keyId 请求参数key
     */
    public static void deleteDemand(String keyId) {
        demandModelMap.remove(keyId);
    }

    public static boolean isDuplicateStart(String keyId) {
        if (PlatformControlDemand.demandModelMap.containsKey(keyId)) {
            LocalDateTime createTime = PlatformControlDemand.demandModelMap.get(keyId).getCreateTime();
            return StringUtil.isNotEmpty(createTime) && DateUtil.compareDiffBetweenSecond(createTime, LocalDateTime.now()) <= 60;
        }
        return false;
    }


    public static JSONObject ctrPileTaskFunc(PlatformDemandModel demandModel, Integer timeout) {
        JSONObject result = new JSONObject();
        final ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable<Object> call = () -> {
            //开始执行耗时操作
            while (true) {
                Thread.sleep(500);
                if (demandModel.getRecFlag()) { //接收到 命令 返回
                    JSONObject map1 = new JSONObject();
                    map1.put("recCode", demandModel.getRecCode()); //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                    map1.put("recMsg", demandModel.getRecMsg()); //执行结果信息
                    map1.put("serialNum", demandModel.getSerialNum()); //交易流水号
                    return map1;
                } else if (demandModel.getTimeOut()) {//启动命令超时
                    JSONObject map1 = new JSONObject();
                    map1.put("recCode", -1);
                    map1.put("recMsg", 0);
                    map1.put("serialNum", demandModel.getSerialNum()); //交易流水号
                    return map1;
                }
            }
        };
        try {
            Future<Object> future = exec.submit(call);
            Object re = future.get(timeout, TimeUnit.SECONDS); //任务处理超时时间设为 秒
            if (re != null) {
                result = (JSONObject) re;
            }
        } catch (TimeoutException ex) {
            demandModel.setTimeOut(true);
            result.put("recCode", -1);
            result.put("recMsg", 0);
            log.error("任务执行超时", ex);
            return result;
        }  catch (Exception e) {
            result.put("recCode", 1);
            result.put("recMsg", 0);
            log.error("任务执行失败", e);
            return result;
        }
        // 关闭线程池
        exec.shutdown();
        return result;
    }

}
