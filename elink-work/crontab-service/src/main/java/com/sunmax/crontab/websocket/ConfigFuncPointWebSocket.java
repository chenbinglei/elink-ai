package com.sunmax.crontab.websocket;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.sunmax.common.constant.ConfigDataTypeConstant;
import com.sunmax.common.dto.device.DeviceBasicInfoDto;
import com.sunmax.common.dto.device.ModelFunctionListDto;
import com.sunmax.common.dto.operate.ConfigurationResultDto;
import com.sunmax.common.model.RealDataModel;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.statics.DeviceParamVo;
import com.sunmax.crontab.util.CrontabCommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import static com.sunmax.crontab.config.WebSocketConfig.deviceService;

/**
 * 组态功能点数据推送
 */
@ServerEndpoint("/configFuncPointWebSocket/{clientId}/{siteId}/{deviceIds}/{functionLogos}")
@Component
@Slf4j
public class ConfigFuncPointWebSocket {

    //{userId}/{siteId}/{deviceIds}/{functionLogos}

    //记录当前的记录数
    private static int onlineCount = 0;

    //存放每个客户端对应的webSocket对象
    private static final CopyOnWriteArraySet<ConfigFuncPointWebSocket> webSocketClients = new CopyOnWriteArraySet<>();

    //与客户端连接的会话，需要通过它来给客户端发送数据
    private Session session;

    //用户id -> 当前会话
    private static final Map<String, ConfigFuncPointWebSocket> webSocketMap = new ConcurrentHashMap<>();

