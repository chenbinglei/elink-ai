package com.sunmax.configure.dto.storage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "推送储能平台统计数据实体类")
public class StationArchiveDto {

    /**
     * 项目编号
     */
    @Schema(description = "项目编号")
    private String stationNo;

    /**
     * 数据日期 日期格式：yyyyMMdd【统计数据日期】
     */
    @Schema(description = "数据日期")
    private String dataDate;

    /**
     * 日充电量 单位：kWh，精度：4位小数
     */
    @Schema(description = "日充电量")
    private Float chaEnergyDay = 0f;

    /**
     * 日放电量 单位：kWh，精度：4位小数
     */
    @Schema(description = "日放电量")
    private Float disEnergyDay = 0f;

    /**
     * 月充电量 单位：kWh，精度：4位小数
     */
    @Schema(description = "月充电量")
    private Float chaEnergyMonth = 0f;

    /**
     * 月放电量 单位：kWh，精度：4位小数
     */
    @Schema(description = "月放电量")
    private Float disEnergyMonth = 0f;

    /**
     * 累计充电量 单位：kWh，精度：4位小数
     */
    @Schema(description = "累计充电量")
    private Float chaEnergyTotal = 0f;

    /**
     * 累计放电量 单位：kWh，精度：4位小数
     */
    @Schema(description = "累计放电量")
    private Float disEnergyTotal = 0f;

    /**
     * 数据来源 00-实时数据 01-补传数据 10-工单重传数据
     */
    @Schema(description = "数据来源 00-实时数据 01-补传数据 10-工单重传数据")
    private String dataType;

    /**
     * 工单编号（dataType=10时为必填项）
     */
    @Schema(description = "工单编号")
    private String orderNo;

}
