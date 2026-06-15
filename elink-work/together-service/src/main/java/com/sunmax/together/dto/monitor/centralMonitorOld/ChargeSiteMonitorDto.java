package com.sunmax.together.dto.monitor.centralMonitorOld;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "充电站监测信息返回实体类")
public class ChargeSiteMonitorDto {

    /**
     * 装机容量
     */
    @Schema(description = "装机容量")
    private Double capacity;

    /**
     * 电桩数量
     */
    @Schema(description = "电桩数量")
    private Integer pileNum;

    /**
     * 直流桩数量
     */
    @Schema(description = "直流桩数量")
    private Integer acPileNum;

    /**
     * 直流枪数量
     */
    @Schema(description = "直流枪数量")
    private Integer acGunNum;

    /**
     * 交流桩数量
     */
    @Schema(description = "交流桩数量")
    private Integer dcPileNum;

    /**
     * 交流枪数量
     */
    @Schema(description = "交流枪数量")
    private Integer dcGunNum;

    /**
     * 累计充电量
     */
    @Schema(description = "累计充电量")
    private Double sumChargeQt;

    /**
     * 今日充电量
     */
    @Schema(description = "今日充电量")
    private Double dayChargeQt;

    /**
     * 昨日充电量
     */
    @Schema(description = "昨日充电量")
    private Double lastDayChargeQt;

    /**
     * 累计V2G电量
     */
    @Schema(description = "累计V2G电量")
    private Double sumV2gQt;

    /**
     * 今日V2G电量
     */
    @Schema(description = "今日V2G电量")
    private Double dayV2gQt;

    /**
     * 昨日V2G电量
     */
    @Schema(description = "昨日V2G电量")
    private Double lastDayV2gQt;

    /**
     * 实际功率
     */
    @Schema(description = "实际功率")
    private Double realPower;
}
