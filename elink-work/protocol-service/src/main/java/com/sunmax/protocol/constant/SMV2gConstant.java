package com.sunmax.protocol.constant;

import java.util.HashMap;
import java.util.Map;

public class SMV2gConstant {
    /*******************************************************************************************************************
     * 晟曼电桩-V2G后台服务与平台通讯协议
     * 版本v1.00
     *
     * ****************************************************************************************************************/
    public static final int P_SMV2G = 0xaa55;//协议起始域
    /**
     * 起始域定义
     * */
    public static final byte SMV2G_START_L = (byte)0x55; //第二个字节
    public static final byte SMV2G_START_H = (byte)0xAA; //第一个字节
    /**
     * 协议版本号
     * */
    public static final byte SMV2G_VERSION = (byte)0x1000;// 版本号V1.00
    /**
     * 发送原因
     */
    public static final byte SMV2G_SEND_REASON1 = 1;//消息发送
    public static final byte SMV2G_SEND_REASON2 = 2;//消息确认(无数据域)
    /**
     * 连接管理 常量定义
     */
    /**
     * 消息码
     */
    public static final int SMV2G_MSG0001 = 1;//上线请求
    public static final int SMV2G_MSG0002 = 2;//上线应答
    public static final int SMV2G_MSG0003 = 3;//密钥下发
    public static final int SMV2G_MSG0004 = 4;//密钥确认
    public static final int SMV2G_MSG0005 = 5;//下线通知
    public static final int SMV2G_MSG0006 = 6;//保活消息

    /**
     * Qos
     */
    public static final byte SMV2G_QOS0001 = 2;
    public static final byte SMV2G_QOS0002 = 2;
    public static final byte SMV2G_QOS0003 = 1;
    public static final byte SMV2G_QOS0004 = 1;
    public static final byte SMV2G_QOS0005 = 0;
    public static final byte SMV2G_QOS0006 = 0;

    /**
     * 充/放电费率  常量定义
     */
    /**
     * 消息码
     */
    public static final int SMV2G_MSG1000 = 1000;//费率请求
    public static final int SMV2G_MSG1001 = 1001;//费率响应
    public static final int SMV2G_MSG1002 = 1002;//费率查询
    public static final int SMV2G_MSG1003 = 1003;//费率下发
    public static final int SMV2G_MSG1004 = 1004;//费率上报

    /**
     * Qos
     */
    public static final byte SMV2G_QOS1000 = 1;
    public static final byte SMV2G_QOS1001 = 1;
    public static final byte SMV2G_QOS1002 = 1;
    public static final byte SMV2G_QOS1003 = 1;
    public static final byte SMV2G_QOS1004 = 1;


    /**
     * 实时数据  常量定义
     */
    /**
     * 消息码
     */
    public static final int SMV2G_MSG2000 = 2000;//电桩状态
    public static final int SMV2G_MSG2001 = 2001;//电桩故障
    public static final int SMV2G_MSG2002 = 2002;//电桩数据

    /**
     * Qos
     */
    public static final byte SMV2G_QOS2000 = 0;
    public static final byte SMV2G_QOS2001 = 0;
    public static final byte SMV2G_QOS2002 = 0;


    /**
     * 充/放电辅助 常量定义
     */
    /**
     * 消息码
     */
    public static final int SMV2G_MSG3000 = 3000;//鉴权请求
    public static final int SMV2G_MSG3001 = 3001;//鉴权响应
    public static final int SMV2G_MSG3002 = 3002;//策略设置
    public static final int SMV2G_MSG3003 = 3003;//策略响应
    public static final int SMV2G_MSG3004 = 3004;//识别码请求
    public static final int SMV2G_MSG3005 = 3005;//识别码响应

    /**
     * Qos
     */
    public static final byte SMV2G_QOS3000 = 1;
    public static final byte SMV2G_QOS3001 = 1;
    public static final byte SMV2G_QOS3002 = 2;
    public static final byte SMV2G_QOS3003 = 1;
    public static final byte SMV2G_QOS3004 = 1;
    public static final byte SMV2G_QOS3005 = 1;


    /**
     * 充/放电过程 常量定义
     */
    /**
     * 消息码
     */
    public static final int SMV2G_MSG4000 = 4000;//启动命令
    public static final int SMV2G_MSG4001 = 4001;//启动命令响应
    public static final int SMV2G_MSG4002 = 4002;//启动事件
    public static final int SMV2G_TRANS_MSG4002 = 40020;//启动事件转发运行数据
    public static final int SMV2G_MSG4003 = 4003;//停止命令
    public static final int SMV2G_MSG4004 = 4004;//停止命令响应
    public static final int SMV2G_MSG4005 = 4005;//停止事件
    public static final int SMV2G_MSG4006 = 4006;//充值命令
    public static final int SMV2G_MSG4007 = 4007;//充值响应
    public static final int SMV2G_MSG4008 = 4008;//Bms数据
    public static final int SMV2G_MSG4009 = 4009;//功率控制请求
    public static final int SMV2G_MSG4010 = 4010;//功率控制响应
    public static final int SMV2G_MSG4011 = 4011;//记录上报
    public static final int SMV2G_MSG4012 = 4012;//记录确认
    public static final int SMV2G_MSG4013 = 4013;//记录查询
    public static final int SMV2G_MSG4014 = 4014;//记录查询响应
    public static final int SMV2G_MSG4015 = 4015;//记录查询结果

