package com.sunmax.together.dto.operation.storageCount;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "储能充放电量统计返回实体类")
public class StorageQtCountDto {

    /**
     * 充电量(总)
     */
    @Schema(description = "充电量(总)")
    private Double chargeQt = 0.0;

    /**
     * 充电量(尖)
     */
    @Schema(description = "充电量(尖)")
    private Double topChargeQt = 0.0;

    /**
     * 充电量(峰)
     */
    @Schema(description = "充电量(峰)")
    private Double peakChargeQt = 0.0;

    /**
     * 充电量(平)
     */
    @Schema(description = "充电量(平)")
    private Double plainChargeQt = 0.0;

    /**
     * 充电量(谷)
     */
    @Schema(description = "充电量(谷)")
    private Double valleyChargeQt = 0.0;

    /**
     * 充电量(深谷)
     */
    @Schema(description = "充电量(深谷)")
    private Double deepChargeQt = 0.0;

    /**
     * 放电量(总)
     */
    @Schema(description = "放电量(总)")
    private Double dischargeQt = 0.0;

    /**
     * 放电量(尖)
     */
    @Schema(description = "放电量(尖)")
    private Double topDischargeQt = 0.0;

    /**
     * 放电量(峰)
     */
    @Schema(description = "放电量(峰)")
    private Double peakDischargeQt = 0.0;

    /**
     * 放电量(平)
     */
    @Schema(description = "放电量(平)")
    private Double plainDischargeQt = 0.0;

    /**
     * 放电量(谷)
     */
    @Schema(description = "放电量(谷)")
    private Double valleyDischargeQt = 0.0;

    /**
     * 放电量(深谷)
     */
    @Schema(description = "放电量(深谷)")
    private Double deepDischargeQt = 0.0;

    /**
     * 日期列表
     */
    @Schema(description = "日期列表")
    private List<String> dateList = Lists.newArrayList();

    /**
     * 充电量(尖)列表
     */
    @Schema(description = "充电量(尖)列表")
    private List<Double> topChargeQtList = Lists.newArrayList();

    /**
     * 充电量(峰)列表
     */
    @Schema(description = "充电量(峰)列表")
    private List<Double> peakChargeQtList = Lists.newArrayList();

    /**
     * 充电量(平)列表
     */
    @Schema(description = "充电量(平)列表")
    private List<Double> plainChargeQtList = Lists.newArrayList();

    /**
     * 充电量(谷)列表
     */
    @Schema(description = "充电量(谷)列表")
    private List<Double> valleyChargeQtList = Lists.newArrayList();

    /**
     * 充电量(深谷)列表
     */
    @Schema(description = "充电量(深谷)列表")
    private List<Double> deepChargeQtList = Lists.newArrayList();

    /**
     * 放电量(尖)列表
     */
    @Schema(description = "放电量(尖)列表")
    private List<Double> topDischargeQtList = Lists.newArrayList();

    /**
     * 放电量(峰)列表
     */
    @Schema(description = "放电量(峰)列表")
    private List<Double> peakDischargeQtList = Lists.newArrayList();

    /**
     * 放电量(平)列表
     */
    @Schema(description = "放电量(平)列表")
    private List<Double> plainDischargeQtList = Lists.newArrayList();

    /**
     * 放电量(谷)列表
     */
    @Schema(description = "放电量(谷)列表")
    private List<Double> valleyDischargeQtList = Lists.newArrayList();

    /**
     * 放电量(深谷)列表
     */
    @Schema(description = "放电量(深谷)列表")
    private List<Double> deepDischargeQtList = Lists.newArrayList();

}
