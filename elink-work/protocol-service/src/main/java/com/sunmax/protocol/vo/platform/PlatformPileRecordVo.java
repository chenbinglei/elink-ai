package com.sunmax.protocol.vo.platform;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 充放电记录
 */
@Data
@ApiModel(value = "PlatformPileRecordVo", description = "平台充放电记录返回实体类")
public class PlatformPileRecordVo {

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "充电桩编号", required = true)
    private String pileCode;

    /**
     * 记录上报类型 0-正常 1-断网上传
     */
    @ApiModelProperty(value = "记录上报类型 0-正常 1-断网上传", required = true)
    private Integer dealType;

    /**
     * 枪口标识
     */
    @ApiModelProperty(value = "枪口标识", required = true)
    private Integer gunCode;

    /**
     * 充/放电接口运行模式 0-充电模式 1-放电模式
     */
    @ApiModelProperty(value = "充/放电接口运行模式 0-充电模式 1-放电模式", required = true)
    private Integer itfRunPattern;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩
     */
    @ApiModelProperty(value = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩", required = true)
    private Integer startMode;

    /**
     * 交易流水号
     */
    @ApiModelProperty(value = "交易流水号", required = true)
    private String serialNum;

    /**
     * 开始充/放电时间
     */
    @ApiModelProperty(value = "开始充/放电时间", required = true)
    private String startTime;

    /**
     * 结束充/放电时间
     */
    @ApiModelProperty(value = "结束充/放电时间", required = true)
    private String endTime;

    /**
     * 开始直流电表读数
     */
    @ApiModelProperty(value = "开始直流电表读数", required = true)
    private Double startDirMeterNum = 0.0;

    /**
     * 结束直流电表读数
     */
    @ApiModelProperty(value = "结束直流电表读数", required = true)
    private Double endDirMeterNum = 0.0;

    /**
     * 开始交流电表读数
     */
    @ApiModelProperty(value = "开始交流电表读数", required = true)
    private Double startAlterMeterNum = 0.0;

    /**
     * 结束交流电表读数
     */
    @ApiModelProperty(value = "结束交流电表读数", required = true)
    private Double endAlterMeterNum = 0.0;

    /**
     * 本次充/放电总电量
     */
    @ApiModelProperty(value = "本次充/放电总电量", required = true)
    private Double totalCurQt;

    /**
     * 本次充/放电总电费
     */
    @ApiModelProperty(value = "本次充/放电总电费", required = true)
    private BigDecimal totalCost;

    /**
     * 开始 SOC 范围 0～100
     */
    @ApiModelProperty(value = "开始 SOC 范围 0～100", required = true)
    private Integer startSoc;

    /**
     * 结束 SOC 范围 0～100
     */
    @ApiModelProperty(value = "结束 SOC 范围 0～100", required = true)
    private Integer endSoc;

    /**
     * 停止详细原因
     */
    @ApiModelProperty(value = "停止详细原因", required = true)
    private int stopDetailReason;

    /**
     * 费率模型ID
     */
    @ApiModelProperty(value = "费率模型ID", required = true)
    private String rateTemplateId;

    /**
     * 车辆vin码
     */
    @ApiModelProperty(value = "车辆vin码", required = true)
    private String busVin;

    /**
     * 有效时段总数 取值 0～48
     */
    @ApiModelProperty(value = "有效时段总数 取值0～48", required = true)
    private Integer timeFrameNum;

    /**
     * 充/放电时段电量
     */
    @ApiModelProperty(value = "充/放电时段电量", required = true)
    private List<Double> timeFrameQt;

}
