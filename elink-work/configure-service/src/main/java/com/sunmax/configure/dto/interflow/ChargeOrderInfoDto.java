package com.sunmax.configure.dto.interflow;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Author: yqz
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "ChargeOrderInfoDto", description = "充电订单信息实体类")
public class ChargeOrderInfoDto {

    /**
     * 接口编码 (是)
     */
    @ApiModelProperty(value = "接口编码")
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 充电订单号 (是)
     */
    @ApiModelProperty(value = "充电订单号")
    @JSONField(name = "StartChargeSeq")
    private String startChargeSeq;

    /**
     * 充电开始时间 (是)
     */
    @ApiModelProperty(value = "充电开始时间")
    @JSONField(name = "StartTime")
    private String startTime;

    /**
     * 充电结束时间 (是)
     */
    @ApiModelProperty(value = "充电结束时间")
    @JSONField(name = "EndTime")
    private String endTime;

    /**
     * 累计充电量 (是)
     */
    @ApiModelProperty(value = "累计充电量")
    @JSONField(name = "TotalPower")
    private Double totalPower = 0.0;

    /**
     * 总电费 (是)
     */
    @ApiModelProperty(value = "总电费")
    @JSONField(name = "TotalElecMoney")
    private Double totalElecMoney = 0.0;

    /**
     * 总服务费 (是)
     */
    @ApiModelProperty(value = "总服务费")
    @JSONField(name = "TotalSeviceMoney")
    private Double totalSeviceMoney = 0.0;

    /**
     * 累计总金额 (是)
     */
    @ApiModelProperty(value = "累计总金额")
    @JSONField(name = "TotalMoney")
    private Double totalMoney = 0.0;

    /**
     * 充电结束原因 (是)
     *
     * 0：用户手动停止充电；
     * 1：客户归属地运营商平台停止充电；
     * 2： BMS 停止充电；
     * 3：充电机设备故障；
     * 4：连接器断开；
     * 大于 5,自定义，未知情况
     */
    @ApiModelProperty(value = "充电结束原因")
    @JSONField(name = "StopReason")
    private Integer stopReason;

    /**
     * 时段数N (否)
     */
    @ApiModelProperty(value = "时段数N")
    @JSONField(name = "SumPeriod")
    private Integer sumPeriod;

    /**
     * Vin码 (否)
     */
    @ApiModelProperty(value = "Vin码")
    @JSONField(name = "Vin")
    private String vin;

    /**
     * 车牌号 (否)
     */
    @ApiModelProperty(value = "车牌号")
    @JSONField(name = "LicensePlate")
    private String licensePlate;

    /**
     * 充电明细信息 (否)
     */
    @ApiModelProperty(value = "充电明细信息")
    @JSONField(name = "ChargeDetails")
    private List<ChargeDetailsDto> chargeDetailsDtoList;
}
