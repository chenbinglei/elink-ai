package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@Schema(description = "充电枪功率数据返回实体类")
public class PileGunPowerDto {

    /**
     * 桩编号
     */
    @Schema(description = "桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @Schema(description = "枪编号")
    private String gunCode;

    /**
     * 枪名称
     */
    @Schema(description = "枪名称")
    private String gunName;

    /**
     * 今日充电量
     */
    @Schema(description = "今日充电量")
    private Double dayChargeQt;

    /**
     * 今日放电量
     */
    @Schema(description = "今日放电量")
    private Double dayV2gQt;

    /**
     * 时间列表
     */
    @Schema(description = "时间列表")
    private List<String> timeList = Lists.newArrayList();

    /**
     * 需求功率列表
     */
    @Schema(description = "需求功率列表")
    private List<Double> reqPowerList = Lists.newArrayList();

    /**
     * 输出功率列表
     */
    @Schema(description = "输出功率列表")
    private List<Double> outPowerList = Lists.newArrayList();

}
