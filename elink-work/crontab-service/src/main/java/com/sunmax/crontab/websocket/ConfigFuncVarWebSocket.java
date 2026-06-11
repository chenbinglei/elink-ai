package com.sunmax.crontab.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.configure.DeviceVariableDto;
import com.sunmax.common.dto.crontab.LocalCacheDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.crontab.dto.ConfigFuncVarDto;
import com.sunmax.crontab.entity.ComputeNodeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import static com.sunmax.crontab.config.WebSocketConfig.*;

/**
 * 组态系统变量数据推送
 */
@ServerEndpoint("/configFuncVarWebSocket/{clientId}/{domainId}")
@Component
@Slf4j
public class ConfigFuncVarWebSocket {

    //记录当前的记录数
    private static int onlineCount = 0;

    //存放每个客户端对应的webSocket对象
    private static final CopyOnWriteArraySet<ConfigFuncVarWebSocket> webSocketClients = new CopyOnWriteArraySet<>();

    //与客户端连接的会话，需要通过它来给客户端发送数据
    private Session session;

    //用户id -> 当前会话
    private static final Map<String, ConfigFuncVarWebSocket> webSocketMap = new ConcurrentHashMap<>();

    //客户端id -> 域名id
    private static final Map<String, String> clientParamMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session sessionId, @PathParam("clientId") String clientId, @PathParam("domainId") String domainId) {
        log.info("组态系统变量实时数据新开启了一个webSocket连接" + sessionId.getId());

        //加入到set中
        session = sessionId;
        webSocketClients.add(this);
        webSocketMap.put(clientId, this);
        if (StringUtil.isNotEmpty(domainId)) {
            clientParamMap.put(clientId, domainId);
            try {
                //发送消息
                this.sendMessage(JSON.toJSONString(getConfigFuncVarList(domainId)));
            } catch (IOException ie) {
                log.error("组态系统变量实时数据推送消息失败", ie);
            }
        }
        addOnlineCount();
        log.info("有新的连接加入！当前组态系统变量实时数据在线人数为：" + getOnlineCount() + " ");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session sessionId, @PathParam("clientId") String clientId)  {
        webSocketClients.remove(this);
        webSocketMap.remove(clientId, this);
        clientParamMap.remove(clientId);
        subOnlineCount();
        log.info("组态系统变量实时数据有一连接关闭" + sessionId.getId());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {

    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Throwable t) {
        log.error(t.getMessage(), t);
    }

    public void sendMessage(String message) throws IOException {
        //this.session.getAsyncRemote().sendText(message);
        if (this.session.isOpen()) {
            this.session.getBasicRemote().sendText(message);
        }
    }

    /**
     * 外部调用发送消息
     */
    public static void sendAllMessage() {
        webSocketMap.forEach((key, value) -> {
            try {
                value.sendMessage(JSON.toJSONString(getConfigFuncVarList(clientParamMap.get(key))));
            } catch (IOException e) {
                log.error("推送组态站点变量实时数据失败", e);
            }
        });

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        ConfigFuncVarWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        ConfigFuncVarWebSocket.onlineCount--;
    }

    public static Map<String, ConfigFuncVarDto> getConfigFuncVarList(String domainId) {
        //返回的对象
        Map<String, ConfigFuncVarDto> resultMap = Maps.newHashMap();
        //根据域名id查询关联系统变量标识信息
        DeviceVariableDto deviceVariable = configService.findAllVariableByDomainId(domainId).getData();
        if (deviceVariable != null && StringUtil.isNotEmpty(deviceVariable.getSiteId()) && MapUtils.isNotEmpty(deviceVariable.getVariableDataMap())) {

            //变量数据参数
            Map<String, DeviceVariableDto.VariableData> variableDataMap = deviceVariable.getVariableDataMap();

            //根据多个设备id和多个变量标识查询计算节点数据
            //设备id -> (变量标识 -> 节点id)
            Map<String, Map<String, String>> deviceNodeMap = Maps.newHashMap();
            variableDataMap.forEach((deviceId, variableData) -> {
                if (CollectionUtils.isNotEmpty(variableData.getNodeList())) {
                    deviceNodeMap.put(deviceId, computeNodeDao.findAllByDeviceIdAndNodeCodeIn(deviceId, variableData.getNodeList()).stream()
                            .collect(Collectors.toMap(ComputeNodeEntity::getNodeCode, ComputeNodeEntity::getId, (k1, k2) -> k1)));
                }
            });
            //获取计算节点的缓存数据
            Map<String, LocalCacheDto> sysNodeDataMap = Maps.newHashMap();
            if (MapUtils.isNotEmpty(deviceNodeMap)) {
                //根据多个节点id查询节点数据
                List<String> nodeIds = deviceNodeMap.values().stream().flatMap(s -> s.values().stream()).collect(Collectors.toList());
                sysNodeDataMap = computeNodeService.findLocalCacheDataByIds(String.join(FileUtil.COMMA, nodeIds)).getData();
            }

            //获取设备功能点数据
            Map<String, Map<String, RealDataModel>> functionDataMap = Maps.newHashMap();
            List<Map.Entry<String, DeviceVariableDto.VariableData>> deviceVarList = variableDataMap.entrySet().stream().filter(s ->
                    CollectionUtils.isNotEmpty(s.getValue().getFunctionList())).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(deviceVarList)) {
                Set<String> deviceIds = new HashSet<>();
                Set<String> functionIds = new HashSet<>();
                deviceVarList.forEach(variable -> {
                    deviceIds.add(variable.getKey());
                    functionIds.addAll(variable.getValue().getFunctionList().stream().filter(StringUtil::isNotEmpty).map(d -> {
                        if (d.contains(FileUtil.EIT)) {
                            return d.substring(0, d.indexOf(FileUtil.EIT));
                        }
                        return d;
                    }).collect(Collectors.toSet()));
                });
                if (CollectionUtils.isNotEmpty(deviceIds) && CollectionUtils.isNotEmpty(functionIds)) {
                    //根据设备id和功能点标识获取功能点数据
                    String functionLogos = String.join(FileUtil.COMMA, functionIds);
                    functionDataMap = deviceService.getDeviceFunctionsRealDataByIds(deviceIds, functionLogos).getData();
                }
            }

            //对数据进行组装
            for (Map.Entry<String, DeviceVariableDto.VariableData> entry : variableDataMap.entrySet()) {
                String deviceId = entry.getKey();
                DeviceVariableDto.VariableData value = entry.getValue();
                ConfigFuncVarDto result = new ConfigFuncVarDto();
                //获取计算节点数据
                if (CollectionUtils.isNotEmpty(value.getNodeList()) && deviceNodeMap.containsKey(deviceId)) {
                    Map<String, Object> nodeDataMap = Maps.newHashMap();
                    Map<String, String> nodeCodeIdMap = deviceNodeMap.get(deviceId);
                    Map<String, LocalCacheDto> finalSysNodeDataMap = sysNodeDataMap;
                    nodeCodeIdMap.forEach((nodeCode, nodeId) -> {
                        if (finalSysNodeDataMap.containsKey(nodeId) && StringUtil.isNotEmpty(finalSysNodeDataMap.get(nodeId).getResultValue())) {
                            nodeDataMap.put(nodeCode, finalSysNodeDataMap.get(nodeId).getResultValue());
                        }
                    });
                    result.setNodeMap(nodeDataMap);
                }
                //获取功能点数据
                if (CollectionUtils.isNotEmpty(value.getFunctionList()) && functionDataMap.containsKey(deviceId)) {
                    Map<String, Object> functionMap = Maps.newHashMap();
                    Map<String, RealDataModel> dataMap = functionDataMap.get(deviceId);
                    value.getFunctionList().stream().filter(StringUtil::isNotEmpty).forEach(functionData -> {
                        if (functionData.contains(FileUtil.EIT)) {
                            List<String> functionDataList = Arrays.stream(functionData.split(FileUtil.EIT)).collect(Collectors.toList());
                            if (CollectionUtils.isNotEmpty(functionDataList) && functionDataList.size() > 1) {
                                String functionLogo = functionDataList.get(0);
                                String functionIndex = functionDataList.get(1);
                                if (StringUtil.isNotEmpty(dataMap.get(functionLogo)) && StringUtil.isNotEmpty(dataMap.get(functionLogo).getDataValue())
                                        && StringUtil.isNotEmpty(functionIndex)) {
                                    Object dataValue = dataMap.get(functionLogo).getDataValue();
                                    JSONArray dataValueList = JSON.parseArray(String.valueOf(dataValue));
                                    if (Integer.parseInt(functionIndex) < dataValueList.size()) {
                                        functionMap.put(functionData, dataValueList.get(Integer.parseInt(functionIndex)));
                                    }
                                }
                            }
                        } else {
                            if (dataMap.containsKey(functionData)) {
                                functionMap.put(functionData, dataMap.get(functionData).getDataValue());
                            }
                        }
                    });
                    result.setFunctionMap(functionMap);
                }
                resultMap.put(deviceId, result);
            }

        }
        return resultMap;
    }

}
