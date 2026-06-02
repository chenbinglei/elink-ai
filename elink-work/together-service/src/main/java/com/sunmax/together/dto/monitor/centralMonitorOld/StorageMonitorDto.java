package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StorageMonitorDto", description = "储能电站监测信息返回实体类")
public class StorageMonitorDto {

    /**
     * pcs总额定功率
     */
    @ApiModelProperty(value = "pcs总额定功率")
    private Double pcsTotalPower;

    /**
     * 电池簇总额定容量
     */
    @ApiModelProperty(value = "电池簇总额定容量")
    private Double batteryTotalCapacity;

    /**
     * pcs数量
     */
    @ApiModelProperty(value = "pcs数量")
    private Integer pcsNum;

    /**
     * 电池簇数量
     */
    @ApiModelProperty(value = "电池簇数量")
    private Integer batteryNum;

    /**
     * 电池包数量
     */
    @ApiModelProperty(value = "电池包数量")
    private Integer batteryPackNum;

    /**
     * 电芯数量
     */
    @ApiModelProperty(value = "电芯数量")
    private Integer batteryCellNum;

    /**
     * 充放电倍率
     */
    @ApiModelProperty(value = "充放电倍率")
    private Double chargeMagnification;

    /**
     * 昨日系统效率
     */
    @ApiModelProperty(value = "昨日系统效率")
    private Double lastDayEff;

    /**
     * 累计充电量
     */
    @ApiModelProperty(value = "累计充电量")
    private Double sumChargeQt;

    /**
     * 累计放电量
     */
    @ApiModelProperty(value = "累计放电量")
    private Double sumDischargeQt;

    /**
     * 昨日充电量
     */
    @ApiModelProperty(value = "昨日充电量")
    private Double lastDayChargeQt;

    /**
     * 昨日放电量
     */
    @ApiModelProperty(value = "昨日放电量")
    private Double lastDayDischargeQt;

    /**
     * 累计充放电循环次数
     */
    @ApiModelProperty(value = "累计充放电循环次数")
    private Integer sumChargeCycleNum;

    /**
     * 实际功率
     */
    @ApiModelProperty(value = "实际功率")
    private Double realPower;
}
