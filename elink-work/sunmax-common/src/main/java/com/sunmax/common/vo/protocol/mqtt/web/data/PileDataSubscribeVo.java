package com.sunmax.common.vo.protocol.mqtt.web.data;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 电桩数据订阅实体类
 */
@Data
public class PileDataSubscribeVo {

    /**
     * 充电枪数据
     */
    private List<GunDataVo> gun_data;

    /**
     * 电桩内部温度  范围 0-2500
     */
    private Integer innerTemp;

    /**
     * 电力模块最高温度
     */
    private Integer powerModMaxTemp;

    /**
     * 电力模块最高温度序号
     */
    private Integer maxTempMod;

    /**
     * 电桩开机运行时间
     */
    private Long runtime;

    /**
     * 电桩累计充电时间 所有枪累加
     */
    private Long totalChargeTime;

    /**
     * 电桩累计充电电量 所有枪累加
     */
    private Double totalChargeQt;

    /**
     * 电桩累计充电次数 所有枪累加
     */
    private Long totalChargeNum;

    /**
     * 电桩累计放电时间 所有枪累加
     */
    private Long totalDischargeTime;

    /**
     * 电桩累计放电电量 所有枪累加
     */
    private Double totalDischargeQt;

    /**
     * 电桩累计放电次数 所有枪累加
     */
    private Long totalDischargeNum;

    @Data
    public static class GunDataVo {

        /**
         * 枪标识 从 1 开始
         */
        private Integer gunCode;

        /**
         * 电池 SOC 范围 0～100，
         */
        private Integer soc;

        /**
         * 充/放电接口运行模式
         */
        private int runmode;

        /**
         * 保留
         */
        private byte[] reserve1 = new byte[1];

        /**
         * 枪温度 1
         */
        private Double gunTemp1;

        /**
         * 枪温度 2
         */
        private Double gunTemp2;

        /**
         * 输出电压
         */
        private Double outVolt;

        /**
         * 输出电流
         */
        private Double outCurrent;

        /**
         * 需求电压
         */
        private Double reqVolt;

        /**
         * 需求电流
         */
        private Double reqCurrent;

        /**
         * 直流电表读数
         */
        private Double dirMeterNum;

        /**
         * 交流电表读数
         */
        private Double alterMeterNum;

        /**
         * A 相输出电压
         */
        private Double outUa;

        /**
         * B 相输出电压
         */
        private Double outUb;

        /**
         * C 相输出电压
         */
        private Double outUc;

        /**
         * A 相输出电流
         */
        private Double outIa;

        /**
         * B 相输出电流
         */
        private Double outIb;

        /**
         * C 相输出电流
         */
        private Double outIc;
        /**
         * 运行时间
         */
        private int runTime;

        /**
         * 剩余时间
         */
        private int remainTime;

        /**
         * 总电量
         */
        private Double totalQt;

        /**
         * 总费用
         */
        private BigDecimal totalCost;

    }

}
