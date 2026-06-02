package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 通讯协议版本
     */
    @ApiModelProperty(value = "通讯协议版本", required = true)
    private String protocolVer;

    /**
     * 电池类型 01H:铅酸电池; 02H:氢电池; 03H:磷酸铁锂电池; 04H:锰酸锂电池; 05H:钴酸锂电池; 06H:三元电池; 07H:聚合物锂离子电池; 08H:钛酸锂电池; FFH:其他;
     */
    @ApiModelProperty(value = "电池类型", required = true)
    private Integer batteryType;

    /**
     * 额定容量 精度 0.1AH
     */
    @ApiModelProperty(value = "额定容量", required = true)
    private Integer rateCap;

    /**
     * 额定电压 精度 0.1V
     */
    @ApiModelProperty(value = "额定电压", required = true)
    private Integer rateVolt;

    /**
     * 电池生产厂商
     */
    @ApiModelProperty(value = "电池生产厂商", required = true)
    private Integer manufacturer;

    /**
     * 生产日期-年
     */
    @ApiModelProperty(value = "生产日期-年", required = true)
    private Integer productYear;

    /**
     * 生产日期-月
     */
    @ApiModelProperty(value = "生产日期-月", required = true)
    private Integer productMonth;

    /**
     * 生产日期-日
     */
    @ApiModelProperty(value = "生产日期-日", required = true)
    private Integer productDay;

    /**
     * 电池组产权标识 0-租赁 1-车自由,按 BMS 实际报文转发
     */
    @ApiModelProperty(value = "电池组产权标识", required = true)
    private Integer batteryProperty;

    /**
     * 电池充电次数 1 次/bit 0 偏移
     */
    @ApiModelProperty(value = "电池充电次数", required = true)
    private String chargeTimes;

    /**
     * 车辆vin码
     */
    @ApiModelProperty(value = "车辆vin码", required = true)
    private String busVin;

    /**
     * Bms软件版本号
     */
    @ApiModelProperty(value = "Bms软件版本号", required = true)
    private String bmsSoftVer;

    /**
     * 单体最高允许充电电压 数据范围0-2400 精度0.01V
     */
    @ApiModelProperty(value = "单体最高允许充电电压", required = true)
    private Integer bcpAllowChargeCellVmax;

    /**
     * 最高允许充电电流 精度 0.1A
     */
    @ApiModelProperty(value = "最高允许充电电流", required = true)
    private Integer bcpAllowChargeCurrentMax;

    /**
     * 动力电池标称总能量 精度 0.1kW.h
     */
    @ApiModelProperty(value = "动力电池标称总能量", required = true)
    private Integer bcpBatteryNorminalCap;

    /**
     * 动力电池最高允许电压 精度 0.1V
     */
    @ApiModelProperty(value = "动力电池最高允许电压", required = true)
    private Integer bcpAllowChargeVmax;

    /**
     * 最高允许充电温度 精度 1℃
     */
    @ApiModelProperty(value = "最高允许充电温度", required = true)
    private Integer bcpAllowTempMax;

    /**
     * 蓄电池荷电状态 数据范围 0-100，精度 1%
     */
    @ApiModelProperty(value = "蓄电池荷电状态", required = true)
    private Integer curSoc;

    /**
     * 蓄电池当前电池电压 精度 0.1V
     */
    @ApiModelProperty(value = "蓄电池当前电池电压", required = true)
    private Integer curVoltage;

    /**
     * BMS请求电压 精度 0.1V
     */
    @ApiModelProperty(value = "BMS请求电压", required = true)
    private Integer bmsRequestVolt;

    /**
     * BMS请求电流 精度 0.1A
     */
    @ApiModelProperty(value = "BMS请求电流", required = true)
    private Integer bmsRequestCurrent;

    /**
     * Bms测量电压 精度 0.1V
     */
    @ApiModelProperty(value = "Bms测量电压", required = true)
    private Integer bmsMeasureVolt;

    /**
     * Bms测量电流 精度 0.1A
     */
    @ApiModelProperty(value = "Bms测量电流", required = true)
    private Integer bmsMeasureCurrent;

    /**
     * 最高单体蓄电池电压对应组号 数据范围 0-15 ，1 组/bit
     */
    @ApiModelProperty(value = "最高单体蓄电池电压对应组号", required = true)
    private Integer bcs_BatteryVoltageMaxGN;

    /**
     * 最高单体蓄电池电压所对应组号 数据范围 0-15 ，1 组/bit
     */
    @ApiModelProperty(value = "最高单体蓄电池电压所对应组号", required = true)
    private Integer bcs_BatteryVoltageMinGN;

    /**
     * 最高单体蓄电池电压 数据范围 0-2400，精度 0.01V
     */
    @ApiModelProperty(value = "最高单体蓄电池电压", required = true)
    private Integer bcs_BatteryVoltageMax;

    /**
     * 最低单体蓄电池电压 数据范围 0-2400，精度 0.01V
     */
    @ApiModelProperty(value = "最低单体蓄电池电压", required = true)
    private Integer bcs_BatteryVoltageMin;

    /**
     * 剩余充电时长 数据范围 0-600min，1min/bit
     */
    @ApiModelProperty(value = "剩余充电时长", required = true)
    private Integer bcs_RemainingTime;

    /**
     * 最高单体蓄电池电压所对应编号; 数据范围 1-256，1/bit
     */
    @ApiModelProperty(value = "最高单体蓄电池电压所对应编号", required = true)
    private Integer bsm_BatteryVoltageMaxNo;

    /**
     * 最高动力蓄电池温度 ;1℃/bit
     */
    @ApiModelProperty(value = "最高动力蓄电池温度", required = true)
    private Integer bsm_BatteryTempMax;

    /**
     * 最高动力蓄电池温度检测点编号 ;数据范围 1-128，1/bit
     */
    @ApiModelProperty(value = "最高动力蓄电池温度检测点编号", required = true)
    private Integer bsm_BatteryTempMaxNo;

    /**
     * 最低动力蓄电池温度; 1℃/bit
     */
    @ApiModelProperty(value = "最低动力蓄电池温度", required = true)
    private Integer bsm_BatteryTempMin;

    /**
     * 最低动力蓄电池温度检测点编号; 数据范围 1-128，1/bit
     */
    @ApiModelProperty(value = "最低动力蓄电池温度检测点编号", required = true)
    private Integer bsm_BatteryTempMinNo;

    /**
     * 动力蓄电池电压过高/过低 0x00-正常，0x01-过高，0x02-过低
     */
    @ApiModelProperty(value = "动力蓄电池电压过高/过低", required = true)
    private Integer bsm_BatteryVoltageState;

    /**
     * 整车动力蓄电池荷电状态 soc过高或过低 0x00-正常，0x01-过高，0x02-过低
     */
    @ApiModelProperty(value = "整车动力蓄电池荷电状态", required = true)
    private Integer bsm_SocState;

    /**
     * 动力蓄电池充电过电流 0x00-正常，0x01-过流，0x10-不可信状态
     */
    @ApiModelProperty(value = "动力蓄电池充电过电流", required = true)
    private Integer bsm_BatteryCurrentState;

    /**
     * 动力蓄电池温度过高 0x00-正常，0x01-过流，0x10-不可信状态
     */
    @ApiModelProperty(value = "动力蓄电池温度过高", required = true)
    private Integer bsm_BatteryTempState;

    /**
     * 动力蓄电池绝缘状态 0x00-正常，0x01-不正常，0x10-不可信状态
     */
    @ApiModelProperty(value = "动力蓄电池绝缘状态", required = true)
    private Integer bsm_InsulationState;

    /**
     * 动力蓄电池组输出连接器连接状态 0x00-正常，0x01-不正常，0x10-不可信状态
     */
    @ApiModelProperty(value = "动力蓄电池组输出连接器连接状态", required = true)
    private Integer bsm_BatteryOutputConnectorState;

    /**
     * 充电模式 0x01-恒压充电；0x02-恒流充电
     */
    @ApiModelProperty(value = "充电模式", required = true)
    private Integer chargeMode;

    /**
     * 允许充电 0x00-禁止，0x01-允许
     */
    @ApiModelProperty(value = "允许充电", required = true)
    private Integer bsm_allow_charge;

}
