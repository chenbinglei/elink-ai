package com.sunmax.common.vo.protocol.mqtt.web.response;

import com.sunmax.common.dto.protocol.mqtt.web.model.Strategy;
import com.sunmax.common.dto.protocol.mqtt.web.model.UserAccount;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 电桩记录订阅实体类
 */
@Data
public class PileRecordSubscribeVo {
    /**
     * 记录序号     从1开始
     */
    private Long recordSeq;
    /**

    /**
     * 记录上报类型 0 正常；1 断网上传；
     */
    private Integer reportType;

    /**
     * 平台标识
     */
    private String platformId;

    /**
     * 电桩编号
     */
    private String pilesCode;

    /**
     * 枪标识
     */
    private Integer gunCode;

    /**
     * 运行模式 0 充电模式；1 放电模式
     */
    private Integer runMode;

    /**
     * 发起者 1-App 2-第三方平台 3-电卡 4-VIN码 5-电桩屏幕强制启动 6-有序控制
     */
    private Integer starter;

    /**
     * 用户账号
     */
    private UserAccount userAccount;

    /**
     * 策略
     */
    private Strategy strategy;

    /**
     * 交易记录号
     */
    private String recordId;

    /**
     * 开始时间
     */
    private int startTime;

    /**
     * 结束时间
     */
    private int endTime;

    /**
     * 开始直流电表读数
     */
    private Integer startDCMeters;

    /**
     * 结束直流电表读数
     */
    private Integer endDCMeters;

    /**
     * 开始交流电表读数
     */
    private Double startACMeters;

    /**
     * 结束交流电表读数
     */
    private Double endACMeters;

    /**
     * 总电量
     */
    private Double totalQ;

    /**
     * 总费用
     */
    private BigDecimal totalCost;

    /**
     * 总电费
     */
    private BigDecimal totalElecFee;

    /**
     * 总服务费
     */
    private BigDecimal totalServiceFee;

    /**
     * 尖电费
     */
    private BigDecimal sharpElecFee;

    /**
     * 尖服务费
     */
    private BigDecimal sharpServiceFee;

    /**
     * 尖电量
     */
    private Double sharpQ;

    /**
     * 峰电费
     */
    private BigDecimal peakElecFee;

    /**
     * 峰服务费
     */
    private BigDecimal peakServiceFee;

    /**
     * 峰电量
     */
    private Double peakQ;

    /**
     * 平电费
     */
    private BigDecimal flatElecFee;

    /**
     * 平服务费
     */
    private BigDecimal flatServiceFee;

    /**
     * 平电量
     */
    private Double flatQ;

    /**
     * 谷电费
     */
    private BigDecimal valleyElecFee;

    /**
     * 谷服务费
     */
    private BigDecimal valleyServiceFee;

    /**
     * 谷电量
     */
    private Double valleyQ;

    /**
     * 起始SOC
     */
    private Integer startSoc;

    /**
     * 结束Soc
     */
    private Integer endSoc;

    /**
     * 停止原因
     */
    private int stopReason;

    /**
     * 停止响应原因
     */
    private String stopDetail;

    /**
     * 费率模型ID
     */
    private byte[] rateId = new byte[8];

    /**
     * 费率模型ID
     */
    private String rateTemplateId;

    /**
     * 车量 vin 码
     */
    private String busVin;

    /**
     * 有效时段数
     */
    private Integer timeFrameNum;

    /**
     * 充放电时段电量
     */
    private List<Double> timeFrameQ;


}
