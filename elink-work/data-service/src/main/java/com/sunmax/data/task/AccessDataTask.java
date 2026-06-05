package com.sunmax.data.task;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sunmax.common.config.redis.KeyUtil;
import com.sunmax.common.config.redis.RedisDeviceUtil;
import com.sunmax.common.config.redis.RedisGeneralUtil;
import com.sunmax.common.config.redis.RedisUtil;
import com.sunmax.common.enums.GeneralFieldEnum;
import com.sunmax.common.model.ChannelModel;
import com.sunmax.common.model.DeviceModel;
import com.sunmax.common.model.FunctionModel;
import com.sunmax.common.model.PointTableModel;
import com.sunmax.common.model.general.GatewayRealModel;
import com.sunmax.common.model.general.PileRealModel;
import com.sunmax.common.util.DateUtil;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.JsonUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.common.util.oss.FileUtil;
import com.sunmax.data.service.AccessDataService;
import com.sunmax.data.vo.DeviceDataVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunmax.common.util.DeviceUtil.dataTypeConvertFieldType;

@Configuration
@Slf4j
public class AccessDataTask {

    @Autowired
    private AccessDataService accessDataService;

    @Scheduled(cron = "0/30 * * * * ?")
    public void run() {
        //当前时间
        Long curTime = System.currentTimeMillis();
        //获取网关通用设备列表 转换成设备实时缓存
        Set<String> gatewayKeys = RedisUtil.keys(KeyUtil.GENERAL_GW_PREFIX + FileUtil.ASTERISK);
//        log.info("网关设备存储日志: " + gatewayKeys);
        gatewayKeys.forEach(terminalKey -> {
            String terminalCode = terminalKey.replace(KeyUtil.GENERAL_GW_PREFIX, FileUtil.separator);
            GatewayRealModel gatewayRealModel = RedisGeneralUtil.getGatewayRealModel(terminalCode);
            if (gatewayRealModel != null) {
                DeviceModel device = RedisDeviceUtil.getDevice(terminalCode);
                if (device != null && StringUtil.isNotEmpty(device.getTxStatus()) && device.getTxStatus() != 0) {
                    device.setTxStatus(gatewayRealModel.getDeviceStatus());
                    //设备状态获取不到 默认给离线状态
                    if (StringUtil.isEmpty(gatewayRealModel.getDeviceStatus())) {
                        device.setTxStatus(88);
                    }
                    if (gatewayRealModel.getChannelRealMap().containsKey(KeyUtil.MQTT)) {
                        GatewayRealModel.ChannelRealModel channelRealModel = gatewayRealModel.getChannelRealMap().get(KeyUtil.MQTT);
                        Map<String, PointTableModel> pointTableMap = channelRealModel.getPointTableMap();
                        if (device.getChannelMap().containsKey(KeyUtil.MQTT)) {
                            ChannelModel channelModel = device.getChannelMap().get(KeyUtil.MQTT);
                            channelModel.setTxStatus(channelRealModel.getTxStatus());
                            Map<String, PointTableModel> pointTableModelMap = Maps.newConcurrentMap();
                            channelModel.getPointTableMap().forEach((key, pointTable) -> {
                                if (pointTableMap.containsKey(key)) {
                                    PointTableModel pointTableModel = pointTableMap.get(key);
                                    pointTable.setDateTime(pointTableModel.getDateTime());
//                                log.info("网关编号:{},点号:{},数据类型:{},数据:{}", terminalCode, key, pointTable.getDataType(),pointTableModel.getDataValue());
                                    Object dataValue = !Objects.equals("INVALID", pointTableModel.getDataValue()) ? getPointDataValue(pointTable.getDataType(),
                                            pointTableModel.getDataValue(), pointTable.getCoefficient(), pointTable.getOffset()) : null;
                                    pointTable.setDataValue(getFunctionDataValue(pointTable.getDataType(), dataValue, pointTable.getAccuracy(), pointTable.getDataObject()));
                                }
                                pointTableModelMap.put(key, pointTable);
                            });
                            channelModel.setPointTableMap(pointTableModelMap);
                            device.getChannelMap().put(KeyUtil.MQTT, channelModel);
                        }
                    }
                    RedisDeviceUtil.setDevice(terminalCode, device);
                }
            }
        });

        //获取电桩通用设备列表 转换成设备实时缓存
        Set<String> pileKeys = RedisUtil.keys(KeyUtil.GENERAL_PILE_PREFIX + FileUtil.ASTERISK);
//        log.info("电桩设备存储日志: " + pileKeys);
        pileKeys.forEach(pileKey -> {
            String pileCode = pileKey.replace(KeyUtil.GENERAL_PILE_PREFIX, FileUtil.separator);
            PileRealModel pileRealModel = RedisGeneralUtil.getPileRealModel(pileCode);
            if (pileRealModel != null && StringUtil.isNotEmpty(pileRealModel.getWorkStatus())) {
                DeviceModel device = RedisDeviceUtil.getDevice(pileCode);
                if (device != null && StringUtil.isNotEmpty(device.getTxStatus()) && device.getTxStatus() != 0) {
                    device.setTxStatus(pileRealModel.getWorkStatus());

                    //电桩通用实体类转换jsonObject 用于方便获取数据
                    JSONObject pileData = JSONObject.parseObject(JSON.toJSONString(pileRealModel));
                    List<JSONObject> gunDataList = pileRealModel.getGunRealModelMap().values().stream()
                            .sorted(Comparator.comparing(PileRealModel.GunRealModel::getGunCode))
                            .map(s -> JSONObject.parseObject(JSON.toJSONString(s)))
                            .collect(Collectors.toList());
                    if (MapUtils.isNotEmpty(device.getFunctionMap())) {
                        for (Map.Entry<String, FunctionModel> function : device.getFunctionMap().entrySet()) {
                            String key = function.getKey();
                            FunctionModel value = function.getValue();
                            if (StringUtil.isEmpty(value.getFieldCode())) {
                                continue;
                            }
                            GeneralFieldEnum generalField = GeneralFieldEnum.getByFieldCode(value.getFieldCode());
                            if (generalField != null) {
                                int fieldType = generalField.getFieldType();
                                value.setDateTime(pileRealModel.getDateTime());
                                if (fieldType == 1) { //充电桩级
                                    value.setDataValue(getFunctionDataValue(value.getDataType(), pileData.getOrDefault(value.getFieldCode(), null), value.getAccuracy(), value.getDataObject()));
                                }
                                if (fieldType == 2 && value.getDataType() == 8) { //充电枪级 且数组
                                    value.setDataValue(Lists.newArrayList());
                                    if (CollectionUtils.isNotEmpty(gunDataList)) {
                                        String dataValue = JSON.toJSONString(gunDataList.stream().map(pileGun -> pileGun.getOrDefault(value.getFieldCode(), null)).collect(Collectors.toList()));
                                        value.setDataValue(getFunctionDataValue(value.getDataType(), dataValue, value.getAccuracy(), value.getDataObject()));
                                    }
                                }
                            }
                            device.getFunctionMap().put(key, value);
                        }
                    }
                    RedisDeviceUtil.setDevice(pileCode, device);
                }
            }
        });

        Set<String> keys = RedisUtil.keys(KeyUtil.DEVICE_KEY + FileUtil.ASTERISK);
        keys.forEach(deviceKey -> {
            DeviceModel deviceModel = JsonUtil.objectToEntity(RedisUtil.get(deviceKey), DeviceModel.class);
            Integer txStatus = deviceModel.getTxStatus();
            if (StringUtil.isNotEmpty(txStatus) && txStatus != 0 && txStatus != 88) {
                //直连功能点存储
                if (deviceModel.getFunctionMap() != null && !deviceModel.getFunctionMap().isEmpty()) {
                    List<DeviceDataVo> deviceDataVos = Lists.newArrayList();
                    deviceModel.getFunctionMap().forEach((key, value) -> {
                        if (StringUtil.isNotEmpty(value.getDataType()) && value.getDataType() != 0) {
                            DeviceDataVo deviceDataVo = new DeviceDataVo();
                            deviceDataVo.setFieldName(key);
                            deviceDataVo.setFieldType(dataTypeConvertFieldType(value.getDataType()));
                            deviceDataVo.setDateTime(value.getDateTime());
                            deviceDataVo.setDataValue(value.getDataValue());
                            deviceDataVos.add(deviceDataVo);
                        }
                    });
                    accessDataService.batchInsertTableData(deviceKey, curTime, deviceDataVos);
                }
                //网关功能点存储
                if (deviceModel.getChannelMap() != null && !deviceModel.getChannelMap().isEmpty()) {
                    Map<String, ChannelModel> channelMap = deviceModel.getChannelMap();
                    channelMap.forEach((key, value) -> {
                        String channelTableName = key + deviceModel.getDeviceNumber();
                        Map<String, PointTableModel> pointTableMap = value.getPointTableMap();
                        if (pointTableMap != null && !pointTableMap.isEmpty()) {
                            List<DeviceDataVo> deviceDataVos = Lists.newArrayList();
                            pointTableMap.forEach((dataId, pointTable) -> {
                                if (StringUtil.isNotEmpty(pointTable.getDataType()) && pointTable.getDataType() != 0) {
                                    DeviceDataVo deviceDataVo = new DeviceDataVo();
                                    deviceDataVo.setFieldName(dataId);
                                    deviceDataVo.setFieldType(dataTypeConvertFieldType(pointTable.getDataType()));
                                    deviceDataVo.setDateTime(pointTable.getDateTime());
                                    try {
                                        if (StringUtil.isNotEmpty(pointTable.getDateTime()) && DateUtil.strToLocalDateTime(deviceDataVo.getDateTime()).isAfter(LocalDateTime.now().minusMinutes(1))) {
                                            LocalDateTime dateTime = DateUtil.strToLocalDateTime(deviceDataVo.getDateTime());
                                            //缓存时间在当前时间一分钟之内
                                            if (dateTime.isAfter(LocalDateTime.now().minusMinutes(1))) {
                                                deviceDataVo.setDataValue(pointTable.getDataValue());
                                            }
                                        }
                                    } catch (Exception e) {
                                        log.error("网关数据解析异常", e);
                                    }
                                    deviceDataVos.add(deviceDataVo);
                                }
                            });
                            accessDataService.batchInsertTableData(channelTableName, curTime, deviceDataVos);
                        }
                    });
                }
            }
        });
    }

