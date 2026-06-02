package com.sunmax.crontab.websocket;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.operate.ConfigurationResultDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.crontab.SystemVarNewValueVo;
import com.sunmax.crontab.entity.SystemVariableEntity;
import com.sunmax.crontab.entity.VariableNodeEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import static com.sunmax.crontab.config.WebSocketConfig.*;

/**
 * 推送站点或设备系统变量实时数据
 */
@ServerEndpoint("/varRealDataWebSocket/{clientId}/{siteId}/{deviceIds}/{varCodes}")
@Component
@Slf4j
public class VarRealDataWebSocket {

    //记录当前的记录数
    private static int onlineCount = 0;

    //存放每个客户端对应的webSocket对象
    private static final CopyOnWriteArraySet<VarRealDataWebSocket> webSocketClients = new CopyOnWriteArraySet<>();

    //与客户端连接的会话，需要通过它来给客户端发送数据
    private Session session;

    //用户id -> 当前会话
    private static final Map<String, VarRealDataWebSocket> webSocketMap = new ConcurrentHashMap<>();

    //客户端id -> 设备参数
    private static final Map<String, Map<String, String>> clientDataMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session sessionId, @PathParam("clientId") String clientId, @PathParam("siteId") String siteId,
                       @PathParam("deviceIds") String deviceIds, @PathParam("varCodes") String varCodes) {
        System.out.println("系统变量实时数据推送新开启了一个webSocket连接" + sessionId.getId());

        //加入到set中
        session = sessionId;
        webSocketClients.add(this);
        webSocketMap.put(clientId, this);
        ResponseResult<ConfigurationResultDto> resultList = new ResponseResult<>();
        if (StringUtil.isNotEmpty(siteId) || StringUtil.isNotEmpty(deviceIds)) {
            Map<String, String> hashMap = Maps.newHashMap();
            hashMap.put("siteId", siteId);
            hashMap.put("deviceIds", deviceIds);
            hashMap.put("varCodes", varCodes);
            clientDataMap.put(clientId, hashMap);
            resultList = getVarRealDataList(hashMap);
        }
        try {
            //发送消息
            this.sendMessage(JSON.toJSONString(resultList));
        } catch (IOException ie) {
            log.error("系统变量实时数据推送消息失败", ie);
        }
        addOnlineCount();
        System.out.println("有新的连接加入！当前系统变量实时数据推送在线人数为：" + getOnlineCount() + " ");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session sessionId, @PathParam("clientId") String clientId)  {
        webSocketClients.remove(this);
        webSocketMap.remove(clientId, this);
        clientDataMap.remove(clientId);
        subOnlineCount();
        System.out.println("系统变量实时数据推送有一连接关闭" + sessionId.getId());
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
        t.printStackTrace();
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
//        for (VarRealDataWebSocket item : webSocketClients) {
//            try {
//                item.sendMessage(DateUtil.localDateTimeToStr(LocalDateTime.now()));
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }
        webSocketMap.forEach((key, value) -> {
            ResponseResult<ConfigurationResultDto> resultList = getVarRealDataList(clientDataMap.get(key));
            try {
                value.sendMessage(JSON.toJSONString(resultList));
            } catch (IOException e) {
                log.error("系统变量实时数据推送失败", e);
            }
        });

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        VarRealDataWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        VarRealDataWebSocket.onlineCount--;
    }

    public static ResponseResult<ConfigurationResultDto> getVarRealDataList(Map<String, String> hashMap) {
        //返回的集合
        ConfigurationResultDto result = new ConfigurationResultDto();
        try {
            result.setDesc("sunos平台推送系统变量实时数据接口");
            //站点id
            String siteId = hashMap.get("siteId");
            //多个设备id
            String deviceIds = hashMap.get("deviceIds");
            //多个系统变量标识
            String varCodes = hashMap.get("varCodes");
            if (StringUtil.isNotEmpty(siteId) && StringUtil.isNotEmpty(deviceIds)) {
                return ResponseResult.error("查询失败，站点id和设备id参数只能同时存在一个", result);
            }
            //判断是否传了系统变量标识，如果没有，则查询当前所传站点或多个设备下关联的所有系统变量标识
            if (StringUtil.isEmpty(varCodes)) {
                List<VariableNodeEntity> variableNodeEntityList = Lists.newArrayList();
                //查询设备基本信息，根据设备模型id查询一遍关联的系统变量信息
                if (StringUtil.isNotEmpty(deviceIds)) {
                    ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByIds = deviceService.findDeviceBasicInfoByIds(Arrays.stream(deviceIds.split(",")).map(String::trim).collect(Collectors.toList()));
                    if (deviceBasicInfoByIds.isSuccess() && !deviceBasicInfoByIds.getData().isEmpty()) {
                        Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceBasicInfoByIds.getData();
                        List<String> modelIdList = deviceBasicInfoDtoMap.values().stream().map(DeviceBasicInfoDto::getModelId).distinct().collect(Collectors.toList());
                        variableNodeEntityList = variableNodeDao.findAllByDeviceIdIn(modelIdList);
                    }
                }
                variableNodeEntityList.addAll(variableNodeDao.findAllByDeviceIdIn( StringUtil.isNotEmpty(siteId) ? Collections.singletonList(siteId): Arrays.stream(deviceIds.split(",")).map(String::trim).collect(Collectors.toList())));
                //根据站点或设备id，查询系统变量标识
                if (CollectionUtils.isNotEmpty(variableNodeEntityList)) {
                    List<String> varidList = variableNodeEntityList.stream().map(VariableNodeEntity::getVarId).distinct().collect(Collectors.toList());
                    varCodes = systemVariableDao.findAllById(varidList).stream().map(SystemVariableEntity::getVarCode).collect(Collectors.joining(","));
                }
            }
            if (StringUtil.isNotEmpty(varCodes)) {
                SystemVarNewValueVo systemVarNewValueVo = new SystemVarNewValueVo();
                systemVarNewValueVo.setVarCodes(varCodes);
                systemVarNewValueVo.setDeviceIds(deviceIds);
                systemVarNewValueVo.setSiteId(siteId);
                result = configFuncPointService.findSystemVarNewValue(systemVarNewValueVo).getData();
            }
        } catch (Exception e) {
            log.error("查询功能点实时数据失败", e);
            return ResponseResult.error("程序出现异常", result);
        }

        return ResponseResult.ok(result);
    }
}
