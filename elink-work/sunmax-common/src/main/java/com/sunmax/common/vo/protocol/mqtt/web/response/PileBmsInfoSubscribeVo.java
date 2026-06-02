package com.sunmax.common.vo.protocol.mqtt.web.response;

import lombok.Data;

/**
 * 业务命令响应-bms信息 订阅实体类
 */
@Data
public class PileBmsInfoSubscribeVo {

    /**
     * 充电桩编码
     */
    private String pilesCode;

    /**
     *枪标识
     */
    private Integer gunCode;

    /**
     * Bms信息
     */
    private BmsInfoVo bmsInfo;

    @Data
    public static class BmsInfoVo {

        /**
         * BMS通信协议版本号
         */
        private String protocolVer;
        /**
         * 电池类型
         */
        private Integer batteryType;

        /**
         * 额定容量
         */
        private Double rateCap;

        /**
         * 额定电压
         */
        private Double rateVolt;

        /**
         * 电池生产商
         */
        private String manufacturer;

        /**
         * 电池序号
         */
        private String batterySn;

        /**
         * 电池生产日期-年
         */
        private Integer productYear;

        /**
         * 电池生产日期-月
         */
        private Integer productMonth;

        /**
         * 电池生产日期-日
         */
        private Integer productDay;

        /**
         * 电池组产权标识
         */
        private Integer batteryProperty;

        /**
         * 电池充电次数
         */
        private Long chargeTimes;

        /**
         * 车辆识别码VIN
         */
        private String busVin;

        /**
         * BMS软件版本号
         */
        private String bmsSoftVer;

        /**
         * 单体动力蓄电池最高允许充电电压
         */
        private Double bcpAllowChargeCellVmax;

        /**
         * 最高允许充电电流
         */
        private Double bcpAllowChargeCurrentMax;

        /**
         * 动力电池标称总能量
         */
        private Double bcpBatteryNorminalCap;

        /**
         * 最高允许充电电压
         */
        private Double bcpAllowChargeVmax;

        /**
         * 最高允许充电温度
         */
        private Integer bcpAllowTempMax;

        /**
         * 蓄电池荷电状态
         */
        private Integer curSoc;

        /**
         * 蓄电池当前电池电压
         */
        private Double curVoltage;

        /**
         * BMS 请求电压
         */
        private Double bmsRequestVolt;

        /**
         * BMS 请求电流
         */
        private Double bmsRequestCurrent;

        /**
         * BMS 测量电压
         */
        private Double bmsMeasureVolt;

        /**
         * BMS 测量电流
         */
        private Double bmsMeasureCurrent;

        /**
         * 最高单体蓄电池电压所对应组号
         */
        private Integer bcs_BatteryVoltageMaxGN;

        /**
         * 最高单体蓄电池电压
         */
        private Double bcs_BatteryVoltageMax;

        /**
         * 最低单体蓄电池电压所对应组号
         */
        private Integer bcs_BatteryVoltageMinGN;

        /**
         * 最低单体蓄电池电压
         */
        private Double bcs_BatteryVoltageMin;

        /**
         * 剩余充电时长
         */
        private Integer bcs_RemainingTime;

        /**
         * 最高单体蓄电池电压所对应编号
         */
        private Integer bsm_BatteryVoltageMaxNo;

        /**
         * 最高动力蓄电池温度
         */
        private Integer bsm_BatteryTempMax;

        /**
         * 最高动力蓄电池温度检测点编号
         */
        private Integer bsm_BatteryTempMaxNo;

        /**
         * 最低动力蓄电池温度
         */
        private Integer bsm_BatteryTempMin;

        /**
         * 最低动力蓄电池温度检测点编号
         */
        private Integer bsm_BatteryTempMinNo;

        /**
         * 动力蓄电池电压过高/过低
         * 0x00-正常，0x01-过高，0x02-过低
         */
        private Integer bsm_BatteryVoltageState;

        /**
         * 整车动力蓄电池荷电状态 soc 过高或过低
         * 0x00-正常，0x01-过高，0x10-过低
         */
        private Integer bsm_SocState;

        /**
         * 动力蓄电池充电过电流
         */
        private Integer bsm_BatteryCurrentState;

        /**
         * 动力蓄电池温度过高
         */
        private Integer bsm_BatteryTempState;

        /**
         * 动力蓄电池绝缘状态
         */
        private Integer bsm_InsulationState;

        /**
         * 动力蓄电池组输出连接器连接状态
         */
        private Integer bsm_BatteryOutputConnectorState;

        /**
         * 充电模式
         */
        private Integer chargeMode;

        /**
         * 允许充电
         */
        private Integer bsm_ChargeEnable;

        /**
         * BRM 超时
         */
        private Integer brmTimeout;

        /**
         * BCP 超时
         */
        private Integer bcpTimeout;

        /**
         * BRO 超时
         */
        private Integer broTimeout;

        /**
         * BCL 超时
         */
        private Integer bclTimeout;

        /**
         * BCS 超时
         */
        private Integer bcsTimeout;

        /**
         * BSM 超时
         */
        private Integer bsmTimeout;

        /**
         * BST 超时
         */
        private Integer bstTimeout;

        /**
         * BSD 超时
         */
        private Integer bsdTimeout;

        /**
         * BEM 中 CRM 超时
         */
        private Integer bemCRMTimeout;

        /**
         * BEM 中 CML 超时
         */
        private Integer bemCMLTimeout;

        /**
         * BEM 中 CRO 超时
         */
        private Integer bemCROTimeout;

        /**
         * BEM 中 CCS 超时
         */
        private Integer bemCCSTimeout;

        /**
         * BEM 中 CST 超时
         */
        private Integer bemCSTTimeout;

        /**
         * BEM 中 CSD 超时
         */
        private Integer bemCSDTimeout;

        /**
         * 最大允许放电电流
         */
        private Double bcpAllowMaxDischargeI;

        /**
         * 额定最低放电电压
         */
        private Double bcpRateMinDischargeV;

        /**
         * 额定最高放电电压
         */
        private Double bcpRateMaxDischargeV;

        /**
         * 放电状态最低允许 SOC
         */
        private Integer bcpAllowMinDischargeSOC;

        /**
         * 充放电循环调整最高允许 SOC
         */
        private Integer bcpAllowMaxCirSOC;

    }
}
