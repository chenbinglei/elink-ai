package com.sunmax.device.websocket;

import com.alibaba.fastjson.JSON;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.dto.protocol.mqtt.UpdateInfoDto;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.device.config.WebSocketConfig;
import com.sunmax.device.dao.access.DeviceTaskDao;
import com.sunmax.device.dao.access.DeviceTaskRecordDao;
import com.sunmax.device.dto.task.DeviceTaskWebSocketDto;
import com.sunmax.device.entity.access.DeviceTaskEntity;
import com.sunmax.device.entity.access.DeviceTaskRecordEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@ServerEndpoint("/deviceUpdateWebSocket/{userId}")
@Component
@Slf4j
public class DeviceUpdateWebSocket {

    //记录当前的记录数
    private static int onlineCount = 0;

    //与客户端连接的会话，需要通过它来给客户端发送数据
    private Session session;

    private static final ConcurrentMap<String, DeviceUpdateWebSocket> webSocketMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session sessionId, @PathParam("userId") String userId) throws Exception {
        if (StringUtil.isEmpty(userId)) {
            return;
        }
        System.out.println("新开启了一个websocket连接" + sessionId.getId());
        //加入到set中
        session = sessionId;
        webSocketMap.put(userId, this);
        addOnlineCount();
        System.out.println("有新的连接加入！当前在线人数为：" + getOnlineCount() + " ");
        this.sendMessage(JSON.toJSONString(getDeviceTaskProgress()));
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session sessionId, @PathParam("userId") String userId) {
        webSocketMap.remove(userId, this);
        subOnlineCount();
        System.out.println("有一连接关闭" + sessionId.getId());
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
    public static void externalSendMessage() {
        webSocketMap.forEach((webSocketId, webSocket) -> {
            try {
                webSocket.sendMessage(JSON.toJSONString(getDeviceTaskProgress()));
            } catch (IOException e) {
                log.error("推送数据报错", e);
            }
        });
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        DeviceUpdateWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        DeviceUpdateWebSocket.onlineCount--;
    }


    private static List<DeviceTaskWebSocketDto> getDeviceTaskProgress() {
        List<DeviceTaskWebSocketDto> resultList = Lists.newArrayList();
        //查询设备升级进行中的任务
        List<DeviceTaskEntity> deviceTaskList = WebSocketConfig.deviceTaskDao.findAllByTaskStatusIn(Arrays.asList(1, 2));
        if (CollectionUtils.isNotEmpty(deviceTaskList)) {

            //根据多个任务id查询任务记录数据
            List<String> taskIds = deviceTaskList.stream().map(DeviceTaskEntity::getId).collect(Collectors.toList());
            List<DeviceTaskRecordEntity> deviceTaskRecordList = WebSocketConfig.deviceTaskRecordDao.findAllByTaskIdIn(taskIds);

            //校验是否更新任务状态
            updateDeviceTaskStatus(deviceTaskList, deviceTaskRecordList, WebSocketConfig.deviceTaskDao, WebSocketConfig.deviceTaskRecordDao);

            Map<String, List<DeviceTaskRecordEntity>> deviceTaskRecordMap = deviceTaskRecordList.stream().collect(Collectors.groupingBy(DeviceTaskRecordEntity::getTaskId));

            //对数据进行组装
            resultList = deviceTaskList.stream().map(deviceTask -> {
                DeviceTaskWebSocketDto result = new DeviceTaskWebSocketDto();
                result.setTaskId(deviceTask.getId());
                result.setTaskName(deviceTask.getTaskName());
                result.setTaskStatus(deviceTask.getTaskStatus());
                if (deviceTaskRecordMap.containsKey(deviceTask.getId())) {
                    List<DeviceTaskWebSocketDto.DeviceUpdateData> deviceUpdateDataList = deviceTaskRecordMap.get(deviceTask.getId()).stream().map(deviceTaskRecord -> {
                        DeviceTaskWebSocketDto.DeviceUpdateData deviceUpdateData = new DeviceTaskWebSocketDto.DeviceUpdateData();
                        BeanUtils.copyProperties(deviceTaskRecord, deviceUpdateData);
                        //下载中的桩 获取进度条
                        if (deviceTaskRecord.getStatus() == 2) {
                            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(deviceTaskRecord.getDeviceNumber());
                            if (pileRealModel != null && pileRealModel.getUpdateInfoMap().containsKey(deviceTask.getFirmwareType())) {
                                UpdateInfoDto updateInfo = pileRealModel.getUpdateInfoMap().get(deviceTask.getFirmwareType());
                                if (StringUtil.isNotEmpty(updateInfo.getDataBlockLabel()) && StringUtil.isNotEmpty(updateInfo.getDataBlockSum())) {
                                    deviceUpdateData.setProgress(Math.round((double)updateInfo.getDataBlockLabel() / updateInfo.getDataBlockSum() * 100));
                                }
                            }
                        }
                        if (StringUtil.isNotEmpty(deviceTaskRecord.getUpgradeTime())) {
                            deviceUpdateData.setUpgradeTime(DateUtil.localDateTimeToStr(deviceTaskRecord.getUpgradeTime()));
                        }
                        if (StringUtil.isNotEmpty(deviceTaskRecord.getEndTime())) {
                            deviceUpdateData.setEndTime(DateUtil.localDateTimeToStr(deviceTaskRecord.getEndTime()));
                        }
                        return deviceUpdateData;
                    }).collect(Collectors.toList());
                    result.setDeviceDataList(deviceUpdateDataList);
                }
                return result;
            }).collect(Collectors.toList());
        }
        return resultList;
    }

    public static void updateDeviceTaskStatus(List<DeviceTaskEntity> deviceTaskList, List<DeviceTaskRecordEntity> deviceTaskRecordList,
                                              DeviceTaskDao deviceTaskDao, DeviceTaskRecordDao deviceTaskRecordDao) {

        Map<String, List<DeviceTaskRecordEntity>> deviceTaskRecordMap = deviceTaskRecordList.stream().collect(Collectors.groupingBy(DeviceTaskRecordEntity::getTaskId));

        //校验任务是否关闭
        List<DeviceTaskRecordEntity> updateTaskRecordList = Lists.newArrayList();
        List<Integer> statusList = Arrays.asList(1, 2, 4);
        List<DeviceTaskEntity> updatedeviceTaskList = deviceTaskList.stream().filter(deviceTask -> {
            //任务超过十分钟 自动关闭
            if (DateUtil.compareDiffBetweenSecond(deviceTask.getCreateTime(), LocalDateTime.now()) >= 600) {
                return true;
            }
            if (deviceTaskRecordMap.containsKey(deviceTask.getId())) {
                List<DeviceTaskRecordEntity> taskRecordList = deviceTaskRecordMap.get(deviceTask.getId()).stream()
                        .filter(d -> statusList.contains(d.getStatus())).collect(Collectors.toList());
                return taskRecordList.isEmpty();
            }
            return false;
        }).peek(deviceTask -> {
            deviceTask.setTaskStatus(3);
            if (deviceTaskRecordMap.containsKey(deviceTask.getId())) {
                updateTaskRecordList.addAll(deviceTaskRecordMap.get(deviceTask.getId()).stream().filter(d -> statusList.contains(d.getStatus())).peek(d -> {
                    d.setStatus(5);
                    d.setReason("任务超时自动关闭");
                }).collect(Collectors.toList()));
            }
        }).collect(Collectors.toList());

        //更新设备升级任务状态
        if (CollectionUtils.isNotEmpty(updatedeviceTaskList)) {
            Map<String, DeviceTaskEntity> updateDeviceTaskMap = updatedeviceTaskList.stream().collect(Collectors.toMap(DeviceTaskEntity::getId, d -> d, (k1,k2) -> k1));
            deviceTaskList = deviceTaskList.stream().map(d -> {
                if (updateDeviceTaskMap.containsKey(d.getId())) {
                    d = updateDeviceTaskMap.get(d.getId());
                }
                return d;
            }).collect(Collectors.toList());

            //保存设备升级任务
            deviceTaskDao.saveAll(updatedeviceTaskList);
        }
        if (CollectionUtils.isNotEmpty(updateTaskRecordList)) {
            Map<String, DeviceTaskRecordEntity> updateTaskRecordMap = updateTaskRecordList.stream().collect(Collectors.toMap(DeviceTaskRecordEntity::getId, d -> d, (k1, k2) -> k1));
            deviceTaskRecordList = deviceTaskRecordList.stream().map(d -> {
                if (updateTaskRecordMap.containsKey(d.getId())) {
                    d = updateTaskRecordMap.get(d.getId());
                }
                return d;
            }).collect(Collectors.toList());

            //保存设备升级任务记录
            deviceTaskRecordDao.saveAll(updateTaskRecordList);
        }
    }

}
