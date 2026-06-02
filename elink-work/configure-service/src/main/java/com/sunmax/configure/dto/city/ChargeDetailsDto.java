package com.sunmax.configure.dto.city;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: yqz
 * @Date: 2023/9/2015:49
 * @version: 1.0
 * @注释:
 */
@Data
@ApiModel(value = "ChargeDetailsDto", description = "充电明细信息实体类")
public class ChargeDetailsDto {

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间 ")
    @JSONField(name = "DetailStartTime")
    private String detailStartTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间")
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
    @ApiModelProperty(value = "时段充电量")
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
