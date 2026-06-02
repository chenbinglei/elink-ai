package com.sunmax.configure.dto.storage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "StationArchiveDto", description = "推送储能平台统计数据实体类")
public class StationArchiveDto {

    /**
     * 项目编号
     */
    @ApiModelProperty(value = "项目编号", required = true)
    private String stationNo;

    /**
     * 数据日期 日期格式：yyyyMMdd【统计数据日期】
     */
    @ApiModelProperty(value = "数据日期", required = true)
    private String dataDate;

    /**
     * 日充电量 单位：kWh，精度：4位小数
     */
    @ApiModelProperty(value = "日充电量", required = true)
    private Float chaEnergyDay = 0f;

    /**
     * 日放电量 单位：kWh，精度：4位小数
     */
    @ApiModelProperty(value = "日放电量", required = true)
    private Float disEnergyDay = 0f;

    /**
     * 月充电量 单位：kWh，精度：4位小数
     */
    @ApiModelProperty(value = "月充电量", required = true)
    private Float chaEnergyMonth = 0f;

    /**
     * 月放电量 单位：kWh，精度：4位小数
     */
    @ApiModelProperty(value = "月放电量", required = true)
    private Float disEnergyMonth = 0f;

    /**
     * 累计充电量 单位：kWh，精度：4位小数
     */
    @ApiModelProperty(value = "累计充电量", required = true)
    private Float chaEnergyTotal = 0f;

    /**
     * 累计放电量 单位：kWh，精度：4位小数
     */
    @ApiModelProperty(value = "累计放电量", required = true)
    private Float disEnergyTotal = 0f;

    /**
     * 数据来源 00-实时数据 01-补传数据 10-工单重传数据
     */
    @ApiModelProperty(value = "数据来源 00-实时数据 01-补传数据 10-工单重传数据", required = true)
    private String dataType;

    /**
     * 工单编号（dataType=10时为必填项）
     */
    @ApiModelProperty(value = "工单编号")
    private String orderNo;

}
