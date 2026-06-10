package com.sunmax.configure.dto.province;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "SupChargeDetailsDto")
public class ChargeDetailsDto {

    /**
     * 充电订单号
     * 格式“平台运营商 ID+订单唯一编号”，即 T/CEC 102定义的 StartChargeSeq
     */
    @Schema(description = "充电订单号")
    @JSONField(name = "OrderNo")
    private String orderNo;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    @JSONField(name = "DetailStartTime")
    private String detailStartTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    @JSONField(name = "DetailEndTime")
    private String detailEndTime;

    /**
     * 时段电价
     */
    @Schema(description = "时段电价")
    @JSONField(name = "ElecPrice")
    private Double elecPrice;

    /**
     * 时段服务费价格
     */
    @Schema(description = "时段服务费价格")
    @JSONField(name = "ServicePrice")
    private Double servicePrice;

    /**
     * 时段充电量
     */
    @Schema(description = "时段充电量")
    @JSONField(name = "DetailPower")
    private Double detailPower;

    /**
     * 时段电费
     */
    @Schema(description = "时段电费")
    @JSONField(name = "DetailElecMoney")
    private Double detailElecMoney;

    /**
     * 时段服务费
     */
    @Schema(description = "时段服务费")
    @JSONField(name = "DetailServiceMoney")
    private Double detailServiceMoney;

}