    //客户端id -> 设备参数
    private static final Map<String, Map<String, String>> clientDataMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session sessionId, @PathParam("clientId") String clientId, @PathParam("siteId") String siteId,
                       @PathParam("deviceIds") String deviceIds, @PathParam("functionLogos") String functionLogos) {
        System.out.println("组态功能点实时数据新开启了一个webSocket连接" + sessionId.getId());

        //加入到set中
        session = sessionId;
        webSocketClients.add(this);
        webSocketMap.put(clientId, this);
        ResponseResult<ConfigurationResultDto> resultList = new ResponseResult<>();
        if (StringUtil.isNotEmpty(siteId) || StringUtil.isNotEmpty(deviceIds)) {
            Map<String, String> hashMap = Maps.newHashMap();
            hashMap.put("siteId", siteId);
            hashMap.put("deviceIds", deviceIds);
            hashMap.put("functionLogos", functionLogos);
            clientDataMap.put(clientId, hashMap);
            resultList = getConfigFuncPointList(hashMap);
        }
        try {
            //发送消息
            this.sendMessage(JSON.toJSONString(resultList));
        } catch (IOException ie) {
            log.error("组态功能点实时数据推送消息失败", ie);
        }
        addOnlineCount();
        System.out.println("有新的连接加入！当前组态功能点实时数据在线人数为：" + getOnlineCount() + " ");
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
        System.out.println("组态功能点实时数据有一连接关闭" + sessionId.getId());
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
        /*for (ConfigFuncPointWebSocket item : webSocketClients) {
            try {
                item.sendMessage(DateUtil.localDateTimeToStr(LocalDateTime.now()));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }*/
        webSocketMap.forEach((key, value) -> {
            ResponseResult<ConfigurationResultDto> resultList = getConfigFuncPointList(clientDataMap.get(key));
            try {
                value.sendMessage(JSON.toJSONString(resultList));
            } catch (IOException e) {
                log.error("推送组态功能点实时实时数据失败", e);
            }
        });

    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        ConfigFuncPointWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        ConfigFuncPointWebSocket.onlineCount--;
    }

    public static ResponseResult<ConfigurationResultDto> getConfigFuncPointList(Map<String, String> hashMap) {
        //返回的集合
        ConfigurationResultDto result = new ConfigurationResultDto();
        result.setDesc("sunos平台推送设备功能点数据接口");
        //站点id
        String siteId = hashMap.get("siteId");
//        String siteId = hashMap.getOrDefault("siteId", null);
        //多个设备id
        String deviceIds = hashMap.get("deviceIds");
//        String deviceIds = hashMap.getOrDefault("deviceIds", null);
        //多个功能点标识
        String functionLogos = hashMap.get("functionLogos");
//        String functionLogos = hashMap.getOrDefault("functionLogos", null);
        if (StringUtil.isNotEmpty(siteId) && StringUtil.isNotEmpty(deviceIds)) {
            return ResponseResult.error("查询失败，站点id和设备id参数只能同时存在一个", result);
        }
        try {
            //是否传了功能点标识字段
            boolean isFlag;
            List<DeviceBasicInfoDto> deviceBasicInfoDtoList = Lists.newArrayList();
            //根据站点id查询站点下所有设备信息
            if (StringUtil.isNotEmpty(siteId)) {
                ResponseResult<Map<String, List<DeviceBasicInfoDto>>> deviceBasicInfoBySiteIds = deviceService.findDeviceBasicInfoBySiteIds(Collections.singletonList(siteId), null);
                if (deviceBasicInfoBySiteIds.isSuccess() && !deviceBasicInfoBySiteIds.getData().isEmpty()) {
                    deviceBasicInfoDtoList = deviceBasicInfoBySiteIds.getData().get(siteId);
                }
            } else if (StringUtil.isNotEmpty(deviceIds)) {
                ResponseResult<Map<String, DeviceBasicInfoDto>> deviceBasicInfoByIds = deviceService.findDeviceBasicInfoByIds(Arrays.stream(deviceIds.split(",")).map(String::trim).collect(Collectors.toList()));
                if (deviceBasicInfoByIds.isSuccess() && !deviceBasicInfoByIds.getData().isEmpty()) {
                    Map<String, DeviceBasicInfoDto> deviceBasicInfoDtoMap = deviceBasicInfoByIds.getData();
                    deviceBasicInfoDtoList = new ArrayList<>(deviceBasicInfoDtoMap.values());
                }
            }
            if (CollectionUtils.isNotEmpty(deviceBasicInfoDtoList)) {
                //模型关联功能点数据
                Map<String, List<ModelFunctionListDto>> modeFunctionMap = Maps.newHashMap();
                //获取多个模型id
                List<String> modelIdList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getModelId).filter(StringUtil::isNotEmpty).distinct().collect(Collectors.toList());
                ResponseResult<Map<String, List<ModelFunctionListDto>>> modelFunctionListByModelIds = deviceService.findModelFunctionListByModelIds(modelIdList);
                if (modelFunctionListByModelIds.isSuccess() && !modelFunctionListByModelIds.getData().isEmpty()) {
                    modeFunctionMap = modelFunctionListByModelIds.getData();
                    //判断有没有传功能点标识，如果没传则查询设备下关联的所有功能点
                    if (StringUtil.isEmpty(functionLogos)) {
                        isFlag = false;
                        functionLogos = modeFunctionMap.values().stream().flatMap(Collection::stream).map(ModelFunctionListDto::getFunctionLogo).distinct().collect(Collectors.joining(","));
                    } else {
                        isFlag = true;
                    }
                } else {
                    isFlag = true;
                }
                //根据多个设备id和功能点标识，查询功能点数据
                Set<String> deviceIdList = deviceBasicInfoDtoList.stream().map(DeviceBasicInfoDto::getId).collect(Collectors.toSet());
                ResponseResult<Map<String, Map<String, RealDataModel>>> deviceFunctionsRealData = deviceService.getDeviceFunctionsRealDataByIds(deviceIdList, functionLogos);
                //获取设备通信状态
                Map<String, Integer> deviceTxStateMap = deviceService.findDeviceTxStatusById(deviceBasicInfoDtoList.stream().collect(Collectors.toMap(DeviceBasicInfoDto::getId,
                        d -> StringUtil.isEmpty(d.getDeviceNumber()) ? "" : d.getDeviceNumber(), (k1, k2) -> k1))).getData();
                //循环设备组装数据
                Map<String, ConfigurationResultDto.FieldData> dataMap = Maps.newHashMap();
                Map<String, List<ModelFunctionListDto>> finalModeFunctionMap = modeFunctionMap;
                String finalFunctionLogos = functionLogos;
                deviceBasicInfoDtoList.forEach(deviceBasicInfoDto -> {
                    //存储设备级数据信息
                    ConfigurationResultDto.FieldData deviceFieldData = new ConfigurationResultDto.FieldData();
                    deviceFieldData.setFieldType(ConfigDataTypeConstant.ARRAYLIST);
                    deviceFieldData.setChName(deviceBasicInfoDto.getDeviceName());
                    deviceFieldData.setEnName(deviceBasicInfoDto.getId());
                    //存储功能点数据
                    List<ConfigurationResultDto.FieldData> fieldDataList = Lists.newArrayList();
                    //生成设备通信状态
                    fieldDataList.add(generateDeviceTxState(deviceBasicInfoDto, deviceTxStateMap));
                    if (isFlag) {
                        Arrays.stream(finalFunctionLogos.split(",")).forEach(functionLogo -> {
                            //获取当前设备当前功能点数据
                            if (deviceFunctionsRealData.isSuccess() && !deviceFunctionsRealData.getData().isEmpty()
                                    && deviceFunctionsRealData.getData().containsKey(deviceBasicInfoDto.getId())) {
                                Map<String, RealDataModel> realDataMap = deviceFunctionsRealData.getData().get(deviceBasicInfoDto.getId());
                                if (realDataMap.containsKey(functionLogo)) {
                                    RealDataModel realDataModel = realDataMap.get(functionLogo);
                                    //判断是否有数据，如果有则返回
                                    if (StringUtil.isNotEmpty(realDataModel) && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                                        //获取模型下关联的功能点信息
                                        if (!finalModeFunctionMap.isEmpty() && finalModeFunctionMap.containsKey(deviceBasicInfoDto.getModelId())) {
                                            List<ModelFunctionListDto> modelFunctionListDtos = finalModeFunctionMap.get(deviceBasicInfoDto.getModelId());
                                            Map<String, ModelFunctionListDto> modelFuncationMap = modelFunctionListDtos.stream().collect(Collectors.toMap(ModelFunctionListDto::getFunctionLogo, modelFunctionListDto -> modelFunctionListDto, (k1, k2) -> k1));
                                            //获取功能点信息
                                            if (modelFuncationMap.containsKey(functionLogo)) {
                                                //组装功能点信息数据
                                                ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                                                ModelFunctionListDto modelFunctionListDto = modelFuncationMap.get(functionLogo);
                                                fieldData.setChName(modelFunctionListDto.getFunctionName());
                                                fieldData.setEnName(functionLogo);
                                                fieldData.setFieldDesc(modelFunctionListDto.getFunctionDesc());
                                                Object dataValue = realDataModel.getDataValue();
                                                if (deviceTxStateMap.containsKey(deviceBasicInfoDto.getId()) && deviceTxStateMap.get(deviceBasicInfoDto.getId()) == 88) {
                                                    dataValue = null;
                                                }
                                                fieldData.setFieldData(dataValue);
                                                fieldData.setFieldType(CrontabCommonUtil.dataTypeConvertStr(realDataModel.getDataType()));
                                                fieldDataList.add(fieldData);
                                            }
                                        }

                                    }
                                }
                            }
                        });
                    } else {
                        //获取模型下关联的功能点信息
                        if (!finalModeFunctionMap.isEmpty() && finalModeFunctionMap.containsKey(deviceBasicInfoDto.getModelId())) {
                            List<ModelFunctionListDto> modelFunctionListDtos = finalModeFunctionMap.get(deviceBasicInfoDto.getModelId());
                            //循环模型功能标识，获取功能点数据
                            modelFunctionListDtos.forEach(modelFunctionListDto -> {
                                //获取当前设备当前功能点数据
                                if (deviceFunctionsRealData.isSuccess() && !deviceFunctionsRealData.getData().isEmpty()
                                        && deviceFunctionsRealData.getData().containsKey(deviceBasicInfoDto.getId())) {
                                    Map<String, RealDataModel> realDataMap = deviceFunctionsRealData.getData().get(deviceBasicInfoDto.getId());
                                    if (realDataMap.containsKey(modelFunctionListDto.getFunctionLogo())) {
                                        RealDataModel realDataModel = realDataMap.get(modelFunctionListDto.getFunctionLogo());
                                        //判断是否有数据，如果有则返回
                                        if (StringUtil.isNotEmpty(realDataModel) && StringUtil.isNotEmpty(realDataModel.getDataValue())) {
                                            ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
                                            fieldData.setChName(modelFunctionListDto.getFunctionName());
                                            fieldData.setEnName(modelFunctionListDto.getFunctionLogo());
                                            fieldData.setFieldDesc(modelFunctionListDto.getFunctionDesc());
                                            Object dataValue = realDataModel.getDataValue();
                                            if (deviceTxStateMap.containsKey(deviceBasicInfoDto.getId()) && deviceTxStateMap.get(deviceBasicInfoDto.getId()) == 88) {
                                                dataValue = null;
                                            }
                                            fieldData.setFieldData(dataValue);
                                            fieldData.setFieldType(CrontabCommonUtil.dataTypeConvertStr(realDataModel.getDataType()));
                                            fieldDataList.add(fieldData);
                                        }
                                    }
                                }
                            });
                        }
                    }
                    deviceFieldData.setFieldData(fieldDataList);
                    dataMap.put(deviceBasicInfoDto.getId(), deviceFieldData);
                });
                result.setDataMap(dataMap);
            }
        } catch (Exception e) {
            log.error("查询功能点实时数据失败", e);
            return ResponseResult.error("程序出现异常", result);
        }

        return ResponseResult.ok(result);
    }

    /**
     * 生成设备通信状态对象
     * @param deviceBasicInfoDto
     * @param deviceTxStateMap
     * @return
     */
    private static ConfigurationResultDto.FieldData generateDeviceTxState(DeviceBasicInfoDto deviceBasicInfoDto, Map<String, Integer> deviceTxStateMap) {
        ConfigurationResultDto.FieldData fieldData = new ConfigurationResultDto.FieldData();
        fieldData.setChName("通信状态");
        fieldData.setEnName(DeviceParamVo.TX_STATUS);
        if (deviceTxStateMap.containsKey(deviceBasicInfoDto.getId())) {
            Integer txState = deviceTxStateMap.get(deviceBasicInfoDto.getId());
            fieldData.setFieldData(txState);
        } else {
            fieldData.setFieldData(88);
        }
        fieldData.setFieldType(ConfigDataTypeConstant.INTEGER);
        return fieldData;
    }
}

