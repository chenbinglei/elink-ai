package com.sunmax.common.vo.protocol.mqtt.web.data;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 网关日志类
 */
@Data
public class GatewayLogSubscribeVo implements Serializable {


    /**
     * 负载允许最大功率
     */
    private Double max_Load;
    /**
     * 当前负载
     */
    private Double now_Load;
    /**
     * 当前充电功率
     */
    private Double now_pilesLoad;
    /**
     * 充电功率目标值
     */
    private Double allCtrlPower;
    /**
     * 调控时间
     */
    private Long cmdTime;

    /**
     * 枪状态
     */
    private List<LogPowerCtrlVo> pilesCtrl = new ArrayList<>();


    @Data
    public static class LogPowerCtrlVo implements Serializable {
        /**
         * 桩编码
         */
        private String pilesCode;
        /**
         * 枪编号
         */
        private Integer gunCode;
        /**
         * 运行模式
         */
        private Integer runMode;
        /**
         * 控制类型
         */
        private Integer ctrlType;
        /**
         * 电桩需求
         */
        private Integer maxPower;
        /**
         * 控制结果
         */
        private Integer cmdResult;
        /**
         * 当前电桩功率
         */
        private Double oldOutP;
        /**
         * 控制功率
         */
        private Double newOutP;
    }


}
