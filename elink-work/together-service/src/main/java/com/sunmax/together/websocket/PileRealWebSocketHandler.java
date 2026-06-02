package com.sunmax.together.websocket;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.DeviceGunInfoDto;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.together.dto.websocket.PileRealWebsocketDto;
import com.sunmax.together.service.feign.DeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.sunmax.common.config.redis.RedisDeviceUtil.getDevice;
import static com.sunmax.common.config.redis.RedisGeneralUtil.getPileRealModel;
import static com.sunmax.common.util.StringUtil.convertGunStatus;

@Configuration
@Slf4j
public class PileRealWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private DeviceService deviceService;

    //用户id -> session会话
    private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();

    //用户id -> 电桩编号
    private final Map<String, String> userPileCodeMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("电桩实时数据新开启了一个webSocket连接{}", session.getId());
        //从请求参数中获取 userId, 例如: ws://.../together/pileRealWebSocket/{userId}/{pileCode}
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String[] pathParts = path.split(FileUtil.SLASH);
        if (pathParts.length >= 5) {
            String userId = pathParts[3];
            String pileCode = pathParts[4];
            sessionMap.put(userId, session);
            userPileCodeMap.put(userId, pileCode);
            //发送第一条数据
            this.sendMessage(userId, JSON.toJSONString(getPileRealList(pileCode)));
            log.info("有新的连接加入！访问电桩实时数据用户id: {}", userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionMap.values().removeIf(s -> s.equals(session));
        String path = Objects.requireNonNull(session.getUri()).getPath();
        String[] pathParts = path.split(FileUtil.SLASH);
        if (pathParts.length >= 5) {
            String userId = pathParts[3];
            userPileCodeMap.remove(userId);
        }
        log.info("电桩实时数据有一连接关闭，sessionId={}, status={}", session.getId(), status);
    }

    private void sendMessage(String userId, String message) {
        WebSocketSession session = sessionMap.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                log.error("推送电桩实时数据失败", e);
            }
        }
    }

    /**
     * 外部调用发送消息
     */
    public void sendAllMessage() {
        sessionMap.keySet().forEach(userId -> sendMessage(userId, JSON.toJSONString(getPileRealList(userPileCodeMap.get(userId)))));
    }

    private PileRealWebsocketDto getPileRealList(String pileCode) {
        //返回的对象
        PileRealWebsocketDto result = new PileRealWebsocketDto();

        result.setPileCode(pileCode);
        //获取设备缓存数据
        DeviceModel device = getDevice(pileCode);
        if (device == null) {
            result.setWorkStatus(0);
            return result;
        }
        result.setWorkStatus(device.getTxStatus());
        //获取电桩通用缓存数据
        PileRealModel pileRealModel = getPileRealModel(pileCode);
        if (pileRealModel != null) {
            result.setTotalPower(pileRealModel.getTotalPower());
            result.setRecChargePower(pileRealModel.getRecChargePower());
            result.setDisChargePower(pileRealModel.getDisChargePower());
            //根据电桩编码查询设备信息
            DeviceBasicInfoDto deviceBasicInfoDto = deviceService.findDeviceBasicInfoByCodes(Collections.singletonList(pileCode)).getData().get(pileCode);
            if (deviceBasicInfoDto != null && MapUtils.isNotEmpty(pileRealModel.getGunRealModelMap())) {
                //根据电桩id查询充电枪信息
                Map<String, DeviceGunInfoDto> deviceGunInfoDtoMap = Maps.newHashMap();
                ResponseResult<Map<String, List<DeviceGunInfoDto>>> deviceGunInfoByDeviceIds = deviceService.findDeviceGunInfoByDeviceIds(Collections.singletonList(deviceBasicInfoDto.getId()));
                if (deviceGunInfoByDeviceIds.isSuccess() && !deviceGunInfoByDeviceIds.getData().isEmpty() && deviceGunInfoByDeviceIds.getData().containsKey(deviceBasicInfoDto.getId())) {
                    deviceGunInfoDtoMap = deviceGunInfoByDeviceIds.getData().get(deviceBasicInfoDto.getId()).stream().collect(Collectors.toMap(DeviceGunInfoDto::getGunCode, deviceGunInfoDto -> deviceGunInfoDto, (v1, v2) -> v2));
                }
                Map<String, DeviceGunInfoDto> finalDeviceGunInfoDtoMap = deviceGunInfoDtoMap;
                result.setGunRealModelList(pileRealModel.getGunRealModelMap().values().stream().map(gunRealModel -> {
                    PileRealWebsocketDto.GunRealModel gunReal = new PileRealWebsocketDto.GunRealModel();
                    BeanUtils.copyProperties(gunRealModel, gunReal);
                    if (!finalDeviceGunInfoDtoMap.isEmpty() && finalDeviceGunInfoDtoMap.containsKey(gunRealModel.getGunCode())) {
                        gunReal.setGunName(finalDeviceGunInfoDtoMap.get(gunRealModel.getGunCode()).getGunName());
                    }
                    gunReal.setGunWorkState(convertGunStatus(pileRealModel, gunRealModel.getGunCode()));
                    if (StringUtil.isNotEmpty(gunRealModel.getRunTime())) {
                        gunReal.setRunTime(gunRealModel.getRunTime() / 60);
                    }
                    return gunReal;
                }).collect(Collectors.toList()));
            }
        }
        return result;
    }

}
