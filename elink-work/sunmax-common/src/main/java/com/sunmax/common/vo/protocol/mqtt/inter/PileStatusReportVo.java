package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 4.15 CMD_PILE_STATUS_REPORT,//电桩状态上报 15
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PileStatusReportVo {

    /**
     * 工作状态 0-待机 1-工作 2-维护 3-故障
     */
    @ApiModelProperty(value = "工作状态", required = true)
    private Integer workState;

    /**
     * 复位次数
     */
    @ApiModelProperty(value = "复位次数", required = true)
    private Integer resetTimes;

    /**
     * 充放电接口状态
     */
    @ApiModelProperty(value = "充放电接口状态", required = true)
    private List<ItfRunState> gun_state;

    @Data
    public static class ItfRunState {

        /**
         * 枪标识 从1开始
         */
        @ApiModelProperty(value = "枪标识", required = true)
        private Integer guncode;

        /**
         * 枪工作状态 0-空闲 1-充电准备 2-充电中 3-充电完成 4-放电准备 5-放电中 6-放电完成 7-预约 8-暂停 255-故障
         */
        @ApiModelProperty(value = "枪工作状态", required = true)
        private Integer workState;

        /**
         * 地锁状态 0-未知 1-降下 2-升起 3-运动中 4-故障
         */
        @ApiModelProperty(value = "地锁状态", required = true)
        private Integer parkingLockState;

        /**
         * 车辆连接状态 0-未连接 1-半连接 2-连接 3-连接故障
         */
        @ApiModelProperty(value = "车辆连接状态", required = true)
        private Integer vehicleConnState;

        /**
         * 枪锁状态 0-未连接 1-半连接 2-连接 3-连接故障
         */
        @ApiModelProperty(value = "枪锁状态", required = true)
        private Integer gunLockState;

        /**
         * 枪座状态 0-未连接 1-半连接 2-连接 3-连接故障
         */
        @ApiModelProperty(value = "枪座状态", required = true)
        private Integer gunHolderState;

        /**
         * 接触器k1k2状态 0-未连接 1-半连接 2-连接 3-连接故障
         */
        @ApiModelProperty(value = "接触器k1k2状态", required = true)
        private Integer k1k2State;

    }

}
