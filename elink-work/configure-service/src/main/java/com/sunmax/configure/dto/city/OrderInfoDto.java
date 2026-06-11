package com.sunmax.configure.dto.city;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/9/1918:42
 * @version: 1.0
 * @注释:
 */
@Data
@Schema(description = "订单信息实体类")
public class OrderInfoDto {

    /**
     * 接口编码 (是)
     */
    @Schema(description = "接口编码")
    @JSONField(name = "ConnectorID")
    private String connectorId;

    /**
     * 充电订单号 (是)
     */
    @Schema(description = "充电订单号")
    @JSONField(name = "StartChargeSeq")
    private String startChargeSeq;

    /**
     * 充电开始时间 (是)
     */
    @Schema(description = "充电开始时间")
    @JSONField(name = "StartTime")
    private String startTime;

    /**
     * 充电结束时间 (是)
     */
    @Schema(description = "充电结束时间")
    @JSONField(name = "EndTime")
    private String endTime;

    /**
     * 累计充电量 (是)
     */
    @Schema(description = "累计充电量")
    @JSONField(name = "TotalPower")
    private Double totalPower = 0.0;

    /**
     * 尖电量 (是)
     */
    @Schema(description = "尖电量")
    @JSONField(name = "ToppkPower")
    private Double toppkPower = 0.0;

    /**
     * 峰电量 (是)
     */
    @Schema(description = "峰电量")
    @JSONField(name = "PeakPower")
    private Double peakPower = 0.0;

    /**
     * 平电量 (是)
     */
    @Schema(description = "平电量")
    @JSONField(name = "FlatPower")
    private Double flatPower = 0.0;

    /**
     * 谷电量 (是)
     */
    @Schema(description = "谷电量")
    @JSONField(name = "ValleyPower")
    private Double valleyPower = 0.0;

    /**
     * 总电费 (是)
     */
    @Schema(description = "总电费")
    @JSONField(name = "TotalElecMoney")
    private Double totalElecMoney = 0.0;

    /**
     * 总服务费 (是)
     */
    @Schema(description = "总服务费")
    @JSONField(name = "TotalSeviceMoney")
    private Double totalSeviceMoney = 0.0;

    /**
     * 累计总金额 (是)
     */
    @Schema(description = "累计总金额")
    @JSONField(name = "TotalMoney")
    private Double totalMoney = 0.0;

    /**
     * 充电结束原因 (是)
     */
    @Schema(description = "充电结束原因")
    @JSONField(name = "StopReason")
    private Integer stopReason;

    /**
     * 时段数N (否)
     */
    @Schema(description = "时段数N")
    @JSONField(name = "SumPeriod")
    private Integer sumPeriod;

    /**
     * 充电明细信息 (否)
     */
    @Schema(description = "充电明细信息")
    @JSONField(name = "ChargeDetails")
    private ChargeDetailsDto chargeDetails;
}
