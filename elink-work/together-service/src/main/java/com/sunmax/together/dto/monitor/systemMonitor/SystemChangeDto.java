package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "SystemChangeDto", description = "换电系统数据返回实体类")
public class SystemChangeDto {

    /**
     * 总功率
     */
    @ApiModelProperty(value = "总功率")
    private Double totalPower = 0.0;

    /**
     * 总充电量
     */
    @ApiModelProperty(value = "总充电量")
    private Double totalChargeQt = 0.0;

    /**
     * 总耗电量
     */
    @ApiModelProperty(value = "总耗电量")
    private Double totalDischargeQt = 0.0;

    /**
     * 可换SOC
     */
    @ApiModelProperty(value = "可换SOC")
    private Double soc = 0.0;

    /**
     * 今日换电次数
     */
    @ApiModelProperty(value = "今日换电次数")
    private Integer dayChangeCount = 0;

    /**
     * 充电能耗占比
     */
    @ApiModelProperty(value = "充电能耗占比")
    private Double chargeEnergyRatio = 0.0;

}
