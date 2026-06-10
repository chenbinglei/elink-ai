package com.sunmax.devops.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "站点关口总览曲线数据返回实体类")
public class SiteGwCurveDataDto {

    /**
     * 关口设备曲线数据1 日-(前一日功率) 月、年、总 -(关口下网电量)
     */
    @Schema(description = "关口设备曲线数据1 日-(前一日功率) 月、年、总 -(关口下网电量)")
    private List<Double> curve1List = Lists.newArrayList();

    /**
     * 关口设备曲线数据2 日-(当日功率) 月、年、总 -(关口上网电量)
     */
    @Schema(description = "关口设备曲线数据2 日-(当日功率) 月、年、总 -(关口上网电量)")
    private List<Double> curve2List = Lists.newArrayList();

    /**
     * 时间列表
     */
    @Schema(description = "时间列表")
    private List<String> timeList = Lists.newArrayList();

    /**
     * 下网电量(总)
     */
    @Schema(description = "下网电量(总)")
    public Double supKwh = 0.0;

    /**
     * 下网电量(尖时)
     */
    @Schema(description = "下网电量(尖)")
    public Double topSupKwh = 0.0;

    /**
     * 下网电量(峰时)
     */
    @Schema(description = "下网电量(峰)")
    public Double peakSupKwh = 0.0;

    /**
     * 下网电量(平时)
     */
    @Schema(description = "下网电量(平)")
    public Double plainSupKwh = 0.0;

    /**
     * 下网电量(谷时)
     */
    @Schema(description = "下网电量(谷)")
    public Double valleySupKwh = 0.0;

    /**
     * 下网电量(谷时)
     */
    @Schema(description = "下网电量(深谷)")
    public Double deepSupKwh = 0.0;

    /**
     * 上网电量(总)
     */
    @Schema(description = "上网电量(总)")
    public Double revKwh = 0.0;

    /**
     * 上网电量(尖时)
     */
    @Schema(description = "上网电量(尖)")
    public Double topRevKwh = 0.0;

    /**
     * 上网电量(峰时)
     */
    @Schema(description = "上网电量(峰)")
    public Double peakRevKwh = 0.0;

    /**
     * 上网电量(平时)
     */
    @Schema(description = "上网电量(平)")
    public Double plainRevKwh = 0.0;

    /**
     * 上网电量(谷时)
     */
    @Schema(description = "上网电量(谷)")
    public Double valleyRevKwh = 0.0;

    /**
     * 上网电量(谷时)
     */
    @Schema(description = "上网电量(深谷)")
    public Double deepRevKwh = 0.0;

}
