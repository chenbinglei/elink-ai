package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/1918:42
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "订单信息实体类")
public class ChargeOrderInfoDto {

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
     * 接口编码
     */
    @Schema(description = "接口编码")
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
     * 车辆唯一识别码
     */
    @Schema(description = "车辆唯一识别码")
    @JSONField(name = "VIN")
    private String vin;

    /**
     * 用户手机号
     */
    @Schema(description = "用户手机号")
    @JSONField(name = "Phone")
    private String phone;

    /**
     * 总金额 单位：元
     */
    @Schema(description = "总金额")
    @JSONField(name = "TotalMoney")
    private Double totalMoney = 0.0;

    /**
     * 总电费 单位：元
     */
    @Schema(description = "总电费")
    @JSONField(name = "TotalElecMoney")
    private Double totalElecMoney = 0.0;

    /**
     * 总服务费 单位：元
     */
    @Schema(description = "总服务费")
    @JSONField(name = "TotalServiceMoney")
    private Double totalServiceMoney = 0.0;

    /**
     * 累计充电量 单位kWh
     */
    @Schema(description = "累计充电量")
    @JSONField(name = "TotalElect")
    private Double totalElect = 0.0;

    /**
     * 尖阶段电量 单位kWh
     */
    @Schema(description = "尖阶段电量")
    @JSONField(name = "CuspElect")
    private Double cuspElect;

    /**
     * 峰阶段电量 单位kWh
     */
    @Schema(description = "峰阶段电量")
    @JSONField(name = "PeakElect")
    private Double peakElect;

    /**
     * 平阶段电量 单位kWh
     */
    @Schema(description = "平阶段电量")
    @JSONField(name = "FlatElect")
    private Double flatElect;

    /**
     * 谷阶段电量 单位kWh
     */
    @Schema(description = "谷阶段电量")
    @JSONField(name = "ValleyElect")
    private Double valleyElect;

    /**
     * 本次充电开始时间 (是)
     */
    @Schema(description = "本次充电开始时间")
    @JSONField(name = "StartTime")
    private String startTime;

    /**
     * 本次充电结束时间 (是)
     */
    @Schema(description = "本次充电结束时间")
    @JSONField(name = "EndTime")
    private String endTime;

    /**
     * 支付金额
     */
    @Schema(description = "支付金额")
    @JSONField(name = "PaymentAmount")
    private Double paymentAmount;

    /**
     * 支付时间
     */
    @Schema(description = "支付时间")
    @JSONField(name = "PayTime")
    private String payTime;

    /**
     * 支付方式
     * 1：支付宝
     * 2：微信支付
     * 3：交通卡
     * 4：预充卡
     * 5：银联
     * 6：其他自定义
     */
    @Schema(description = "支付方式")
    @JSONField(name = "PayChannel")
    private Integer payChannel;

    /**
     * 优惠信息描述
     * 描述支付的相关优惠信息，如优惠券，折扣等
     */
    @Schema(description = "优惠信息描述")
    @JSONField(name = "DiscountInfo")
    private String discountInfo;

    /**
     * 充电结束原因
     * 0：用户手动停止充电
     * 1：客户归属地运营商平台停止充电
     * 2：BMS 停止充电
     * 3：充电机设备故障
     * 4：连接器断开
     * 其他：自定义
     */
    @Schema(description = "充电结束原因")
    @JSONField(name = "StopReason")
    private String stopReason;

    /**
     * 充电结束原因描述
     * 充电结束原因为自定义时的含义描述
     */
    @Schema(description = "充电结束原因描述")
    @JSONField(name = "StopDesc")
    private String stopDesc;

    /**
     * 时段数N
     * 范围 0-32
     */
    @Schema(description = "时段数N")
    @JSONField(name = "SumPeriod")
    private Integer sumPeriod;

    /**
     * 充电明细信息
     */
    @Schema(description = "充电明细信息")
    @JSONField(name = "ChargeDetails")
    private List<ChargeDetailsDto> chargeDetails;

    /**
     * 推送时间
     * 充电订单推送至平台运营商的时间，格式 yyyy-MM-ddHH:mm:ss
     */
    @Schema(description = "推送时间")
    @JSONField(name = "PushTime")
    private String pushTime;

}