    /**
     * 根据数据类型、数据值、精度和数据对象，处理并返回相应的数据值。
     *
     * @param dataType   数据类型标识符，用于确定如何解析数据值。
     *                   1-int32, 2-int64, 3-float, 4-double, 5-enum, 6-bool, 8-array
     * @param dataValue  原始数据值，可能为字符串或其他类型
     * @param accuracy   精度控制参数，决定小数点后保留的位数（1表示整数，2表示一位小数，以此类推）
     * @param dataObject 数据对象描述信息，用于数组类型数据的进一步处理
     * @return 处理后的数据值，根据数据类型和精度进行转换；若出现异常则返回原始值
     */
    private Object getFunctionDataValue(Integer dataType, Object dataValue, Integer accuracy, String dataObject) {
        // 参数校验：防止空值或非法值导致异常
        if (StringUtil.isEmpty(accuracy) || StringUtil.isEmpty(dataType) || StringUtil.isEmpty(dataValue)) {
            return dataValue;
        }

        // 根据精度计算小数点位置
        Integer radixPoint = null;
        if (StringUtil.isNotEmpty(accuracy)) {
            radixPoint = accuracy - 1;
        }

        String dataValueStr = String.valueOf(dataValue);
        Object result;

        try {
            switch (dataType) {
                case 1: // int32
                case 2: // int64
                case 5: // enum
                case 6: // bool
                    result = (int) Double.parseDouble(dataValueStr);
                    break;
                case 3: // float
                case 4: // double
                    if (radixPoint != null) {
                        result = DoubleUtil.getToDouble(Double.parseDouble(dataValueStr), radixPoint);
                    } else {
                        result = Double.parseDouble(dataValueStr);
                    }
                    break;
                case 8: // array
                    result = processArrayData(dataValueStr, dataObject, radixPoint);
                    break;
                default:
                    result = dataValue; // 默认返回原始值
            }
        } catch (NumberFormatException e) {
            // 记录异常并返回默认值
            log.warn("Failed to parse dataValue: {}", dataValueStr, e);
            result = dataValue;
        }

        return result;
    }


