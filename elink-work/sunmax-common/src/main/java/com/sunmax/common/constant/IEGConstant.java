package com.sunmax.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xt
 * @brief 智能融合终端协议常量
 * @date 2021/6/28 10:51
 */
public class IEGConstant {

    public static final int P_SMIGP = 0x1;//网关协议起始域

    /**
     * 终端类型常量
     */
    public static class Type {

        /**
         * 电桩数据
         */
        public static final String CMD_REPORT_DATA = "CMD_REPORTDATA";

        /**
         * 电桩响应
         */
        public static final String CMD_SERVICE = "CMD_SERVICE";

        /**
         * 上线请求(1900)
         */
        public static final String EVENT_LINK_UP = "EVENT_LINKUP";

        /**
         * 下线请求
         */
        public static final String EVENT_LINK_DOWN = "EVENT_LINKDOWN";

        /**
         * 费率请求(及响应)(1000)
         */
        public static final String EVENT_RATE_REQ = "EVENT_RATEREQ";

        /**
         * 更新子设备状态(1100)
         */
        public static final String CMD_TO_PO_UPDATE = "CMD_TOPO_UPDATE";

        /**
         * 设备心跳(保活消息 0006)
         */
        public static final String EVENT_HEARTBEAT = "EVENT_HEARTBEAT";

        /**
         * 请求子设备状态及响应(120000)
         */
        public static final String CMD_TO_PO_CALL_STATE = "CMD_TOPO_CALLSTATE";

        /**
         * 电桩状态(2000)
         */
        public static final String CMD_REPORT_PILE_STATE = "CMD_REPORT_PILE_STATE";

        /**
         * 电桩数据(2002)
         */
        public static final String CMD_REPORT_PILE_DATA = "CMD_REPORT_PILE_DATA";

        /**
         * 电桩故障(2001)
         */
        public static final String CMD_REPORT_PILE_FAULT = "CMD_REPORT_PILE_FAULT";

        /**
         * 鉴权请求(3000)
         */
        public static final String EVENT_AUTHENTICATION_REQUEST = "EVENT_AUTHENTICATION_REQUEST";

        /**
         * 鉴权响应(3001)
         */
        public static final String EVENT_AUTHENTICATION_RESPONSE = "EVENT_AUTHENTICATION_RESPONSE";

        /**
         * 策略设置(3002)
         */
        public static final String EVENT_STRATEGY_SETTING = "EVENT_STRATEGY_SETTING";

        /**
         * 策略响应(3003)
         */
        public static final String EVENT_STRATEGY_CONFIRM = "EVENT_STRATEGY_CONFIRM";

        /**
         * 固件概要信息下发（8001）
         */
        public static final String CMD_DEVICE_UPDATE = "CMD_DEVICE_UPDATE";
        /**
         * 固件数据块请求（8002）
         */
        public static final String CMD_REQUEST_FW_DATA = "CMD_REQUEST_FW_DATA";

        /**
         * 固件数据块请求（8003）
         */
        public static final String CMD_RESPONSE_FW_DATA = "CMD_RESPONSE_FW_DATA";

        /**
         * 网关接收完成上报（8005）
         */
        public static final String EVENT_DEVICE_UPDATE = "EVENT_DEVICE_UPDATE";

        /**
         * 电桩接收完成上报（8004）
         */
        public static final String EVENT_PLIES_UPDATE = "EVENT_PLIES_UPDATE";

        /**
         * 网关日志上报
         */
        public static final String EVENT_GATEWAY_LOG = "EVENT_GATEWAY_LOG";

        /**
         * 离线交易记录上报
         */
        public static final String EVENT_OFFLINE_RECORD = "EVENT_OFFLINE_RECORD";

        /**
         * 离线交易记录响应
         */
        public static final String EVENT_OFFLINE_RECORD_RESPONSE = "EVENT_OFFLINE_RECORD_RESPONSE";

        /**
         * 控制板信息请求
         */
        public static final String CMD_TOPO_FW_REQ = "CMD_TOPO_FW_REQ";

        /**
         * 控制板信息响应
         */
        public static final String CMD_TOPO_FW_RES = "CMD_TOPO_FW_RES";

    }

    /**
     * 终端服务常量
     */
    public static class Param {

        /**
         * 电桩启动
         */
        public static final String PILE_START = "pile_start";

        /**
         * 电桩停止
         */
        public static final String PILE_STOP = "pile_stop";

        /**
         * 功率控制
         */
        public static final String POWER_CTRL = "pile_powerCtrl";
        /**
         * 费率下发
         */
        public static final String RATE_SET = "pile_rateSet";

        /**
         * 电桩状态
         */
        public static final String PILE_STATE = "pile_state";

        /**
         * 电桩故障
         */
        public static final String PILE_FAULT = "pile_fault";

        /**
         * 电桩数据
         */
        public static final String PILE_DATA = "pile_data";

        /**
         * 电桩日志上报
         */
        public static final String PILE_LOG = "pile_log";

        /**
         * 启动响应(4001)
         */
        public static final String PILE_START_RESPONSE = "pile_start_response";

        /**
         * 启动事件(4002)
         */
        public static final String PILE_START_EVENT = "pile_start_event";

        /**
         * 启动事件(4002)
         */
        public static final String TRANS_PILE_START_EVENT = "trans_pile_start_event";

        /**
         * 停止响应(4004)
         */
        public static final String PILE_STOP_RESPONSE = "pile_stop_response";

