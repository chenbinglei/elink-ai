package com.sunmax.protocol.demand;

import com.google.common.collect.Maps;
import com.sunmax.protocol.model.GatewayDemandModel;

import java.time.LocalDateTime;
import java.util.Map;

public class GatewayDemand {

    //缓存充电桩需求的映射
    public static Map<String, GatewayDemandModel> demandModelMap = Maps.newConcurrentMap();

    /**
     * 创建需求
     */
    public static GatewayDemandModel createDemand(String keyId, String terminalCode) {
        GatewayDemandModel demandModel = new GatewayDemandModel();
        demandModel.setTerminalCode(terminalCode);
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
    public static void addDemand(String keyId, GatewayDemandModel demandModel) {
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

}
