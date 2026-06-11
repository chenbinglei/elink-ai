package com.sunmax.protocol.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.constant.IEGConstant;
import com.sunmax.common.dto.protocol.ControlResponseDto;
import com.sunmax.common.dto.protocol.StationInfoDto;
import com.sunmax.common.util.ResponseResult;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.vo.protocol.ControlResponseVo;
import com.sunmax.common.vo.protocol.ControlStopVo;
import com.sunmax.protocol.constant.SMV2gConstant;
import com.sunmax.protocol.dao.DispatchRecordDao;
import com.sunmax.protocol.demand.GatewayDemand;
import com.sunmax.protocol.entity.DispatchRecordEntity;
import com.sunmax.protocol.model.GatewayDemandModel;
import com.sunmax.protocol.service.DispatchService;
import com.sunmax.protocol.task.ControlTask;
import com.sunmax.protocol.util.MqttTopicUtil;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DispatchServiceImpl implements DispatchService {

    @Autowired
    private DispatchRecordDao dispatchRecordDao;

    //定义网关站点信息map
    public static Map<String, StationInfoDto> stationInfoMap = Maps.newConcurrentMap();

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<List<StationInfoDto>> queryStationInfo(Long stationId) {
        //返回的集合
        List<StationInfoDto> resultList = Lists.newArrayList();
        String deviceCode = this.getGatewayCode(stationId);
        if (StringUtil.isNotEmpty(deviceCode) && stationInfoMap.containsKey(deviceCode)) {
            StationInfoDto stationInfo = stationInfoMap.get(deviceCode);
            stationInfo.setStationId(stationId);
            resultList.add(stationInfo);
        }
        //添加调控记录数据
        this.saveDispatchRecord(stationId, deviceCode, 1, stationId, resultList);
        return ResponseResult.ok(resultList);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<ControlResponseDto> demandControlResponse(ControlResponseVo controlResponseVo) {
        ControlResponseDto controlResponse = new ControlResponseDto();
        controlResponse.setStationId(controlResponseVo.getStationId());
        controlResponse.setSjbh(controlResponseVo.getControlInfo().getSjbh());
        controlResponse.setResult(1);
        String deviceCode = this.getGatewayCode(controlResponseVo.getStationId());
        if (StringUtil.isNotEmpty(deviceCode)) {
            //创建需求key
            String keyId = deviceCode + IEGConstant.Param.GATEWAY_PEAK_VALLEY_CONTROL_SET;
            //创建控制需求
            GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);
            MqttTopicUtil.sendControlResponseIssuedTopic(deviceCode, controlResponseVo.getControlInfo());
            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 10);
            //移除需求
            GatewayDemand.deleteDemand(keyId);
            //成功返回状态
            if (MapUtils.isNotEmpty(asyncTaskResult) && StringUtil.isNotEmpty(asyncTaskResult.getString("recCode"))) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                    case -1:
                        //添加调控记录
                        this.saveDispatchRecord(controlResponseVo.getStationId(), deviceCode, 2, controlResponseVo, controlResponse);
                        return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                    case 0:
                        controlResponse.setResult(asyncTaskResult.getInteger("recMsg"));
                        //添加调控记录
                        this.saveDispatchRecord(controlResponseVo.getStationId(), deviceCode, 2, controlResponseVo, controlResponse);
                        return ResponseResult.ok(controlResponse);
                    case 1:
                        //添加调控记录
                        this.saveDispatchRecord(controlResponseVo.getStationId(), deviceCode, 2, controlResponseVo, controlResponse);
                        return ResponseResult.error(SMV2gConstant.StatusFailed);
                    case 255:
                        //添加调控记录
                        this.saveDispatchRecord(controlResponseVo.getStationId(), deviceCode, 2, controlResponseVo, controlResponse);
                        return ResponseResult.error(SMV2gConstant.StatusOther);
                }
            }
            //添加调控记录
            this.saveDispatchRecord(controlResponseVo.getStationId(), deviceCode, 2, controlResponseVo, controlResponse);
            return ResponseResult.paramError(ResponseResult.FAIL);
        }
        //添加调控记录数据
        this.saveDispatchRecord(controlResponseVo.getStationId(), deviceCode, 2, controlResponseVo, controlResponse);
        return new ResponseResult<>(ResponseResult.CodeStatus.ACCESS_FAIL, "未获取到该站点下面的网关", controlResponse);
    }

    @Override
    @Transactional(transactionManager = "transactionManager", rollbackFor = Exception.class)
    public ResponseResult<ControlResponseDto> demandControlStop(ControlStopVo controlStopVo) {
        //返回的控制响应终止参数
        ControlResponseDto controlResponse = new ControlResponseDto();
        controlResponse.setStationId(controlStopVo.getStationId());
        controlResponse.setSjbh(controlStopVo.getSjbh());
        controlResponse.setResult(1);
        //根据站点id查询网关编号
        String deviceCode = this.getGatewayCode(controlStopVo.getStationId());
        if (StringUtil.isNotEmpty(deviceCode)) {
            //创建需求key
            String keyId = deviceCode + IEGConstant.Param.GATEWAY_PEAK_VALLEY_CONTROL_STOP;

            //创建控制响应终止需求
            GatewayDemandModel demandModel = GatewayDemand.createDemand(keyId, deviceCode);

            //下发数据
            MqttTopicUtil.sendControlStopIssuedTopic(deviceCode, controlStopVo);

            //开启查询线程等待返回
            JSONObject asyncTaskResult = ControlTask.ctrGatewayTaskFunc(demandModel, 10);

            //移除需求
            GatewayDemand.deleteDemand(keyId);

            //成功返回状态
            if (MapUtils.isNotEmpty(asyncTaskResult) && StringUtil.isNotEmpty(asyncTaskResult.getString("recCode"))) {
                Integer recCode = asyncTaskResult.getInteger("recCode");
                switch (recCode) { //执行结果 -1-超时 0-执行成功 1-执行失败 255-其他原因
                    case -1:
                        //添加调控记录
                        this.saveDispatchRecord(controlStopVo.getStationId(), deviceCode, 3, controlStopVo, controlResponse);
                        return ResponseResult.error(SMV2gConstant.StatusTimeOut);
                    case 0:
                        controlResponse.setResult(asyncTaskResult.getInteger("recMsg"));
                        //添加调控记录
                        this.saveDispatchRecord(controlStopVo.getStationId(), deviceCode, 3, controlStopVo, controlResponse);
                        return ResponseResult.ok(controlResponse);
                    case 1:
                        //添加调控记录
                        this.saveDispatchRecord(controlStopVo.getStationId(), deviceCode, 3, controlStopVo, controlResponse);
                        return ResponseResult.error(SMV2gConstant.StatusFailed);
                    case 255:
                        //添加调控记录
                        this.saveDispatchRecord(controlStopVo.getStationId(), deviceCode, 3, controlStopVo, controlResponse);
                        return ResponseResult.error(SMV2gConstant.StatusOther);
                }
            }
            //添加调控记录
            this.saveDispatchRecord(controlStopVo.getStationId(), deviceCode, 3, controlStopVo, controlResponse);
            return ResponseResult.paramError(ResponseResult.FAIL);
        }
        //添加调控记录数据
        this.saveDispatchRecord(controlStopVo.getStationId(), deviceCode, 3, controlStopVo, controlResponse);
        return new ResponseResult<>(ResponseResult.CodeStatus.ACCESS_FAIL, "未获取到该站点下面的网关", controlResponse);
    }



    //根据站点记录id获取网关编号
    private String getGatewayCode(Long siteRecordId) {
        String deviceCode = null;
        //TODO 后期调整
//        Map<Long, List<ModelDataRecordDto>> siteDataRecordMap = accessService.findDataRecordListBySiteRecordIds(Collections.singletonList(siteRecordId)).getData();
//        if (siteDataRecordMap != null && !siteDataRecordMap.isEmpty()) {
//            List<ModelDataRecordDto> dataRecordList = siteDataRecordMap.get(siteRecordId).stream().filter(s -> Objects.equals(s.getTypeId().intValue(),
//                    ModelIdEnum.COMMUNICATION_EQUIPMENT.getCode())).collect(Collectors.toList());
//            if (CollectionUtils.isNotEmpty(dataRecordList)) {
//                deviceCode = dataRecordList.get(0).getDataCode();
//            }
//        }
        return deviceCode;
    }

    private void saveDispatchRecord(Long stationId, String gatewayCode, Integer type, Object requestData, Object responseData) {
        //添加调控记录数据
        dispatchRecordDao.save(DispatchRecordEntity.builder()
                .stationId(stationId)
                .gatewayCode(gatewayCode)
                .type(type)
                .requestData(JSON.toJSONString(requestData))
                .responseData(JSON.toJSONString(responseData))
                .createTime(LocalDateTime.now()).build());
    }

}
