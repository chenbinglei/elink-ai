package com.sunmax.configure.dto.interflow;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "ChargeStateDto", description = "充电状态信息实体类")
public class ChargeStateDto {

    /**
     * 充电订单号
     */
    @ApiModelProperty(value = "充电订单号")
    @JSONField(name = "StartChargeSeq")
    private String startChargeSeq;

    /**
     * 充电订单状态
     * 1：启动中；
     * 2：充电中；
     * 3：停止中；
     * 4：已结束；
     * 5：未知
     */
    @ApiModelProperty(value = "充电订单状态")
    @JSONField(name = "StartChargeSeqStat")
    private Integer startChargeSeqStat;

    /**
     * 充电设备接口编码
     */
    @ApiModelProperty(value = "充电设备接口编码")
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 充电设备接口状态
     * 1：空闲；
     * 2：占用（未充电）；
     * 3：占用（充电中）；
     * 4：占用（预约锁定）；
     * 255：故障
     */
    @ApiModelProperty(value = "充电设备接口状态")
    @JSONField(name = "ConnectorStatus")
    private Integer connectorStatus;

    /**
     * A相电流
     */
    @ApiModelProperty(value = "A相电流")
    @JSONField(name = "CurrentA")
    private Double currentA = 0.0;

    /**
     * B相电流
     */
    @ApiModelProperty(value = "B相电流")
    @JSONField(name = "CurrentB")
    private Double currentB = 0.0;

    /**
     * C相电流
     */
    @ApiModelProperty(value = "C相电流")
    @JSONField(name = "CurrentC")
    private Double currentC = 0.0;

    /**
     * A相电压
     */
    @ApiModelProperty(value = "A相电压")
    @JSONField(name = "VoltageA")
    private Double voltageA = 0.0;

    /**
     * B相电压
     */
    @ApiModelProperty(value = "B相电压")
    @JSONField(name = "VoltageB")
    private Double voltageB = 0.0;

    /**
     * C相电压
     */
    @ApiModelProperty(value = "C相电压")
    @JSONField(name = "VoltageC")
    private Double voltageC = 0.0;

    /**
     * 电池剩余电量
     */
    @ApiModelProperty(value = "电池剩余电量")
    @JSONField(name = "Soc")
    private Double soc = 0.0;

    /**
     * 开始充电时间
     */
    @ApiModelProperty(value = "开始充电时间")
    @JSONField(name = "StartTime")
    private String startTime;

    /**
     * 结束充电时间
     */
    @ApiModelProperty(value = "结束充电时间")
    @JSONField(name = "EndTime")
    private String endTime;

    /**
     * 累计充电量
     */
    @ApiModelProperty(value = "累计充电量")
    @JSONField(name = "TotalPower")
    private Double totalPower;

    /**
     * 累计电费
     */
    @ApiModelProperty(value = "累计电费")
    @JSONField(name = "ElecMoney")
    private BigDecimal elecMoney;

    /**
     * 累计服务费
     */
    @ApiModelProperty(value = "累计服务费")
    @JSONField(name = "SeviceMoney")
    private BigDecimal seviceMoney;

    /**
     * 累计总金额
     */
    @ApiModelProperty(value = "累计总金额")
    @JSONField(name = "TotalMoney")
    private BigDecimal totalMoney;

    /**
     * 时段数N
     */
    @ApiModelProperty(value = "时段数N")
    @JSONField(name = "Sumperiod")
    private Integer sumperiod;

    /**
     * 需求电压
     */
    @ApiModelProperty(value = "需求电压")
    @JSONField(name = "BclNeedVoltage")
    private Double bclNeedVoltage;

    /**
     * 需求电流
     */
    @ApiModelProperty(value = "需求电流")
    @JSONField(name = "BclNeedCurrent")
    private Double BclNeedCurrent;

    /**
     * 最高动力蓄电池温度
     */
    @ApiModelProperty(value = "最高动力蓄电池温度")
    @JSONField(name = "BatteryTempMax")
    private Integer batteryTempMax;

    /**
     * 最高动力蓄电池温度
     */
    @ApiModelProperty(value = "最高动力蓄电池温度检测点编号")
    @JSONField(name = "BatteryTempMaxNo")
    private Integer batteryTempMaxNo;

    /**
     * 最高动力蓄电池温度
     */
    @ApiModelProperty(value = "最低动力蓄电池温度")
    @JSONField(name = "BatteryTempMin")
    private Integer batteryTempMin;

    /**
     * 最高动力蓄电池温度
     */
    @ApiModelProperty(value = "最低动力蓄电池温度检测点编号")
    @JSONField(name = "BatteryTempMinNo")
    private Integer batteryTempMinNo;

    /**
     * 最高动力蓄电池温度
     */
    @ApiModelProperty(value = "最高单体蓄电池电压所对应组号")
    @JSONField(name = "BatteryVoltageMaxGn")
    private Integer batteryVoltageMaxGn;

    /**
     * 最高动力蓄电池温度
     */
    @ApiModelProperty(value = "最高单体蓄电池电压 精度1V")
    @JSONField(name = "BatteryVoltageMax")
    private Double batteryVoltageMax;

    /**
     * 最高动力蓄电池温度
     */
    @ApiModelProperty(value = "最低单体蓄电池电压所对应组号")
    @JSONField(name = "BatteryVoltageMinGn")
    private Integer batteryVoltageMinGn;

    /**
     * 最高动力蓄电池温度
     */
    @ApiModelProperty(value = "最低单体蓄电池电压 精度1V")
    @JSONField(name = "BatteryVoltageMin")
    private Double batteryVoltageMin;

    /**
     * 充电明细信息
     */
    @ApiModelProperty(value = "充电明细信息")
    @JSONField(name = "ChargeDetails")
    private List<ChargeDetailsDto> chargeDetailsDtoList;
}
