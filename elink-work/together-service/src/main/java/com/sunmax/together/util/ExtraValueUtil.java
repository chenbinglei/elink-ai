package com.sunmax.together.util;

import com.alibaba.fastjson2.JSONObject;
import com.sunmax.common.dto.device.DeviceReaDto;
import com.sunmax.common.util.DoubleUtil;
import com.sunmax.common.util.StringUtil;
import com.sunmax.together.dto.monitor.centralMonitor.*;
import com.sunmax.together.dto.monitor.systemMonitor.SuperPileDto;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class ExtraValueUtil {

    /**
     * 获取设备扩展数据
     *
     * @param deviceReaDto 设备扩展字段数据
     * @return 转换后的设备数据
     */
    public static String getValue(DeviceReaDto deviceReaDto) {
        if (StringUtil.isNotEmpty(deviceReaDto.getValue())) {
            String value = deviceReaDto.getValue().toString();
            ExtraValueDto extraValueDto = JSONObject.parseObject(deviceReaDto.getExtraValue(), ExtraValueDto.class);
            List<EnumDto> enumDtoList = extraValueDto.getEnumArray();
            for (EnumDto enumDto : enumDtoList) {
                if (enumDto.getId().equals(value)) {
                    return enumDto.getName();
                }
            }
        }
        return null;
    }

    /**
     * 获取功率曲线列表
     *
     * @param curveList      曲线列表
     * @param chargePowerMap 充电功率map
     * @param powerCurve     功率曲线
     */
    public static void getPowerCurveList(List<CurveDto> curveList, Map<String, Double> chargePowerMap, CurveDto powerCurve) {
        powerCurve.setData(chargePowerMap.entrySet().stream().map(d -> {
            HistoryDto historyDto = new HistoryDto();
            historyDto.setTime(d.getKey());
            historyDto.setValue(DoubleUtil.getToDouble(d.getValue()));
            return historyDto;
        }).sorted(Comparator.comparing(HistoryDto::getTime)).collect(Collectors.toList()));
        curveList.add(powerCurve);
    }

    /**
     * 获取设备通信状态
     *
     * @param status 通信状态
     * @return 通信状态中文
     */
    public static String getTxStatus(Integer status) {
        String result;
        //设备状态 0-未注册 1-在线 2-故障 88-离线
        switch (status) {
            case 0:
                result = "未注册";
                break;
            case 1:
                result = "在线";
                break;
            case 2:
                result = "故障";
                break;
            case 88:
                result = "离线";
                break;
            default:
                result = "未知";
        }
        return result;
    }

    /**
     * 获取储能电池簇状态
     *
     * @param status 状态
     * @return 电池簇状态状态中文
     */
    public static String getBatteryStatus(Integer status) {
        String result = null;
        //电池簇状态 0-待机 1-禁充 2-禁放 3-故障 4-告警 5-充电 6-放电
        switch (status) {
            case 0:
                result = "待机";
                break;
            case 1:
                result = "禁充";
                break;
            case 2:
                result = "禁放";
                break;
            case 3:
                result = "故障";
                break;
            case 4:
                result = "告警";
                break;
            case 5:
                result = "充电";
                break;
            case 6:
                result = "放电";
                break;
        }
        return result;
    }

    /**
     * 获取电桩枪状态
     *
     * @param status 状态
     * @return 状态中文
     */
    public static String getPileGunStatus(Integer status) {
        //枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
        String result = null;
        switch (status) {
            case -1:
                result = "未知";
                break;
            case 1:
                result = "充电";
                break;
            case 2:
                result = "放电";
                break;
            case 3:
                result = "空闲";
                break;
            case 4:
                result = "占用";
                break;
            case 5:
                result = "故障";
                break;
            case 6:
                result = "离线";
                break;
            case 7:
                result = "未注册";
                break;
            case 8:
                result = "预约中";
                break;
        }
        return result;
    }

    /**
     * 计算超充枪状态码和状态名
     * 枪状态 -1-未知 1-充电 2-放电 3-空闲 4-占用 5-故障 6-离线 7-未注册 8-预约中
     *
     * @param txStatus     通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     * @param gunState     枪原始状态 0-空闲 1-准备中 2-充电中 3-充电结束 4-启动失败 5-预约中 6-故障
     * @param vehicleState 车辆连接状态 0-断开 1-半连接 2-已连接
     * @return [状态码, 状态名]
     */
    private static Object[] calcSuperPileGunStatus(Integer txStatus, Integer gunState, Integer vehicleState) {
        if (StringUtil.isEmpty(txStatus)) {
            return new Object[]{-1, "未知"};
        }
        switch (txStatus) {
            case 0:
                return new Object[]{7, "未注册"};
            case 1:
                //枪原始状态 0-空闲 1-准备中 2-充电中 3-充电结束 4-启动失败 5-预约中 6-故障
                if (StringUtil.isEmpty(gunState)) {
                    return new Object[]{-1, "未知"};
                }
                switch (gunState) {
                    case 0:
                    case 3:
                    case 4:
                        return vehicleState != null && vehicleState == 2
                                ? new Object[]{4, "占用"}
                                : new Object[]{3, "空闲"};
                    case 1:
                    case 2:
                        return new Object[]{1, "充电"};
                    case 5:
                        return new Object[]{8, "预约中"};
                    case 6:
                        return new Object[]{5, "故障"};
                    default:
                        return new Object[]{-1, "未知"};
                }
            case 2:
            case 3:
                return new Object[]{5, "故障"};
            case 88:
                return new Object[]{6, "离线"};
            default:
                return new Object[]{-1, "未知"};
        }
    }

    /**
     * 获取超充枪状态（SuperPileDto）
     *
     * @param txStatus     通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     * @param gunState     枪原始状态 0-空闲 1-准备中 2-充电中 3-充电结束 4-启动失败 5-预约中 6-故障
     * @param vehicleState 车辆连接状态 0-断开 1-半连接 2-已连接
     * @param result       SuperPileDto结果
     */
    public static void getSuperPileGunStatus(Integer txStatus, Integer gunState, Integer vehicleState, SuperPileDto result) {
        Object[] status = calcSuperPileGunStatus(txStatus, gunState, vehicleState);
        result.setRunState((Integer) status[0]);
        result.setRunStateName((String) status[1]);
    }

    /**
     * 获取超充枪状态（DeviceDto）
     *
     * @param txStatus     通信状态 0-未注册 1-在线 2-维护 3-故障 88-离线
     * @param gunState     枪原始状态 0-空闲 1-准备中 2-充电中 3-充电结束 4-启动失败 5-预约中 6-故障
     * @param vehicleState 车辆连接状态 0-断开 1-半连接 2-已连接
     * @param result       DeviceDto结果
     */
    public static void getSuperPileGunStatus(Integer txStatus, Integer gunState, Integer vehicleState, DeviceDto result) {
        Object[] status = calcSuperPileGunStatus(txStatus, gunState, vehicleState);
        result.setDeviceStatus((Integer) status[0]);
        result.setDeviceStatusName((String) status[1]);
    }

}
