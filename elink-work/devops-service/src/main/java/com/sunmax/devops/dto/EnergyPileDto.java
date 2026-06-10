package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "电能趋势-电桩返回实体类")
public class EnergyPileDto {

    /**
     * 电桩充电量
     */
    @Schema(description = "电桩充电量")
    private Double chargeQt = 0.0;

    /**
     * 电桩放电量
     */
    @Schema(description = "电桩放电量")
    private Double dischargeQt = 0.0;

    /**
     * 电桩充电金额
     */
    @Schema(description = "电桩充电金额")
    private BigDecimal chargeMoney = BigDecimal.ZERO;

    /**
     * 电桩充电次数
     */
    @Schema(description = "电桩充电次数")
    private Integer chargeCount = 0;

    /**
     * 电桩曲线数据1 日-(前一日功率) 月、年、总 -(电桩充电量)
     */
    @Schema(description = "电桩曲线数据1 日-(前一日功率) 月、年、总 -(电桩充电量)")
    private List<Double> curve1List = Lists.newArrayList();

    /**
     * 电桩曲线数据2 日-(当日功率) 月、年、总 -(电桩放电量)
     */
    @Schema(description = "电桩曲线数据2 日-(当日功率) 月、年、总 -(电桩放电量)")
    private List<Double> curve2List = Lists.newArrayList();

    /**
     * 时间列表
     */
    @Schema(description = "时间列表")
    private List<String> timeList = Lists.newArrayList();

}
