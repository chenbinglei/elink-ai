package com.sunmax.protocol.vo.platform;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@ApiModel(value = "PileStatusVo", description = "电桩状态参数实体类")
public class PlatformPileStatusVo {

    /**
     * 电桩编号
     */
    @ApiModelProperty(value = "充电桩编号", required = true)
    private String pileCode;

    /**
     * 电桩状态 0-待机 1-工作 2-维护 3-故障
     */
    @ApiModelProperty(value = "电桩状态 0-待机 1-工作 2-维护 3-故障", required = true)
    private Integer workStatus;

    /**
     * 充/放电接口状态数据
     */
    @ApiModelProperty(value = "充/放电接口状态数据", required = true)
    private List<ItfData> itfDataList;


    /**
     * 充/放电接口数据
     */
    @Data
    @ApiModel(value = "充/放电接口数据")
    public static class ItfData {

        /**
         * 枪标识
         */
        @ApiModelProperty(value = "枪标识", required = true)
        private Integer gunCode;

        /**
         * 枪工作状态 0-空闲 1-充电准备 2-充电中 3-充电完成 4-放电准备 5-放电中 6-放电完成 7-预约 255-故障
         */
        @ApiModelProperty(value = "枪工作状态 0-空闲 1-充电准备 2-充电中 3-充电完成 4-放电准备 5-放电中 6-放电完成 7-预约 255-故障", required = true)
        private Integer gunWorkStatus;

        /**
         * 地锁状态 0-未知 1-降下 2-升起 3-运动中 4-故障
         */
        @ApiModelProperty(value = "地锁状态 0-未知 1-降下 2-升起 3-运动中 4-故障", required = true)
        private Integer parkingStatus;

        /**
         * 车辆连接状态 0-未连接 1-连接 2-半连接;
         */
        @ApiModelProperty(value = "车辆连接状态 0-未连接 1-连接 2-半连接", required = true)
        private Integer VehicleConnStatus;

        /**
         * 电池SOC
         */
        @ApiModelProperty(value = "电池SOC", required = true)
        private Integer batterySoc = 0;

        /**
         *  运行模式 -1-未知 1-充电模式 2-放电模式
         */
        @ApiModelProperty(value = "运行模式 -1-未知 1-充电模式 2-放电模式")
        private int runMode = -1;

        /**
         *  输出电压
         */
        @ApiModelProperty(value = "输出电压", required = true)
        private Double outVolt = 0.0;

        /**
         *  输出电流
         */
        @ApiModelProperty(value = "输出电流", required = true)
        private Double outCurrent = 0.0;

        /**
         *  需求电压
         */
        @ApiModelProperty(value = "需求电压", required = true)
        private Double reqVolt = 0.0;

        /**
         *  需求电流
         */
        @ApiModelProperty(value = "需求电流", required = true)
        private Double reqCurrent = 0.0;

        /**
         *  直流电表读数
         */
        @ApiModelProperty(value = "直流电表读数", required = true)
        private Double dirMeterNum = 0.0;

        /**
         *  交流电表读数
         */
        @ApiModelProperty(value = "交流电表读数", required = true)
        private Double alterMeterNum = 0.0;

        /**
         *  运行时间
         */
        @ApiModelProperty(value = "运行时间", required = true)
        private Integer runTime = 0;

        /**
         *  剩余时间
         */
        @ApiModelProperty(value = "剩余时间", required = true)
        private Integer remainTime = 0;

        /**
         *  总电量
         */
        @ApiModelProperty(value = "总电量", required = true)
        private Double totalQt = 0.0;

        /**
         *  总费用
         */
        @ApiModelProperty(value = "总费用", required = true)
        private BigDecimal totalCost = new BigDecimal(0);

        /**
         *  交易流水号
         */
        @ApiModelProperty(value = "交易流水号", required = true)
        private String serialNum;

    }

}
