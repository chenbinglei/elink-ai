package com.sunmax.webapp.config.websocket;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModel;

@ServerEndpoint("/appStartWebSocket/{pileCode}/{gunCode}/{userId}")
@Component
@Slf4j
public class AppStartWebSocket {

    //记录当前的记录数
    private static int onlineCount = 0;

    //存放每个客户端对应的websocket对象
    private static final CopyOnWriteArraySet<AppStartWebSocket> webSocketClients = new CopyOnWriteArraySet<>();
    //与客户端连接的会话，需要通过它来给客户端发送数据
    private Session session;
    private static final ConcurrentMap<String, AppStartWebSocket> webSocketMap = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, String> userMap = new ConcurrentHashMap<>();//用户和充电桩id的映射

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session sessionId, @PathParam("pileCode") String pileCode, @PathParam("gunCode") String gunCode, @PathParam("userId") String userId) throws IOException {
        log.info("能链E充小程序启动WebSocket连接:{},桩编号:{},枪编号:{},用户id:{}", sessionId.getId(), pileCode, gunCode, userId);
        //加入到set中
        String webSocketKey = userId + pileCode + gunCode;
        session = sessionId;
        webSocketClients.add(this);
        webSocketMap.put(webSocketKey, this);
        userMap.put(userId, pileCode + "@" + gunCode);
        addOnlineCount();
        AppStartWebSocket webSocket = webSocketMap.get(webSocketKey);

        JSONObject result = packageData(pileCode, gunCode);
        webSocket.sendMessage(JSON.toJSONString(result));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session sessionId, @PathParam("pileCode") String pileCode, @PathParam("gunCode") String gunCode, @PathParam("userId") String userId) {
        String webSocketKey = userId + pileCode + gunCode;
        webSocketClients.remove(this);
        webSocketMap.remove(webSocketKey, this);
        userMap.remove(userId);
        subOnlineCount();
        log.info("能链E充小程序启动WebSocket有一连接关闭:{},桩编号:{},枪编号:{},用户id:{}", sessionId.getId(), pileCode, gunCode, userId);
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
     * 主动发送充电状态给停留在充电页面的用户
     */
    public static void externalSendMessage() throws IOException {
        //根据用户id 查询充电桩id
        for (String uid : userMap.keySet()) {
            String[] str = userMap.get(uid).split("@");
            String pileCode = str[0];//充电桩编号
            String gunCode = str[1];//充电桩枪号
            String webSocketKey = uid + pileCode + gunCode;
            AppStartWebSocket webSocket = webSocketMap.get(webSocketKey);
            JSONObject result = packageData(pileCode, gunCode);
            webSocket.sendMessage(JSON.toJSONString(result));
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        AppStartWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        AppStartWebSocket.onlineCount--;
    }


    private static JSONObject packageData(String pileCode, String gunCode) {
        PileRealModel pileRealModel = getPileRealModel(pileCode);
        JSONObject result = new JSONObject();
        if (StringUtil.isNotEmpty(pileRealModel) && StringUtil.isNotEmpty(pileRealModel.getPileCode())) {
            result.put("pileCode", pileCode);
            result.put("gunCode", gunCode);
            Map<String, PileRealModel.GunRealModel> gunRealModelMap = pileRealModel.getGunRealModelMap();
            if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(gunCode)) {
                PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(gunCode);
                Integer workState = gunRealModel.getGunStatus();
                if (workState == 1 || workState == 4) {
                    result.put("gunStateLogo", 1); //充放电准备
                } else if (workState == 2 || workState == 5) {
                    result.put("gunStateLogo", 2); //充放电中
                } else if (workState == 7) {
                    result.put("gunStateLogo", 7); //预约中
                }
            }
            if (StringUtil.isEmpty(result.getInteger("gunStateLogo"))) {
                result.put("gunStateLogo", 0); //枪状态标识 已结束
            }
        }
        return result;
    }

}
