package com.sunmax.common.vo.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 记录上报序号
     */
    @ApiModelProperty(value = "记录上报序号", required = true)
    private Integer recordSeq;

    /**
     * 枪标识 从1开始
     */
    @ApiModelProperty(value = "枪标识", required = true)
    private Integer gunCode;

    /**
     * 运行模式 0-充电模式 1-放电模式
     */
    @ApiModelProperty(value = "运行模式", required = true)
    private Integer runMode;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩
     */
    @ApiModelProperty(value = "发起者", required = true)
    private Integer starter;

    /**
     * 用户账号
     */
    @ApiModelProperty(value = "用户账号", required = true)
    private UserAccount userAccount;

    /**
     * 策略
     */
    @ApiModelProperty(value = "策略", required = true)
    private Strategy strategy;

    /**
     * 交易记录号
     */
    @ApiModelProperty(value = "交易记录号", required = true)
    private String recordId;

    /**
     * 开始时间
     */
    @ApiModelProperty(value = "开始时间", required = true)
    private Long startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    private Long endTime;

    /**
     * 开始直流电表读数 精度 0.001kW·h
     */
    @ApiModelProperty(value = "开始直流电表读数", required = true)
    private Integer startDCMeters;

    /**
     * 结束直流电表读数 精度 0.001kW·h
     */
    @ApiModelProperty(value = "结束直流电表读数", required = true)
    private Integer endDCMeters;

    /**
     * 开始交流电表读数 精度 0.001kW·h
     */
    @ApiModelProperty(value = "开始交流电表读数", required = true)
    private Integer startACMeters;

    /**
     * 结束交流电表读数 精度 0.001kW·h
     */
    @ApiModelProperty(value = "结束交流电表读数", required = true)
    private Integer endACMeters;

    /**
     * 总电量 精度 0.001kW·h
     */
    @ApiModelProperty(value = "总电量", required = true)
    private Integer totalQ;

    /**
     * 总电费 精度 0.001元
     */
    @ApiModelProperty(value = "总电费", required = true)
    private Integer totalCost;

    /**
     * 起始Soc 范围 0～100。精度 1%
     */
    @ApiModelProperty(value = "起始Soc", required = true)
    private Integer startSoc;

    /**
     * 结束Soc 范围 0～100。精度 1%
     */
    @ApiModelProperty(value = "结束Soc", required = true)
    private Integer endSoc;

    /**
     * 停止详细原因
     */
    @ApiModelProperty(value = "停止详细原因", required = true)
    private Integer stopReason;

    /**
     * 结束详细描述
     */
    @ApiModelProperty(value = "结束详细描述", required = true)
    private String stopDetail;

    /**
     * 费率模型ID
     */
    @ApiModelProperty(value = "费率模型ID", required = true)
    private byte[] rateId = new byte[8];

    /**
     * 新费率id
     */
    @ApiModelProperty(value = "新费率id", required = true)
    private String rateTemplateId;

    /**
     * 车辆VIN码
     */
    @ApiModelProperty(value = "车辆VIN码", required = true)
    private String busVin;

    /**
     * 有效时段数
     */
    @ApiModelProperty(value = "有效时段数", required = true)
    private Integer timeFrameNum;

    /**
     * 充放电时段电量
     */
    @ApiModelProperty(value = "充放电时段电量", required = true)
    private List<Double> timeFrameQ;

    /**
     * 平台标识
     */
    @ApiModelProperty(value = "平台标识", required = true)
    private String platformId;

}
