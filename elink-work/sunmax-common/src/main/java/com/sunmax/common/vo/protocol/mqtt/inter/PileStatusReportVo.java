package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "工作状态")
    private Integer workState;

    /**
     * 复位次数
     */
    @Schema(description = "复位次数")
    private Integer resetTimes;

    /**
     * 充放电接口状态
     */
    @Schema(description = "充放电接口状态")
    private List<ItfRunState> gun_state;

    @Data
    public static class ItfRunState {

        /**
         * 枪标识 从1开始
         */
        @Schema(description = "枪标识")
        private Integer guncode;

        /**
         * 枪工作状态 0-空闲 1-充电准备 2-充电中 3-充电完成 4-放电准备 5-放电中 6-放电完成 7-预约 8-暂停 255-故障
         */
        @Schema(description = "枪工作状态")
        private Integer workState;

        /**
         * 地锁状态 0-未知 1-降下 2-升起 3-运动中 4-故障
         */
        @Schema(description = "地锁状态")
        private Integer parkingLockState;

        /**
         * 车辆连接状态 0-未连接 1-半连接 2-连接 3-连接故障
         */
        @Schema(description = "车辆连接状态")
        private Integer vehicleConnState;

        /**
         * 枪锁状态 0-未连接 1-半连接 2-连接 3-连接故障
         */
        @Schema(description = "枪锁状态")
        private Integer gunLockState;

        /**
         * 枪座状态 0-未连接 1-半连接 2-连接 3-连接故障
         */
        @Schema(description = "枪座状态")
        private Integer gunHolderState;

        /**
         * 接触器k1k2状态 0-未连接 1-半连接 2-连接 3-连接故障
         */
        @Schema(description = "接触器k1k2状态")
        private Integer k1k2State;

    }

}