    /**
     * 处理数组数据，根据数据类型和小数点位数对数组中的元素进行格式化。
     *
     * @param dataValueStr 原始数据字符串，通常为JSON数组格式
     * @param dataObject   包含数据类型信息的JSON对象字符串，用于确定元素类型
     * @param radixPoint   小数点位数，用于格式化浮点数或双精度数
     * @return 处理后的数据字符串，如果处理失败则返回原始数据字符串
     */
    private Object processArrayData(String dataValueStr, String dataObject, Integer radixPoint) {
        // 如果dataObject为空，直接返回原始数据字符串
        if (StringUtil.isEmpty(dataObject)) {
            return dataValueStr;
        }

        try {
            // 解析dataObject为JSONObject，获取元素类型
            JSONObject jsonObject = JSON.parseObject(dataObject);
            if (jsonObject.containsKey("elementType")) {
                Integer elementType = jsonObject.getInteger("elementType");
                switch (elementType) {
                    case 3: // float类型
                    case 4: // double类型
                        // 如果指定了小数点位数，则对数组中的每个元素进行格式化
                        if (radixPoint != null) {
                            return JSON.toJSONString(JSON.parseArray(dataValueStr).stream().map(item -> {
                                // 对非空元素按指定小数点位数进行格式化
                                if (StringUtil.isNotEmpty(item) && StringUtil.isNotEmpty(radixPoint)) {
                                    return DoubleUtil.getToDouble(Double.parseDouble(String.valueOf(item)), radixPoint);
                                }
                                return item;
                            }).collect(Collectors.toList()));
                        }
                        break;
                    default:
                        // 其他类型直接返回原始数据字符串
                        return dataValueStr;
                }
            }
        } catch (Exception e) {
            // 记录处理失败的日志信息
            log.warn("Failed to process array data: {}", dataValueStr, e);
        }

        // 默认返回原始数据字符串
        return dataValueStr;
    }


