package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "储能系统数据返回实体类")
public class SystemSeDto {

    /**
     * 装机容量
     */
    @Schema(description = "装机容量")
    private Double capacity;

    /**
     * pcs数量
     */
    @Schema(description = "pcs数量")
    private Integer pcsNum;

    /**
     * 电池簇数量
     */
    @Schema(description = "电池簇数量")
    private Integer batteryNum;

    /**
     * 电池包数量
     */
    @Schema(description = "电池包数量")
    private Integer batteryPackNum;

    /**
     * 电芯数量
     */
    @Schema(description = "电芯数量")
    private Integer batteryCellNum;

    /**
     * 充放电倍率
     */
    @Schema(description = "充放电倍率")
    private Double chargeMagnification;

    /**
     * 昨日系统效率
     */
    @Schema(description = "昨日系统效率")
    private Double lastDayEff;

    /**
     * 额定功率
     */
    @Schema(description = "额定功率")
    private Double ratedPower;

    /**
     * 累计充电量
     */
    @Schema(description = "累计充电量")
    private Double sumChargeQt;

    /**
     * 累计放电量
     */
    @Schema(description = "累计放电量")
    private Double sumDischargeQt;

    /**
     * 累计充放电循环次数
     */
    @Schema(description = "累计充放电循环次数")
    private Double sumChargeCycleNum;

    /**
     * 总功率
     */
    @Schema(description = "总功率")
    private Double totalPower;

    /**
     * 今日充电量
     */
    @Schema(description = "今日充电量")
    private Double dayChargeQt;

    /**
     * 今日放电量
     */
    @Schema(description = "今日放电量")
    private Double dayDischargeQt;

    /**
     * SOC
     */
    @Schema(description = "SOC")
    private Double soc;

    /**
     * 近30日综合效率 近30天总放电量/近30日总充电量
     */
    @Schema(description = "近30日综合效率")
    private Double lastDay30Eff;

}
