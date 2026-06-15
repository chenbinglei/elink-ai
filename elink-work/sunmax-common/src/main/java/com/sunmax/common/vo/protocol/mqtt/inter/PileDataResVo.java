package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 4.31 CMD_PileDataResponse,//电桩运行数据请求响应  31
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PileDataResVo {

    /**
     * 桩编码
     */
    @Schema(description = "桩编码")
    private String pilesCode;

    /**
     * 枪编号
     */
    @Schema(description = "枪编号")
    private Integer gunCode;

    /**
     * 电池soc 范围 0～100，精度 1%
     */
    @Schema(description = "电池soc")
    private Integer soc;

    /**
     * 充/放电接口运行模式 0-充电模式 1-放电模式
     */
    @Schema(description = "充/放电接口运行模式")
    private Integer runmode;

    /**
     * 枪温度 1;精度 0.1ºC
     */
    @Schema(description = "枪温度1")
    private Integer gunTemp1;

    /**
     * 枪温度 1;精度 0.1ºC
     */
    @Schema(description = "枪温度2")
    private Integer gunTemp2;

    /**
     * 输出电压 精度 0.1V
     */
    @Schema(description = "输出电压")
    private Integer outVolt;

    /**
     * 输出电流 精度 0.01A
     */
    @Schema(description = "输出电流")
    private Integer outCurrent;

    /**
     * 需求电压 精度 0.1V
     */
    @Schema(description = "需求电压")
    private Integer reqVolt;

    /**
     * 需求电流 精度 0.01A
     */
    @Schema(description = "需求电流")
    private Integer reqCurrent;

    /**
     * 直流电表读数 精度 0.001kW·h
     */
    @Schema(description = "直流电表读数")
    private Integer dirMeterNum;

    /**
     * 交流电表读数 精度 0.001kW·h
     */
    @Schema(description = "交流电表读数")
    private Integer alterMeterNum;

    /**
     * A相电压 精度 0.1V
     */
    @Schema(description = "A相电压")
    private Integer outUa;

    /**
     * B相电压 精度 0.1V
     */
    @Schema(description = "B相电压")
    private Integer outUb;

    /**
     * C相电压 精度 0.1V
     */
    @Schema(description = "C相电压")
    private Integer outUc;

    /**
     * A相电流 精度 0.01A
     */
    @Schema(description = "A相电流")
    private Integer outIa;

    /**
     * B相电流 精度 0.01A
     */
    @Schema(description = "B相电流")
    private Integer outIb;

    /**
     * C相电流 精度 0.01A
     */
    @Schema(description = "C相电流")
    private Integer outIc;

    /**
     * 运行时长 精度 1 秒
     */
    @Schema(description = "运行时长")
    private Long runTime;

    /**
     * 剩余时长 精度 1 秒
     */
    @Schema(description = "剩余时长")
    private Long remainTime;

    /**
     * 总电量 精度 0.01kW·h
     */
    @Schema(description = "总电量")
    private Integer totalQt;

    /**
     * 总金额 精度 0.001 元
     */
    @Schema(description = "总金额")
    private Integer totalCost;

}
