package com.sunmax.common.vo.protocol.mqtt.inter;

import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 4.16 CMD_PILE_RECORD_REPORT,//充电记录上报 16
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PileRecordReportVo {

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
     * 记录上报类型 0 正常记录；1 离网记录
     */
    @ApiModelProperty(value = "记录上报序号", required = true)
    private Integer reportType;

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
    private Integer startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty(value = "结束时间", required = true)
    private Integer endTime;

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
    private Double totalQ;

    /**
     * 总费用 精度 0.001元
     */
    @ApiModelProperty(value = "总费用", required = true)
    private BigDecimal totalCost;

    /**
     * 总电费 精度 0.001元
     */
    @ApiModelProperty(value = "总电费", required = true)
    private BigDecimal totalElecFee;

    /**
     * 总服务费 精度 0.001元
     */
    @ApiModelProperty(value = "总服务费", required = true)
    private BigDecimal totalServiceFee;

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

    /**
     * 尖电价 精度 0.001元/kwh
     */
    @ApiModelProperty(value = "尖电价", required = true)
    private Integer sharpPrice;

    /**
     * 尖服务费 精度 0.001元/kwh
     */
    @ApiModelProperty(value = "尖服务费", required = true)
    private Integer sharpService;

    /**
     * 尖电量 精度 0.001kW·h
     */
    @ApiModelProperty(value = "尖电量", required = true)
    private Integer sharpQ;

    /**
     * 峰电价 精度 0.001元/kwh
     */
    @ApiModelProperty(value = "峰电价", required = true)
    private Integer peakPrice;

    /**
     * 峰服务费 精度 0.001元/kwh
     */
    @ApiModelProperty(value = "峰服务费", required = true)
    private Integer peakService;

    /**
     * 峰电量 精度 0.001kW·h
     */
    @ApiModelProperty(value = "峰电量", required = true)
    private Integer peakQ;

    /**
     * 平电价 精度 0.001元/kwh
     */
    @ApiModelProperty(value = "平电价", required = true)
    private Integer flatPrice;

    /**
     * 平服务费 精度 0.001元/kwh
     */
    @ApiModelProperty(value = "平服务费", required = true)
    private Integer flatService;

    /**
     * 平电量 精度 0.001kW·h
     */
    @ApiModelProperty(value = "平电量", required = true)
    private Integer flatQ;

    /**
     * 谷电价 精度 0.001元/kwh
     */
    @ApiModelProperty(value = "谷电价", required = true)
    private Integer valleyPrice;

    /**
     * 谷服务费 精度 0.001元/kwh
     */
    @ApiModelProperty(value = "谷服务费", required = true)
    private Integer valleyService;

    /**
     * 谷电量 精度 0.001kW·h
     */
    @ApiModelProperty(value = "谷电量", required = true)
    private Integer valleyQ;

}
