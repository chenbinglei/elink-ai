package com.sunmax.common.vo.protocol.mqtt.inter;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "紧急停止故障", required = true)
    private Integer emergencyStopFault;

    /**
     * 电桩通用故障
     */
    @ApiModelProperty(value = "电桩通用故障", required = true)
    private Integer generalFault;

    /**
     * Tcu上报故障
     */
    @ApiModelProperty(value = "Tcu上报故障", required = true)
    private Integer tcuReportFault;

    /**
     * 充放电接口故障
     */
    @ApiModelProperty(value = "充放电接口故障", required = true)
    private List<ItfRunFault> gun_fault;

    /**
     * 电力模块故障
     */
    @ApiModelProperty(value = "电力模块故障", required = true)
    private List<EleModuleFault> emodule_fault;

    @Data
    public static class ItfRunFault {

        /**
         * 枪标识
         */
        @ApiModelProperty(value = "枪标识", required = true)
        private Integer gunCode;

        /**
         * ccu上报故障信息
         */
        @ApiModelProperty(value = "ccu上报故障信息", required = true)
        private Integer ccuReportFault;

        /**
         * 车辆BMS故障信息
         */
        @ApiModelProperty(value = "车辆BMS故障信息", required = true)
        private Integer bmsReportFault;

        /**
         * ccu超时类故障信息
         */
        @ApiModelProperty(value = "ccu超时类故障信息", required = true)
        private Integer ccuTimeOutFault;

        /**
         * Bms超时类故障
         */
        @ApiModelProperty(value = "Bms超时类故障", required = true)
        private Integer bmsTimeOutFault;

        /**
         * 绝缘监测故障
         */
        @ApiModelProperty(value = "绝缘监测故障", required = true)
        private Integer idmFault;
    }

    @Data
    public static class EleModuleFault {

        /**
         * 模块地址
         */
        @ApiModelProperty(value = "模块地址", required = true)
        private Integer addr;

        /**
         * pcu上报故障信息
         */
        @ApiModelProperty(value = "pcu上报故障信息", required = true)
        private Integer pcuReportFault;

        /**
         * pcu超时类故障信息
         */
        @ApiModelProperty(value = "pcu超时类故障信息", required = true)
        private Integer pcuTimeOutFault;

    }

}