    /**
     * 根据数据类型对数据值进行处理，并应用系数和偏移量。
     *
     * @param dataType    数据类型标识符，用于确定如何处理数据值。
     *                    支持的类型包括：
     *                    - 1: int32
     *                    - 2: int64
     *                    - 3: float
     *                    - 4: double
     *                    - 5: enum
     *                    - 6: bool
     *                    - 8: array
     * @param dataValue   原始数据值，可以是任意类型，但最终会被转换为字符串处理。
     * @param coefficient 系数，用于对数值型数据进行缩放。
     * @param offset      偏移量，用于对数值型数据进行平移。
     * @return 处理后的数据值，具体类型取决于数据类型和处理逻辑。
     * @throws IllegalArgumentException 如果 dataType 为空或不支持，或者 dataValue 的格式无效。
     */
    private Object getPointDataValue(Integer dataType, Object dataValue, Float coefficient, Integer offset) {
        // 初始校验：dataValue 为空直接返回
        if (StringUtil.isEmpty(dataValue)) {
            return dataValue;
        }

        // 校验 dataType 是否有效
        if (StringUtil.isEmpty(dataType)) {
            throw new IllegalArgumentException("dataType cannot be null");
        }

        String dataValueStr = String.valueOf(dataValue);
        try {
            switch (dataType) {
                case 1: // int32
                case 2: // int64
                case 5: // enum
                case 6: // bool
                    // 对整数、枚举和布尔类型应用系数和偏移量，并按整数规则处理
                    return applyCoefficientAndOffset(dataValueStr, coefficient, offset, true);
                case 3: // float
                case 4: // double
                    // 对浮点数类型应用系数和偏移量，并按浮点数规则处理
                    return applyCoefficientAndOffset(dataValueStr, coefficient, offset, false);
                case 8: // array
                    // 对数组类型进行特殊处理
                    return processArrayData(dataValueStr, coefficient, offset);
                default:
                    // 不支持的数据类型抛出异常
                    throw new IllegalArgumentException("Unsupported dataType: " + dataType);
            }
        } catch (NumberFormatException e) {
            // 捕获数字格式异常并包装为更具体的业务异常
            throw new IllegalArgumentException("Invalid numeric format in dataValue: " + dataValueStr, e);
        }
    }


    /**
     * 应用系数和偏移量计算
     *
     * @param value       原始值字符串
     * @param coefficient 系数
     * @param offset      偏移量
     * @param isInteger   是否强制转为整数
     * @return 计算后的结果
     */
    private Object applyCoefficientAndOffset(String value, Float coefficient, Integer offset, boolean isInteger) {
        double result = Double.parseDouble(value);

        if (StringUtil.isNotEmpty(coefficient)) {
            result *= coefficient;
        }
        if (StringUtil.isNotEmpty(offset)) {
            result += offset;
        }

        return isInteger ? (int) result : result;
    }

    /**
     * 处理数组类型数据
     *
     * @param dataValueStr 原始数组字符串，可能为JSON格式或普通字符串
     * @param coefficient  系数，用于对数组元素进行数值变换
     * @param offset       偏移量，用于对数组元素进行数值变换
     * @return 处理后的数组字符串，以JSON格式返回
     */
    private String processArrayData(String dataValueStr, Float coefficient, Integer offset) {
        List<String> dataValueList = new ArrayList<>();

        // 解析数组内容：判断是否为合法的数组格式并提取元素
        if (!dataValueStr.contains(FileUtil.LEFT_SQUARE) || !dataValueStr.contains(FileUtil.RIGHT_SQUARE)) {
            dataValueList.add(dataValueStr.replace(FileUtil.LEFT_SQUARE, FileUtil.separator).replace(FileUtil.RIGHT_SQUARE, FileUtil.separator));
        } else {
            try {
                dataValueList.addAll(JSON.parseArray(dataValueStr, String.class));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid JSON array format: " + dataValueStr, e);
            }
        }

        // 对每个元素应用系数和偏移量：遍历数组元素并进行数值处理
        List<Object> processedList = dataValueList.stream().map(value -> {
            if (StringUtil.isNotEmpty(value)) {
                return applyCoefficientAndOffset(value, coefficient, offset, false);
            }
            return value;
        }).collect(Collectors.toList());

        // 将处理后的列表转换为JSON字符串并返回
        return JSON.toJSONString(processedList);
    }

}
