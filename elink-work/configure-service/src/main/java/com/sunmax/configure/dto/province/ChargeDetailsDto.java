package com.sunmax.configure.dto.province;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "充电订单明细信息实体类", description = "SupChargeDetailsDto")
public class ChargeDetailsDto {

    /**
     * 充电订单号
     * 格式“平台运营商 ID+订单唯一编号”，即 T/CEC 102定义的 StartChargeSeq
     */
    @ApiModelProperty(value = "充电订单号", required = true)
    @JSONField(name = "OrderNo")
    private String orderNo;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    @JSONField(name = "DetailStartTime")
    private String detailStartTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    @JSONField(name = "DetailEndTime")
    private String detailEndTime;

    /**
     * 时段电价
     */
    @ApiModelProperty(value = "时段电价")
    @JSONField(name = "ElecPrice")
    private Double elecPrice;

    /**
     * 时段服务费价格
     */
    @ApiModelProperty(value = "时段服务费价格")
    @JSONField(name = "ServicePrice")
    private Double servicePrice;

    /**
     * 时段充电量
     */
    @ApiModelProperty(value = "时段充电量", required = true)
    @JSONField(name = "DetailPower")
    private Double detailPower;

    /**
     * 时段电费
     */
    @ApiModelProperty(value = "时段电费")
    @JSONField(name = "DetailElecMoney")
    private Double detailElecMoney;

    /**
     * 时段服务费
     */
    @ApiModelProperty(value = "时段服务费")
    @JSONField(name = "DetailServiceMoney")
    private Double detailServiceMoney;

}
