package com.sunmax.common.util;

public class WebMqttUtil {

    /**
     * 转换电桩状态
     *
     * @return 电桩工作状态 -1-未知 1-在线 2-维护 3-故障 88-离线
     */
    public static Integer convertPileState(Integer workState) {
        switch (workState) {
            case 0:
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 88:
                return 88;
        }
        return -1;
    }

    /**
     * 转换电枪状态
     *
     * @param workState    桩工作状态
     * @param gunWorkState 枪工作状态 0 空闲；1 充电准备; 2 充电中；3 充电完成；4 放电准备; 5 放电中；6 放电完成；7 预约；8 暂停；255 故障
     * @param vehicleState 车辆连接状态 0 未连接；1 半连接；2 连接；
     * @return 枪状态 -1-未知 0-空闲 1-充电准备 2-充电中 3-占用(已连接) 4-放电准备 5-放电中 7-预约 8-暂停 88-离线 255-故障
     */
    public static Integer convertGunState(Integer workState, Integer gunWorkState, Integer vehicleState) {
        if (workState == 2 || workState == 3) { //桩维护或故障
            return 255;
        }
        if (workState == 88) { //桩离线
            return 88;
        }
        switch (gunWorkState) {
            case 0:
            case 3:
            case 6:
                if (vehicleState == 2) {
                    return 3;
                } else {
                    return 0;
                }
            case 1:
            case 2:
            case 4:
            case 5:
            case 7:
            case 8:
            case 255:
                return gunWorkState;
        }
        return -1;
    }

    /**
     * 功能点类型转换
     *
     * @param appType 数值类型 0-无 1-遥测(浮点型) 2-遥信(整形) 3-遥控(暂无) 4-遥调(暂无)
     * @return 功能点类型 1-int32(整数) 2-int64(长整数型) 3-float(单精度浮点型) 4-double(双精度浮点型) 5-enum(枚举) 6-bool(布尔) 7-string(字符串) 8-array(数组) 9-date(时间)
     */
    public static Integer convertFunction(Integer appType) {
        switch (appType) {
            case 0:
                return 0;
            case 1:
                return 4;
            case 2:
                return 2;
            case 3:
            case 4:
                return null;
        }
        return null;
    }

}
