package com.sunmax.protocol.vo.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 充放电记录
 */
@Data
@Schema(description = "平台充放电记录返回实体类")
public class PlatformPileRecordVo {

    /**
     * 电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 记录上报类型 0-正常 1-断网上传
     */
    @Schema(description = "记录上报类型 0-正常 1-断网上传")
    private Integer dealType;

    /**
     * 枪口标识
     */
    @Schema(description = "枪口标识")
    private Integer gunCode;

    /**
     * 充/放电接口运行模式 0-充电模式 1-放电模式
     */
    @Schema(description = "充/放电接口运行模式 0-充电模式 1-放电模式")
    private Integer itfRunPattern;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩
     */
    @Schema(description = "发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩")
    private Integer startMode;

    /**
     * 交易流水号
     */
    @Schema(description = "交易流水号")
    private String serialNum;

    /**
     * 开始充/放电时间
     */
    @Schema(description = "开始充/放电时间")
    private String startTime;

    /**
     * 结束充/放电时间
     */
    @Schema(description = "结束充/放电时间")
    private String endTime;

    /**
     * 开始直流电表读数
     */
    @Schema(description = "开始直流电表读数")
    private Double startDirMeterNum = 0.0;

    /**
     * 结束直流电表读数
     */
    @Schema(description = "结束直流电表读数")
    private Double endDirMeterNum = 0.0;

    /**
     * 开始交流电表读数
     */
    @Schema(description = "开始交流电表读数")
    private Double startAlterMeterNum = 0.0;

    /**
     * 结束交流电表读数
     */
    @Schema(description = "结束交流电表读数")
    private Double endAlterMeterNum = 0.0;

    /**
     * 本次充/放电总电量
     */
    @Schema(description = "本次充/放电总电量")
    private Double totalCurQt;

    /**
     * 本次充/放电总电费
     */
    @Schema(description = "本次充/放电总电费")
    private BigDecimal totalCost;

    /**
     * 开始 SOC 范围 0～100
     */
    @Schema(description = "开始 SOC 范围 0～100")
    private Integer startSoc;

    /**
     * 结束 SOC 范围 0～100
     */
    @Schema(description = "结束 SOC 范围 0～100")
    private Integer endSoc;

    /**
     * 停止详细原因
     */
    @Schema(description = "停止详细原因")
    private int stopDetailReason;

    /**
     * 费率模型ID
     */
    @Schema(description = "费率模型ID")
    private String rateTemplateId;

    /**
     * 车辆vin码
     */
    @Schema(description = "车辆vin码")
    private String busVin;

    /**
     * 有效时段总数 取值 0～48
     */
    @Schema(description = "有效时段总数 取值0～48")
    private Integer timeFrameNum;

    /**
     * 充/放电时段电量
     */
    @Schema(description = "充/放电时段电量")
    private List<Double> timeFrameQt;

}
