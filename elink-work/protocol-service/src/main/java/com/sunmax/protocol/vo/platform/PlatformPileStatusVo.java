package com.sunmax.protocol.vo.platform;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "电桩状态参数实体类")
public class PlatformPileStatusVo {

    /**
     * 电桩编号
     */
    @Schema(description = "充电桩编号")
    private String pileCode;

    /**
     * 电桩状态 0-待机 1-工作 2-维护 3-故障
     */
    @Schema(description = "电桩状态 0-待机 1-工作 2-维护 3-故障")
    private Integer workStatus;

    /**
     * 充/放电接口状态数据
     */
    @Schema(description = "充/放电接口状态数据")
    private List<ItfData> itfDataList;


    /**
     * 充/放电接口数据
     */
    @Data
    @Schema(description = "充/放电接口数据")
    public static class ItfData {

        /**
         * 枪标识
         */
        @Schema(description = "枪标识")
        private Integer gunCode;

        /**
         * 枪工作状态 0-空闲 1-充电准备 2-充电中 3-充电完成 4-放电准备 5-放电中 6-放电完成 7-预约 255-故障
         */
        @Schema(description = "枪工作状态 0-空闲 1-充电准备 2-充电中 3-充电完成 4-放电准备 5-放电中 6-放电完成 7-预约 255-故障")
        private Integer gunWorkStatus;

        /**
         * 地锁状态 0-未知 1-降下 2-升起 3-运动中 4-故障
         */
        @Schema(description = "地锁状态 0-未知 1-降下 2-升起 3-运动中 4-故障")
        private Integer parkingStatus;

        /**
         * 车辆连接状态 0-未连接 1-连接 2-半连接;
         */
        @Schema(description = "车辆连接状态 0-未连接 1-连接 2-半连接")
        private Integer VehicleConnStatus;

        /**
         * 电池SOC
         */
        @Schema(description = "电池SOC")
        private Integer batterySoc = 0;

        /**
         *  运行模式 -1-未知 1-充电模式 2-放电模式
         */
        @Schema(description = "运行模式 -1-未知 1-充电模式 2-放电模式")
        private int runMode = -1;

        /**
         *  输出电压
         */
        @Schema(description = "输出电压")
        private Double outVolt = 0.0;

        /**
         *  输出电流
         */
        @Schema(description = "输出电流")
        private Double outCurrent = 0.0;

        /**
         *  需求电压
         */
        @Schema(description = "需求电压")
        private Double reqVolt = 0.0;

        /**
         *  需求电流
         */
        @Schema(description = "需求电流")
        private Double reqCurrent = 0.0;

        /**
         *  直流电表读数
         */
        @Schema(description = "直流电表读数")
        private Double dirMeterNum = 0.0;

        /**
         *  交流电表读数
         */
        @Schema(description = "交流电表读数")
        private Double alterMeterNum = 0.0;

        /**
         *  运行时间
         */
        @Schema(description = "运行时间")
        private Integer runTime = 0;

        /**
         *  剩余时间
         */
        @Schema(description = "剩余时间")
        private Integer remainTime = 0;

        /**
         *  总电量
         */
        @Schema(description = "总电量")
        private Double totalQt = 0.0;

        /**
         *  总费用
         */
        @Schema(description = "总费用")
        private BigDecimal totalCost = new BigDecimal(0);

        /**
         *  交易流水号
         */
        @Schema(description = "交易流水号")
        private String serialNum;

    }

}
