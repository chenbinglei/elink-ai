package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @Date: 2023/9/1918:42
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "SupChargeOrderInfoDto", description = "订单信息实体类")
public class ChargeOrderInfoDto {

    /**
     * 平台运营商ID
     */
    @ApiModelProperty(value = "平台运营商ID", required = true)
    @JSONField(name = "OperatorID")
    private String operatorId;

    /**
     * 充电服务运营商 ID，
     * 所属方为个人时填写 999999999
     */
    @ApiModelProperty(value = "充电服务运营商ID", required = true)
    @JSONField(name = "EquipmentOwnerID")
    private String equipmentOwnerId;

    /**
     * 充电站ID
     */
    @ApiModelProperty(value = "充电站ID", required = true)
    @JSONField(name = "StationID")
    private String stationId;

    /**
     * 充电设备编码
     */
    @ApiModelProperty(value = "充电设备编码", required = true)
    @JSONField(name = "EquipmentID")
    private String equipmentId;

    /**
     * 接口编码
     */
    @ApiModelProperty(value = "接口编码", required = true)
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 充电订单号
     */
    @ApiModelProperty(value = "充电订单号", required = true)
    @JSONField(name = "OrderNo")
    private String orderNo;

    /**
     * 车牌号
     */
    @ApiModelProperty(value = "车牌号")
    @JSONField(name = "LicensePlate")
    private String licensePlate;

    /**
     * 车辆唯一识别码
     */
    @ApiModelProperty(value = "车辆唯一识别码")
    @JSONField(name = "VIN")
    private String vin;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @JSONField(name = "Phone")
    private String phone;

    /**
     * 总金额 单位：元
     */
    @ApiModelProperty(value = "总金额", required = true)
    @JSONField(name = "TotalMoney")
    private Double totalMoney = 0.0;

    /**
     * 总电费 单位：元
     */
    @ApiModelProperty(value = "总电费", required = true)
    @JSONField(name = "TotalElecMoney")
    private Double totalElecMoney = 0.0;

    /**
     * 总服务费 单位：元
     */
    @ApiModelProperty(value = "总服务费", required = true)
    @JSONField(name = "TotalServiceMoney")
    private Double totalServiceMoney = 0.0;

    /**
     * 累计充电量 单位kWh
     */
    @ApiModelProperty(value = "累计充电量", required = true)
    @JSONField(name = "TotalElect")
    private Double totalElect = 0.0;

    /**
     * 尖阶段电量 单位kWh
     */
    @ApiModelProperty(value = "尖阶段电量")
    @JSONField(name = "CuspElect")
    private Double cuspElect;

    /**
     * 峰阶段电量 单位kWh
     */
    @ApiModelProperty(value = "峰阶段电量")
    @JSONField(name = "PeakElect")
    private Double peakElect;

    /**
     * 平阶段电量 单位kWh
     */
    @ApiModelProperty(value = "平阶段电量")
    @JSONField(name = "FlatElect")
    private Double flatElect;

    /**
     * 谷阶段电量 单位kWh
     */
    @ApiModelProperty(value = "谷阶段电量")
    @JSONField(name = "ValleyElect")
    private Double valleyElect;

    /**
     * 本次充电开始时间 (是)
     */
    @ApiModelProperty(value = "本次充电开始时间", required = true)
    @JSONField(name = "StartTime")
    private String startTime;

    /**
     * 本次充电结束时间 (是)
     */
    @ApiModelProperty(value = "本次充电结束时间", required = true)
    @JSONField(name = "EndTime")
    private String endTime;

    /**
     * 支付金额
     */
    @ApiModelProperty(value = "支付金额")
    @JSONField(name = "PaymentAmount")
    private Double paymentAmount;

    /**
     * 支付时间
     */
    @ApiModelProperty(value = "支付时间")
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
    @ApiModelProperty(value = "支付方式", required = true)
    @JSONField(name = "PayChannel")
    private Integer payChannel;

    /**
     * 优惠信息描述
     * 描述支付的相关优惠信息，如优惠券，折扣等
     */
    @ApiModelProperty(value = "优惠信息描述")
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
    @ApiModelProperty(value = "充电结束原因", required = true)
    @JSONField(name = "StopReason")
    private String stopReason;

    /**
     * 充电结束原因描述
     * 充电结束原因为自定义时的含义描述
     */
    @ApiModelProperty(value = "充电结束原因描述")
    @JSONField(name = "StopDesc")
    private String stopDesc;

    /**
     * 时段数N
     * 范围 0-32
     */
    @ApiModelProperty(value = "时段数N")
    @JSONField(name = "SumPeriod")
    private Integer sumPeriod;

    /**
     * 充电明细信息
     */
    @ApiModelProperty(value = "充电明细信息")
    @JSONField(name = "ChargeDetails")
    private List<ChargeDetailsDto> chargeDetails;

    /**
     * 推送时间
     * 充电订单推送至平台运营商的时间，格式 yyyy-MM-ddHH:mm:ss
     */
    @ApiModelProperty(value = "推送时间", required = true)
    @JSONField(name = "PushTime")
    private String pushTime;

}
