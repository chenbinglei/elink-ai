package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.37 CMD_PILE_BMSINFO_REPORT,//充电bms信息 43
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PileBmsInfoReportVo {

    /**
     * 枪标识 从1开始
     */
    @Schema(description = "枪标识")
    private Integer gunCode;

    /**
     * 通讯协议版本
     */
    @Schema(description = "通讯协议版本")
    private String protocolVer;

    /**
     * 电池类型 01H:铅酸电池; 02H:氢电池; 03H:磷酸铁锂电池; 04H:锰酸锂电池; 05H:钴酸锂电池; 06H:三元电池; 07H:聚合物锂离子电池; 08H:钛酸锂电池; FFH:其他;
     */
    @Schema(description = "电池类型")
    private Integer batteryType;

    /**
     * 额定容量 精度 0.1AH
     */
    @Schema(description = "额定容量")
    private Integer rateCap;

    /**
     * 额定电压 精度 0.1V
     */
    @Schema(description = "额定电压")
    private Integer rateVolt;

    /**
     * 电池生产厂商
     */
    @Schema(description = "电池生产厂商")
    private Integer manufacturer;

    /**
     * 生产日期-年
     */
    @Schema(description = "生产日期-年")
    private Integer productYear;

    /**
     * 生产日期-月
     */
    @Schema(description = "生产日期-月")
    private Integer productMonth;

    /**
     * 生产日期-日
     */
    @Schema(description = "生产日期-日")
    private Integer productDay;

    /**
     * 电池组产权标识 0-租赁 1-车自由,按 BMS 实际报文转发
     */
    @Schema(description = "电池组产权标识")
    private Integer batteryProperty;

    /**
     * 电池充电次数 1 次/bit 0 偏移
     */
    @Schema(description = "电池充电次数")
    private String chargeTimes;

    /**
     * 车辆vin码
     */
    @Schema(description = "车辆vin码")
    private String busVin;

    /**
     * Bms软件版本号
     */
    @Schema(description = "Bms软件版本号")
    private String bmsSoftVer;

    /**
     * 单体最高允许充电电压 数据范围0-2400 精度0.01V
     */
    @Schema(description = "单体最高允许充电电压")
    private Integer bcpAllowChargeCellVmax;

    /**
     * 最高允许充电电流 精度 0.1A
     */
    @Schema(description = "最高允许充电电流")
    private Integer bcpAllowChargeCurrentMax;

    /**
     * 动力电池标称总能量 精度 0.1kW.h
     */
    @Schema(description = "动力电池标称总能量")
    private Integer bcpBatteryNorminalCap;

    /**
     * 动力电池最高允许电压 精度 0.1V
     */
    @Schema(description = "动力电池最高允许电压")
    private Integer bcpAllowChargeVmax;

    /**
     * 最高允许充电温度 精度 1℃
     */
    @Schema(description = "最高允许充电温度")
    private Integer bcpAllowTempMax;

    /**
     * 蓄电池荷电状态 数据范围 0-100，精度 1%
     */
    @Schema(description = "蓄电池荷电状态")
    private Integer curSoc;

    /**
     * 蓄电池当前电池电压 精度 0.1V
     */
    @Schema(description = "蓄电池当前电池电压")
    private Integer curVoltage;

    /**
     * BMS请求电压 精度 0.1V
     */
    @Schema(description = "BMS请求电压")
    private Integer bmsRequestVolt;

    /**
     * BMS请求电流 精度 0.1A
     */
    @Schema(description = "BMS请求电流")
    private Integer bmsRequestCurrent;

    /**
     * Bms测量电压 精度 0.1V
     */
    @Schema(description = "Bms测量电压")
    private Integer bmsMeasureVolt;

    /**
     * Bms测量电流 精度 0.1A
     */
    @Schema(description = "Bms测量电流")
    private Integer bmsMeasureCurrent;

    /**
     * 最高单体蓄电池电压对应组号 数据范围 0-15 ，1 组/bit
     */
    @Schema(description = "最高单体蓄电池电压对应组号")
    private Integer bcs_BatteryVoltageMaxGN;

    /**
     * 最高单体蓄电池电压所对应组号 数据范围 0-15 ，1 组/bit
     */
    @Schema(description = "最高单体蓄电池电压所对应组号")
    private Integer bcs_BatteryVoltageMinGN;

    /**
     * 最高单体蓄电池电压 数据范围 0-2400，精度 0.01V
     */
    @Schema(description = "最高单体蓄电池电压")
    private Integer bcs_BatteryVoltageMax;

    /**
     * 最低单体蓄电池电压 数据范围 0-2400，精度 0.01V
     */
    @Schema(description = "最低单体蓄电池电压")
    private Integer bcs_BatteryVoltageMin;

    /**
     * 剩余充电时长 数据范围 0-600min，1min/bit
     */
    @Schema(description = "剩余充电时长")
    private Integer bcs_RemainingTime;

    /**
     * 最高单体蓄电池电压所对应编号; 数据范围 1-256，1/bit
     */
    @Schema(description = "最高单体蓄电池电压所对应编号")
    private Integer bsm_BatteryVoltageMaxNo;

    /**
     * 最高动力蓄电池温度 ;1℃/bit
     */
    @Schema(description = "最高动力蓄电池温度")
    private Integer bsm_BatteryTempMax;

    /**
     * 最高动力蓄电池温度检测点编号 ;数据范围 1-128，1/bit
     */
    @Schema(description = "最高动力蓄电池温度检测点编号")
    private Integer bsm_BatteryTempMaxNo;

    /**
     * 最低动力蓄电池温度; 1℃/bit
     */
    @Schema(description = "最低动力蓄电池温度")
    private Integer bsm_BatteryTempMin;

    /**
     * 最低动力蓄电池温度检测点编号; 数据范围 1-128，1/bit
     */
    @Schema(description = "最低动力蓄电池温度检测点编号")
    private Integer bsm_BatteryTempMinNo;

    /**
     * 动力蓄电池电压过高/过低 0x00-正常，0x01-过高，0x02-过低
     */
    @Schema(description = "动力蓄电池电压过高/过低")
    private Integer bsm_BatteryVoltageState;

    /**
     * 整车动力蓄电池荷电状态 soc过高或过低 0x00-正常，0x01-过高，0x02-过低
     */
    @Schema(description = "整车动力蓄电池荷电状态")
    private Integer bsm_SocState;

    /**
     * 动力蓄电池充电过电流 0x00-正常，0x01-过流，0x10-不可信状态
     */
    @Schema(description = "动力蓄电池充电过电流")
    private Integer bsm_BatteryCurrentState;

    /**
     * 动力蓄电池温度过高 0x00-正常，0x01-过流，0x10-不可信状态
     */
    @Schema(description = "动力蓄电池温度过高")
    private Integer bsm_BatteryTempState;

    /**
     * 动力蓄电池绝缘状态 0x00-正常，0x01-不正常，0x10-不可信状态
     */
    @Schema(description = "动力蓄电池绝缘状态")
    private Integer bsm_InsulationState;

    /**
     * 动力蓄电池组输出连接器连接状态 0x00-正常，0x01-不正常，0x10-不可信状态
     */
    @Schema(description = "动力蓄电池组输出连接器连接状态")
    private Integer bsm_BatteryOutputConnectorState;

    /**
     * 充电模式 0x01-恒压充电；0x02-恒流充电
     */
    @Schema(description = "充电模式")
    private Integer chargeMode;

    /**
     * 允许充电 0x00-禁止，0x01-允许
     */
    @Schema(description = "允许充电")
    private Integer bsm_allow_charge;

}
