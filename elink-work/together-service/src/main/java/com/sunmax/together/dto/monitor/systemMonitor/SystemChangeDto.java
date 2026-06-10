package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "换电系统数据返回实体类")
public class SystemChangeDto {

    /**
     * 总功率
     */
    @Schema(description = "总功率")
    private Double totalPower = 0.0;

    /**
     * 总充电量
     */
    @Schema(description = "总充电量")
    private Double totalChargeQt = 0.0;

    /**
     * 总耗电量
     */
    @Schema(description = "总耗电量")
    private Double totalDischargeQt = 0.0;

    /**
     * 可换SOC
     */
    @Schema(description = "可换SOC")
    private Double soc = 0.0;

    /**
     * 今日换电次数
     */
    @Schema(description = "今日换电次数")
    private Integer dayChangeCount = 0;

    /**
     * 充电能耗占比
     */
    @Schema(description = "充电能耗占比")
    private Double chargeEnergyRatio = 0.0;

}
