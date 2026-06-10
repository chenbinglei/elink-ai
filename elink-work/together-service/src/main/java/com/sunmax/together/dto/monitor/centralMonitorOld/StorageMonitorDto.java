package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "储能电站监测信息返回实体类")
public class StorageMonitorDto {

    /**
     * pcs总额定功率
     */
    @Schema(description = "pcs总额定功率")
    private Double pcsTotalPower;

    /**
     * 电池簇总额定容量
     */
    @Schema(description = "电池簇总额定容量")
    private Double batteryTotalCapacity;

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
     * 昨日充电量
     */
    @Schema(description = "昨日充电量")
    private Double lastDayChargeQt;

    /**
     * 昨日放电量
     */
    @Schema(description = "昨日放电量")
    private Double lastDayDischargeQt;

    /**
     * 累计充放电循环次数
     */
    @Schema(description = "累计充放电循环次数")
    private Integer sumChargeCycleNum;

    /**
     * 实际功率
     */
    @Schema(description = "实际功率")
    private Double realPower;
}
