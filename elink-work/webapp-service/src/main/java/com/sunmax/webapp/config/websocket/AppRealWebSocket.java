package com.sunmax.webapp.config.websocket;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.SiteInfoDto;
import com.sunmax.common.dto.webapp.AppInHandOrderDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.*;
import com.sunmax.webapp.dto.RealWebSocketDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModelList;
import static com.sunmax.common.util.StringUtil.convertGunStatus;
import static com.sunmax.webapp.config.websocket.WebSocketConfig.deviceService;
import static com.sunmax.webapp.config.websocket.WebSocketConfig.togetherService;

@ServerEndpoint("/appRealWebSocket/{userId}/{orderType}")
@Component
@Slf4j
public class AppRealWebSocket {

    //记录当前的记录数
    private static int onlineCount = 0;

    //存放每个客户端对应的websocket对象
    private static final CopyOnWriteArraySet<AppRealWebSocket> webSocketClients = new CopyOnWriteArraySet<>();
    //与客户端连接的会话，需要通过它来给客户端发送数据
    private Session session;

    private static final ConcurrentMap<String, AppRealWebSocket> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session sessionId, @PathParam("userId") String userId, @PathParam("orderType") Integer orderType) throws Exception {
        if (StringUtil.isEmpty(userId) || StringUtil.isEmpty(orderType)) {
            return;
        }
        log.info("能链E充新开启了一个实时websocket连接:{},用户id:{},订单类型:{}", sessionId.getId(), userId, orderType);
        //加入到set中
        session = sessionId;
        webSocketClients.add(this);
        webSocketMap.put(userId + "@" + orderType, this);
        addOnlineCount();
//        System.out.println("有新的连接加入！当前在线人数为：" + getOnlineCount() + " ");
        List<RealWebSocketDto> result = packageData(userId, orderType);
        this.sendMessage(JSON.toJSONString(result));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session sessionId, @PathParam("userId") String userId, @PathParam("orderType") Integer orderType) {
        String webSocketKey = userId + "@" + orderType;
        webSocketClients.remove(this);
        webSocketMap.remove(webSocketKey, this);
        subOnlineCount();
//        log.info("能链E充实时websocket有一个连接关闭:{},用户id:{},订单类型:{}", sessionId.getId(), userId, orderType);
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
     * 主动发送充电状态给停留在充电页面的用户
     */
    public static void externalSendMessage() throws IOException {
        //根据用户id 查询充电桩id
        for (String webSocketId : webSocketMap.keySet()) {
            String[] split = webSocketId.split("@");
            String userId = split[0];
            Integer orderType = Integer.parseInt(split[1]);
            List<RealWebSocketDto> result = packageData(userId, orderType);
            AppRealWebSocket webSocket = webSocketMap.get(webSocketId);
            webSocket.sendMessage(JSON.toJSONString(result));
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        AppRealWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        AppRealWebSocket.onlineCount--;
    }


    private static List<RealWebSocketDto> packageData(String userId, Integer orderType) {
        List<RealWebSocketDto> resultList = Lists.newArrayList();
        List<AppInHandOrderDto> inHandOrderList = togetherService.findAppInHandOrderListByAppletUserId(userId, orderType).getData();
        if (CollectionUtils.isNotEmpty(inHandOrderList)) {
            //电桩实时数据
            Map<String, PileRealModel> pileRealModelMap = Maps.newHashMap();
            //根据多个电桩编码查询电桩实时数据
            List<PileRealModel> pileRealModelList = getPileRealModelList(inHandOrderList.stream().map(AppInHandOrderDto::getPileCode).collect(Collectors.toList()));
            if (CollectionUtils.isNotEmpty(pileRealModelList)) {
                pileRealModelMap = pileRealModelList.stream().collect(Collectors.toMap(PileRealModel::getPileCode, Function.identity(), (k1, k2) -> k1));
            }

            //根据多个站点id，查询站点信息
            ResponseResult<Map<String, SiteInfoDto>> siteBasicInfoByIds = deviceService.findSiteBasicInfoByIds(inHandOrderList.stream().map(AppInHandOrderDto::getSiteId).collect(Collectors.toList()));

            Map<String, PileRealModel> finalPileRealModelMap = pileRealModelMap;
            inHandOrderList.forEach(inHandOrder -> {
                String orderNum = inHandOrder.getOrderNum();
                String pileCode = inHandOrder.getPileCode();
                String gunCode = String.valueOf(inHandOrder.getGunCode());
                if (!finalPileRealModelMap.isEmpty() && finalPileRealModelMap.containsKey(pileCode) && StringUtil.isNotEmpty(finalPileRealModelMap.get(pileCode).getPileCode())) {
                    Map<String, PileRealModel.GunRealModel> gunRealModelMap = finalPileRealModelMap.get(pileCode).getGunRealModelMap();
                    if (!gunRealModelMap.isEmpty() && gunRealModelMap.containsKey(gunCode)) {
                        PileRealModel.GunRealModel gunRealModel = gunRealModelMap.get(gunCode);
                        RealWebSocketDto result = new RealWebSocketDto();
                        //获取站点信息
                        if (StringUtil.isNotEmpty(inHandOrder.getSiteId()) && siteBasicInfoByIds.isSuccess() && !siteBasicInfoByIds.getData().isEmpty() &&
                                siteBasicInfoByIds.getData().containsKey(inHandOrder.getSiteId())) {
                            result.setSiteId(inHandOrder.getSiteId());//站点id
                            result.setSiteName(siteBasicInfoByIds.getData().get(inHandOrder.getSiteId()).getSiteName());//站点名称
                        }
                        result.setOrderNum(orderNum);//订单编码
                        result.setPileCode(pileCode);//电桩编码
                        result.setGunCode(Integer.parseInt(gunCode));//电枪编码
                        result.setClockingTime(gunRealModel.getClockingTime());//预约时间
                        result.setWorkState(convertGunStatus(finalPileRealModelMap.get(pileCode), gunRealModel.getGunCode()));//电枪工作状态
                        result.setPrepayMoney(StringUtil.isEmpty(gunRealModel.getPrepayMoney()) ? BigDecimal.valueOf(0.0) : gunRealModel.getPrepayMoney());//预付金额
                        //根据开始时间和当前时间，计算已充时长
                        if (StringUtil.isNotEmpty(gunRealModel.getStartTime())) {
                            LocalDateTime startTime = DateUtil.strToLocalDateTime(gunRealModel.getStartTime());
                            result.setChargeTime(DateUtil.secToTime(DateUtil.compareDiffBetweenSecond(startTime, LocalDateTime.now()).intValue()));//已充时长
                        }
                        result.setStrategy(gunRealModel.getStrategy()); //策略
                        result.setStrategyCfg(gunRealModel.getStrategyCfg()); //策略值
                        result.setStartSoc(gunRealModel.getStartSoc()); //起始SOC
                        result.setVoltage(gunRealModel.getOutVolt()); //电压
                        result.setCurrent(gunRealModel.getOutCurrent()); //电流
                        if (StringUtil.isNotEmpty(gunRealModel.getOutVolt()) && StringUtil.isNotEmpty(gunRealModel.getOutCurrent())) {
                            result.setPower(DoubleUtil.getToDouble(gunRealModel.getOutVolt() * gunRealModel.getOutCurrent() / 1000)); //功率
                        }
                        if (StringUtil.isNotEmpty(gunRealModel.getRemainTime())) {
                            result.setRemainTime(SunMaxUtil.minToTime(gunRealModel.getRemainTime() / 60)); //剩余时间
                        }
                        result.setTotalQt(DoubleUtil.getAbsDouble(gunRealModel.getTotalQt())); //当前总电量
                        result.setTotalCost(DoubleUtil.getAbsBigDecimal(gunRealModel.getTotalCost())); //当前总金额
                        result.setBatterySoc(gunRealModel.getBatterySoc()); //当前电池soc
                        resultList.add(result);
                    }
                }
            });
        }
        return resultList;
    }

}
