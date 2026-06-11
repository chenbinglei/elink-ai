package com.sunmax.common.vo.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 4.36 CMD_PileRecordReportInfoResponse,//电桩记录查询结果响应 36
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PileRecordReportInfoResVo {

    /**
     * 桩编码
     */
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 记录上报序号
     */
    @Schema(description = "记录上报序号")
    private Integer recordSeq;

    /**
     * 枪标识 从1开始
     */
    @Schema(description = "枪标识")
    private Integer gunCode;

    /**
     * 运行模式 0-充电模式 1-放电模式
     */
    @Schema(description = "运行模式")
    private Integer runMode;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩
     */
    @Schema(description = "发起者")
    private Integer starter;

    /**
     * 用户账号
     */
    @Schema(description = "用户账号")
    private UserAccount userAccount;

    /**
     * 策略
     */
    @Schema(description = "策略")
    private Strategy strategy;

    /**
     * 交易记录号
     */
    @Schema(description = "交易记录号")
    private String recordId;

    /**
     * 开始时间
     */
    @Schema(description = "开始时间")
    private Long startTime;

    /**
     * 结束时间
     */
    @Schema(description = "结束时间")
    private Long endTime;

    /**
     * 开始直流电表读数 精度 0.001kW·h
     */
    @Schema(description = "开始直流电表读数")
    private Integer startDCMeters;

    /**
     * 结束直流电表读数 精度 0.001kW·h
     */
    @Schema(description = "结束直流电表读数")
    private Integer endDCMeters;

    /**
     * 开始交流电表读数 精度 0.001kW·h
     */
    @Schema(description = "开始交流电表读数")
    private Integer startACMeters;

    /**
     * 结束交流电表读数 精度 0.001kW·h
     */
    @Schema(description = "结束交流电表读数")
    private Integer endACMeters;

    /**
     * 总电量 精度 0.001kW·h
     */
    @Schema(description = "总电量")
    private Integer totalQ;

    /**
     * 总电费 精度 0.001元
     */
    @Schema(description = "总电费")
    private Integer totalCost;

    /**
     * 起始Soc 范围 0～100。精度 1%
     */
    @Schema(description = "起始Soc")
    private Integer startSoc;

    /**
     * 结束Soc 范围 0～100。精度 1%
     */
    @Schema(description = "结束Soc")
    private Integer endSoc;

    /**
     * 停止详细原因
     */
    @Schema(description = "停止详细原因")
    private Integer stopReason;

    /**
     * 结束详细描述
     */
    @Schema(description = "结束详细描述")
    private String stopDetail;

    /**
     * 费率模型ID
     */
    @Schema(description = "费率模型ID")
    private byte[] rateId = new byte[8];

    /**
     * 新费率id
     */
    @Schema(description = "新费率id")
    private String rateTemplateId;

    /**
     * 车辆VIN码
     */
    @Schema(description = "车辆VIN码")
    private String busVin;

    /**
     * 有效时段数
     */
    @Schema(description = "有效时段数")
    private Integer timeFrameNum;

    /**
     * 充放电时段电量
     */
    @Schema(description = "充放电时段电量")
    private List<Double> timeFrameQ;

    /**
     * 平台标识
     */
    @Schema(description = "平台标识")
    private String platformId;

}
