package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "桩编码", required = true)
    private String pilesCode;

    /**
     * 枪编号
     */
    @ApiModelProperty(value = "枪编号", required = true)
    private Integer gunCode;

    /**
     * 电池soc 范围 0～100，精度 1%
     */
    @ApiModelProperty(value = "电池soc", required = true)
    private Integer soc;

    /**
     * 充/放电接口运行模式 0-充电模式 1-放电模式
     */
    @ApiModelProperty(value = "充/放电接口运行模式", required = true)
    private Integer runmode;

    /**
     * 枪温度 1;精度 0.1ºC
     */
    @ApiModelProperty(value = "枪温度1", required = true)
    private Integer gunTemp1;

    /**
     * 枪温度 1;精度 0.1ºC
     */
    @ApiModelProperty(value = "枪温度2", required = true)
    private Integer gunTemp2;

    /**
     * 输出电压 精度 0.1V
     */
    @ApiModelProperty(value = "输出电压", required = true)
    private Integer outVolt;

    /**
     * 输出电流 精度 0.01A
     */
    @ApiModelProperty(value = "输出电流", required = true)
    private Integer outCurrent;

    /**
     * 需求电压 精度 0.1V
     */
    @ApiModelProperty(value = "需求电压", required = true)
    private Integer reqVolt;

    /**
     * 需求电流 精度 0.01A
     */
    @ApiModelProperty(value = "需求电流", required = true)
    private Integer reqCurrent;

    /**
     * 直流电表读数 精度 0.001kW·h
     */
    @ApiModelProperty(value = "直流电表读数", required = true)
    private Integer dirMeterNum;

    /**
     * 交流电表读数 精度 0.001kW·h
     */
    @ApiModelProperty(value = "交流电表读数", required = true)
    private Integer alterMeterNum;

    /**
     * A相电压 精度 0.1V
     */
    @ApiModelProperty(value = "A相电压", required = true)
    private Integer outUa;

    /**
     * B相电压 精度 0.1V
     */
    @ApiModelProperty(value = "B相电压", required = true)
    private Integer outUb;

    /**
     * C相电压 精度 0.1V
     */
    @ApiModelProperty(value = "C相电压", required = true)
    private Integer outUc;

    /**
     * A相电流 精度 0.01A
     */
    @ApiModelProperty(value = "A相电流", required = true)
    private Integer outIa;

    /**
     * B相电流 精度 0.01A
     */
    @ApiModelProperty(value = "B相电流", required = true)
    private Integer outIb;

    /**
     * C相电流 精度 0.01A
     */
    @ApiModelProperty(value = "C相电流", required = true)
    private Integer outIc;

    /**
     * 运行时长 精度 1 秒
     */
    @ApiModelProperty(value = "运行时长", required = true)
    private Long runTime;

    /**
     * 剩余时长 精度 1 秒
     */
    @ApiModelProperty(value = "剩余时长", required = true)
    private Long remainTime;

    /**
     * 总电量 精度 0.01kW·h
     */
    @ApiModelProperty(value = "总电量", required = true)
    private Integer totalQt;

    /**
     * 总金额 精度 0.001 元
     */
    @ApiModelProperty(value = "总金额", required = true)
    private Integer totalCost;

}