        /**
         * 停止事件(4005)
         */
        public static final String PILE_STOP_EVENT = "pile_stop_event";

        /**
         * 功率控制响应(4009)
         */
        public static final String PILE_CTRL_RESPONSE = "pile_powerCtrl_response";
        /**
         * 费率下发响应(1004)
         */
        public static final String PILE_RATE_SET_RESPONSE = "pile_rateSet_response";
        /**
         * 业务命令响应-bms信息(4008)
         */
        public static final String PILE_BMS_INFO_RESPONSE = "pile_bmsinfo_response";

        /**
         * 记录上报(4011)
         */
        public static final String PILE_RECORD_REQUEST = "pile_record_request";
        /**
         * 记录确认(84012)
         */
        public static final String PILE_RECORD_CONFIRM = "pile_recordConfirm";

        /**
         * 电桩复位命令(6010)
         */
        public static final String PILE_RESET = "pile_reset";

        /**
         * 电桩复位响应(6011)
         */
        public static final String PILE_RESET_RESPONSE = "pile_reset_response";

        /**
         * 遥测数据(123456)
         */
        public static final String TTU_YC = "ttu_yc";

        /**
         * 遥信数据(123456)
         */
        public static final String TTU_YX = "ttu_yx";

        /**
         * 转发遥测数据(123456)
         */
        public static final String ZF_IEG_YC = "zf_ieg_yc";

        /**
         * 转发遥测数据(123456)
         */
        public static final String ZF_IEG_YX = "zf_ieg_yx";

        /**
         * 转发遥测，遥信，遥控，遥脉
         */
        public static final String IEG_MQTT_ZF = "ieg_mqtt_zf";

        /**
         * 网关调控参数下发
         */
        public static final String LOAD_PARAMSET = "load_paramSet";

        /**
         * 网关调控参数下发响应
         */
        public static final String LOAD_PARAMSET_RESPONSE = "load_paramSet_response";

        /**
         * 网关通用参数下发
         */
        public static final String GENERAL_PARAMGETSET = "general_paramGetSet";

        /**
         * 网关通用参数下发响应
         */
        public static final String GENERAL_PARAM_RESPONSE = "general_param_response";

        /**
         * 整站信息上报
         */
        public static final String GATEWAY_STATION_INFO = "gateway_stationInfo";

        /**
         * 调控需求下发
         */
        public static final String GATEWAY_PEAK_VALLEY_CONTROL_SET = "gateway_peakValleyControlSet";

        /**
         * 调控需求下发响应
         */
        public static final String GATEWAY_PEAK_VALLEY_CONTROL_SET_RESPONSE = "gateway_peakValleyControlSet_response";

        /**
         * 调控需求终止下发
         */
        public static final String GATEWAY_PEAK_VALLEY_CONTROL_STOP = "gateway_peakValleyControlStop";

        /**
         * 调控需求终止下发
         */
        public static final String GATEWAY_PEAK_VALLEY_CONTROL_STOP_RESPONSE = "gateway_peakValleyControlStop_response";

    }

    /**
     * @brief 定义点表数据类型
     * @author xt
     * @date 2021/6/28 13:59
     * @param
     * @return
     */
    public static final String TYPE_INT = "T_INT";
    public static final String TYPE_DOUBLE = "T_DOUBLE";
    public static final String TYPE_BOOLEAN = "T_BOOLEAN";
    public static final String TYPE_STRING = "T_STRING";

    /**
     * 定义网关交流采样信息点表
     */
    public final static Map<Integer, String> IEG_TTU_POINT_TABLE = new HashMap<Integer, String>() {
        {
            //遥信部分0x00-0x2000
            put(0x1, "ind1");//输入信号1
            put(0x2, "ind2");//输入信号2
            put(0x3, "ind3");//输入信号3
            put(0x4, "ind4");//输入信号4
            //遥测部分 0x2000-0x4000
            put(0x4001, "phV_phsA");//A相电压
            put(0x4002, "phV_phsB");//B相电压
            put(0x4003, "phV_phsC");//C相电压
            put(0x4004, "phW_P");//总功率
            put(0x4005, "phW_phsA");//A相功率
            put(0x4006, "phW_phsB");//B相功率
            put(0x4007, "phW_phsC");//C相功率
            put(0x4008, "a_phsA");//A相电流
            put(0x4009, "a_phsB");//B相电流
            put(0x400A, "a_phsC");//C相电流
            put(0x400B, "hz");//频率
            put(0x400C, "phVAr_phsA");//A相无功功率
            put(0x400D, "phVAr_phsB");//B相无功功率
            put(0x400E, "phVAr_phsC");//C相无功功率
            put(0x400F, "phVA_S");//相视在功功率
            put(0x4010, "phVA_phsA");//A相视在功功率
            put(0x4011, "phVA_phsB");//B相视在功功率
            put(0x4012, "phVA_phsC");//C相视在功功率
            put(0x4013, "cos");//功率因数
            put(0x4014, "supWh");//正向有功电能
            put(0x4015, "supWh1");//当前正向有功电能尖示值
            put(0x4016, "supWh2");//当前正向有功电能峰示值
            put(0x4017, "supWh3");//当前正向有功电能平示值
            put(0x4018, "supWh4");//当前正向有功电能谷示值
            put(0x4019, "revWh");//当前反向有功电能示值
            put(0x401A, "phVAr_Q");//无功总
        }
    };
}