    /**
     * Qos
     */
    public static final byte SMV2G_QOS4000 = 1;
    public static final byte SMV2G_QOS4001 = 1;
    public static final byte SMV2G_QOS4002 = 0;
    public static final byte SMV2G_QOS4003 = 1;
    public static final byte SMV2G_QOS4004 = 1;
    public static final byte SMV2G_QOS4005 = 0;
    public static final byte SMV2G_QOS4006 = 2;
    public static final byte SMV2G_QOS4007 = 2;
    public static final byte SMV2G_QOS4008 = 0;
    public static final byte SMV2G_QOS4009 = 2;
    public static final byte SMV2G_QOS4010 = 2;
    public static final byte SMV2G_QOS4011 = 1;
    public static final byte SMV2G_QOS4012 = 1;


    /**
     * 设备维护 常量定义
     */
    /**
     * 消息码
     */
    public static final int SMV2G_MSG6000 = 6000;
    public static final int SMV2G_MSG6001 = 6001;
    public static final int SMV2G_MSG6002 = 6002;
    public static final int SMV2G_MSG6003 = 6003;
    public static final int SMV2G_MSG6004 = 6004;
    public static final int SMV2G_MSG6005 = 6005;
    public static final int SMV2G_MSG6006 = 6006;
    public static final int SMV2G_MSG6007 = 6007;
    public static final int SMV2G_MSG6008 = 6008;
    public static final int SMV2G_MSG6009 = 6009;
    public static final int SMV2G_MSG6010 = 6010;
    public static final int SMV2G_MSG6011 = 6011;

    /**
     * Qos
     */
    public static final byte SMV2G_QOS6000 = 1;
    public static final byte SMV2G_QOS6001 = 1;
    public static final byte SMV2G_QOS6002 = 1;
    public static final byte SMV2G_QOS6003 = 1;
    public static final byte SMV2G_QOS6004 = 1;
    public static final byte SMV2G_QOS6005 = 1;
    public static final byte SMV2G_QOS6006 = 1;


    /**
     * 固件更新 常量定义
     */
    /**
     * 消息码
     */
    public static final int SMV2G_MSG8000 = 8000;
    public static final int SMV2G_MSG8001 = 8001;
    public static final int SMV2G_MSG8002 = 8002;
    public static final int SMV2G_MSG8003 = 8003;
    public static final int SMV2G_MSG8004 = 8004;

    /**
     * Qos
     */
    public static final byte SMV2G_QOS8000 = 1;
    public static final byte SMV2G_QOS8001 = 1;
    public static final byte SMV2G_QOS8002 = 1;
    public static final byte SMV2G_QOS8003 = 1;
    public static final byte SMV2G_QOS8004 = 0;

    /**
     * 启动命令 失败原因
     * */
    public static final int SUNMAX_START_OK = 0; //启动成功
    public static final int SUNMAX_START_FAIL = 1; //启动失败
    public static final int SUNMAX_APPOINTMENT_OK = 2; //预约成功
    public static final int SUNMAX_APPOINTMENT_FAIL = 3; //预约失败
    public static final int SUNMAX_APPOINTMENT_OTHER = 255; //其它故障

    /**
     * 充电策略
     * */
    public final static Map<Integer, String> SUNMAX_STRATEGY_MAP = new HashMap<Integer, String>() {
        {
            put(0,"充满/放空");
            put(1,"定SOC");
            put(2,"定金额");
            put(3,"定电量");
        }
    };

    /**
     * 停止命令
     * */
    public static final short SUNMAX_STOP_OK = 0; //停止成功
    public static final short SUNMAX_STOP_FAIL = 1; //停止失败
    public static final short SUNMAX_STOP_OTHER = 255; //其他原因停止

    /**
     * 功率控制
     * */
    public static final short SUNMAX_POWERCTRL_OK = 0; //停止成功
    public static final short SUNMAX_POWERCTRL_FAIL = 255; //停止失败

    public static final String StatusSuccess = "执行成功";
    public static final String StatusTimeOut = "执行超时";
    public static final String StatusFailed = "执行失败";
    public static final String StatusWorking = "充电枪工作中";
    public static final String StatusOther = "其他原因";

    public static final String STARTCMD = "STARTCMD"; //启动命令
    public static final String STOPCMD = "STOPCMD"; //停止命令
    public static final String POWERCTRL = "POWERCTRL"; //功率控制
    public static final String RATESET = "RATESET"; //费率下发
    public static final String PILEUPDATE = "PILEUPDATE"; //电桩升级
    public static final String PILEUPDATEBYIEG = "PILEUPDATEBYIEG"; //网关下的电桩升级
    public static final String SUB_MODEL_INFO_REQ = "MODELINFOREQ"; //控制板信息请求
    public static final String VEHICLE_INFO_REQUEST = "VEHICLE_INFO_REQUEST"; //车辆信息请求
    public static final String PILE_LOGQUERY = "PILE_LOGQUERY"; //电桩日志查询
    public static final String PILE_RESET = "PILE_RESET"; //电桩复位
    public static final String PILE_SETQR = "PILE_SETQR"; //设置二维码

}
