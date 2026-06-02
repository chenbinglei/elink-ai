package com.sunmax.together.dto.monitor.systemMonitor;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

@Data
@ApiModel(value = "PileGunPowerDto", description = "充电枪功率数据返回实体类")
public class PileGunPowerDto {

    /**
     * 桩编号
     */
    @ApiModelProperty(value = "桩编号")
    private String pileCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "枪编号")
    private String gunCode;

    /**
     * 枪名称
     */
    @ApiModelProperty(value = "枪名称")
    private String gunName;

    /**
     * 今日充电量
     */
    @ApiModelProperty(value = "今日充电量")
    private Double dayChargeQt;

    /**
     * 今日放电量
     */
    @ApiModelProperty(value = "今日放电量")
    private Double dayV2gQt;

    /**
     * 时间列表
     */
    @ApiModelProperty(value = "时间列表")
    private List<String> timeList = Lists.newArrayList();

    /**
     * 需求功率列表
     */
    @ApiModelProperty(value = "需求功率列表")
    private List<Double> reqPowerList = Lists.newArrayList();

    /**
     * 输出功率列表
     */
    @ApiModelProperty(value = "输出功率列表")
    private List<Double> outPowerList = Lists.newArrayList();

}
