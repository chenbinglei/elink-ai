package com.sunmax.configure.vo.city;

import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChargeDetailsVo {

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
    @JSONField(name = "SevicePrice")
    private Double sevicePrice;

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
    @JSONField(name = "DetailSeviceMoney")
    private Double detailSeviceMoney;



}
