package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 4.14 CMD_PILE_FAULT_REPORT,//电桩故障上报 14
 * 发送方向：前置服务--->平台服务
 */
@Data
public class PileFaultReportVo {

    /**
     * 电桩紧急停止故障
     */
    @Schema(description = "紧急停止故障")
    private Integer emergencyStopFault;

    /**
     * 电桩通用故障
     */
    @Schema(description = "电桩通用故障")
    private Integer generalFault;

    /**
     * Tcu上报故障
     */
    @Schema(description = "Tcu上报故障")
    private Integer tcuReportFault;

    /**
     * 充放电接口故障
     */
    @Schema(description = "充放电接口故障")
    private List<ItfRunFault> gun_fault;

    /**
     * 电力模块故障
     */
    @Schema(description = "电力模块故障")
    private List<EleModuleFault> emodule_fault;

    @Data
    public static class ItfRunFault {

        /**
         * 枪标识
         */
        @Schema(description = "枪标识")
        private Integer gunCode;

        /**
         * ccu上报故障信息
         */
        @Schema(description = "ccu上报故障信息")
        private Integer ccuReportFault;

        /**
         * 车辆BMS故障信息
         */
        @Schema(description = "车辆BMS故障信息")
        private Integer bmsReportFault;

        /**
         * ccu超时类故障信息
         */
        @Schema(description = "ccu超时类故障信息")
        private Integer ccuTimeOutFault;

        /**
         * Bms超时类故障
         */
        @Schema(description = "Bms超时类故障")
        private Integer bmsTimeOutFault;

        /**
         * 绝缘监测故障
         */
        @Schema(description = "绝缘监测故障")
        private Integer idmFault;
    }

    @Data
    public static class EleModuleFault {

        /**
         * 模块地址
         */
        @Schema(description = "模块地址")
        private Integer addr;

        /**
         * pcu上报故障信息
         */
        @Schema(description = "pcu上报故障信息")
        private Integer pcuReportFault;

        /**
         * pcu超时类故障信息
         */
        @Schema(description = "pcu超时类故障信息")
        private Integer pcuTimeOutFault;

    }

}
