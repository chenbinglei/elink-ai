package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "电桩系统数据返回实体类")
public class SystemPileDto {

    /**
     * 装机容量
     */
    @Schema(description = "装机容量")
    private Double capacity;

    /**
     * 电桩数量
     */
    @Schema(description = "电桩数量")
    private Integer pileNum = 0;

    /**
     * 直流桩数量
     */
    @Schema(description = "直流桩数量")
    private Integer dcPileNum = 0;

    /**
     * 直流枪数量
     */
    @Schema(description = "直流枪数量")
    private Integer dcGunNum = 0;

    /**
     * 交流桩数量
     */
    @Schema(description = "交流桩数量")
    private Integer acPileNum = 0;

    /**
     * 交流枪数量
     */
    @Schema(description = "交流枪数量")
    private Integer acGunNum = 0;

    /**
     * 累计充电量
     */
    @Schema(description = "累计充电量")
    private Double sumChargeQt = 0.0;

    /**
     * 今日充电量
     */
    @Schema(description = "今日充电量")
    private Double dayChargeQt = 0.0;

    /**
     * 昨日充电量
     */
    @Schema(description = "昨日充电量")
    private Double lastDayChargeQt = 0.0;

    /**
     * 累计V2G电量
     */
    @Schema(description = "累计V2G电量")
    private Double sumV2gQt = 0.0;

    /**
     * 今日V2G电量
     */
    @Schema(description = "今日V2G电量")
    private Double dayV2gQt = 0.0;

    /**
     * 昨日V2G电量
     */
    @Schema(description = "昨日V2G电量")
    private Double lastDayV2gQt = 0.0;

    /**
     * 总功率
     */
    @Schema(description = "总功率")
    private Double totalPower = 0.0;

    /**
     * 今日枪均电量(度)
     */
    @Schema(description = "今日枪均电量(度)")
    private Double dayAvgChargeQt;

    /**
     * 今日时间利用率(%)
     */
    @Schema(description = "今日时间利用率(%)")
    private Double dayTimeRatio;

}
