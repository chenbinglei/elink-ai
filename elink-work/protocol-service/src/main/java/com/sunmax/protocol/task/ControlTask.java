package com.sunmax.protocol.task;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.protocol.model.GatewayDemandModel;
import com.sunmax.protocol.model.PileDemandModel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class ControlTask {

    //缓存充电桩需求的映射
    public static Map<String, PileDemandModel> pileDemandMap = new ConcurrentHashMap<>();

    public static JSONObject ctrPileTaskFunc(PileDemandModel demandModel, Integer timeout) {
        JSONObject result = new JSONObject();
        ExecutorService exec = Executors.newFixedThreadPool(1);
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

    public static JSONObject ctrGatewayTaskFunc(GatewayDemandModel demandModel, Integer timeout) {
        JSONObject result = new JSONObject();
        ExecutorService exec = Executors.newFixedThreadPool(1);
        Callable<Object> call = () -> {
            //开始执行耗时操作
            while (true) {
                Thread.sleep(500);
                if (demandModel.getRecFlag()) { //接收到 命令 返回
                    JSONObject map1 = new JSONObject();
                    map1.put("recCode", demandModel.getRecCode()); //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                    map1.put("recMsg", demandModel.getRecMsg()); //执行结果信息
                    return map1;
                } else if (demandModel.getTimeOut()) {//启动命令超时
                    JSONObject map1 = new JSONObject();
                    map1.put("recCode", -1);
                    map1.put("recMsg", null);
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
            result.put("recMsg", null);
        }  catch (Exception e) {
            result.put("recCode", 1);
            result.put("recMsg", null);
            log.error("任务执行超时", e);
            return result;
        }
        // 关闭线程池
        exec.shutdown();
        return result;
    }

}
