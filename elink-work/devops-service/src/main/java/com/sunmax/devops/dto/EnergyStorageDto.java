package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "电能趋势-储能返回实体类")
public class EnergyStorageDto {

    /**
     * 储能充电量
     */
    @Schema(description = "储能充电量")
    private Double chargeQt = 0.0;

    /**
     * 储能放电量
     */
    @Schema(description = "储能放电量")
    private Double dischargeQt = 0.0;

    /**
     * 储能曲线数据1 日-(前一日功率) 月、年、总 -(储能充电量)
     */
    @Schema(description = "储能曲线数据1 日-(前一日功率) 月、年、总 -(储能充电量)")
    private List<Double> curve1List = Lists.newArrayList();

    /**
     * 储能曲线数据2 日-(当日功率) 月、年、总 -(储能放电量)
     */
    @Schema(description = "储能曲线数据2 日-(当日功率) 月、年、总 -(储能放电量)")
    private List<Double> curve2List = Lists.newArrayList();

    /**
     * 时间列表
     */
    @Schema(description = "时间列表")
    private List<String> timeList = Lists.newArrayList();

}
