package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 4.13 CMD_PILE_DATA_REPORT,//电桩数据上报 13
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PileDataReportVo {

    /**
     * 电桩开机运行时长
     */
    @ApiModelProperty(value = "电桩开机运行时长", required = true)
    private Long runTime;

    /**
     * 累计充电时长
     */
    @ApiModelProperty(value = "累计充电时长", required = true)
    private Long accChargeTime;

    /**
     * 累计充电电量
     */
    @ApiModelProperty(value = "累计充电电量", required = true)
    private Long accChargeQt;

    /**
     * 电桩累计充电次数
     */
    @ApiModelProperty(value = "电桩累计充电次数", required = true)
    private Long accChargeNum;

    /**
     * 电桩累计放电时长
     */
    @ApiModelProperty(value = "电桩累计放电时长", required = true)
    private Long accDischargeTime;

    /**
     * 电桩累计放电电量
     */
    @ApiModelProperty(value = "电桩累计放电电量", required = true)
    private Long accDischargeQt;

    /**
     * 电桩累计放电次数
     */
    @ApiModelProperty(value = "电桩累计放电次数", required = true)
    private Long accDischargeNum;

    /**
     * 电桩内部温度
     */
    @ApiModelProperty(value = "电桩内部温度", required = true)
    private Integer innerTemp;

    /**
     * 电力模块最高温度
     */
    @ApiModelProperty(value = "电力模块最高温度", required = true)
    private Integer powerModMaxTemp;

    /**
     * 电力模块最高温度序号
     */
    @ApiModelProperty(value = "电力模块最高温度序号", required = true)
    private Integer maxTempMod;

    /**
     * 充放电接口状态
     */
    @ApiModelProperty(value = "充放电接口状态", required = true)
    private List<ItfRunData> gun_data;

    @Data
    public static class ItfRunData {

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
         * 充放电接口运行模式 0-充电模式 1-放电模式
         */
        @ApiModelProperty(value = "充放电接口运行模式 0-充电模式 1-放电模式", required = true)
        private Integer runmode;

        /**
         * 枪温度1 精度 0.1ºC
         */
        @ApiModelProperty(value = "枪温度1", required = true)
        private Integer gunTemp1;

        /**
         * 枪温度2 精度 0.1ºC
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
        private Integer runTime;

        /**
         * 剩余时长 精度 1 秒
         */
        @ApiModelProperty(value = "剩余时长", required = true)
        private Integer remainTime;

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

}
