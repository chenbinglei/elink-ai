package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SystemSeDto", description = "储能系统数据返回实体类")
public class SystemSeDto {

    /**
     * 装机容量
     */
    @ApiModelProperty(value = "装机容量")
    private Double capacity;

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
     * 额定功率
     */
    @ApiModelProperty(value = "额定功率")
    private Double ratedPower;

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
     * 累计充放电循环次数
     */
    @ApiModelProperty(value = "累计充放电循环次数")
    private Double sumChargeCycleNum;

    /**
     * 总功率
     */
    @ApiModelProperty(value = "总功率")
    private Double totalPower;

    /**
     * 今日充电量
     */
    @ApiModelProperty(value = "今日充电量")
    private Double dayChargeQt;

    /**
     * 今日放电量
     */
    @ApiModelProperty(value = "今日放电量")
    private Double dayDischargeQt;

    /**
     * SOC
     */
    @ApiModelProperty(value = "SOC")
    private Double soc;

    /**
     * 近30日综合效率 近30天总放电量/近30日总充电量
     */
    @ApiModelProperty(value = "近30日综合效率")
    private Double lastDay30Eff;

}
