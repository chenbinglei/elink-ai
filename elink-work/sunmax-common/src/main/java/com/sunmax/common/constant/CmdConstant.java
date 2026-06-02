package com.sunmax.common.constant;

/**
 * 前置服务通讯命令常量
 */
public class CmdConstant {

    public static final String CMD_NONE = "0"; //无
    public static final String CMD_RATE_REQ = "1"; //费率请求
    public static final String CMD_RATE_RES = "2"; //费率响应
    public static final String CMD_SUBLINKUP_NOTIFY = "3"; //子设备上线通知
    public static final String CMD_CBINFO_NOTIFY = "4"; //硬件控制板软硬件信息通知
    public static final String CMD_START = "5"; //启动命令
    public static final String CMD_START_RES = "6"; //启动响应
    public static final String CMD_START_EVENT = "7"; //启动事件
    public static final String CMD_STOP = "8"; //停止命令
    public static final String CMD_STOP_RES = "9"; //停止响应
    public static final String CMD_STOP_EVENT = "10"; //停止事件
    public static final String CMD_POWERCONTROL = "11"; //功率控制
    public static final String CMD_POWERCONTROL_RES = "12"; //功率控制响应
    public static final String CMD_PILE_DATA_REPORT = "13"; //电桩数据上报
    public static final String CMD_PILE_FAULT_REPORT = "14"; //电桩故障上报
    public static final String CMD_PILE_STATUS_REPORT = "15"; //电桩状态上报
    public static final String CMD_PILE_RECORD_REPORT = "16"; //充电记录上报
    public static final String CMD_PILE_RECORD_CONFIRM = "17"; //充电记录上报确认
    public static final String CMD_AuthenticationRequest = "18"; //鉴权请求
    public static final String CMD_AuthenticationResponse = "19"; //鉴权响应
    public static final String CMD_StrategySettingRequest = "20"; //策略设置
    public static final String CMD_StrategySettingResponse = "21"; //策略设置响应
    public static final String CMD_DevUpdate = "22"; //设备升级响应，平台-网关-设备
    public static final String CMD_DevDataBlockRequest = "23"; //设备升级-数据块请求
    public static final String CMD_DevDataBlockResponse = "24"; //设备升级-数据块响应
    public static final String CMD_DevUpdateReport = "25"; //设备升级-升级结果上报
    public static final String CMD_RATE_SET = "26"; //费率下发
    public static final String CMD_RATE_SET_RES = "27"; //费率下发响应
    public static final String CMD_PARAM_SET = "28"; //网关通用参数设置
    public static final String CMD_PARAM_SET_RES = "29"; //网关通用参数设置响应
    public static final String CMD_PileDataRequest = "30"; //电桩运行数据请求
    public static final String CMD_PileDataResponse = "31"; //电桩运行数据请求响应
    public static final String CMD_VehicleInfoRequest = "32"; //车辆信息请求
    public static final String CMD_VehicleInfoResponse = "33"; //车辆信息请求响应
    public static final String CMD_PileRecordReportRequest = "34"; //电桩记录查询
    public static final String CMD_PileRecordReportResponse = "35"; //电桩记录查询命令响应
    public static final String CMD_PileRecordReportInfoResponse = "36"; //电桩记录查询结果响应
    public static final String CMD_ModbusSetSingleCoil = "37"; //Modbus协议写单个线圈
    public static final String CMD_ModbusSetSingleReg = "38"; //Modbus协议写单个寄存器
    public static final String CMD_CreatePileShadow = "39"; //电桩上线创建电桩影子线程
    public static final String CMD_DeletePileShadow = "40"; //电桩离线 删除电桩影子线程
    public static final String CMD_PileToShadow = "41"; //电桩转发数据到影子电桩
    public static final String CMD_ShadowToPile = "42"; //影子电桩收到的平台报文发送给电桩
    public static final String CMD_PILE_BMSINFO_REPORT = "43"; //充电bms信息
    public static final String CMD_MODBUS_INFO_REPORT = "44"; //modbus数据
    public static final String CMD_Dev_HeartBeat = "47";//设备心跳
    public static final String CMD_PILE_LOGREPORT = "52";//日志数据上报
    public static final String CMD_PILE_LOGQUERY_CMD = "53";//电桩日志查询
    public static final String CMD_PILE_LOGQUERY_RESPONSE = "54";//电桩日志查询响应
    public static final String CMD_PILE_LOGQUERY_result = "55";//电桩日志查询结果
    public static final String CMD_PILE_RESET = "56";//电桩复位
    public static final String CMD_PILE_RESET_RESULT = "57";//电桩复位响应
    public static final String CMD_PILE_SETQR = "58";//设置二维码前缀
    public static final String CMD_PILE_SETQR_RES = "59";//设置二维码前缀响应
}
