package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "充电订单状态信息实体类")
public class EquipChargeStatusDto {

    /**
     * 平台运营商ID
     */
    @Schema(description = "平台运营商ID")
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 充电服务运营商 ID，
     * 所属方为个人时填写 999999999
     */
    @Schema(description = "充电服务运营商ID")
    @JSONField(name = "EquipmentOwnerID")
    private String equipmentOwnerId;

    /**
     * 充电站ID
     */
    @Schema(description = "充电站ID")
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 充电设备编码
     */
    @Schema(description = "充电设备编码")
    @JSONField(name = "EquipmentID")
    private String equipmentId;

    /**
     * 充电设备接口编码
     */
    @Schema(description = "充电设备接口编码")
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 充电订单号
     */
    @Schema(description = "充电订单号")
    @JSONField(name = "OrderNo")
    private String orderNo;

    /**
     * 车牌号
     */
    @Schema(description = "车牌号")
    @JSONField(name = "LicensePlate")
    private String licensePlate;

    /**
     * 车牌唯一识别码
     */
    @Schema(description = "车牌唯一识别码")
    @JSONField(name = "VIN")
    private String vin;

    /**
     * 充电订单状态
     * 1：启动中
     * 2：充电中
     * 3：停止中
     * 4：充电完成
     * 5：订单挂起
     * 6：充电异常结束
     * 7：启动失败
     */
    @Schema(description = "充电订单状态")
    @JSONField(name = "OrderStatus")
    private Integer orderStatus;

    /**
     * 推送时间
     * yyyy-MM-dd HH:mm:ss，充电设备推送给运营商平台的时间
     */
    @Schema(description = "推送时间")
    @JSONField(name = "PushTimeStamp")
    private String pushTimeStamp;

    /**
     * 充电设备状态
     * 1：空闲;
     * 2：占用(未充电)
     * 3：占用(充电中)
     * 4：占用(预约锁定)
     * 255：故障
     */
    @Schema(description = "充电设备状态")
    @JSONField(name = "ConnectorStatus")
    private Integer connectorStatus;

    /**
     * A相电流
     */
    @Schema(description = "A相电流")
    @JSONField(name = "CurrentA")
    private Double currentA = 0.0;

    /**
     * B相电流
     */
    @Schema(description = "B相电流")
    @JSONField(name = "CurrentB")
    private Double currentB = 0.0;

    /**
     * C相电流
     */
    @Schema(description = "C相电流")
    @JSONField(name = "CurrentC")
    private Double currentC = 0.0;

    /**
     * A相电压
     */
    @Schema(description = "A相电压")
    @JSONField(name = "VoltageA")
    private Double voltageA = 0.0;

    /**
     * B相电压
     */
    @Schema(description = "B相电压")
    @JSONField(name = "VoltageB")
    private Double voltageB = 0.0;

    /**
     * C相电压
     */
    @Schema(description = "C相电压")
    @JSONField(name = "VoltageC")
    private Double voltageC = 0.0;

    /**
     * 电池剩余电量 0-100
     */
    @Schema(description = "电池剩余电量")
    @JSONField(name = "SOC")
    private Double soc = 0.0;

    /**
     * 开始充电时间
     */
    @Schema(description = "开始充电时间")
    @JSONField(name = "StartTime")
    private String startTime;

    /**
     * 本次采样时间
     */
    @Schema(description = "本次采样时间")
    @JSONField(name = "EndTime")
    private String endTime;

    /**
     * 累计充电量 单位度
     */
    @Schema(description = "累计充电量")
    @JSONField(name = "TotalPower")
    private Double totalPower;

    /**
     * 累计电费 单位：度
     */
    @Schema(description = "累计电费")
    @JSONField(name = "ElecMoney")
    private Double ElecMoney;

    /**
     * 累计服务费 单位：元
     */
    @Schema(description = "累计服务费")
    @JSONField(name = "ServiceMoney")
    private Double serviceMoney;

    /**
     * 累计总金额 单位：元
     */
    @Schema(description = "累计总金额")
    @JSONField(name = "TotalMoney")
    private Double totalMoney;

    /**
     * 时段数 N
     */
    @Schema(description = "时段数")
    @JSONField(name = "SumPeriod")
    private Integer sumPeriod;

    /**
     * 充电明细信息
     */
    @Schema(description = "充电明细信息")
    @JSONField(name = "ChargeDetails")
    private String chargeDetails;

    /**
     * 上报时间
     */
    @Schema(description = "上报时间")
    @JSONField(name = "eventTime")
    private String eventTime;

    /**
     * 需求电压
     * 数据分辨率：0.1V/位
     */
    @Schema(description = "需求电压")
    @JSONField(name = "bclNeedVoltage")
    private Integer bclNeedVoltage;

    /**
     * 需求电流
     * 数据分辨率：0.1A/位
     * 偏移量：-400A
     */
    @Schema(description = "需求电流")
    @JSONField(name = "bclNeedCurrent")
    private Integer bclNeedCurrent;

    /**
     * 输出电压
     * 数据分辨率：0.1V/位
     */
    @Schema(description = "输出电压")
    @JSONField(name = "chargeVoltage")
    private Integer chargeVoltage = 0;

    /**
     * 输出电流
     * 数据分辨率：0.1A/位
     * 偏移量：-400A
     */
    @Schema(description = "输出电流")
    @JSONField(name = "chargeCurrent")
    private Integer chargeCurrent = 0;

    /**
     * 最高单体动力蓄电池电压
     * 数据分辨率：0.01V/位
     */
    @Schema(description = "最高单体动力蓄电池电压")
    @JSONField(name = "bcsCellMaxVoltage")
    private Integer bcsCellMaxVoltage;

    /**
     * 最高动力蓄电池温度
     * 偏移量：-50A
     */
    @Schema(description = "最高动力蓄电池温度")
    @JSONField(name = "bsmMaxTemperature")
    private Integer bsmMaxTemperature;

    /**
     * 最低动力蓄电池温度
     * 偏移量：-50A
     */
    @Schema(description = "最低动力蓄电池温度")
    @JSONField(name = "bsmMinTemperature")
    private Integer bsmMinTemperature;

}
