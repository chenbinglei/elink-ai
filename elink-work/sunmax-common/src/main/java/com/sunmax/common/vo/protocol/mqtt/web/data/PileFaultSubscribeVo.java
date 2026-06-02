package com.sunmax.common.vo.protocol.mqtt.web.data;

import lombok.Data;

import java.util.List;

/**
 * 电桩故障订阅实体类
 */
@Data
public class PileFaultSubscribeVo {

    /**
     * 电桩紧急停止故障
     */
    private Integer emergencyStopFault;

    /**
     * 电力模块故障
     */
    private List<ModuleFaultVo> emodule_fault;

    /**
     * 电桩通用故障
     */
    private Integer generalFault;

    /**
     * 充电接口故障
     */
    private List<GunFaultVo> gun_fault;

    /**
     * Tcu上报故障
     */
    private Integer tcuReportFault;


    @Data
    public static class ModuleFaultVo {

        /**
         * 模块地址
         */
        private Integer addr;

        /**
         * PCU上报故障
         */
        private Integer pcuReportFault;

        /**
         * PCU超时类故障
         */
        private Integer pcuTimeOutFalut;

    }

    @Data
    public static class GunFaultVo {

        /**
         * 枪编号
         */
        private Integer gunCode;

        /**
         * CCU上报故障
         */
        private Integer ccuReportFault;

        /**
         * BMS上报故障
         */
        private Integer bmsReportFault;

        /**
         * CCU超时类故障
         */
        private Integer ccuTimeOutFault;

        /**
         * BMS超时类故障
         */
        private Integer bmsTimeOutFault;

        /**
         * IDM绝缘检测故障
         */
        private Integer idmFault;

    }

}
