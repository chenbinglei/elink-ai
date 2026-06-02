package com.sunmax.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FaultParseUtil {

    /**
     * 故障码 编码
     * 电桩紧急停止
     */
    public static final byte SUNMAX_PILE_EMERGENCY_FAULT = 0x00;//电桩紧急停止

    public final static Map<Integer, Byte> SUNMAX_PILE_EMERGENCY_FAULT_MAP = new HashMap<Integer, Byte>() {
        {
            put(0, (byte) 0x00);//预留 占位 不可被使用
            put(1, (byte) 0x01);//停电
            put(2, (byte) 0x02);//急停按钮按下
            put(3, (byte) 0x03);//电桩箱门打开
            put(4, (byte) 0x04);//预留
            put(5, (byte) 0x05);//预留
            put(6, (byte) 0x06);//预留
            put(7, (byte) 0x07);//预留
        }
    };

    /**
     * 故障码 大项 小项
     * 电桩通用故障
     */
    public static final byte SUNMAX_PILE_GENERAL_FAULT = 0x01;//电桩通用故障

    public final static Map<Integer, Byte> SUNMAX_PILE_GENERAL_FAULT_MAP = new HashMap<Integer, Byte>() {
        {
            put(0, (byte) 0x00);//电箱门故障
            put(1, (byte) 0x01);//防雷器故障
            put(2, (byte) 0x02);//交流接触器故障
            put(3, (byte) 0x03);//电桩风扇故障
            put(4, (byte) 0x04);//地锁故障故障
            put(5, (byte) 0x05);//桩体过温故障
            put(6, (byte) 0x06);//PE掉线故障
            put(7, (byte) 0x07);//母线连接接触器故障
            put(8, (byte) 0x08);//A 组投切枪 1 接触器故障
            put(9, (byte) 0x09);//A 组投切枪 2 接触器故障
            put(10, (byte) 0x10);//B 组投切枪 1 接触器故障
            put(11, (byte) 0x11);//B 组投切枪 2 接触器故障
            put(12, (byte) 0x12);//预留
            put(13, (byte) 0x13);//预留
            put(14, (byte) 0x14);//预留
            put(15, (byte) 0x15);//预留
        }
    };

    /**
     * 故障码 大项 小项
     * TCU上报故障
     */
    public static final byte SUNMAX_PILE_TCU_FAULT = 0x11;//TCU上报故障 编码大项
    public final static Map<Integer, Byte> SUNMAX_PILE_TCU_FAULT_MAP = new HashMap<Integer, Byte>() {
        {
            put(0, (byte) 0x00);//主控板元件故障
            put(1, (byte) 0x01);//桩与平台通讯断开故障
            put(2, (byte) 0x02);//RTC时钟错误
        }
    };

    /**
     * 故障码 大项 小项
     * TCU  超时类故障 上报故障
     */
    public static final byte SUNMAX_PILE_TCU_TIMEOUT_FAULT = 0x12;
    public final static Map<Integer, Byte> SUNMAX_PILE_TCU_TIMEOUT_FAULT_MAP = new HashMap<Integer, Byte>() {
        {
            put(0, (byte) 0x00);//TCU与CCU通信超时
            put(1, (byte) 0x01);//显示屏通信超时
            put(2, (byte) 0x02);//刷卡板通信超时
            put(3, (byte) 0x03);//预留
            put(4, (byte) 0x04);//预留
        }
    };

    /**
     * 故障码 大项 小项
     * CCU上报故障
     */
    public static final byte SUNMAX_PILE_CCU_FAULT = 0x21;//TCU上报故障 编码大项
    public final static Map<Integer, Byte> SUNMAX_PILE_CCU_FAULT_MAP = new HashMap<Integer, Byte>() {
        {
            put(0, (byte) 0x00);//CC1连接故障
            put(1, (byte) 0x01);//CC1接地
            put(2, (byte) 0x02);//电磁锁故障
            put(3, (byte) 0x03);//外侧电池电压大于10V
            put(4, (byte) 0x04);//DC+、DC-反接
            put(5, (byte) 0x05);//直流接触器故障
            put(6, (byte) 0x06);//直流熔断器故障
            put(7, (byte) 0x07);//泄放故障
            put(8, (byte) 0x08);//输出过压
            put(9, (byte) 0x09);//预充前车端电压超桩端电压阀值
            put(10, (byte) 0x0A);//预充前车端电压未达到目标值
            put(11, (byte) 0x0B);//BMS请求电压过高或过低
            put(12, (byte) 0x0C);//输出过流
            put(13, (byte) 0x0D);//输出总电压过压
            put(14, (byte) 0x0E);//输出总电流过流
            put(15, (byte) 0x0F);//BMS通信中断
            put(16, (byte) 0x10);//直流电表数据异常
            put(17, (byte) 0x11);//交流电表数据异常
            put(18, (byte) 0x12);//辅助电源异常
            put(19, (byte) 0x13);//枪未归位告警
            put(20, (byte) 0x14);//枪过温告警
            put(21, (byte) 0x15);//BCL充电协议不匹配
            put(22, (byte) 0x16);//BCL放电协议不匹配
            put(23, (byte) 0x17);//BCP参数不匹配
        }
    };

    /**
     * 故障码 大项 小项
     * CCU  超时类故障 上报故障
     */
    public static final byte SUNMAX_PILE_CCU_TIMEOUT_FAULT = 0x22;
    public final static Map<Integer, Byte> SUNMAX_PILE_CCU_TIMEOUT_FAULT_MAP = new HashMap<Integer, Byte>() {
        {
            put(0, (byte) 0x00);//BRM 报文接收超时
            put(1, (byte) 0x01);//BCP 报文接收超时
            put(2, (byte) 0x02);//BRO 报文接收超时
            put(3, (byte) 0x03);//BCL 报文接收超时
            put(4, (byte) 0x04);//BCS 报文接收超时
            put(5, (byte) 0x05);//BSM 报文接收超时
            put(6, (byte) 0x06);//BST 报文接收超时
            put(7, (byte) 0x07);//BSD 报文接收超时
            put(8, (byte) 0x08);//CCU与TCU通讯超时
            put(9, (byte) 0x09);//CCU与PCU通讯超时
            put(10, (byte) 0x0A);//直流电表通讯超时
            put(11, (byte) 0x0B);//交流电表通讯超时
            put(12, (byte) 0x0C);//电力模块输出电压超时-绝缘检测阶段
            put(13, (byte) 0x0D);//电力模块输出电压超时-预充阶段
            put(14, (byte) 0x0E);//绝缘检测后，输出电压降低到60V的时间大于5秒
            put(15, (byte) 0x0F);//BCPP 报文接收超时
        }
    };

    /**
     * 故障码 大项 小项
     * PCU 上报故障
     */
    public static final byte SUNMAX_PILE_PCU_FAULT = 0x31;
    public final static Map<Integer, Byte> SUNMAX_PILE_PCU_FAULT_MAP = new HashMap<Integer, Byte>() {
        {
            put(0, (byte) 0x00);//交流输入缺相
            put(1, (byte) 0x01);//交流输入过压
            put(2, (byte) 0x02);//交流输入欠压
            put(3, (byte) 0x03);//交流输入不平衡
            put(4, (byte) 0x04);//电力模块上报ID重复
            put(5, (byte) 0x05);//电力模块上报短路
            put(6, (byte) 0x06);//电力模块上报输出过压
            put(7, (byte) 0x07);//电力模块上报输出过流
            put(8, (byte) 0x08);//电力模块上报输出欠压
            put(9, (byte) 0x09);//电力模块上报风扇故障
            put(10, (byte) 0x0A);//电力模块上报过温
            put(11, (byte) 0x0B);//枪不可用（所有电力模块故障）
            put(12, (byte) 0x0C);//电力模块放电异常
            put(13, (byte) 0x0D);//电力模块严重不均流
            put(14, (byte) 0x0E);//电力模块 AC 故障
            put(15, (byte) 0x0F);//电力模块锁相错误
            put(16, (byte) 0x10);//电力模块通讯超时
            put(17, (byte) 0x11);//电力模块保护告警
            put(18, (byte) 0x12);//电力模块故障告警
            put(19, (byte) 0x13);//电力模块上报输出过压
            put(20, (byte) 0x14);//电力模块通信中断告警
            put(21, (byte) 0x15);//电力模块监控不匹配
            put(22, (byte) 0x16);//预留
            put(23, (byte) 0x17);//预留
        }
    };

    /**
     * 故障码 大项 小项
     * 车辆 BMS 故障信息
     * 车辆BMS 上报故障
     */
    public static final byte SUNMAX_PILE_BMS_FAULT = 0x41;
    public final static Map<Integer, Byte> SUNMAX_PILE_BMS_FAULT_MAP = new HashMap<Integer, Byte>() {
        {
            put(0, (byte) 0x00);//BSM报文-单体动力蓄电池电压过高
            put(1, (byte) 0x01);//BSM报文-单体动力蓄电池电压过低
            put(2, (byte) 0x02);//BSM报文-SOC过高
            put(3, (byte) 0x03);//BSM报文-SOC过低
            put(4, (byte) 0x04);//BSM报文-动力蓄电池过流\不可信
            put(5, (byte) 0x05);//BSM报文-动力蓄电池过温\不可信
            put(6, (byte) 0x06);//BSM报文-动力蓄电池绝缘不正常\不可信
            put(7, (byte) 0x07);//BSM报文-动力蓄电池输出连接器不正常\不可信
            put(8, (byte) 0x08);//BSM报文-充电禁止超时
            put(9, (byte) 0x09);//BST报文-绝缘故障
            put(10, (byte) 0x0A);//BST报文-输出连接器过温故障
            put(11, (byte) 0x0B);//BST报文-BMS元件、输出连接器过温
            put(12, (byte) 0x0C);//BST报文-充电连接器故障
            put(13, (byte) 0x0D);//BST报文-电池组温度过高故障
            put(14, (byte) 0x0E);//BST报文-高压继电器故障
            put(15, (byte) 0x0F);//BST报文-检测点2故障
            put(16, (byte) 0x10);//BST报文-其他故障
            put(17, (byte) 0x11);//BST报文-电流过大
            put(18, (byte) 0x12);//BST报文-电压异常
            put(19, (byte) 0x13);//BST报文-没报具体错误
            put(20, (byte) 0x14);//BEM 报文-A+、A-断开超时；
            put(21, (byte) 0x15);//BEM 报文-车辆充电参数不匹配；
            put(22, (byte) 0x16);//BST 报文-充电参数不匹配
            put(23, (byte) 0x17);//预留
            put(24, (byte) 0x18);//预留
            put(25, (byte) 0x19);//预留
            put(26, (byte) 0x1A);//预留
            put(27, (byte) 0x1B);//预留
            put(28, (byte) 0x1C);//预留
            put(29, (byte) 0x1D);//预留
            put(30, (byte) 0x1E);//预留
            put(31, (byte) 0x1F);//预留
        }
    };

    /**
     * 故障码 大项 小项
     * BMS  超时类故障 上报故障
     */
    public static final byte SUNMAX_PILE_BMS_TIMEOUT_FAULT = 0x42;
    public final static Map<Integer, Byte> SUNMAX_PILE_BMS_TIMEOUT_FAULT_MAP = new HashMap<Integer, Byte>() {
        {
            put(0, (byte) 0x00);//BEM报文-CRM超时
            put(1, (byte) 0x01);//BEM报文-CML超时
            put(2, (byte) 0x02);//BEM报文-CRO超时
            put(3, (byte) 0x03);//BEM报文-CCS超时
            put(4, (byte) 0x04);//BEM报文-CST超时
            put(5, (byte) 0x05);//BEM报文-CSD超时
            put(6, (byte) 0x06);//BEM报文-CMLP超时
            put(7, (byte) 0x07);//BEM报文-CCD超时
        }
    };

    /**
     * 故障码 大项 小项
     * IMD 绝缘检测故障 上报故障
     */
    public static final byte SUNMAX_PILE_IMD_FAULT = 0x51;
    public final static Map<Integer, Byte> SUNMAX_PILE_IMD_FAULT_MAP = new HashMap<Integer, Byte>() {
        {
            put(0, (byte) 0x00);//绝缘检测结果小于100欧姆/v
            put(1, (byte) 0x01);//绝缘检测结果超时
            put(2, (byte) 0x02);//预留
            put(3, (byte) 0x03);//预留
            put(4, (byte) 0x04);//预留
            put(5, (byte) 0x05);//预留
            put(6, (byte) 0x06);//预留
            put(7, (byte) 0x07);//预留
        }
    };

    /**
     * @param
     * @return
     * @brief 解析电桩急停故障
     * @author xt
     * @date 2021/9/10 18:19
     */
    public static List<Integer> parseEmergcyFault(int code) {
        List<Integer> re = new ArrayList<>();
        for (int i = 0; i < SUNMAX_PILE_EMERGENCY_FAULT_MAP.size(); i++) {
            if (0x01 == ((code >> i) & 0x01)) { //有故障
                Integer temp = (SUNMAX_PILE_EMERGENCY_FAULT_MAP.get(i) & 0xFF) | ((SUNMAX_PILE_EMERGENCY_FAULT << 8) & 0xFF00);
                re.add(temp);
            }
        }
        return re;
    }

    /**
     * @param
     * @return
     * @brief 解析电桩通用故障
     * @author xt
     * @date 2021/9/10 19:49
     */
    public static List<Integer> parseGeneralFault(int code) {
        List<Integer> re = new ArrayList<>();
        for (int i = 0; i < SUNMAX_PILE_GENERAL_FAULT_MAP.size(); i++) {
            if (0x01 == ((code >> i) & 0x01)) { //有故障
                Integer temp = (SUNMAX_PILE_GENERAL_FAULT_MAP.get(i) & 0xFF) | ((SUNMAX_PILE_GENERAL_FAULT << 8) & 0xFF00);
                re.add(temp);
            }
        }
        return re;
    }

    /**
     * @param
     * @return
     * @brief 解析tcu故障
     * @author xt
     * @date 2021/9/10 21:47
     */
    public static List<Integer> parseTcuFault(int code) {
        List<Integer> re = new ArrayList<>();
        int tcuSize = SUNMAX_PILE_TCU_FAULT_MAP.size() + SUNMAX_PILE_TCU_TIMEOUT_FAULT_MAP.size();
        for (int i = 0; i < tcuSize; i++) {
            if (0x01 == ((code >> i) & 0x01)) { //有故障
                int temp;
                if (i < SUNMAX_PILE_TCU_FAULT_MAP.size()) {
                    temp = (SUNMAX_PILE_TCU_FAULT_MAP.get(i) & 0xFF) | ((SUNMAX_PILE_TCU_FAULT << 8) & 0xFF00);
                } else {
                    temp = (SUNMAX_PILE_TCU_TIMEOUT_FAULT_MAP.get(i - SUNMAX_PILE_TCU_FAULT_MAP.size()) & 0xFF) | ((SUNMAX_PILE_TCU_TIMEOUT_FAULT << 8) & 0xFF00);
                }
                re.add(temp);
            }
        }
        return re;
    }

    /**
     * @param
     * @return
     * @brief 解析TCU超时故障
     * @author xt
     * @date 2021/9/10 19:49
     */
    public static List<Integer> parseTcuTimeOutFault(int code) {
        List<Integer> re = new ArrayList<>();
        for (int i = 0; i < SUNMAX_PILE_TCU_TIMEOUT_FAULT_MAP.size(); i++) {
            if (0x01 == ((code >> i) & 0x01)) { //有故障
                Integer temp = (SUNMAX_PILE_TCU_TIMEOUT_FAULT_MAP.get(i) & 0xFF) | ((SUNMAX_PILE_TCU_TIMEOUT_FAULT << 8) & 0xFF00);
                re.add(temp);
            }
        }
        return re;
    }

    /**
     * @param
     * @return
     * @brief 解析ccu上报故障
     * @author xt
     * @date 2021/9/10 19:49
     */
    public static List<Integer> parseCcuFault(int code) {
        List<Integer> re = new ArrayList<>();
        for (int i = 0; i < SUNMAX_PILE_CCU_FAULT_MAP.size(); i++) {
            if (0x01 == ((code >> i) & 0x01)) { //有故障
                Integer temp = (SUNMAX_PILE_CCU_FAULT_MAP.get(i) & 0xFF) | ((SUNMAX_PILE_CCU_FAULT << 8) & 0xFF00);
                re.add(temp);
            }
        }
        return re;
    }

    /**
     * @param
     * @return
     * @brief CCU超时类故障
     * @author xt
     * @date 2021/9/10 22:23
     */
    public static List<Integer> parseCcuTimeOutFault(int code) {
        List<Integer> re = new ArrayList<>();
        for (int i = 0; i < SUNMAX_PILE_CCU_TIMEOUT_FAULT_MAP.size(); i++) {
            if (0x01 == ((code >> i) & 0x01)) { //有故障
                Integer temp = (SUNMAX_PILE_CCU_TIMEOUT_FAULT_MAP.get(i) & 0xFF) | ((SUNMAX_PILE_CCU_TIMEOUT_FAULT << 8) & 0xFF00);
                re.add(temp);
            }
        }
        return re;
    }

    /**
     * @param
     * @return
     * @brief PCU上报故障
     * @author xt
     * @date 2021/9/11 9:26
     */
    public static List<Integer> parsePcuFault(int code) {
        List<Integer> re = new ArrayList<>();
        for (int i = 0; i < SUNMAX_PILE_PCU_FAULT_MAP.size(); i++) {
            if (0x01 == ((code >> i) & 0x01)) {
                Integer temp = (SUNMAX_PILE_PCU_FAULT_MAP.get(i) & 0xFF) | ((SUNMAX_PILE_PCU_FAULT << 8) & 0xFF00);
                re.add(temp);
            }

        }
        return re;
    }

    /**
     * @param
     * @return
     * @brief BMS上报故障
     * @author xt
     * @date 2021/9/11 9:47
     */
    public static List<Integer> parseBmsFault(int code) {
        List<Integer> re = new ArrayList<>();
        for (int i = 0; i < SUNMAX_PILE_BMS_FAULT_MAP.size(); i++) {
            if (0x01 == ((code >> i) & 0x01)) {
                Integer temp = (SUNMAX_PILE_BMS_FAULT_MAP.get(i) & 0xFF) | ((SUNMAX_PILE_BMS_FAULT << 8) & 0xFF00);
                re.add(temp);
            }
        }
        return re;
    }

    /**
     * @param
     * @return
     * @brief bms 超时故障
     * @author xt
     * @date 2021/9/11 9:51
     */
    public static List<Integer> parseBmsTimeOutFault(int code) {
        List<Integer> re = new ArrayList<>();
        for (int i = 0; i < SUNMAX_PILE_BMS_TIMEOUT_FAULT_MAP.size(); i++) {
            if (0x01 == ((code >> i) & 0x01)) {
                Integer temp = (SUNMAX_PILE_BMS_TIMEOUT_FAULT_MAP.get(i) & 0xFF) | ((SUNMAX_PILE_BMS_TIMEOUT_FAULT << 8) & 0xFF00);
                re.add(temp);
            }
        }
        return re;
    }

    /**
     * @param
     * @return
     * @brief 绝缘你检测故障
     * @author xt
     * @date 2021/9/11 11:53
     */
    public static List<Integer> parseIMDFault(int code) {
        List<Integer> re = new ArrayList<>();
        for (int i = 0; i < SUNMAX_PILE_IMD_FAULT_MAP.size(); i++) {
            if (0x01 == ((code >> i) & 0x01)) {
                Integer temp = (SUNMAX_PILE_IMD_FAULT_MAP.get(i) & 0xFF) | ((SUNMAX_PILE_IMD_FAULT << 8) & 0xFF00);
                re.add(temp);
            }
        }
        return re;
    }
}
